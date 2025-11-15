/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Gestor central de eventos del juego
 * Patrón: Mediator + Observer
 * Principio: SRP - Solo gestiona suscripciones y notificaciones
 * 
 * Thread-safe usando CopyOnWriteArrayList
 */
public class AdministradorEventos {
    private static AdministradorEventos instance;
    private final List<IJugadorEventos> playerListeners;
    
    private AdministradorEventos() {
        this.playerListeners = new CopyOnWriteArrayList<>();
    }
    
    public static AdministradorEventos getInstance() {
        if (instance == null) {
            instance = new AdministradorEventos();
        }
        return instance;
    }
    
    public void registerPlayerListener(IJugadorEventos listener) {
        if (!playerListeners.contains(listener)) {
            playerListeners.add(listener);
            System.out.println("[EVENT] Listener registrado: " + 
                             listener.getClass().getSimpleName());
        }
    }
    
    public void unregisterPlayerListener(IJugadorEventos listener) {
        playerListeners.remove(listener);
    }
    
    public void clearAllListeners() {
        playerListeners.clear();
    }
    
    public void firePlayerDamaged(EventoJugadorDañado event) {
        for (IJugadorEventos listener : playerListeners) {
            try {
                listener.onJugadordanado(event);
            } catch (Exception e) {
                System.err.println("[EVENT] Error en listener: " + e.getMessage());
            }
        }
    }
    
    public void firePlayerDeath(EventoMuerteJugador event) {
        for (IJugadorEventos listener : playerListeners) {
            try {
                listener.onJugadorMuerto(event);
            } catch (Exception e) {
                System.err.println("[EVENT] Error en listener: " + e.getMessage());
            }
        }
    }
    
    public void firePlayerRespawn(EventoReaparicionJugador event) {
        for (IJugadorEventos listener : playerListeners) {
            try {
                listener.onJuegadorReaparece(event);
            } catch (Exception e) {
                System.err.println("[EVENT] Error en listener: " + e.getMessage());
            }
        }
    }
    
    public void firePlayerCollectItem(EventoJugadorRecogeObjeto event) {
        for (IJugadorEventos listener : playerListeners) {
            try {
                listener.onJugadorRecogeObjeto(event);
            } catch (Exception e) {
                System.err.println("[EVENT] Error en listener: " + e.getMessage());
            }
        }
    }
    
    public void firePlayerPowerUp(EventoJugadorPoder event) {
        for (IJugadorEventos listener : playerListeners) {
            try {
                listener.onJugadorPoder(event);
            } catch (Exception e) {
                System.err.println("[EVENT] Error en listener: " + e.getMessage());
            }
        }
    }
    
    public int getListenerCount() {
        return playerListeners.size();
    }
}
