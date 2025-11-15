package SistemaDeSoporte;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import mariotest.GestorEstados;

/**
 * Manejo de teclas mejorado
 * @author LENOVO 
 */
public class Teclas extends KeyAdapter {
    private boolean[] keyAbajo = new boolean[5];
    private Handler handler;
    private GestorEstados gestorEstados;
    
    // Índices de las teclas
    private static final int KEY_SPACE = 0;
    private static final int KEY_W = 1;
    private static final int KEY_S = 2;
    private static final int KEY_A = 3;
    private static final int KEY_D = 4;
    
    public Teclas(Handler handler, GestorEstados gestorEstados) {
        this.handler = handler;
        this.gestorEstados = gestorEstados;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // ✅ CRÍTICO: Delegar PRIMERO al gestor de estados
        if (gestorEstados != null) {
            gestorEstados.keyPressed(e);
        }
        
        // ✅ Si NO estamos jugando, NO procesar teclas de movimiento
        if (gestorEstados != null && !gestorEstados.estaJugando()) {
            return; // ⚠️ IMPORTANTE: Salir aquí para menús/victoria
        }
        
        int key = e.getKeyCode();
        
        // Verificar que el jugador exista
        if (handler.getPlayer() == null) {
            return;
        }
        
        // ==================== ESPACIO - SALTAR ====================
        if (key == KeyEvent.VK_SPACE) {
            if (!keyAbajo[KEY_SPACE]) {
                handler.getPlayer().iniciarSalto();
                keyAbajo[KEY_SPACE] = true;
            }
        }
        
        // ==================== W - SUBIR ESCALERA ====================
        if (key == KeyEvent.VK_W) {
            if (!keyAbajo[KEY_W]) {
                handler.getPlayer().subirEscalera();
                keyAbajo[KEY_W] = true;
            }
        }
        
        // ==================== S - BAJAR ESCALERA ====================
        if (key == KeyEvent.VK_S) {
            if (!keyAbajo[KEY_S]) {
                handler.getPlayer().bajarEscalera();
                keyAbajo[KEY_S] = true;
            }
        }
        
        // ==================== A - MOVER IZQUIERDA ====================
        if (key == KeyEvent.VK_A) {
            if (!keyAbajo[KEY_A]) {
                handler.getPlayer().moverIzquierda();
                keyAbajo[KEY_A] = true;
            }
        }
        
        // ==================== D - MOVER DERECHA ====================
        if (key == KeyEvent.VK_D) {
            if (!keyAbajo[KEY_D]) {
                handler.getPlayer().moverDerecha();
                keyAbajo[KEY_D] = true;
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // ✅ Delegar al gestor de estados
        if (gestorEstados != null) {
            gestorEstados.keyReleased(e);
        }

        // ✅ Si NO estamos jugando, NO procesar
        if (gestorEstados != null && !gestorEstados.estaJugando()) {
            return;
        }

        int key = e.getKeyCode();
        
        if (handler.getPlayer() == null) {
            return;
        }
        
        // ==================== ESPACIO SOLTADO ====================
        if (key == KeyEvent.VK_SPACE) {
            keyAbajo[KEY_SPACE] = false;
        }
        
        // ==================== W SOLTADO ====================
        if (key == KeyEvent.VK_W) {
            keyAbajo[KEY_W] = false;
            if (handler.getPlayer().isEnEscalera() && !keyAbajo[KEY_S]) {
                handler.getPlayer().detenerMovimientoVertical();
            }
        }
        
        // ==================== S SOLTADO ====================
        if (key == KeyEvent.VK_S) {
            keyAbajo[KEY_S] = false;
            if (handler.getPlayer().isEnEscalera() && !keyAbajo[KEY_W]) {
                handler.getPlayer().detenerMovimientoVertical();
            }
        }
        
        // ==================== A SOLTADO ====================
        if (key == KeyEvent.VK_A) {
            keyAbajo[KEY_A] = false;
        }
        
        // ==================== D SOLTADO ====================
        if (key == KeyEvent.VK_D) {
            keyAbajo[KEY_D] = false;
        }
        
        // ==================== DETENER MOVIMIENTO HORIZONTAL ====================
        if (!keyAbajo[KEY_A] && !keyAbajo[KEY_D]) {
            handler.getPlayer().detenerMovimiento();
        }
    }
    
    // ... resto del código sin cambios ...
    
    public boolean isKeyDown(int keyIndex) {
        if (keyIndex >= 0 && keyIndex < keyAbajo.length) {
            return keyAbajo[keyIndex];
        }
        return false;
    }
    
    public boolean isSpacePressed() {
        return keyAbajo[KEY_SPACE];
    }
    
    public boolean isWPressed() {
        return keyAbajo[KEY_W];
    }
    
    public boolean isSPressed() {
        return keyAbajo[KEY_S];
    }
    
    public boolean isAPressed() {
        return keyAbajo[KEY_A];
    }
    
    public boolean isDPressed() {
        return keyAbajo[KEY_D];
    }
    
    public void resetKeys() {
        for (int i = 0; i < keyAbajo.length; i++) {
            keyAbajo[i] = false;
        }
        
        if (handler.getPlayer() != null) {
            handler.getPlayer().detenerMovimiento();
            handler.getPlayer().detenerMovimientoVertical();
        }
    }
}