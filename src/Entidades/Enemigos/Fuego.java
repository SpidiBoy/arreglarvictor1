package Entidades.Enemigos;

import Entidades.EfectosVisuales.ParticulaFuego;
import SistemaDeSoporte.Handler;
import mariotest.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Clase Fuego - Enemigo tipo fuego pequeño (16x16)
 * 
 * HEREDA DE: EnemigoFuego (Template Method)
 * 
 * CARACTERÍSTICAS ESPECÍFICAS:
 * - Sprites de fuego (colores naranjas/rojos)
 * - Genera partículas de fuego
 * - NO puede saltar
 * - Tamaño: 16x16 píxeles
 * 
 * @author LENOVO
 * @version 2.0 - Refactorizada heredando de EnemigoFuego
 */
public class Fuego extends EnemigoFuego {
    
    // ==================== CONSTANTES ESPECÍFICAS ====================
    
    private static final float WIDTH = 16F;
    private static final float HEIGHT = 16F;
    
    // Control de partículas
    private Random random;
    private int ticksParticula = 0;
    private static final int FRECUENCIA_PARTICULA = 3; // Cada 3 ticks
    
    // ==================== CONSTRUCTORES ====================
    
    /**
     * Constructor completo
     */
    public Fuego(float x, float y, int scale, Handler handler, 
                 ComportamientoFuego comportamiento, int direccion) {
        super(x, y, WIDTH, HEIGHT, scale, handler, comportamiento, direccion);
        
        this.random = new Random();
        
        cargarSprites();
        inicializarAnimacion();
        
        System.out.println("[FUEGO] Creado en (" + x + ", " + y + ") " +
                          "comportamiento: " + comportamiento + 
                          " dirección: " + (direccion > 0 ? "DERECHA" : "IZQUIERDA"));
    }
    
    /**
     * Constructor simplificado: PATRULLA con dirección derecha
     */
    public Fuego(float x, float y, int scale, Handler handler) {
        this(x, y, scale, handler, ComportamientoFuego.PATRULLA, 1);
    }
    
    /**
     * Factory method para fuego ESTATICO
     */
    public static Fuego crearEstatico(float x, float y, int scale, Handler handler) {
        return new Fuego(x, y, scale, handler, ComportamientoFuego.ESTATICO, 1);
    }
    
    /**
     * Factory method para fuego RAPIDO
     */
    public static Fuego crearRapido(float x, float y, int scale, Handler handler, int direccion) {
        return new Fuego(x, y, scale, handler, ComportamientoFuego.RAPIDO, direccion);
    }
    
    /**
     * Factory method para fuego PERSEGUIDOR
     */
    public static Fuego crearPerseguidor(float x, float y, int scale, Handler handler) {
        return new Fuego(x, y, scale, handler, ComportamientoFuego.PERSEGUIDOR, 1);
    }
    
    // ==================== IMPLEMENTACIÓN DE MÉTODOS ABSTRACTOS ====================
    
    @Override
    protected void cargarSprites() {
        try {
            sprites = Juego.getTextura().getFuegoSprites();
            
            if (sprites == null || sprites.length == 0) {
                System.err.println("[ERROR] No se pudieron cargar sprites de fuego");
            } else {
                System.out.println("[FUEGO] Sprites cargados: " + sprites.length);
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Excepción al cargar sprites de fuego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void inicializarAnimacion() {
        if (sprites == null || sprites.length < 2) {
            System.err.println("[ERROR] No hay suficientes sprites para animación de fuego");
            return;
        }
        
        // Animación de fuego (usar todos los frames disponibles)
        if (sprites.length >= 4) {
            animacion = new SistemaGFX.Animacion(4, 
                sprites[0], 
                sprites[1]
            );
        } else {
            animacion = new SistemaGFX.Animacion(5, 
                sprites[0], 
                sprites[1],
                sprites[2], 
                sprites[3]
            );
        }
        
        System.out.println("[FUEGO] Animación inicializada");
    }
    
    @Override
    protected void renderPlaceholder(Graphics g) {
    }
    
    @Override
    protected void generarEfectosVisuales() {
        // Generar partículas de fuego
        ticksParticula++;
        
        if (ticksParticula >= FRECUENCIA_PARTICULA) {
            ticksParticula = 0;
            
            // Crear partícula de fuego
            float particulaX = getX() + getWidth() / 2 + (random.nextFloat() - 0.5f) * 8;
            float particulaY = getY() + getHeight() / 2;
            
            ParticulaFuego particula = new ParticulaFuego(
                particulaX, 
                particulaY, 
                1, 
                handler
            );
            
            handler.addObj(particula);
        }
    }
    
    @Override
    protected String getNombreTipo() {
        return "FUEGO";
    }
    
    @Override
    protected boolean puedeSaltar() {
        // Los fuegos NO pueden saltar
        return false;
    }
}