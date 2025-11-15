package mariotest;

import Entidades.*;
import Entidades.JuegoEstadoLlamado;
import SistemaDeNiveles.GestorNiveles;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.Teclas;
import SistemaGFX.Texturas;
import SistemaGFX.Ventana;
import Entidades.Jugador;
import SistemaDeSoporte.EstadoJuego;
import java.awt.Canvas;

/**
 * Patrón BUILDER - Construye el juego paso a paso
 * 
 * Responsabilidad: Orquestar la creación de todos los componentes del juego
 * 
 * Principios aplicados:
 * - SRP: Solo se encarga de construir
 * - OCP: Extensible para nuevos pasos de construcción
 * - DIP: Depende de abstracciones (interfaces) donde sea posible
 * 
 * @author LENOVO
 */
public class JuegoBuilder {
    
    // ==================== COMPONENTES A CONSTRUIR ====================
    private AdministradorEventos eventManager;
    private Texturas texturas;
    private Handler handler;
    private GestorEstados gestorEstados;
    private GestorNiveles gestorNiveles;
    private Teclas teclas;
    private Jugador jugador;
    private Ventana ventana;
    
    // ==================== DEPENDENCIAS EXTERNAS ====================
    
    private final Juego instanciaJuego;  // Facade para callbacks
    private final Canvas canvas;          // Componente gráfico
    
    /**
     * Constructor del Builder
     * 
     * @param instanciaJuego Referencia al orquestador principal
     * @param canvas Canvas donde se renderizará el juego
     */
    public JuegoBuilder(Juego instanciaJuego, Canvas canvas) {
        this.instanciaJuego = instanciaJuego;
        this.canvas = canvas;
        
        validarDependencias();
    }
    
    /**
     * Valida que las dependencias externas sean válidas
     */
    private void validarDependencias() {
        if (instanciaJuego == null) {
            throw new IllegalArgumentException("La instancia del juego no puede ser null");
        }
        if (canvas == null) {
            throw new IllegalArgumentException("El canvas no puede ser null");
        }
    }
    
    // ==================== PASOS DE CONSTRUCCIÓN ====================
    
    /**
     * Paso 1: Cargar texturas
     * 
     * Patrón SINGLETON: Texturas usa getInstance()
     */
    public JuegoBuilder cargarTexturas() {
        System.out.println("[BUILDER] Paso 1/6: Cargando texturas...");
        
        this.texturas = Texturas.getInstance();
        
        System.out.println("[BUILDER] ✓ Texturas cargadas");
        return this;
    }
    
    /**
     * Paso 2: Inicializar handler
     * 
     * Handler gestiona todos los objetos del juego (enemigos, items, etc.)
     */
    public JuegoBuilder inicializarHandler() {
        System.out.println("[BUILDER] Paso 2/6: Inicializando handler...");
        
        this.handler = new Handler();
        
        System.out.println("[BUILDER] ✓ Handler inicializado");
        return this;
    }
    
      public JuegoBuilder inicializarEventos() {
        System.out.println("[BUILDER] Inicializando sistema de eventos...");
        
        this.eventManager = AdministradorEventos.getInstance();
        
        // Registrar listeners
        EstadoJuego estadoJuego = EstadoJuego.getInstance();
        eventManager.registerPlayerListener(new JuegoEstadoLlamado(estadoJuego));
        eventManager.registerPlayerListener(new PoderLlamado());
        
        System.out.println("[BUILDER] ✅ Sistema de eventos inicializado con " + 
                          eventManager.getListenerCount() + " listeners");
        
        return this;
    }
    
    /**
     * Paso 3: Crear jugador
     * 
     * El jugador es el objeto central del juego
     */
    public JuegoBuilder crearJugador() {
        System.out.println("[BUILDER] Creando jugador...");
        
        if (handler == null) {
            throw new IllegalStateException("Handler debe inicializarse antes");
        }
        if (eventManager == null) {
            throw new IllegalStateException("EventManager debe inicializarse antes");
        }
        
        // ✅ Inyectar AdministradorEventos al Jugador
        this.jugador = new Jugador(100, 100, 2, handler, eventManager);
        handler.setPlayer(jugador);
        
        System.out.println("[BUILDER] ✅ Jugador creado con eventos");
        return this;
    }
    
