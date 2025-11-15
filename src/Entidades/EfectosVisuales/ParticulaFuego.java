package Entidades.EfectosVisuales;

import Entidades.JuegoObjetos;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

/**
 * Clase ParticulaFuego - Efecto visual de chispas de fuego
 * Se generan desde el enemigo Fuego y flotan hacia arriba
 * 
 * @author LENOVO
 */
public class ParticulaFuego extends JuegoObjetos {
    
    // Dimensiones de la partícula
    private static final float WIDTH = 4F;
    private static final float HEIGHT = 4F;
    
    // Componentes
    private Handler handler;
    private Random random;
    
    // Física y comportamiento
    private float velocidadY;
    private float velocidadX;
    private int vida;
    private int vidaMaxima;
    private float alpha; // Transparencia (0.0 - 1.0)
    
    // Colores
    private Color color;
    private static final Color[] COLORES_FUEGO = {
        new Color(255, 69, 0),    // Rojo-naranja
        new Color(255, 140, 0),   // Naranja oscuro
        new Color(255, 165, 0),   // Naranja
        new Color(255, 215, 0),   // Amarillo-naranja
        new Color(255, 255, 0)    // Amarillo
    };
    
    /**
     * Constructor de la partícula de fuego
     */
    public ParticulaFuego(float x, float y, int scale, Handler handler) {
        super(x, y, ObjetosID.Particula, WIDTH, HEIGHT, scale);
        this.handler = handler;
        this.random = new Random();
        
        // Vida aleatoria (20-40 ticks = 0.33-0.66 segundos)
        this.vidaMaxima = 20 + random.nextInt(20);
        this.vida = vidaMaxima;
        this.alpha = 1.0f;
        
        // Velocidad aleatoria hacia arriba
        this.velocidadY = -1.0f - random.nextFloat() * 1.5f; // -1.0 a -2.5
        this.velocidadX = (random.nextFloat() - 0.5f) * 0.5f; // Ligero movimiento horizontal
        
        // Color aleatorio del fuego
        this.color = COLORES_FUEGO[random.nextInt(COLORES_FUEGO.length)];
        
        // Establecer velocidades
        setVely(velocidadY);
        setVelX(velocidadX);
    }
    
    @Override
    public void tick() {
        // Reducir vida
        vida--;
        
        // Calcular transparencia (fade out)
        alpha = (float) vida / vidaMaxima;
        
        // Aplicar movimiento
        setX(getX() + getVelX());
        setY(getY() + getVely());
        
        // Desacelerar verticalmente (simula resistencia del aire)
        setVely(getVely() * 0.98f);
        
        // Eliminar cuando la vida llega a 0
        if (vida <= 0 || alpha <= 0) {
            destruir();
        }
    }
    
    @Override
    public void aplicarGravedad() {
        // Las partículas de fuego flotan, no caen
        // No aplicar gravedad
    }

    @Override
    public void render(Graphics g) {
        // Crear color con transparencia
        int alphaInt = (int) (alpha * 255);
        Color colorTransparente = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            alphaInt
        );
        
        g.setColor(colorTransparente);
        
        // Renderizar como círculo pequeño
        int size = (int) (getWidth() * alpha); // El tamaño se reduce con la transparencia
        if (size < 1) size = 1;
        
        g.fillOval(
            (int) (getX() - size / 2), 
            (int) (getY() - size / 2), 
            size, 
            size
        );
        
        // Agregar brillo en el centro (más brillante)
        if (alpha > 0.5f) {
            Color brilloTransparente = new Color(255, 255, 255, alphaInt / 2);
            g.setColor(brilloTransparente);
            int brilloSize = size / 2;
            if (brilloSize < 1) brilloSize = 1;
            g.fillOval(
                (int) (getX() - brilloSize / 2), 
                (int) (getY() - brilloSize / 2), 
                brilloSize, 
                brilloSize
            );
        }
    }

    @Override
    public Rectangle getBounds() {
        // Las partículas no tienen colisión
        return new Rectangle(0, 0, 0, 0);
    }
    
    /**
     * Destruye la partícula
     */
    private void destruir() {
        handler.removeObj(this);
    }
    
    // ==================== GETTERS ====================
    
    public int getVida() {
        return vida;
    }
    
    public float getAlpha() {
        return alpha;
    }
    
    public Color getColor() {
        return color;
    }
}