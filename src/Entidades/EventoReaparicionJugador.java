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
 * Evento: Jugador respawneó
 */
public class EventoReaparicionJugador extends EventoJuego {
    private final float spawnX;
    private final float spawnY;
    
    public EventoReaparicionJugador(Jugador source, float x, float y) {
        super(source);
        this.spawnX = x;
        this.spawnY = y;
    }
    
    public float getSpawnX() { return spawnX; }
    public float getSpawnY() { return spawnY; }
}
