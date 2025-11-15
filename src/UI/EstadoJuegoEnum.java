package UI;

/**
 * Enum para los diferentes estados del juego
 * Usado por el GestorEstados para cambiar entre menús
 * 
 * @author LENOVO
 */
public enum EstadoJuegoEnum {
    MENU_PRINCIPAL,    // Menú inicial
    JUGANDO,          // En partida
    CONTROLES,        // Pantalla de controles
    GAME_OVER,        // Pantalla de derrota
    VICTORIA ,      // Pantalla de victoria final
    PAUSA             // Juego pausado
}