    /**
     * Paso 4: Configurar controles
     * 
     * Inicializa el sistema de input (teclado) y gestor de estados
     */
    public JuegoBuilder configurarControles() {
        System.out.println("[BUILDER] Paso 4/6: Configurando controles...");
        
        validarPreRequisito(handler, "Handler debe inicializarse antes de los controles");
        
        // Gestor de estados (menús, pause, game over)
        this.gestorEstados = new GestorEstados(instanciaJuego);
        
        // Sistema de teclas
        this.teclas = new Teclas(handler, gestorEstados);
        canvas.addKeyListener(teclas);
        
        System.out.println("[BUILDER] Controles configurados");
        return this;
    }
    
    /**
     * Paso 5: Inicializar gestor de niveles
     * 
     * Gestiona la progresión entre niveles
     */
    public JuegoBuilder inicializarGestorNiveles() {
        System.out.println("[BUILDER] Paso 5/6: Inicializando gestor de niveles...");
        
        validarPreRequisito(handler, "Handler debe inicializarse antes del gestor de niveles");
        
        this.gestorNiveles = new GestorNiveles(instanciaJuego, handler);
        
        System.out.println("[BUILDER] Gestor de niveles inicializado");
        return this;
    }
    
    /**
     * Paso 6: Crear ventana
     * 
     * Crea el JFrame principal y añade el canvas
     */
    public JuegoBuilder crearVentana() {
        System.out.println("[BUILDER] Paso 6/6: Creando ventana...");
        
        this.ventana = new Ventana(
            Configuracion.VENTANA_WIDTH, 
            Configuracion.VENTANA_HEIGHT, 
            Configuracion.NOMBRE_JUEGO, 
            canvas
        );
        
        System.out.println(String.format(
            "[BUILDER] Ventana creada (%dx%d)",
            Configuracion.VENTANA_WIDTH,
            Configuracion.VENTANA_HEIGHT
        ));
        
        return this;
    }
    
    // ==================== MÉTODOS DE VALIDACIÓN ====================
    
    /**
     * Valida que un componente esté inicializado
     * 
     * @param componente Componente a validar
     * @param mensaje Mensaje de error si falla
     */
    private void validarPreRequisito(Object componente, String mensaje) {
        if (componente == null) {
            throw new IllegalStateException(mensaje);
        }
    }
    
    /**
     * Valida que todos los componentes críticos estén inicializados
     */
    private void validarComponentes() {
        if (texturas == null || handler == null || gestorEstados == null || 
            gestorNiveles == null || jugador == null || ventana == null || teclas == null) {
            
            throw new IllegalStateException(
                "No se completaron todos los pasos del builder. " +
                "Use buildCompleto() o ejecute todos los pasos manualmente."
            );
        }
    }
    
    // ==================== CONSTRUCCIÓN FINAL ====================
    
    /**
     * Construye el contexto final con todos los componentes
     * 
     * @return ContextoJuego inmutable con todos los componentes
     */
    public ContextoJuego build() {
        System.out.println("\n[BUILDER] Validando construcción...");
        
        validarComponentes();
        
        System.out.println("[BUILDER] Construcción completada exitosamente");
        
        return new ContextoJuego(
            texturas, 
            handler, 
            gestorEstados, 
            gestorNiveles, 
            teclas, 
            jugador
        );
    }
    
    /**
     * Método fluido: construye todo de una vez
     * 
     * Patrón FLUENT INTERFACE: Permite encadenar llamadas
     * 
     * @return ContextoJuego completamente inicializado
     */
    public ContextoJuego buildCompleto() {
        return this
            .cargarTexturas()
            .inicializarHandler()
            .inicializarEventos() 
            .crearJugador()
            .configurarControles()
            .inicializarGestorNiveles()
            .crearVentana()
            .build();
    }
}