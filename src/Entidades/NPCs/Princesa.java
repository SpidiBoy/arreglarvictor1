package Entidades.NPCs;

import Entidades.JuegoObjetos;
import Entidades.Jugador;
import SistemaGFX.Animacion;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import mariotest.Juego;

/**
 * Clase Princesa con animación de movimiento hacia DK
 * * @author LENOVO
 */
public class Princesa extends JuegoObjetos {
    
    private static final float WIDTH = 16;
    private static final float HEIGHT = 32;
    
    private Handler handler;
    private BufferedImage[] princesaSprites;
    
    private Animacion princesaEspera;
    private Animacion princesaPideAyuda;
    private Animacion animacionActual;
    
    private EstadoPrincesa estado;
    private boolean mirandoDerecha = true;
    
    private int ticksDesdeUltimaAnimacion = 0;
    private int ticksEntreAnimaciones = 160;
    private static final int DURACION_PEDIR_AYUDA = 60;
    
    private boolean rescatada = false;
    
    // Sistema de movimiento
    private boolean moviendose = false;
    private float destinoX, destinoY;
    private static final float VELOCIDAD_MOVIMIENTO = 1.5f;
    
    public enum EstadoPrincesa {
        ESPERANDO,
        PIDIENDO_AYUDA,
        RESCATADA,
        EN_PELIGRO,
        SIENDO_LLEVADA
    }
    
    public Princesa(float x, float y, int scale, Handler handler) {
        super(x, y, ObjetosID.Princesa, WIDTH, HEIGHT, scale);
        this.handler = handler;
        this.estado = EstadoPrincesa.ESPERANDO;
        
        cargarSprites();
        inicializarAnimaciones();
        
        System.out.println("[PRINCESA] Creada en (" + x + ", " + y + ")");
    }
    
    private void cargarSprites() {
        try {
            princesaSprites = Juego.getTextura().getPrincesaSprites();
            System.out.println("[PRINCESA] Sprites cargados: " + princesaSprites.length);
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudieron cargar sprites de Princesa: " + e.getMessage());
        }
    }
    
    private void inicializarAnimaciones() {
        if (princesaSprites == null || princesaSprites.length < 4) {
            System.err.println("[ERROR] No hay suficientes sprites para las animaciones de Princesa");
            return;
        }
        
        princesaEspera = new Animacion(15, princesaSprites[0], princesaSprites[1]);
        princesaPideAyuda = new Animacion(8, princesaSprites[2], princesaSprites[3]);
        
        animacionActual = princesaEspera;
        
        System.out.println("[PRINCESA] Animaciones inicializadas correctamente.");
    }

    @Override
    public void tick() {
        if (animacionActual != null) {
            animacionActual.runAnimacion();
        }
        
        //  Sistema de movimiento
        if (moviendose) {
            moverHaciaDestino();
        }
        
        switch (estado) {
            case ESPERANDO:
                tickEsperando();
                break;
                
            case PIDIENDO_AYUDA:
                tickPidiendoAyuda();
                break;
                
            case RESCATADA:
                tickRescatada();
                break;
                
            case EN_PELIGRO:
                tickEnPeligro();
                break;
                
            case SIENDO_LLEVADA: // 🆕 NUEVO
                tickSiendoLlevada();
                break;
        }
        
        verificarProximidadJugador();
        verificarProximidadDiegoKong();
    }
    
    private void tickEsperando() {
        animacionActual = princesaEspera;
        ticksDesdeUltimaAnimacion++;
        
        if (ticksDesdeUltimaAnimacion >= ticksEntreAnimaciones) {
            estado = EstadoPrincesa.PIDIENDO_AYUDA;
            ticksDesdeUltimaAnimacion = 0;
        }
    }
    
    private void tickPidiendoAyuda() {
        animacionActual = princesaPideAyuda;
        ticksDesdeUltimaAnimacion++;
        
        if (ticksDesdeUltimaAnimacion >= DURACION_PEDIR_AYUDA) {
            estado = EstadoPrincesa.ESPERANDO;
            ticksDesdeUltimaAnimacion = 0;
            
            ticksEntreAnimaciones = 60 + (int)(Math.random() * 180);
        }
    }
    
    private void tickRescatada() {
        animacionActual = princesaPideAyuda;
    }
    
    private void tickEnPeligro() {
        animacionActual = princesaPideAyuda;
        ticksDesdeUltimaAnimacion++;
        
        if (ticksDesdeUltimaAnimacion >= 120 && !diegoKongCerca()) {
            estado = EstadoPrincesa.ESPERANDO;
            ticksDesdeUltimaAnimacion = 0;
        }
    }
    
    // Tick cuando está siendo llevada por DK
    private void tickSiendoLlevada() {
        animacionActual = princesaPideAyuda; // Brazos arriba pidiendo ayuda
        // El movimiento se maneja en moverHaciaDestino()
    }
    
