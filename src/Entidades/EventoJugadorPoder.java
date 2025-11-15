/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

/**
 *
 * @author LENOVO
 */
public class EventoJugadorPoder extends EventoJuego {
    private final String powerUpType;
    
    public EventoJugadorPoder(Jugador source, String powerUpType) {
        super(source);
        this.powerUpType = powerUpType;
    }
    
    public String getPowerUpType() { return powerUpType; }
}
