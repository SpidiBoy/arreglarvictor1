/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Entidades;

/** Interfaz para observadores de eventos de Jugador
 * Patrón: Observer Pattern
 * @author LENOVO
 */
public interface IJugadorEventos {
    void onJugadordanado(EventoJugadorDañado event);
    void onJugadorMuerto(EventoMuerteJugador event);
    void onJuegadorReaparece(EventoReaparicionJugador event);
    void onJugadorRecogeObjeto(EventoJugadorRecogeObjeto event);
    void onJugadorPoder(EventoJugadorPoder event);
}
