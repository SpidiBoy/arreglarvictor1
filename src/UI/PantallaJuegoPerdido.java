package UI;

import UI.EstadoJuegoBase;
import SistemaDeSoporte.EstadoJuego;
import java.awt.*;
import java.awt.event.KeyEvent;
import mariotest.GestorEstados;
import mariotest.Juego;

/**
 * Pantalla de Game Over
 * Muestra estadísticas finales y permite reintentar
 * 
 * @author LENOVO
 */
public class PantallaJuegoPerdido extends EstadoJuegoBase {
    
    private Font fuenteTitulo;
    private Font fuenteStats;
    private Font fuenteOpciones;
    
    private int ticksAnimacion;
    private int puntosFinal;
    private int nivelAlcanzado;
    private int enemigosEliminados;
    private int mejorRacha;
    
    // Animación de fade in
    private float alpha;
    private static final int DURACION_FADE = 60;
    
    public PantallaJuegoPerdido(GestorEstados gestorEstados, Juego juego) {
        super(gestorEstados, juego);
        
        fuenteTitulo = new Font("Arial", Font.BOLD, 64);
        fuenteStats = new Font("Arial", Font.PLAIN, 24);
        fuenteOpciones = new Font("Arial", Font.BOLD, 20);
    }
    
    @Override
    public void entrar() {
        System.out.println("[ESTADO] GAME OVER");
        
        // Capturar estadísticas finales
        EstadoJuego estado = EstadoJuego.getInstance();
        puntosFinal = estado.getPuntos();
        nivelAlcanzado = estado.getNivelActual();
        enemigosEliminados = estado.getEnemigosEliminados();
        mejorRacha = estado.getMejorRacha();
        
        // Resetear animación
        ticksAnimacion = 0;
        alpha = 0f;
        
        // Imprimir resumen en consola
        System.out.println("========================================");
        System.out.println("         PARTIDA FINALIZADA");
        System.out.println("========================================");
        System.out.println("Puntuación: " + puntosFinal);
        System.out.println("Nivel alcanzado: " + nivelAlcanzado);
        System.out.println("Enemigos eliminados: " + enemigosEliminados);
        System.out.println("Mejor racha: " + mejorRacha);
        System.out.println("========================================");
    }
    
    @Override
    public void tick() {
        ticksAnimacion++;
        
        // Fade in
        if (alpha < 1.0f) {
            alpha += 1.0f / DURACION_FADE;
            if (alpha > 1.0f) {
                alpha = 1.0f;
            }
        }
    }
    
    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fondo rojo oscuro (game over)
        g.setColor(new Color(30, 0, 0));
        g.fillRect(0, 0, Juego.getVentanaWidth(), Juego.getVentanaHeight());
        
        // Overlay con fade
        int alphaInt = (int)(alpha * 255);
        
        // ==================== TÍTULO "JUEGO PERDIDO" ====================
        g.setFont(fuenteTitulo);
        Color colorTitulo = new Color(255, 0, 0, alphaInt);
        g.setColor(colorTitulo);
        
        String titulo = "PERDISTE";
        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        int yTitulo = 150;
        g.drawString(titulo, (Juego.getVentanaWidth() - anchoTitulo) / 2, yTitulo);
        
        // Sombra del título
        g.setColor(new Color(0, 0, 0, alphaInt / 2));
        g.drawString(titulo, 
                    (Juego.getVentanaWidth() - anchoTitulo) / 2 + 3, 
                    yTitulo + 3);
        
        // ==================== ESTADÍSTICAS ====================
        g.setFont(fuenteStats);
        Color colorStats = new Color(200, 200, 200, alphaInt);
        g.setColor(colorStats);
        
        int y = 250;
        int espaciado = 45;
        
        String[] stats = {
            "PUNTUACIÓN FINAL: " + formatearNumero(puntosFinal),
            "NIVEL ALCANZADO: " + nivelAlcanzado,
            "ENEMIGOS ELIMINADOS: " + enemigosEliminados,
            "MEJOR RACHA: " + mejorRacha
        };
        
        for (String stat : stats) {
            int anchoStat = g.getFontMetrics().stringWidth(stat);
            g.drawString(stat, (Juego.getVentanaWidth() - anchoStat) / 2, y);
            y += espaciado;
        }
        
        // ==================== OPCIONES ====================
        if (ticksAnimacion > 120) { // Mostrar después de 2 segundos
            g.setFont(fuenteOpciones);
            
            // Opción 1: Reintentar (parpadea)
            if (ticksAnimacion % 60 < 30) {
                g.setColor(new Color(0, 255, 0, alphaInt));
                String reintentar = "ENTER - Reintentar";
                int anchoR = g.getFontMetrics().stringWidth(reintentar);
                g.drawString(reintentar, 
                            (Juego.getVentanaWidth() - anchoR) / 2, 
                            y + 80);
            }
            
            // Opción 2: Menú
            g.setColor(new Color(150, 150, 150, alphaInt));
            String menu = "ESC - Volver al Menú";
            int anchoM = g.getFontMetrics().stringWidth(menu);
            g.drawString(menu, 
                        (Juego.getVentanaWidth() - anchoM) / 2, 
                        y + 120);
        }
    }
    
    /**
     * Formatea números con separadores de miles
     */
    private String formatearNumero(int numero) {
        return String.format("%,d", numero);
    }
    
    @Override
    public void salir() {
        System.out.println("[ESTADO] Saliendo de Game Over");
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Solo permitir acciones después de la animación inicial
        if (ticksAnimacion < 120) {
            return;
        }
        
        int key = e.getKeyCode();
        
        switch (key) {
        case KeyEvent.VK_ENTER:
            System.out.println("[GAME OVER] Reintentando...");
            gestorEstados.cambiarEstado(EstadoJuegoEnum.JUGANDO);
            break;
            
        case KeyEvent.VK_ESCAPE:
            // Volver al menú principal
            System.out.println("[GAME OVER] Volviendo al menú...");
            gestorEstados.cambiarEstado(EstadoJuegoEnum.MENU_PRINCIPAL);
            break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
}