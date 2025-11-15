package mariotest;

import SistemaDeNiveles.GestorNiveles;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.Teclas;
import SistemaGFX.Texturas;
import Entidades.Jugador;

/**
 * Contexto del Juego - Contenedor inmutable de componentes
 * Responsabilidad: Proveer acceso centralizado a todos los componentes
 * 
 * Principio: DEPENDENCY INJECTION
 * 
 * @author LENOVO
 */
public class ContextoJuego {
    
    // Componentes del juego (inmutables)
    private final Texturas texturas;
    private final Handler handler;
    private final GestorEstados gestorEstados;
    private final GestorNiveles gestorNiveles;
    private final Teclas teclas;
    private final Jugador jugador;
    
    /**
     * Constructor (solo accesible por JuegoBuilder)
     */
    ContextoJuego(
        Texturas texturas,
        Handler handler,
        GestorEstados gestorEstados,
        GestorNiveles gestorNiveles,
        Teclas teclas,
        Jugador jugador
    ) {
        this.texturas = texturas;
        this.handler = handler;
        this.gestorEstados = gestorEstados;
        this.gestorNiveles = gestorNiveles;
        this.teclas = teclas;
        this.jugador = jugador;
    }
    
    // ==================== GETTERS (solo lectura) ====================
    
    public Texturas getTexturas() {
        return texturas;
    }
    
    public Handler getHandler() {
        return handler;
    }
    
    public GestorEstados getGestorEstados() {
        return gestorEstados;
    }
    
    public GestorNiveles getGestorNiveles() {
        return gestorNiveles;
    }
    
    public Teclas getTeclas() {
        return teclas;
    }
    
    public Jugador getJugador() {
        return jugador;
    }
    
    /**
     * Información de debug
     */
    public String getInfo() {
        return String.format(
            "ContextoJuego [Texturas: %s, Handler: %s objetos, Jugador: (%.0f, %.0f)]",
            texturas != null ? "✓" : "✗",
            handler != null ? handler.getGameObjs().size() : 0,
            jugador != null ? jugador.getX() : 0,
            jugador != null ? jugador.getY() : 0
        );
    }
    
    /**
     * Valida que todos los componentes estén inicializados
     */
    public boolean esValido() {
        return texturas != null 
            && handler != null 
            && gestorEstados != null 
            && gestorNiveles != null 
            && teclas != null 
            && jugador != null;
    }
}