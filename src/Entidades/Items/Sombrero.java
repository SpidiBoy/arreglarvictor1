package Entidades.Items;

import SistemaDeSoporte.Handler;
import Entidades.Items.Item;
import Entidades.Jugador;
import java.awt.Color;
import java.awt.Graphics;
import mariotest.*;

/**
 * Item Sombrero - Coleccionable de bonificación
 * 
 * 
 * 
 * @author LENOVO
 */
public class Sombrero extends Item {
    private static final float WIDTH = 16f;
    private static final float HEIGHT = 16f;
    private static final int VALOR_PUNTOS = 500;
    
    public Sombrero(float x, float y, int scale, Handler handler) {
        super(x, y, WIDTH, HEIGHT, scale, handler, VALOR_PUNTOS);
        this.flotar = true;
        this.desapareceDespuesDeRecoger = true;
        cargarSprites();
    }
    
    private void cargarSprites() {
        try {
            sprites = Juego.getTextura().getSombreroSprites();
            
            if (sprites != null && sprites.length > 0 && sprites[0] != null) {
                System.out.println("[SOMBRERO] Sprite estático cargado correctamente");
            } else {
                System.err.println("[SOMBRERO] ERROR: Array de sprites vacío o nulo.");
                sprites = null;
            }
        } catch (Exception e) {
            System.err.println("[SOMBRERO] Error cargando sprites: " + e.getMessage());
            e.printStackTrace();
            sprites = null;
        }
    }
    
    @Override
    protected void aplicarEfecto(Jugador player) {
        System.out.println("[SOMBRERO] ¡Recolectado!");
    }
    
    @Override
    protected void renderPlaceholder(Graphics g) {
        // Copa del sombrero
        g.setColor(Color.BLACK);
        g.fillRect((int)getX() + 4, (int)getY() + 2, 8, 8);
        // Ala
        g.fillRect((int)getX() + 2, (int)getY() + 9, 12, 3);
    }
}