/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import SistemaDeSoporte.Handler;

/**
 *
 * @author LENOVO
 */
public class ComponenteFisicasJugador {
    private final Jugador jugador;
    private final Handler handler;
    
    private static final float VELOCIDAD_CAMINAR = 2.2f;
    private static final float VELOCIDAD_ESCALERA = 1f;
    private static final float FUERZA_SALTO = -7.5f;
    private static final float GRAVEDAD = 0.5f;
    
    private boolean salto = false;
    
    public ComponenteFisicasJugador(Jugador jugador, Handler handler) {
        this.jugador = jugador;
        this.handler = handler;
    }
    
    public void tick() {
        // Aplicar movimiento
        jugador.setX(jugador.getVelX() + jugador.getX());
        jugador.setY(jugador.getVely() + jugador.getY());
        
        // Límite de seguridad
        if (jugador.getY() > 2000) {
            System.err.println("[PHYSICS] Player cayó fuera del mapa");
            return;
        }
    }
    
    public void aplicarGravedad() {
        if (!salto) {
            jugador.setVely(jugador.getVely() + GRAVEDAD);
        } else {
            // Aplicar gravedad reducida durante el salto para mejor control
            jugador.setVely(jugador.getVely() + GRAVEDAD);
        }
    }
    
    public void iniciarSalto() {
        if (!salto) {
            jugador.setVely(FUERZA_SALTO);
            salto = true;
        }
    }
    
    public void aterrizar() {
        salto = false;
    }
    
    public void moverIzquierda() {
        jugador.setVelX(-VELOCIDAD_CAMINAR);
    }
    
    public void moverDerecha() {
        jugador.setVelX(VELOCIDAD_CAMINAR);
    }
    
    public void detenerMovimiento() {
        jugador.setVelX(0);
    }
    
    public boolean hasSalto() { return salto; }
    public void setSalto(boolean salto) { this.salto = salto; }
    
    public float getVelocidadEscalera() { return VELOCIDAD_ESCALERA; }
    
}