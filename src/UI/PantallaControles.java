package UI;

import UI.EstadoJuegoBase;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import mariotest.GestorEstados;
import mariotest.Juego;

/**
 * Pantalla de Controles del juego
 * Muestra imagen con los controles o texto explicativo
 * 
 * @author LENOVO
 */
public class PantallaControles extends EstadoJuegoBase {
    
    private BufferedImage imagenControles;
    private Font fuenteTitulo;
    private Font fuenteTexto;
    private int ticksAnimacion;
    
    public PantallaControles(GestorEstados gestorEstados, Juego juego) {
        super(gestorEstados, juego);
        
        fuenteTitulo = new Font("Arial", Font.BOLD, 36);
        fuenteTexto = new Font("Arial", Font.PLAIN, 18);
        
        cargarImagenControles();
    }
    
    /**
     * Carga la imagen de controles si existe
     */
    private void cargarImagenControles() {
        /*
        try {
            imagenControles = ImageIO.read(
                getClass().getResourceAsStream("/Imagenes/controles.png")
            );
            System.out.println("[CONTROLES] Imagen cargada");
        } catch (Exception e) {
            System.err.println("[CONTROLES] No se pudo cargar imagen: " + e.getMessage());
            imagenControles = null;
        }
        */
    }
    
    @Override
    public void entrar() {
        System.out.println("[ESTADO] Mostrando pantalla de controles");
        ticksAnimacion = 0;
    }
    
    @Override
    public void tick() {
        ticksAnimacion++;
    }
    
    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fondo oscuro
        g.setColor(new Color(20, 20, 30));
        g.fillRect(0, 0, Juego.getVentanaWidth(), Juego.getVentanaHeight());
        
        // ==================== TÍTULO ====================
        g.setFont(fuenteTitulo);
        g.setColor(new Color(255, 215, 0));
        String titulo = "CONTROLES";
        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        g.drawString(titulo, (Juego.getVentanaWidth() - anchoTitulo) / 2, 80);
        
        // ==================== CONTENIDO ====================
        if (imagenControles != null) {
            // Mostrar imagen de controles centrada
            int x = (Juego.getVentanaWidth() - imagenControles.getWidth()) / 2;
            int y = 150;
            g.drawImage(imagenControles, x, y, null);
        } else {
            // Mostrar controles en texto
            renderControlesTexto(g);
        }
        
        // ==================== INSTRUCCIÓN PARA VOLVER ====================
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(new Color(200, 200, 200));
        
        // Parpadeo del texto
        if (ticksAnimacion % 60 < 30) {
            String volver = "Presiona ESC para volver al menú";
            int anchoVolver = g.getFontMetrics().stringWidth(volver);
            g.drawString(volver, 
                        (Juego.getVentanaWidth() - anchoVolver) / 2, 
                        Juego.getVentanaHeight() - 50);
        }
    }
    
    /**
     * Renderiza controles en formato texto si no hay imagen
     */
    private void renderControlesTexto(Graphics g) {
        g.setFont(fuenteTexto);
        g.setColor(Color.WHITE);
        
        int x = 200;
        int y = 180;
        int espaciado = 40;
        
        String[] controles = {
            "W / ↑  - Subir escalera",
            "S / ↓  - Bajar escalera",
            "A / ←  - Mover izquierda",
            "D / →  - Mover derecha",
            "ESPACIO - Saltar",
            "",
            "OBJETIVO:",
            "- Rescata a la princesa esquivando barriles",
            "- Recoge items para sumar puntos",
            "- El martillo destruye enemigos",
            "- ¡Cuidado con el fuego!"
        };
        
        for (String linea : controles) {
            if (linea.isEmpty()) {
                y += espaciado / 2;
                continue;
            }
            
            // Resaltar controles principales
            if (linea.contains(" - ")) {
                String[] partes = linea.split(" - ");
                
                // Tecla (color destacado)
                g.setColor(new Color(255, 215, 0));
                g.drawString(partes[0], x, y);
                
                // Descripción
                int anchoTecla = g.getFontMetrics().stringWidth(partes[0]);
                g.setColor(Color.WHITE);
                g.drawString(" - " + partes[1], x + anchoTecla, y);
            } else {
                // Títulos
                g.setColor(new Color(255, 140, 0));
                g.drawString(linea, x, y);
            }
            
            y += espaciado;
        }
    }
    
    @Override
    public void salir() {
        System.out.println("[ESTADO] Saliendo de pantalla de controles");
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Cualquier tecla vuelve al menú
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE || 
            e.getKeyCode() == KeyEvent.VK_ENTER) {
            gestorEstados.cambiarEstado(EstadoJuegoEnum.MENU_PRINCIPAL);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
}