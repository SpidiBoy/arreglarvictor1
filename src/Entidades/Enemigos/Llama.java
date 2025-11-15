package Entidades.Enemigos;

import SistemaDeSoporte.Handler;
import mariotest.Juego;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Clase Llama - Enemigo tipo llama (16x24)
 * 
 * HEREDA DE: EnemigoFuego (Template Method)
 * 
 * CARACTERÍSTICAS ESPECÍFICAS:
 * - Sprites de llama (forma alargada)
 * - SÍ puede saltar (comportamiento SALTARIN)
 * - NO genera partículas
 * - Tamaño: 16x24 píxeles (más alta que Fuego)
 * 
 * @author LENOVO
 * @version 2.0 - Refactorizada heredando de EnemigoFuego
 */
public class Llama extends EnemigoFuego {
    
    // ==================== CONSTANTES ESPECÍFICAS ====================
    
    private static final float WIDTH = 16F;
    private static final float HEIGHT = 24F;
    
    // ==================== CONSTRUCTORES ====================
    
    /**
     * Constructor completo
     */
    public Llama(float x, float y, int scale, Handler handler, 
                 ComportamientoFuego comportamiento, int direccion) {
        super(x, y, WIDTH, HEIGHT, scale, handler, comportamiento, direccion);
        
        cargarSprites();
        inicializarAnimacion();
        
        System.out.println("[LLAMA] Creada en (" + x + ", " + y + ") " +
                          "comportamiento: " + comportamiento + 
                          " dirección: " + (direccion > 0 ? "DERECHA" : "IZQUIERDA"));
    }
    
    /**
     * Constructor simplificado: PATRULLA con dirección derecha
     */
    public Llama(float x, float y, int scale, Handler handler) {
        this(x, y, scale, handler, ComportamientoFuego.PATRULLA, 1);
    }
    
    /**
     * Factory method para llama ESTATICA
     */
    public static Llama crearEstatica(float x, float y, int scale, Handler handler) {
        return new Llama(x, y, scale, handler, ComportamientoFuego.ESTATICO, 1);
    }
    
    /**
     * Factory method para llama SALTARIN
     */
    public static Llama crearSaltarin(float x, float y, int scale, Handler handler, int direccion) {
        return new Llama(x, y, scale, handler, ComportamientoFuego.SALTARIN, direccion);
    }
    
    /**
     * Factory method para llama PERSEGUIDOR
     */
    public static Llama crearPerseguidor(float x, float y, int scale, Handler handler) {
        return new Llama(x, y, scale, handler, ComportamientoFuego.PERSEGUIDOR, 1);
    }
    
    // ==================== IMPLEMENTACIÓN DE MÉTODOS ABSTRACTOS ====================
    
    @Override
    protected void cargarSprites() {
        try {
            sprites = Juego.getTextura().getLlamaSprites();
            
            if (sprites == null || sprites.length == 0) {
                System.err.println("[ERROR] No se pudieron cargar sprites de llama");
            } else {
                System.out.println("[LLAMA] Sprites cargados: " + sprites.length);
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Excepción al cargar sprites de llama: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void inicializarAnimacion() {
        if (sprites == null || sprites.length < 2) {
            System.err.println("[ERROR] No hay suficientes sprites para animación de llama");
            return;
        }
        
        // Animación de llama (todos los frames disponibles)
        if (sprites.length >= 3) {
            animacion = new SistemaGFX.Animacion(3, 
                sprites[0], 
                sprites[1], 
                sprites[2]
            );
        } else {
            animacion = new SistemaGFX.Animacion(4, 
                sprites[0], 
                sprites[1]
            );
        }
        
        System.out.println("[LLAMA] Animación inicializada");
    }
    
    @Override
    protected void renderPlaceholder(Graphics g) {
    }
    
    @Override
    protected void generarEfectosVisuales() {
        // Las llamas NO generan partículas (a diferencia del fuego)
        // Este método se deja vacío intencionalmente
    }
    
    @Override
    protected String getNombreTipo() {
        return "LLAMA";
    }
    
    @Override
    protected boolean puedeSaltar() {
        // Las llamas SÍ pueden saltar (característica diferencial)
        return true;
    }
    
    // ==================== HITBOX ESPECÍFICA ====================
    
    /**
     * Hitbox personalizada para la llama (más alta)
     */
    @Override
    public java.awt.Rectangle getBounds() {
        if (getComportamiento() == ComportamientoFuego.ESTATICO) {
            // Hitbox completa (trampa fija)
            return new java.awt.Rectangle(
                (int)(getX() + 4),
                (int)(getY() + 4),
                (int)(getWidth() - 8),
                (int)(getHeight() - 8)
            );
        } else {
            // Hitbox solo en la parte inferior (móvil)
            return new java.awt.Rectangle(
                (int)(getX() + 4),
                (int)(getY() + getHeight()/2),
                (int)(getWidth() - 8),
                (int)(getHeight()/2)
            );
        }
    }
}