package Entidades.EfectosVisuales;

import Entidades.JuegoObjetos;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

/**
 * Texto flotante que muestra puntos ganados
 */
public class TextoPuntos extends JuegoObjetos {
    
    private String texto;
    private Color color;
    private Handler handler;
    
    private int ticksVida;
    private static final int DURACION = 60; // 1 segundo
    private float velocidadY;
    private float alpha;
    
    private Font fuente;
    
    public TextoPuntos(float x, float y, String texto, Color color, Handler handler) {
        super(x, y, ObjetosID.Puntos, 0, 0, 1);
        this.texto = texto;
        this.color = color;
        this.handler = handler;
        this.ticksVida = 0;
        this.velocidadY = -1.5f;
        this.alpha = 1.0f;
        this.fuente = new Font("Arial", Font.BOLD, 16);
    }
    
    @Override
    public void tick() {
        ticksVida++;
        
        // Flotar hacia arriba
        setY(getY() + velocidadY);
        
        // Fade out
        alpha = 1.0f - ((float)ticksVida / DURACION);
        
        // Eliminar cuando termine
        if (ticksVida >= DURACION) {
            handler.removeObj(this);
        }
    }
    
    @Override
    public void render(Graphics g) {
        int alphaInt = (int)(alpha * 255);
        Color colorTransparente = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            alphaInt
        );
        
        g.setFont(fuente);
        g.setColor(colorTransparente);
        g.drawString(texto, (int)getX(), (int)getY());
        
        // Sombra
        g.setColor(new Color(0, 0, 0, alphaInt / 2));
        g.drawString(texto, (int)getX() + 1, (int)getY() + 1);
    }
    
    @Override
    public void aplicarGravedad() {
        // No aplicar gravedad
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, 0, 0);
    }
}
