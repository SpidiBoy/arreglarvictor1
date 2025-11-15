package UI;

import SistemaDeSoporte.EstadoJuego;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import mariotest.GestorEstados;
import mariotest.Juego;

/**
 * Estado JUGANDO - El juego está activo
 * 
 * 
 * @author LENOVO
 */
public class EstadoJugando extends EstadoJuegoBase {
    
    private boolean primeraVez = true;
    
    public EstadoJugando(GestorEstados gestorEstados, Juego juego) {
        super(gestorEstados, juego);
    }
    
    @Override
    public void entrar() {
        System.out.println("[ESTADO] Entrando a JUGANDO");
        
        //  SOLO reiniciar si es desde el menú (primera vez)
        if (primeraVez) { 
        if (juego.getGestorNiveles() != null) {
            
            // ⭐️ CAMBIO CLAVE: Llama al método que acabamos de crear
            juego.getGestorNiveles().reiniciar(); 
            
            // La llamada a EstadoJuego.getInstance().reiniciar();
            // ya está incluida dentro de GestorNiveles.reiniciar().
            
            System.out.println("[ESTADO] Partida completamente reiniciada (Nivel 1 y stats)");
        }
        primeraVez = false;
    }
    }
    
    @Override
    public void tick() {
        // Verificar si el jugador perdió todas las vidas
        if (EstadoJuego.getInstance().getVidas() <= 0) {
            System.out.println("[ESTADO] ¡GAME OVER! Sin vidas restantes");
            gestorEstados.cambiarEstado(EstadoJuegoEnum.GAME_OVER);
            primeraVez = true; // Reset para próxima partida
        }
    }
    
    @Override
    public void render(Graphics g) {
        // El renderizado lo hace GameLoopFacade
    }
    
    @Override
    public void salir() {
        System.out.println("[ESTADO] Saliendo de JUGANDO");
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Solo capturar ESC para volver al menú
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println("[ESTADO] Volviendo al menú principal...");
            primeraVez = true; // Reset para próxima partida
            gestorEstados.cambiarEstado(EstadoJuegoEnum.MENU_PRINCIPAL);
        }
        if (e.getKeyCode() == KeyEvent.VK_F1) {
        System.out.println("[DEBUG] Forzando victoria...");
        gestorEstados.cambiarEstado(EstadoJuegoEnum.VICTORIA);
        return;
    }
    
    // ESC para volver al menú
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        System.out.println("[ESTADO] Volviendo al menú principal...");
        primeraVez = true;
        gestorEstados.cambiarEstado(EstadoJuegoEnum.MENU_PRINCIPAL);
    }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Manejado por Teclas.java
    }
}