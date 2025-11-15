package SistemaGFX;

import java.awt.Canvas;
import java.awt.Dimension;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Ventana del juego 
 * 
 * 
 * @author LENOVO
 */
public class Ventana {
    private JFrame frame;
    private Dimension size;
    
    public Ventana(int width, int height, String titulo, Canvas canvas) {
        size = new Dimension(width, height);
        frame = new JFrame(titulo);
        
        // Cargar icono
        try { 
            String iconPath = "/Imagenes/icono.png";
            URL iconURL = getClass().getResource(iconPath);

            if (iconURL != null) {
                ImageIcon logo = new ImageIcon(iconURL);
                frame.setIconImage(logo.getImage());
            } 
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: " + e.getMessage());
        }
        
        // Configurar ventana
        frame.setPreferredSize(size);
        frame.setMaximumSize(size);
        frame.setMinimumSize(size);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
        
        System.out.println("[VENTANA] JFrame creado correctamente");
    }
    
    public JFrame getFrame() {
        return frame;
    }
}