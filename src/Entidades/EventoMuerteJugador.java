/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

/**
 *
 * @author LENOVO
 */
/**
 * Evento: Jugador murió
 */
public class EventoMuerteJugador extends EventoJuego {
    private final JuegoObjetos muerte;
    
    public EventoMuerteJugador(Jugador source, JuegoObjetos killer) {
        super(source);
        this.muerte = killer;
    }
    
    public JuegoObjetos getMuerte() { return muerte; }
}