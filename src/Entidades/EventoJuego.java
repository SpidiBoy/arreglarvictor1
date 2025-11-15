/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

/**
 *
 * @author LENOVO
 */
public abstract class EventoJuego {
    private final long marcaTiempo;
    private final Jugador source;
    
    protected EventoJuego(Jugador source) {
        this.source = source;
        this.marcaTiempo = System.currentTimeMillis();
    }
    
    public Jugador getSource() { return source; }
    public long getMarcaTiempo() { return marcaTiempo; }
}
