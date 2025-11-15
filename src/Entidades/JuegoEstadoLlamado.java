/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import SistemaDeSoporte.EstadoJuego;

/**
 *
 * @author LENOVO
 */
public class JuegoEstadoLlamado extends AdaptadorEventosJugador {
    private final EstadoJuego estadoJuego;
    
    public JuegoEstadoLlamado(EstadoJuego estadoJuego) {
        this.estadoJuego = estadoJuego;
    }
    
    @Override
    public void onJugadordanado(EventoJugadorDañado event) {
        System.out.println("[GAME STATE] Player recibió daño");
        estadoJuego.perderVida();
        estadoJuego.resetearRacha();
    }
    
    @Override
    public void onJugadorRecogeObjeto(EventoJugadorRecogeObjeto event) {
        System.out.println("[GAME STATE] Player colectó: " + event.getTipoObjeto());
        estadoJuego.sumarPuntos(event.getPuntosGanados());
    }
}
