package UI;

import SistemaDeSoporte.EstadoJuego;
import java.awt.*;
import java.awt.event.KeyEvent;
import mariotest.GestorEstados;
import mariotest.Juego;

/**
 * Pantalla de Victoria Final
 * @author LENOVO
 */
public class PantallaVictoria extends EstadoJuegoBase {
    
    private Font fuenteTitulo;
    private Font fuenteSubtitulo;
    private Font fuenteStats;
    private Font fuenteOpciones;
    
    private int ticksAnimacion;
    private float alpha;
    private static final int DURACION_FADE = 60; // 1 segundo
    
    private int puntosTotales;
    private int mejorRacha;
    private int enemigosEliminados;
    
    private Color colorTitulo;
    private Color colorSubtitulo;
    private Color colorStats;
    private Color colorOpciones;
    
    public PantallaVictoria(GestorEstados gestorEstados, Juego juego) {
        super(gestorEstados, juego);
        
        fuenteTitulo = new Font("Arial", Font.BOLD, 56);
        fuenteSubtitulo = new Font("Arial", Font.BOLD, 28);
        fuenteStats = new Font("Arial", Font.PLAIN, 22);
        fuenteOpciones = new Font("Arial", Font.BOLD, 20);
        
        colorTitulo = new Color(255, 215, 0);
        colorSubtitulo = new Color(255, 140, 0);
        colorStats = new Color(200, 200, 200);
        colorOpciones = new Color(0, 255, 127);
    }
    
    @Override
    public void entrar() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║     ¡JUEGO COMPLETADO!            ║");
        System.out.println("╚════════════════════════════════════╝\n");
        
        EstadoJuego estado = EstadoJuego.getInstance();
        
        if (estado != null) {
            puntosTotales = estado.getPuntos();
            mejorRacha = estado.getMejorRacha();
            enemigosEliminados = estado.getEnemigosEliminados();
            
            System.out.println("📊 ESTADÍSTICAS CAPTURADAS:");
            System.out.println("   Puntos: " + puntosTotales);
            System.out.println("   Mejor Racha: " + mejorRacha);
            System.out.println("   Enemigos: " + enemigosEliminados);
        } else {
            System.err.println("⚠️ EstadoJuego es NULL - usando valores por defecto");
            puntosTotales = 0;
            mejorRacha = 0;
            enemigosEliminados = 0;
        }
        
        ticksAnimacion = 0;
        alpha = 0f;
        
