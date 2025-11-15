package SistemaDeSoporte;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Gestiona el estado del juego: puntos, vidas, nivel, tiempo
 * Patrón Singleton para acceso global
 * * @author LENOVO
 */
public final class EstadoJuego {
    
    private static EstadoJuego instancia;
    
    // Sistema de puntuación
    private int puntos;
    private int puntosMaximos;
    private int multiplicador;
    
    // Sistema de vidas
    private int vidas;
    private static final int VIDAS_INICIALES = 3;
    private static final int VIDAS_MAXIMAS = 5;
    
    // Sistema de niveles
    private int nivelActual;
    private int enemigosEliminados;
    
    // Tiempo
    private int tiempoRestante;
    private int ticksTiempo;
    private boolean tiempoActivo;
    
    // Bonificaciones
    private int racha; // Racha de enemigos eliminados
    private int mejorRacha;
    
    // UI
    private Font fuentePuntos;
    private Font fuenteInfo;
    
    /**
     * Constructor privado (Singleton)
     */
    private EstadoJuego() {
        reiniciar();
        // Configurar fuentes
        fuentePuntos = new Font("Arial", Font.BOLD, 24);
        fuenteInfo = new Font("Segoe UI Emoji", Font.BOLD, 18);
    }
    
    /**
     * Obtiene la instancia única (Singleton)
     */
    public static EstadoJuego getInstance() {
        if (instancia == null) {
            instancia = new EstadoJuego();
        }
        return instancia;
    }
    
    /**
     * Reinicia el estado del juego
     */
    public void reiniciar() {
        this.puntos = 0;
        this.puntosMaximos = 0;
        this.multiplicador = 1;
        this.vidas = VIDAS_INICIALES;
        this.nivelActual = 1;
        this.enemigosEliminados = 0;
        this.tiempoRestante = 0;
        this.ticksTiempo = 0;
        this.tiempoActivo = false;
        this.racha = 0;
        this.mejorRacha = 0;
        
        System.out.println("[ESTADO] Juego reiniciado");
    }
    
    /**
     * Actualiza el estado cada tick
     */
    public void tick() {
        // Actualizar tiempo si está activo
        if (tiempoActivo) {
            ticksTiempo++;
            
            // Decrementar cada segundo (60 ticks)
            if (ticksTiempo >= 60) {
                ticksTiempo = 0;
                tiempoRestante--;
                
                // Tiempo agotado
                if (tiempoRestante <= 0) {
                    tiempoRestante = 0;
                    tiempoAgotado();
                }
                
                // Advertencia tiempo bajo
                if (tiempoRestante == 30) {
                    System.out.println("[ALERTA] ¡Quedan 30 segundos!");
                }
            }
        }
    }
    
    /**
     * Suma puntos al jugador
     */
    public void sumarPuntos(int cantidad) {
        int puntosGanados = cantidad * multiplicador;
        puntos += puntosGanados;
        
        // Actualizar máximo
        if (puntos > puntosMaximos) {
            puntosMaximos = puntos;
        }
        
        System.out.println("[PUNTOS] +" + puntosGanados + " (Total: " + puntos + ")");
        
        // Verificar vida extra por puntos
        verificarVidaExtra();
    }
    
    /**
     * Registra la eliminación de un enemigo
     */
    public void enemigoEliminado(int puntosBase) {
        enemigosEliminados++;
        racha++;
        
        // Actualizar mejor racha
        if (racha > mejorRacha) {
            mejorRacha = racha;
        }
        
        // Bonus por racha
        int bonusRacha = racha * 50;
        sumarPuntos(puntosBase + bonusRacha);
        
        System.out.println("[ENEMIGO] Eliminado! Racha: " + racha);
    }
    
    /**
     * Resetea la racha (cuando el jugador recibe daño)
     */
    public void resetearRacha() {
        if (racha > 0) {
            System.out.println("[RACHA] Perdida (era " + racha + ")");
            racha = 0;
        }
    }
    
    /**
     * Pierde una vida
     */
    public void perderVida() {
        if (vidas > 0) {
            vidas--;
            resetearRacha();
            
            System.out.println("[VIDAS] Perdida! Quedan: " + vidas);
            
            if (vidas == 0) {
                gameOver();
            }
        }
    }
    
    /**
     * Gana una vida
     */
    public void ganarVida() {
        if (vidas < VIDAS_MAXIMAS) {
            vidas++;
            System.out.println("[VIDAS] ¡Vida extra! Total: " + vidas);
        }
    }
    
    /**
     * Verifica si se gana vida extra por puntos
     */
    private void verificarVidaExtra() {
        // Vida extra cada 10,000 puntos
        int vidasEsperadas = (puntos / 10000) + VIDAS_INICIALES;
        if (vidasEsperadas > vidas && vidas < VIDAS_MAXIMAS) {
            ganarVida();
        }
    }
    
