/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;

/**
 *
 * @author LENOVO
 */
public class ComponenteColisionJugador {
    private final Jugador jugador;
    private final Handler handler;
    private final ComponenteEscaleraJugador componenteEscalera;
    private final ComponenteFisicasJugador componenteFisicas;
    
    public ComponenteColisionJugador(Jugador jugador, Handler handler, 
                                   ComponenteEscaleraJugador componenteEscalera,
                                   ComponenteFisicasJugador componenteFisicas) {
        this.jugador = jugador;
        this.handler = handler;
        this.componenteEscalera = componenteEscalera;
        this.componenteFisicas = componenteFisicas;
    }
    
    public void procesarColisiones() {
        for (JuegoObjetos temp : handler.getGameObjs()) {
            if (temp == null) continue;
            if (temp.getId() == ObjetosID.Tile || temp.getId() == ObjetosID.Pipe) {
            
            // Ignorar colisiones en escalera (si está subiendo o bajando)
            if (componenteEscalera.isEnEscalera() && 
                (componenteEscalera.isBajandoEscalera() || componenteEscalera.isSubiendoEscalera())) {
                continue;
            }
            
            if (jugador.getBounds().intersects(temp.getBounds())) {
                // Aterrizaje: El jugador está cayendo sobre la plataforma (Colisión Inferior)
                // Mover al jugador fuera de la colisión por arriba
                jugador.setY(temp.getY() - jugador.getHeight()); 
                jugador.setVely(0); // Detener la caída
                
                // Marcar que aterrizó (resetear salto)
                if (componenteFisicas != null) {
                    componenteFisicas.aterrizar();
                }
            }

            if (jugador.getBoundsTop().intersects(temp.getBounds())) {
                // Golpe de cabeza: El jugador está chocando con algo por arriba
                // Mover al jugador fuera de la colisión por abajo
                jugador.setY(temp.getY() + temp.getHeight());
                jugador.setVely(0); // Invertir la velocidad de caída/subida
            }

            if (jugador.getBoundsRight().intersects(temp.getBounds())) {
                jugador.setX(temp.getX() - jugador.getWidth());
            }

            if (jugador.getBoundsLeft().intersects(temp.getBounds())) {
                jugador.setX(temp.getX() + temp.getWidth());
            }
        }
    }
  }
}