    //Sistema de movimiento hacia un destino
    private void moverHaciaDestino() {
        float deltaX = destinoX - getX();
        float deltaY = destinoY - getY();
        
        float distancia = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        // Si llegó al destino
        if (distancia < VELOCIDAD_MOVIMIENTO) {
            setX(destinoX);
            setY(destinoY);
            moviendose = false;
            System.out.println("[PRINCESA] Llegó al destino");
            return;
        }
        
        // Normalizar vector de dirección
        float dirX = deltaX / distancia;
        float dirY = deltaY / distancia;
        
        // Mover hacia el destino
        setX(getX() + dirX * VELOCIDAD_MOVIMIENTO);
        setY(getY() + dirY * VELOCIDAD_MOVIMIENTO);
        
        // Actualizar dirección visual
        mirandoDerecha = deltaX > 0;
    }
    
    // Iniciar movimiento hacia una posición
    public void moverHacia(float x, float y) {
        this.destinoX = x;
        this.destinoY = y;
        this.moviendose = true;
        this.estado = EstadoPrincesa.SIENDO_LLEVADA;
        
        System.out.println("[PRINCESA] Iniciando movimiento hacia (" + x + ", " + y + ")");
    }
    
    //Detener movimiento
    public void detenerMovimiento() {
        this.moviendose = false;
    }
    
    private void verificarProximidadJugador() {
        if (rescatada) return;
        
        Jugador player = handler.getPlayer();
        if (player == null) return;
        
        float distanciaX = Math.abs(player.getX() - getX());
        
        // ==================== CAMBIO CLAVE ====================
        // La lógica de victoria (distX < 30 && distY < 30) se elimina.
        // GestorNiveles es ahora el único responsable de detectar la victoria.
        // Esta función ahora solo hace que la princesa mire al jugador.
        // ======================================================
        
        if (distanciaX < 100) {
            mirandoDerecha = player.getX() > getX();
        }
    }
    
    private void verificarProximidadDiegoKong() {
        if (rescatada) return;
        
        boolean dkCerca = false;
        
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.DiegoKong) {
                float distanciaX = Math.abs(obj.getX() - getX());
                float distanciaY = Math.abs(obj.getY() - getY());
                
                if (distanciaX < 150 && distanciaY < 150) {
                    dkCerca = true;
                    break;
                }
            }
        }
        
        if (dkCerca && estado == EstadoPrincesa.ESPERANDO) {
            estado = EstadoPrincesa.EN_PELIGRO;
            ticksDesdeUltimaAnimacion = 0;
        }
    }
    
    private boolean diegoKongCerca() {
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.DiegoKong) {
                float distanciaX = Math.abs(obj.getX() - getX());
                float distanciaY = Math.abs(obj.getY() - getY());
                
                if (distanciaX < 150 && distanciaY < 150) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void rescatar() {
        if (!rescatada) {
            rescatada = true;
            estado = EstadoPrincesa.RESCATADA;
            System.out.println("[PRINCESA] ¡Ha sido rescatada!");
        }
    }
    
    @Override
    public void aplicarGravedad() {
    }

    @Override
    public void render(Graphics g) {
        if (animacionActual != null && princesaSprites != null) {
            if (mirandoDerecha) {
                animacionActual.drawAnimacion(g, 
                    (int) getX(), (int) getY(), 
                    (int) getWidth(), (int) getHeight()
                );
            } else {
                animacionActual.drawAnimacion(g, 
                    (int) (getX() + getWidth()), (int) getY(), 
                    (int) -getWidth(), (int) getHeight()
                );
            }
            
            if (estado == EstadoPrincesa.PIDIENDO_AYUDA || estado == EstadoPrincesa.EN_PELIGRO) {
                g.setColor(Color.WHITE);
                g.drawString("AYUDA!", (int) getX() + 45, (int) getY() - -20);
            }
            
        } else {
            g.setColor(new Color(255, 192, 203));
            g.fillRect((int) getX() + 2, (int) getY() + 8, 
                      (int) getWidth() - 4, (int) getHeight() - 10);
            
            g.setColor(new Color(255, 220, 177));
            g.fillOval((int) getX() + 4, (int) getY() + 2, 
                      (int) getWidth() - 8, 8);
            
            g.setColor(Color.YELLOW);
            g.fillRect((int) getX() + 5, (int) getY(), 6, 2);
            
            g.setColor(Color.WHITE);
            g.drawString("P", (int) getX() + 6, (int) getY() + 7);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(
            (int) getX(),
            (int) getY(),
            (int) getWidth(),
            (int) getHeight()
        );
    }
    
    public EstadoPrincesa getEstado() {
        return estado;
    }
    
    public boolean isRescatada() {
        return rescatada;
    }
    
    public void setRescatada(boolean rescatada) {
        this.rescatada = rescatada;
        if (rescatada) {
            estado = EstadoPrincesa.RESCATADA;
        }
    }
    
    public boolean isMirandoDerecha() {
        return mirandoDerecha;
    }
    
    public void setMirandoDerecha(boolean mirandoDerecha) {
        this.mirandoDerecha = mirandoDerecha;
    }
    
    public boolean isMoviendose() {
        return moviendose;
    }
}