    /**
     * Inicia el temporizador del nivel
     */
    public void iniciarTiempo(int segundos) {
        this.tiempoRestante = segundos;
        this.ticksTiempo = 0;
        this.tiempoActivo = true;
        
        System.out.println("[TIEMPO] Iniciado: " + segundos + " segundos");
    }
    
    /**
     * Detiene el temporizador
     */
    public void detenerTiempo() {
        this.tiempoActivo = false;
    }
    
    /**
     * Bonus por tiempo restante
     */
    public void bonusTiempo() {
        if (tiempoRestante > 0) {
            int bonusPuntos = tiempoRestante * 10;
            sumarPuntos(bonusPuntos);
            System.out.println("[BONUS] Tiempo restante: +" + bonusPuntos + " pts");
        }
    }
    
    /**
     * Llamado cuando se agota el tiempo
     */
    private void tiempoAgotado() {
        System.out.println("[TIEMPO] ¡Tiempo agotado!");
        perderVida();
        // TODO: Trigger evento de tiempo agotado
    }
    
    /**
     *  Establece el nivel actual
     */
    public void setNivelActual(int nivel) {
        if (nivel > this.nivelActual) {
            // Si es un nivel nuevo, sumar bonus de tiempo
            bonusTiempo();
            resetearRacha();
        }
        this.nivelActual = nivel;
        System.out.println("[ESTADO] Nivel actualizado a: " + this.nivelActual);
    }
    
    /**
     * Avanza al siguiente nivel
     */
    public void siguienteNivel() {
        // Usa el nuevo setter
        setNivelActual(this.nivelActual + 1);
    }
    
    /**
     * Game Over
     */
    private void gameOver() {
        System.out.println("\n========================================");
        System.out.println("         GAME OVER");
        System.out.println("========================================");
        System.out.println("Puntuación final: " + puntos);
        System.out.println("Mejor racha: " + mejorRacha);
        System.out.println("Enemigos eliminados: " + enemigosEliminados);
        System.out.println("========================================\n");
    }
    
    /**
     * Renderiza el HUD del juego
     */
    
    public void renderHUD(Graphics g, int screenWidth, int screenHeight) {
        // Puntos
        g.setFont(fuentePuntos);
        g.setColor(Color.YELLOW);
        g.drawString("PUNTOS: " + formatearNumero(puntos), 10, 28);
        
        // Vidas
        g.setFont(fuenteInfo);
        g.setColor(Color.RED);
        String vidasTexto = "❤".repeat(vidas);
        g.drawString("VIDAS: " + vidasTexto, screenWidth - 180, 25);
        
        // Nivel
        g.setColor(Color.CYAN);
        g.drawString("NIVEL " + nivelActual, screenWidth / 2 - -170, 25);
        
        // Tiempo (si está activo)
        if (tiempoActivo) {
            Color colorTiempo = tiempoRestante <= 30 ? Color.RED : Color.WHITE;
            g.setColor(colorTiempo);
            g.drawString("TIEMPO: " + tiempoRestante, screenWidth / 2 + 60, 25);
        }
        
        // Racha (si hay)
        if (racha > 1) {
            g.setColor(Color.ORANGE);
            g.drawString("RACHA x" + racha + "!", 10, screenHeight - 20);
        }
        
        // Multiplicador (si > 1)
        if (multiplicador > 1) {
            g.setColor(Color.GREEN);
            g.drawString("MULT x" + multiplicador, screenWidth - 120, screenHeight - 20);
        }
    }
    
    /**
     * Formatea números con separadores de miles
     */
    private String formatearNumero(int numero) {
        return String.format("%,d", numero);
    }
    
    // ==================== GETTERS Y SETTERS ====================
    
    public int getPuntos() {
        return puntos;
    }
    
    public int getPuntosMaximos() {
        return puntosMaximos;
    }
    
    public int getVidas() {
        return vidas;
    }
    
    public int getNivelActual() {
        return nivelActual;
    }
    
    public int getTiempoRestante() {
        return tiempoRestante;
    }
    
    public int getMultiplicador() {
        return multiplicador;
    }
    
    public void setMultiplicador(int multiplicador) {
        this.multiplicador = Math.max(1, multiplicador);
        System.out.println("[MULT] Multiplicador: x" + this.multiplicador);
    }
    
    public int getRacha() {
        return racha;
    }
    
    public int getMejorRacha() {
        return mejorRacha;
    }
    
    public int getEnemigosEliminados() {
        return enemigosEliminados;
    }
    
    public boolean isTiempoActivo() {
        return tiempoActivo;
    }
    /**
     * Información de debug
     */
    public String getInfo() {
        return String.format(
            "Puntos: %d | Vidas: %d | Nivel: %d | Racha: %d | Tiempo: %d",
            puntos, vidas, nivelActual, racha, tiempoRestante
        );
    }
}