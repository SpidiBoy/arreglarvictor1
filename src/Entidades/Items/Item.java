package Entidades.Items;


import Entidades.JuegoObjetos;
import Entidades.Jugador;
import Entidades.EfectosVisuales.TextoPuntos;
import SistemaGFX.Animacion;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import mariotest.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Clase base para Items coleccionables
 * Usa patrón Strategy para diferentes comportamientos
 * 
 * @author LENOVO
 */
public abstract class Item extends JuegoObjetos {
    
    protected Handler handler;
    protected BufferedImage[] sprites;
    protected Animacion animacion;
    
    // Propiedades del item
    protected int valorPuntos;
    protected boolean recolectado;
    protected boolean desapareceDespuesDeRecoger;
    protected int ticksVida;
    protected int ticksMaxVida;
    
    // Física
    protected boolean tieneGravedad;
    protected boolean flotar;
    protected float amplitudFlotacion;
    protected float velocidadFlotacion;
    protected float offsetFlotacion;
    
    // Efectos visuales
    protected boolean brillar;
    protected int ticksBrillo;
    
    /**
     * Constructor base de Item
     */
    public Item(float x, float y, float width, float height, int scale, 
                Handler handler, int valorPuntos) {
        super(x, y, ObjetosID.Item, width, height, scale);
        this.handler = handler;
        this.valorPuntos = valorPuntos;
        this.recolectado = false;
        this.desapareceDespuesDeRecoger = true;
        this.ticksVida = 0;
        this.ticksMaxVida = -1; // -1 = duración infinita
        
        // Física por defecto
        this.tieneGravedad = true;
        this.flotar = false;
        this.amplitudFlotacion = 0f;
        this.velocidadFlotacion = 0.05f;
        this.offsetFlotacion = 0f;
        
        this.brillar = true;
        this.ticksBrillo = 0;
    }
    
    @Override
    public void tick() {
        if (recolectado) return;
        
        ticksVida++;
        ticksBrillo++;
        
        // Verificar tiempo de vida
        if (ticksMaxVida > 0 && ticksVida >= ticksMaxVida) {
            destruir();
            return;
        }
        
        // Aplicar física
        if (flotar) {
            aplicarFlotacion();
        } else if (tieneGravedad) {
            aplicarGravedad();
            manejarColisiones();
        }
        
        // Aplicar movimiento
        setX(getX() + getVelX());
        setY(getY() + getVely());
        
        // Actualizar animación
        if (animacion != null) {
            animacion.runAnimacion();
        }
        
        // Verificar colisión con jugador
        verificarRecoleccion();
    }
    
    /**
     * Aplica efecto de flotación (movimiento ondulatorio)
     */
    protected void aplicarFlotacion() {
        offsetFlotacion += velocidadFlotacion;
        float yOriginal = getY() - (float)(Math.sin(offsetFlotacion) * amplitudFlotacion);
        setY(yOriginal);
        setVely(0);
    }
    
    /**
     * Maneja colisiones con bloques
     */
    protected void manejarColisiones() {
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.Tile || obj.getId() == ObjetosID.Pipe) {
                // Colisión inferior
                if (getBounds().intersects(obj.getBounds())) {
                    setY(obj.getY() - getHeight());
                    setVely(0);
                }
            }
        }
    }
    
    /**
     * Verifica si el jugador recolecta el item
     */
    protected void verificarRecoleccion() {
        Jugador player = handler.getPlayer();
        if (player == null) return;
        
        if (getBounds().intersects(player.getBounds())) {
            recolectar(player);
        }
    }
    
    /**
     * Método abstracto: define qué sucede al recolectar
     */
    protected abstract void aplicarEfecto(Jugador player);
    
    /**
     * Recolecta el item
     */
    public void recolectar(Jugador player) {
        if (recolectado) return;
        
        recolectado = true;
        //Musica
        
        // Aplicar efecto específico del item
        aplicarEfecto(player);
        
        // Sumar puntos
        if (handler.getEstadoJuego() != null) {
            handler.getEstadoJuego().sumarPuntos(valorPuntos);
        }
        
        // Crear efecto visual de puntos
        crearEfectoPuntos();
        
        System.out.println("[ITEM] Recolectado: " + this.getClass().getSimpleName() + 
                          " (+"+valorPuntos+" pts)");
        
        // Destruir si corresponde
        if (desapareceDespuesDeRecoger) {
            destruir();
        }
    }
    
    /**
     * Crea un efecto visual mostrando los puntos ganados
     */
    protected void crearEfectoPuntos() {
        TextoPuntos texto = new TextoPuntos(
            getX(), 
            getY(), 
            "+" + valorPuntos, 
            Color.YELLOW,
            handler
        );
        handler.addObj(texto);
    }
    
    /**
     * Destruye el item
     */
    protected void destruir() {
        handler.removeObj(this);
    }
    
    /**
     * 
     */
    @Override
    public void render(Graphics g) {
        if (recolectado) return;
        
        // ========================================
        // PRIORIDAD 1: Animación activa
        // ========================================
        if (animacion != null) {
            animacion.drawAnimacion(g, 
                (int)getX(), (int)getY(), 
                (int)getWidth(), (int)getHeight()
            );
        }
        // ========================================
        // PRIORIDAD 2: Sprite estático (sin animación)
        // ========================================
        else if (sprites != null && sprites.length > 0 && sprites[0] != null) {
            g.drawImage(sprites[0], 
                (int)getX(), (int)getY(), 
                (int)getWidth(), (int)getHeight(), 
                null
            );
        }
        // ========================================
        // PRIORIDAD 3: Placeholder (nada se cargó)
        // ========================================
        else {
            renderPlaceholder(g);
        }
        
        // ========================================
        // Efecto de brillo (siempre encima)
        // ========================================
        if (brillar && ticksBrillo % 20 < 10) {
            g.setColor(new Color(255, 255, 255, 100));
            g.fillOval(
                (int)(getX() - 2), 
                (int)(getY() - 2), 
                (int)(getWidth() + 4), 
                (int)(getHeight() + 4)
            );
        }
    }
    
    /**
     * Renderizado placeholder si no hay sprites
     */
    protected abstract void renderPlaceholder(Graphics g);
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(
            (int)getX(),
            (int)getY(),
            (int)getWidth(),
            (int)getHeight()
        );
    }
    
    // ==================== GETTERS Y SETTERS ====================
    
    public int getValorPuntos() {
        return valorPuntos;
    }
    
    public boolean isRecolectado() {
        return recolectado;
    }
    
    public void setFlotar(boolean flotar) {
        this.flotar = flotar;
        if (flotar) {
            this.tieneGravedad = false;
        }
    }
    
    public void setTiempoVida(int ticks) {
        this.ticksMaxVida = ticks;
    }
    
    public void setDesapareceDespuesDeRecoger(boolean desaparece) {
        this.desapareceDespuesDeRecoger = desaparece;
    }
}