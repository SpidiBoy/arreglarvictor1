package Entidades.Enemigos;

import Entidades.Escenario.Escalera;
import Entidades.JuegoObjetos;
import Entidades.Jugador;
import SistemaGFX.Animacion;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import mariotest.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Clase Barril - Enemigo principal de Donkey Kong
 * Rueda por las plataformas y cae por las escaleras
 * 
 * @author LENOVO
 */
public class Barril extends JuegoObjetos {
    
    // Dimensiones del barril
    private static final float WIDTH = 16F;
    private static final float HEIGHT = 16F;
    
    // Componentes
    private Handler handler;
    private BufferedImage[] barrilSprites;
    private Animacion barrilRodando;
    private Animacion barrilCayendo;
    
    // Estados del barril
    private boolean rodando = true;
    private boolean cayendo = false;
    private boolean enSuelo = false;
    
    // Física
    private static final float VELOCIDAD_RODAR = 3.5f;
    private static final float VELOCIDAD_CAIDA_MAX = 12f;
    private static final float GRAVEDAD = 0.5f;
    private static final float REBOTE = -8f;
    
    // Dirección (1 = derecha, -1 = izquierda)
    private int direccion = 1;
    
    // Control de escaleras
    private boolean puedeTomarEscalera = true;
    private int ticksSinEscalera = 0;
    private static final int COOLDOWN_ESCALERA = 60;
    
    // Probabilidad de tomar escalera (0-100)
    private static final int PROBABILIDAD_ESCALERA = 30;
    
    /**
     * Constructor del barril
     */
    public Barril(float x, float y, int scale, Handler handler, int direccion) {
        super(x, y, ObjetosID.Barril, WIDTH, HEIGHT, scale);
        this.handler = handler;
        this.direccion = direccion;
        
        // Establecer velocidad inicial
        setVelX(VELOCIDAD_RODAR * direccion);
        
        // Cargar sprites y animaciones
        cargarSprites();
        inicializarAnimaciones();
        
        System.out.println("[BARRIL] Creado en (" + x + ", " + y + ") direccion: " + direccion);
    }
    
    /**
     * Constructor simplificado (dirección derecha por defecto)
     */
    public Barril(float x, float y, int scale, Handler handler) {
        this(x, y, scale, handler, 1);
    }
    