        System.out.println("\n✅ PantallaVictoria lista");
        System.out.println("   Presiona ENTER para reiniciar");
        System.out.println("   Presiona ESC para volver al menú\n");
    }
    
    @Override
    public void tick() {
        ticksAnimacion++;
        
        if (alpha < 1.0f) {
            alpha += 1.0f / DURACION_FADE;
            if (alpha > 1.0f) alpha = 1.0f;
        }
    }
    
    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fondo con gradiente
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(10, 10, 10),
            0, Juego.getVentanaHeight(), new Color(50, 40, 0)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, Juego.getVentanaWidth(), Juego.getVentanaHeight());
        
        int alphaInt = (int)(alpha * 255);
        
        // ==================== TÍTULO ====================
        g.setFont(fuenteTitulo);
        g.setColor(new Color(colorTitulo.getRed(), colorTitulo.getGreen(), 
                            colorTitulo.getBlue(), alphaInt));
        
        String titulo = "¡VICTORIA!";
        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        g.drawString(titulo, (Juego.getVentanaWidth() - anchoTitulo) / 2, 120);
        
        // ==================== SUBTÍTULO ====================
        g.setFont(fuenteSubtitulo);
        g.setColor(new Color(colorSubtitulo.getRed(), colorSubtitulo.getGreen(), 
                            colorSubtitulo.getBlue(), alphaInt));
        
        String subtitulo = "¡Has derrotado a Diego Kong!";
        int anchoSub = g.getFontMetrics().stringWidth(subtitulo);
        g.drawString(subtitulo, (Juego.getVentanaWidth() - anchoSub) / 2, 180);
        
        // ==================== ESTADÍSTICAS ====================
        renderEstadisticas(g, alphaInt);
        
        // ==================== OPCIONES ====================
        if (ticksAnimacion > 60) { // Mostrar después de 1 segundo
            renderOpciones(g, alphaInt);
        }
    }
    
    private void renderEstadisticas(Graphics g, int alphaInt) {
        g.setFont(fuenteStats);
        g.setColor(new Color(colorStats.getRed(), colorStats.getGreen(), 
                            colorStats.getBlue(), alphaInt));
        
        int y = 280;
        int espaciado = 45;
        
        String[] stats = {
            "═══════════════════════════════",
            "ESTADÍSTICAS FINALES",
            "═══════════════════════════════",
            "",
            "Puntuación Total: " + formatearNumero(puntosTotales),
            "Mejor Racha: x" + mejorRacha,
            "Enemigos Eliminados: " + enemigosEliminados,
            "",
            "═══════════════════════════════"
        };
        
        for (String stat : stats) {
            if (stat.isEmpty()) {
                y += espaciado / 2;
                continue;
            }
            
            int anchoStat = g.getFontMetrics().stringWidth(stat);
            
            if (stat.contains("═") || stat.equals("ESTADÍSTICAS FINALES")) {
                g.setColor(new Color(255, 215, 0, alphaInt));
            } else {
                g.setColor(new Color(colorStats.getRed(), colorStats.getGreen(), 
                                    colorStats.getBlue(), alphaInt));
            }
            
            g.drawString(stat, (Juego.getVentanaWidth() - anchoStat) / 2, y);
            y += espaciado;
        }
    }
    
    private void renderOpciones(Graphics g, int alphaInt) {
        g.setFont(fuenteOpciones);
        
        int y = Juego.getVentanaHeight() - 150;
        
        // Opción 1: Reiniciar (parpadea)
        if (ticksAnimacion % 60 < 30) {
            g.setColor(new Color(colorOpciones.getRed(), colorOpciones.getGreen(), 
                                colorOpciones.getBlue(), alphaInt));
            String reintentar = "ENTER - Jugar de Nuevo";
            int anchoR = g.getFontMetrics().stringWidth(reintentar);
            g.drawString(reintentar, (Juego.getVentanaWidth() - anchoR) / 2, y);
        }
        
        // Opción 2: Menú
        g.setColor(new Color(200, 200, 200, alphaInt));
        String menu = "ESC - Volver al Menú";
        int anchoM = g.getFontMetrics().stringWidth(menu);
        g.drawString(menu, (Juego.getVentanaWidth() - anchoM) / 2, y + 40);
    }
    
    private String formatearNumero(int numero) {
        return String.format("%,d", numero);
    }
    
    @Override
    public void salir() {
        System.out.println("[VICTORIA] Saliendo de pantalla de victoria");
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("[VICTORIA] 🔑 Tecla detectada: " + KeyEvent.getKeyText(e.getKeyCode()));
        
        if (ticksAnimacion < 60) {
            System.out.println("[VICTORIA] ⏳ Esperando animación... (" + ticksAnimacion + "/60)");
            return;
        }
        
        int key = e.getKeyCode();
        
        switch (key) {
            case KeyEvent.VK_ENTER:
                System.out.println("[VICTORIA] ✅ Reiniciando juego desde nivel 1...");
                gestorEstados.cambiarEstado(EstadoJuegoEnum.JUGANDO);
                break;
                
            case KeyEvent.VK_ESCAPE:
                System.out.println("[VICTORIA] ✅ Volviendo al menú principal...");
                gestorEstados.cambiarEstado(EstadoJuegoEnum.MENU_PRINCIPAL);
                break;
                
            default:
                System.out.println("[VICTORIA] ⚠️ Tecla no asignada: " + KeyEvent.getKeyText(key));
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
}