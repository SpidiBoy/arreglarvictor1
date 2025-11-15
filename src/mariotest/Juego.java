package mariotest;

import SistemaDeNiveles.GestorNiveles;
import SistemaDeSoporte.Handler;
import SistemaGFX.Texturas;
import java.awt.Canvas;

/**
 * Orquestador Principal del Juego
 * 
 * Responsabilidades (SRP):
 * 1. Coordinar la inicialización del juego
 * 2. Proveer acceso a servicios (Facade)
 * 3. Gestionar el ciclo de vida del juego
 * 
 * Patrones aplicados:
 * - FACADE: Simplifica acceso a subsistemas
 * - DEPENDENCY INJECTION: A través del Builder
 * 
 * @author LENOVO
 */
public class Juego {
    
    // Componentes core
    private final Canvas canvas;
    private ContextoJuego contexto;
    private FacadeMotorJuego juegoLoop;
    
    /**
     * Constructor - Inicializa el canvas
     */
    public Juego() {
        this.canvas = new Canvas();
        configurarCanvas();
    }
    
    /**
     * Configura el canvas según las especificaciones
     */
    private void configurarCanvas() {
        canvas.setPreferredSize(new java.awt.Dimension(
            Configuracion.VENTANA_WIDTH, 
            Configuracion.VENTANA_HEIGHT
        ));
        canvas.setFocusable(true);
    }
    
    /**
     * Inicializa y arranca todos los subsistemas del juego
     * 
     * Patrón TEMPLATE METHOD: Define el algoritmo de inicialización
     */
    public void iniciar() {
        System.out.println("[INIT] Iniciando construcción del juego...\n");
        
        // 1. Construir contexto usando Builder Pattern
        construirContexto();
        
        // 2. Crear game loop usando Facade Pattern
        crearGameLoop();
        
        // 3. Arrancar el juego
        arrancar();
        
        System.out.println("\n[INIT] Juego iniciado exitosamente");
        System.out.println(contexto.getInfo());
    }
    
    /**
     * Construye el contexto del juego usando el Builder
     */
    private void construirContexto() {
        this.contexto = new JuegoBuilder(this, canvas)
            .buildCompleto();
    }
    
    /**
     * Crea el game loop facade
     */
    private void crearGameLoop() {
        this.juegoLoop = new FacadeMotorJuego(contexto, canvas);
    }
    
    /**
     * Arranca el game loop
     */
    private void arrancar() {
        juegoLoop.iniciar();
    }
    
    // ==================== FACADE API ====================
    // Proporciona acceso controlado a los subsistemas
    
    /**
     * Obtiene el handler de objetos del juego
     * @return Handler o null si no está inicializado
     */
    public Handler getHandler() {
        return contexto != null ? contexto.getHandler() : null;
    }
    
    /**
     * Obtiene el gestor de estados (menús, pause, etc.)
     * @return GestorEstados o null si no está inicializado
     */
    public GestorEstados getGestorEstados() {
        return contexto != null ? contexto.getGestorEstados() : null;
    }
    
    /**
     * Obtiene el gestor de niveles
     * @return GestorNiveles o null si no está inicializado
     */
    public GestorNiveles getGestorNiveles() {
        return contexto != null ? contexto.getGestorNiveles() : null;
    }
    
    /**
     * Obtiene el canvas del juego
     * @return Canvas donde se renderiza el juego
     */
    public Canvas getCanvas() {
        return canvas;
    }
    
    // ==================== MÉTODOS ESTÁTICOS (compatibilidad) ====================
    // Delegan a Configuracion para mantener DRY
    
    /**
     * Obtiene el ancho de la ventana
     * @return Ancho en píxeles
     */
    public static int getVentanaWidth() {
        return Configuracion.VENTANA_WIDTH;
    }
    
    /**
     * Obtiene el alto de la ventana
     * @return Alto en píxeles
     */
    public static int getVentanaHeight() {
        return Configuracion.VENTANA_HEIGHT;
    }
    
    /**
     * Obtiene la instancia singleton de texturas
     * @return Texturas del juego
     */
    public static Texturas getTextura() {
        return Texturas.getInstance(); 
    }
    
    // ==================== CONTROL DEL JUEGO ====================
    
    /**
     * Detiene el game loop de forma segura
     */
    public void detener() {
        if (juegoLoop != null) {
            juegoLoop.detener();
        }
    }
    
    /**
     * Activa/desactiva el modo debug
     */
    public void toggleDebug() {
        if (juegoLoop != null) {
            juegoLoop.toggleDebug();
        }
    }
    
    /**
     * Verifica si el juego está corriendo
     * @return true si el game loop está activo
     */
    public boolean isRunning() {
        return juegoLoop != null && juegoLoop.isRunning();
    }
}