    /**
     * Carga los sprites del barril desde Texturas
     */
    private void cargarSprites() {
        try {
            // Obtener sprites desde la clase Texturas
            barrilSprites = Juego.getTextura().getBarrilSprites();
            
            if (barrilSprites == null || barrilSprites.length == 0) {
                System.err.println("[ERROR] No se pudieron cargar sprites de barril desde Texturas");
            } else {
                System.out.println("[BARRIL] Sprites cargados correctamente: " + barrilSprites.length);
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Excepción al cargar sprites de barril: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * Inicializa las animaciones del barril
     */
    private void inicializarAnimaciones() {
        if (barrilSprites == null || barrilSprites.length < 4) {
            System.err.println("[ERROR] No hay suficientes sprites para animaciones");
            return;
        }
        
        // Animación de rodar (primeros 4 frames)
        barrilRodando = new Animacion(3, 
            barrilSprites[0], 
            barrilSprites[1], 
            barrilSprites[2], 
            barrilSprites[3]
        );
        
        // Animación de caer (frames 4-7 o reutilizar los primeros)
        if (barrilSprites.length >= 8) {
            barrilCayendo = new Animacion(4, 
                barrilSprites[3], 
                barrilSprites[4],
                barrilSprites[5],
                barrilSprites[6]
            );
        } else {
            // Usar los mismos sprites si no hay suficientes
            barrilCayendo = barrilRodando;
        }
        
        System.out.println("[BARRIL] Animaciones inicializadas");
    }

    @Override
    public void tick() {
        // Aplicar gravedad
        aplicarGravedad();
        
        // Aplicar movimiento
        setX(getX() + getVelX());
        setY(getY() + getVely());
        
        // Limitar velocidad de caída
        if (getVely() > VELOCIDAD_CAIDA_MAX) {
            setVely(VELOCIDAD_CAIDA_MAX);
        }
        
        // Actualizar cooldown de escalera
        if (!puedeTomarEscalera) {
            ticksSinEscalera++;
            if (ticksSinEscalera >= COOLDOWN_ESCALERA) {
                puedeTomarEscalera = true;
                ticksSinEscalera = 0;
            }
        }
        
        // Colisiones y comportamiento
        manejarColisiones();
        detectarEscaleras();
        
        // Actualizar animación
        if (barrilRodando != null && barrilCayendo != null) {
            if (cayendo) {
                barrilCayendo.runAnimacion();
            } else {
                barrilRodando.runAnimacion();
            }
        }
        
        // Eliminar si cae fuera del mapa
        if (getY() > 1000) {
            destruir();
        }
    }
    
    /**
     * Maneja las colisiones con bloques y plataformas
     */
    private void manejarColisiones() {
        enSuelo = false;
        
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.Tile || obj.getId() == ObjetosID.Pipe) {
                
                // Colisión inferior (aterrizar en plataforma)
                if (getBounds().intersects(obj.getBounds())) {
                    setY(obj.getY() - getHeight());
                    
                    if (cayendo) {
                        cayendo = false;
                        rodando = true;
                        
                        if (Math.abs(getVely()) > 5) {
                            setVely(REBOTE / 2);
                        } else {
                            setVely(0);
                            enSuelo = true;
                        }
                    } else {
                        setVely(0);
                        enSuelo = true;
                    }
                }
                
                // Colisión superior
                if (getBoundsTop().intersects(obj.getBounds())) {
                    setY(obj.getY() + obj.getHeight());
                    setVely(0);
                }
                
                // Colisión derecha
                if (getBoundsRight().intersects(obj.getBounds())) {
                    setX(obj.getX() - getWidth());
                    direccion = -1;
                    setVelX(VELOCIDAD_RODAR * direccion);
                }
                
                // Colisión izquierda
                if (getBoundsLeft().intersects(obj.getBounds())) {
                    setX(obj.getX() + obj.getWidth());
                    direccion = 1;
                    setVelX(VELOCIDAD_RODAR * direccion);
                }
            }
        }
        
        if (!enSuelo && !cayendo && Math.abs(getVely()) > 1) {
            cayendo = true;
            rodando = false;
        }
    }
    
    /**
     * Detecta escaleras cercanas y decide si bajar
     */
    private void detectarEscaleras() {
        if (!puedeTomarEscalera || !enSuelo) {
            return;
        }
        
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.Escalera) {
                Escalera escalera = (Escalera) obj;
                
                Rectangle areaBusqueda = new Rectangle(
                    (int) getX() - 10,
                    (int) (getY() + getHeight()),
                    (int) getWidth() + 20,
                    10
                );
                
                if (areaBusqueda.intersects(escalera.getBounds())) {
                    if (Math.random() * 100 < PROBABILIDAD_ESCALERA) {
                        tomarEscalera(escalera);
                    }
                    break;
                }
            }
        }
    }
    
    /**
     * Hace que el barril baje por una escalera
     */
    private void tomarEscalera(Escalera escalera) {
        cayendo = true;
        rodando = false;
        puedeTomarEscalera = false;
        ticksSinEscalera = 0;
        
        float centroEscalera = escalera.getX() + escalera.getWidth() / 2;
        setX(centroEscalera - getWidth() / 2);
        
        setVelX(0);
        setVely(4f);
    }
    
    @Override
    public void aplicarGravedad() {
        setVely(getVely() + GRAVEDAD);
    }

    @Override
    public void render(Graphics g) {
        if (barrilSprites != null && barrilSprites[0] != null && barrilRodando != null) {
            if (cayendo && barrilCayendo != null) {
                // Animación de caer
                barrilCayendo.drawAnimacion(g, 
                    (int) getX(), (int) getY(), 
                    (int) getWidth(), (int) getHeight()
                );
            } else {
                // Animación de rodar
                if (direccion > 0) {
                    barrilRodando.drawAnimacion(g, 
                        (int) getX(), (int) getY(), 
                        (int) getWidth(), (int) getHeight()
                    );
                } else {
                    barrilRodando.drawAnimacion(g, 
                        (int) (getX() + getWidth()), (int) getY(), 
                        (int) -getWidth(), (int) getHeight()
                    );
                }
            }
        } else {
            // Placeholder visual
            g.setColor(new Color(139, 69, 19));
            g.fillOval((int) getX(), (int) getY(), 
                      (int) getWidth(), (int) getHeight());
            g.setColor(Color.BLACK);
            g.drawOval((int) getX(), (int) getY(), 
                      (int) getWidth(), (int) getHeight());
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(
            (int) getX() + 2,
            (int) getY() + 2,
            (int) getWidth() - 4,
            (int) getHeight() - 4
        );
    }
    
    public Rectangle getBoundsTop() {
        return new Rectangle(
            (int) getX() + 4,
            (int) getY(),
            (int) getWidth() - 8,
            (int) getHeight() / 2
        );
    }
    
    public Rectangle getBoundsRight() {
        return new Rectangle(
            (int) (getX() + getWidth() - 5),
            (int) getY() + 5,
            5,
            (int) getHeight() - 10
        );
    }
    
    public Rectangle getBoundsLeft() {
        return new Rectangle(
            (int) getX(),
            (int) getY() + 5,
            5,
            (int) getHeight() - 10
        );
    }
    
    public void destruir() {
        handler.removeObj(this);
        System.out.println("[BARRIL] Destruido en (" + (int)getX() + ", " + (int)getY() + ")");
    }
    
    public boolean colisionaConJugador(Jugador player) {
        return getBounds().intersects(player.getBounds());
    }
    
    public boolean isRodando() {
        return rodando;
    }
    
    public boolean isCayendo() {
        return cayendo;
    }
    
    public boolean isEnSuelo() {
        return enSuelo;
    }
    
    public int getDireccion() {
        return direccion;
    }
    
    public void setDireccion(int direccion) {
        this.direccion = direccion;
        if (!cayendo) {
            setVelX(VELOCIDAD_RODAR * direccion);
        }
    }
}