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
 * Adapter para implementar solo los eventos necesarios
 * Patrón: Adapter Pattern
 */
public abstract class AdaptadorEventosJugador implements IJugadorEventos {
    @Override
    public void onJugadordanado(EventoJugadorDañado event) {}
    
    @Override
    public void onJugadorMuerto(EventoMuerteJugador event) {}
    
    @Override
    public void onJuegadorReaparece(EventoReaparicionJugador event) {}
    
    @Override
    public void onJugadorRecogeObjeto(EventoJugadorRecogeObjeto event) {}
    
    @Override
    public void onJugadorPoder(EventoJugadorPoder event) {}
}
