package Entidades.Items;


import Entidades.Items.Item;
import Entidades.Jugador;
import SistemaDeSoporte.Handler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import mariotest.*;

/**
 * Item Martillo - Power-up principal
 * 
 * 
 * 
 * @author LENOVO
 */
public class Martillo extends Item {
    private static final float WIDTH = 16f;
    private static final float HEIGHT = 16f;
    private static final int VALOR_PUNTOS = 300;
    private static final int DURACION_PODER = 600; // 10 segundos
    
    public Martillo(float x, float y, int scale, Handler handler) {
        super(x, y, WIDTH, HEIGHT, scale, handler, VALOR_PUNTOS);
        this.flotar = true;
        this.desapareceDespuesDeRecoger = true;
        this.tieneGravedad = false;
        cargarSprites();
    }

    private void cargarSprites() {
        try {
            sprites = Juego.getTextura().getMartilloSprites();
            
            if (sprites != null && sprites.length > 0 && sprites[0] != null) {
                System.out.println("[MARTILLO] Sprite estático cargado correctamente.");
            } else {
                System.err.println("[MARTILLO] ERROR: Array de sprites vacío o nulo.");
                sprites = null; // Forzar uso de placeholder
            }
        } catch (Exception e) {
            System.err.println("[MARTILLO] Error cargando sprites: " + e.getMessage());
            e.printStackTrace();
            sprites = null;
        }
    }
    
    @Override
    protected void aplicarEfecto(Jugador player) {
        // Activar el poder del martillo
        player.activarMartillo(DURACION_PODER / 60); // Convertir ticks a segundos
        
        System.out.println("[MARTILLO] ¡Power-up activado! Duración: " + 
                          (DURACION_PODER / 60) + " segundos");
    }
    
    @Override
    protected void renderPlaceholder(Graphics g) {
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Rotación suave para efecto visual (si tuvieras animación)
        if (animacion != null) {
            animacion.runAnimacion();
        }
    }
}