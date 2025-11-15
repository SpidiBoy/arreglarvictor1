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
 * Evento: Jugador recibió daño
 */
public class EventoJugadorDañado extends EventoJuego {
    private final JuegoObjetos atacante; // Puede ser null (caída)
    private final int cantidadDano;
    
    public EventoJugadorDañado(Jugador source, JuegoObjetos atacante) {
        super(source);
        this.atacante = atacante;
        this.cantidadDano = 1; // Por ahora siempre es 1
    }
    
    public JuegoObjetos getAtacante() { return atacante; }
    public int getCantidadDano() { return cantidadDano; }
    public boolean isDanoPorCaida() { return atacante == null; }
}
