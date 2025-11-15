package Entidades.Escenario;

import Entidades.JuegoObjetos;
import SistemaDeSoporte.ObjetosID;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author LENOVO
 */
public class Escalera extends JuegoObjetos {
    
    private boolean esRota;      // Indica si la escalera está rota
    private Color colorEscalera;
    
    public Escalera(float x, float y, float width, float height, boolean esRota) {
        super(x, y, esRota ? ObjetosID.EscaleraRota : ObjetosID.Escalera, width, height, 1);
        this.esRota = esRota;
        this.colorEscalera = esRota ? Color.YELLOW : Color.CYAN;
    }

    @Override
    public void tick() {
        // Las escaleras no necesitan lógica de actualización por ahora
        // Aquí podrías agregar animaciones o efectos especiales
    }

    @Override
    public void render(Graphics g) {
        // Dibujar la escalera
        g.setColor(colorEscalera);
        
        int x = (int) getX();
        int y = (int) getY();
        int width = (int) getWidth();
        int height = (int) getHeight();
        
        // Dibujar los bordes verticales de la escalera
        g.fillRect(x, y, 4, height);
        g.fillRect(x + width - 4, y, 4, height);
        
        // Dibujar los peldaños horizontales
        int numPeldanos = height / 8; // Un peldaño cada 8 píxeles
        for (int i = 0; i <= numPeldanos; i++) {
            int peldanoY = y + (i * height / numPeldanos);
            g.fillRect(x, peldanoY, width, 2);
        }
        
        // Si está rota, dibujar algunas partes faltantes
        if (esRota) {
            g.setColor(Color.BLACK);
            // Simular partes rotas eliminando algunos peldaños
            for (int i = 2; i < numPeldanos - 1; i += 3) {
                int peldanoY = y + (i * height / numPeldanos);
                g.fillRect(x + 4, peldanoY, width - 8, 2);
            }
        }
        
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
    }
    
    /**
     * Verifica si el jugador puede usar esta escalera
     * @return true si la escalera es usable
     */
    public boolean esUsable() {
        return !esRota; // Solo escaleras no rotas son usables
    }
    
    /**
     * Obtiene el área donde el jugador puede interactuar con la escalera
     * Útil para detectar cuando el jugador está cerca y puede subir/bajar
     */
    public Rectangle getAreaInteraccion() {
        int margen = 4; // Píxeles de margen para facilitar la interacción
        return new Rectangle(
            (int) getX() - margen, 
            (int) getY(), 
            (int) getWidth() + (margen * 2), 
            (int) getHeight()
        );
    }
    
    /**
     * Verifica si una posición está dentro del área de la escalera
     */
    public boolean contienePositcion(float x, float y) {
        return getBounds().contains(x, y);
    }
    
    // Getters y setters
    public boolean isEsRota() {
        return esRota;
    }
    
    public void setEsRota(boolean esRota) {
        this.esRota = esRota;
        this.colorEscalera = esRota ? Color.YELLOW : Color.CYAN;
        // Actualizar el ID del objeto
        setId(esRota ? ObjetosID.EscaleraRota : ObjetosID.Escalera);
    }
}