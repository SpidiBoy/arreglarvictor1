package Entidades.Items;

import Entidades.Items.Item;
import Entidades.Jugador;
import SistemaDeSoporte.Handler;
import java.awt.Color;
import java.awt.Graphics;
import mariotest.Juego;

/**
 * Item Paraguas - Coleccionable de bonificación
 * 
 * 
 * 
 * @author LENOVO
 */
public class Paraguas extends Item {
    private static final float WIDTH = 16f;
    private static final float HEIGHT = 16f;
    private static final int VALOR_PUNTOS = 600;
    
    public Paraguas(float x, float y, int scale, Handler handler) {
        super(x, y, WIDTH, HEIGHT, scale, handler, VALOR_PUNTOS);
        this.flotar = true;
        this.desapareceDespuesDeRecoger = true;
        cargarSprites();
    }

    private void cargarSprites() {
        try {
            sprites = Juego.getTextura().getParaguasSprites();
            
            if (sprites != null && sprites.length > 0 && sprites[0] != null) {
                System.out.println("[PARAGUAS] Sprite estático cargado correctamente");
            } else {
                System.err.println("[PARAGUAS] ERROR: Array de sprites vacío o nulo.");
                sprites = null;
            }
        } catch (Exception e) {
            System.err.println("[PARAGUAS] Error cargando sprites: " + e.getMessage());
            e.printStackTrace();
            sprites = null;
        }
    }
    
    @Override
    protected void aplicarEfecto(Jugador player) {
        // Solo da puntos (podría dar protección temporal contra caídas)
        System.out.println("[PARAGUAS] ¡Recolectado!");
    }
    
    @Override
    protected void renderPlaceholder(Graphics g) {
        // Paraguas cerrado
        g.setColor(new Color(255, 0, 0)); // Rojo
        
        // Tela del paraguas
        int[] xPoints = {(int)getX() + 8, (int)getX() + 2, (int)getX() + 14};
        int[] yPoints = {(int)getY() + 2, (int)getY() + 6, (int)getY() + 6};
        g.fillPolygon(xPoints, yPoints, 3);
        
        // Mango
        g.setColor(new Color(139, 69, 19));
        g.fillRect((int)getX() + 7, (int)getY() + 6, 2, 8);
        
        // Gancho
        g.drawArc((int)getX() + 5, (int)getY() + 12, 6, 4, 180, 180);
    }
}