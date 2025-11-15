/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import Entidades.Escenario.Escalera;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import java.awt.Rectangle;

/**Componente responsable del sistema de escaleras
 *
 * @author LENOVO
 */
public class ComponenteEscaleraJugador {
   private final Jugador jugador;
    private final Handler handler;
    
    private boolean enEscalera = false;
    private boolean puedeMoverseEnEscalera = false;
    private boolean subiendoEscalera = false;
    private boolean bajandoEscalera = false;
    private Escalera escaleraActual = null;
    private int ticksEnEscalera = 0;
    
    private static final int TICKS_MIN_ESCALERA = 5;
    
    public ComponenteEscaleraJugador(Jugador jugador, Handler handler) {
        this.jugador = jugador;
        this.handler = handler;
    }
    
    public void tick() {
        verificarEscalera();
        
        if (enEscalera) {
            ticksEnEscalera++;
            
            if (subiendoEscalera && ticksEnEscalera > TICKS_MIN_ESCALERA) {
                verificarSalidaSuperiorEscalera();
            }
            
            if (bajandoEscalera && ticksEnEscalera > TICKS_MIN_ESCALERA) {
                verificarSalidaInferiorEscalera();
            }
        } else {
            ticksEnEscalera = 0;
        }
    }
    
    private void verificarEscalera() {
        escaleraActual = null;
        puedeMoverseEnEscalera = false;
        
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.Escalera || obj.getId() == ObjetosID.EscaleraRota) {
                Escalera escalera = (Escalera) obj;
                
                Rectangle areaEscalera = escalera.getAreaInteraccion();
                Rectangle jugadorBounds = jugador.getBounds();
                
                if (areaEscalera.intersects(jugadorBounds)) {
                    if (escalera.esUsable()) {
                        escaleraActual = escalera;
                        puedeMoverseEnEscalera = true;
                        break;
                    }
                }
                
                Rectangle areaDebajo = new Rectangle(
                    (int)(jugador.getX() + jugador.getWidth() / 4),
                    (int)(jugador.getY() + jugador.getHeight() - 5),
                    (int)(jugador.getWidth() / 2),
                    25
                );
                
                if (areaDebajo.intersects(escalera.getBounds()) && escalera.esUsable()) {
                    escaleraActual = escalera;
                    puedeMoverseEnEscalera = true;
                    break;
                }
            }
        }
        
        if (!puedeMoverseEnEscalera && enEscalera && !bajandoEscalera) {
            salirEscalera();
        }
    }
    
    private void verificarSalidaSuperiorEscalera() {
        if (!enEscalera || !subiendoEscalera) return;
        
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.Tile) {
                Rectangle bloqueArea = obj.getBounds();
                
                Rectangle areaDeteccion = new Rectangle(
                    (int)(jugador.getX() + jugador.getWidth() / 4),
                    (int)(jugador.getY() - 10),
                    (int)(jugador.getWidth() / 2),
                    15
                );
                
                if (areaDeteccion.intersects(bloqueArea)) {
                    float nuevaY = bloqueArea.y - jugador.getHeight();
                    
                    if (Math.abs(jugador.getY() - nuevaY) < 20) {
                        jugador.setY(nuevaY);
                        salirEscalera();
                        jugador.setVely(0);
                        System.out.println("[LADDER] Salió de escalera arriba");
                        return;
                    }
                }
            }
        }
    }
    
    private void verificarSalidaInferiorEscalera() {
        if (!enEscalera || !bajandoEscalera) return;
        
        boolean hayBloqueDebajo = false;
        boolean hayEscaleraDebajo = false;
        
        Rectangle areaDebajo = new Rectangle(
            (int)(jugador.getX() + jugador.getWidth() / 4),
            (int)(jugador.getY() + jugador.getHeight()),
            (int)(jugador.getWidth() / 2),
            10
        );
        
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.Tile || obj.getId() == ObjetosID.Pipe) {
                if (areaDebajo.intersects(obj.getBounds())) {
                    hayBloqueDebajo = true;
                    
                    if (Math.abs(jugador.getY() + jugador.getHeight() - obj.getY()) < 15) {
                        jugador.setY(obj.getY() - jugador.getHeight());
                    }
                }
            }
            
            if (obj.getId() == ObjetosID.Escalera) {
                if (areaDebajo.intersects(obj.getBounds())) {
                    hayEscaleraDebajo = true;
                }
            }
        }
        
        if (hayBloqueDebajo && !hayEscaleraDebajo) {
            salirEscalera();
            jugador.setVely(0);
            System.out.println("[LADDER] Salió de escalera abajo");
        }
    }
    
    public void subirEscalera(float velocidad) {
        if (puedeMoverseEnEscalera && escaleraActual != null) {
            if (!enEscalera) {
                float centroEscalera = escaleraActual.getX() + escaleraActual.getWidth() / 2;
                jugador.setX(centroEscalera - jugador.getWidth() / 2);
            }
            
            enEscalera = true;
            subiendoEscalera = true;
            bajandoEscalera = false;
            jugador.setVely(-velocidad);
            jugador.setVelX(0);
        }
    }
    
    public void bajarEscalera(float velocidad) {
        if (enEscalera && escaleraActual != null) {
            bajandoEscalera = true;
            subiendoEscalera = false;
            jugador.setVely(velocidad);
            jugador.setVelX(0);
            return;
        }
        
        if (puedeMoverseEnEscalera && escaleraActual != null) {
            float centroEscalera = escaleraActual.getX() + escaleraActual.getWidth() / 2;
            jugador.setX(centroEscalera - jugador.getWidth() / 2);
            
            enEscalera = true;
            bajandoEscalera = true;
            subiendoEscalera = false;
            jugador.setVely(velocidad);
            jugador.setVelX(0);
        }
    }
    
    public void detenerMovimientoVertical() {
        if (enEscalera) {
            subiendoEscalera = false;
            bajandoEscalera = false;
            jugador.setVely(0);
        }
    }
    
    public void salirEscalera() {
        if (enEscalera) {
            enEscalera = false;
            subiendoEscalera = false;
            bajandoEscalera = false;
            jugador.setVely(0);
            ticksEnEscalera = 0;
        }
    }
    
    // Getters
    public boolean isEnEscalera() { return enEscalera; }
    public boolean isSubiendoEscalera() { return subiendoEscalera; }
    public boolean isBajandoEscalera() { return bajandoEscalera; }
    public Escalera getEscaleraActual() { return escaleraActual; }
}
