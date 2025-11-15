package mariotest;

import UI.EstadoJuegoEnum;
import UI.EstadoJugando;
import UI.PantallaJuegoPerdido;
import UI.PantallaControles;
import UI.EstadoJuegoBase;
import UI.MenuPrincipal;
import UI.PantallaVictoria;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * Gestor de Estados del Juego - Patrón STATE
 * Maneja transiciones entre Menú, Juego, Game Over, etc.
 * 
 * @author LENOVO
 */
public class GestorEstados {
    
    private Juego juego;
    private EstadoJuegoBase estadoActual;
    
    // Estados disponibles
    private MenuPrincipal menuPrincipal;
    private EstadoJugando estadoJugando;
    private PantallaControles pantallaControles;
    private PantallaJuegoPerdido pantallaGameOver;
    private PantallaVictoria pantallaVictoria;
    
    /**
     * Constructor
     */
    public GestorEstados(Juego juego) {
        this.juego = juego;
        
        // Inicializar todos los estados
        this.menuPrincipal = new MenuPrincipal(this, juego);
        this.estadoJugando = new EstadoJugando(this, juego);
        this.pantallaControles = new PantallaControles(this, juego);
        this.pantallaGameOver = new PantallaJuegoPerdido(this, juego);
        this.pantallaVictoria = new PantallaVictoria(this, juego);
        
        // Estado inicial: Menú Principal
        cambiarEstado(EstadoJuegoEnum.MENU_PRINCIPAL);
    }
    
    /**
     * Cambia el estado actual del juego
     */
    public void cambiarEstado(EstadoJuegoEnum nuevoEstado) {
        // Salir del estado actual
        if (estadoActual != null) {
            estadoActual.salir();
        }
        
        // Cambiar al nuevo estado
        switch (nuevoEstado) {
            case MENU_PRINCIPAL:
                estadoActual = menuPrincipal;
                break;
                
            case JUGANDO:
                estadoActual = estadoJugando;
                break;
                
            case CONTROLES:
                estadoActual = pantallaControles;
                break;
                
            case GAME_OVER:
                estadoActual = pantallaGameOver;
                break;
            case VICTORIA:
                estadoActual = pantallaVictoria;
                break;
                
            default:
                System.err.println("[ERROR] Estado desconocido: " + nuevoEstado);
                estadoActual = menuPrincipal;
        }
        
        // Entrar al nuevo estado
        estadoActual.entrar();
        
        System.out.println("[GESTOR] Estado cambiado a: " + nuevoEstado);
    }
    
    /**
     * Actualiza el estado actual
     */
    public void tick() {
        if (estadoActual != null) {
            estadoActual.tick();
        }
    }
    
    /**
     * Renderiza el estado actual
     */
    public void render(Graphics g) {
        if (estadoActual != null) {
            estadoActual.render(g);
        }
    }
    
    /**
     * Maneja eventos de teclado
     */
    public void keyPressed(KeyEvent e) {
        if (estadoActual != null) {
            estadoActual.keyPressed(e);
        }
    }
    
    public void keyReleased(KeyEvent e) {
        if (estadoActual != null) {
            estadoActual.keyReleased(e);
        }
    }
    
    // ==================== GETTERS ====================
    
    public EstadoJuegoBase getEstadoActual() {
        return estadoActual;
    }
    
    public Juego getJuego() {
        return juego;
    }
    
    /**
     * Verifica si el juego está en estado jugable
     */
    public boolean estaJugando() {
        return estadoActual == estadoJugando;
    }
}