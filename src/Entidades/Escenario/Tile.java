package Entidades.Escenario;

import Entidades.JuegoObjetos;
import SistemaDeSoporte.ObjetosID;
import mariotest.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * 
 * 
 * @author LENOVO
 */
public class Tile extends JuegoObjetos {
    
    // ==================== ATRIBUTOS ====================
    
    private int tileID;                // ID del tile en Tiled
    private BufferedImage sprite;      // Sprite a renderizar
    private boolean tieneColision;     // ¿Es sólido? (LA DIFERENCIA CLAVE)
    private boolean esFondo;           // ¿Es parte del fondo lejano?
    
    // ==================== CONSTRUCTORES ====================
    
    /**
     * Constructor completo (privado - usar factory methods)
     * 
     * @param x Posición X en el mundo
     * @param y Posición Y en el mundo
     * @param width Ancho del tile
     * @param height Alto del tile
     * @param scale Escala de renderizado
     * @param tileID ID del tile en Tiled
     * @param tieneColision Si el tile es sólido (colisiona)
     * @param esFondo Si el tile es parte del fondo lejano
     */
    protected Tile(int x, int y, int width, int height, int scale, 
                 int tileID, boolean tieneColision, boolean esFondo) {
        super(x, y, ObjetosID.Tile, width, height, scale);
        this.tileID = tileID;
        this.tieneColision = tieneColision;
        this.esFondo = esFondo;
        cargarSprite();
    }
    
    // ==================== FACTORY METHODS ====================
    
    /**
     * Crea un BLOQUE SÓLIDO (con colisión)
     * Equivalente a la antigua clase Bloque
     * 
     * Uso: plataformas, muros, suelos
     */
    public static Tile crearBloque(int x, int y, int width, int height, 
                                   int scale, int tileID) {
        return new Tile(x, y, width, height, scale, tileID, true, false);
        //                                                   ↑ CON colisión
    }
    
    /**
     * Crea un TILE VISUAL (sin colisión)
     * Equivalente a la antigua clase TileVisual
     * 
     * Uso: decoraciones, plantas, nubes
     * 
     * @param esFondo true = fondo lejano, false = primer plano decorativo
     */
    public static Tile crearVisual(int x, int y, int width, int height, 
                                   int scale, int tileID, boolean esFondo) {
        return new Tile(x, y, width, height, scale, tileID, false, esFondo);
        //                                                   ↑ SIN colisión
    }
    
    /**
     * Crea un tile con configuración manual completa
     * Útil para casos especiales
     */
    public static Tile crear(int x, int y, int width, int height, int scale,
                            int tileID, boolean tieneColision, boolean esFondo) {
        return new Tile(x, y, width, height, scale, tileID, tieneColision, esFondo);
    }
    
    // ==================== CARGA DE RECURSOS ====================
    
    /**
     * Carga el sprite correspondiente al tile ID desde Texturas
     */
    private void cargarSprite() {
        this.sprite = Juego.getTextura().getSpritePorID(this.tileID);
        
        // Advertencia solo para tiles con ID válido sin sprite
        if (this.sprite == null && this.tileID > 0 && this.tileID != -1) {
            System.err.println(String.format(
                "[TILE] Advertencia: No se encontró sprite para tileID: %d (%s)",
                this.tileID,
                tieneColision ? "BLOQUE" : "VISUAL"
            ));
        }
    }
    
    // ==================== LÓGICA ====================
    
    @Override
    public void tick() {
        // Los tiles son estáticos (no tienen comportamiento por tick)
    }
    
    // ==================== RENDERIZADO ====================
    
    @Override
    public void render(Graphics g) {
        // Renderizar sprite si existe
        if (sprite != null) {
            g.drawImage(sprite, 
                (int)getX(), (int)getY(), 
                (int)getWidth(), (int)getHeight(), 
                null
            );
        } else {
            // Placeholder solo para bloques con colisión
            renderPlaceholder(g);
        }
    }
    
    /**
     * Renderiza un placeholder cuando no hay sprite disponible
     * Solo se muestra para bloques sólidos (ayuda visual en debug)
     */
    private void renderPlaceholder(Graphics g) {
        // Solo mostrar placeholder para bloques sólidos
        if (!tieneColision || tileID <= 0) {
            return;
        }
        
        // Color según rango de tileID (compatibilidad con código antiguo)
        if (tileID >= 10 && tileID <= 10000) {
            g.setColor(new Color(139, 69, 19)); // Marrón (plataformas)
        } else if (tileID >= 1134 && tileID <= 1141) {
            g.setColor(new Color(255, 0, 0)); // Rojo (bloques especiales)
        } else {
            g.setColor(new Color(100, 100, 100)); // Gris (genérico)
        }
        
        // Dibujar rectángulo relleno
        g.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        
        // Dibujar borde negro
        g.setColor(Color.BLACK);
        g.drawRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        
        // Debug: Mostrar tileID (opcional)
        if (getWidth() >= 16 && getHeight() >= 16) {
            g.setColor(Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 8));
            g.drawString(String.valueOf(tileID), (int)getX() + 2, (int)getY() + 10);
        }
    }
    
    // ==================== COLISIÓN (LA MAGIA) ====================
    
    /**
     * Retorna el área de colisión del tile
     * 
     * COMPORTAMIENTO:
     * - Si tieneColision = true:  Retorna rectángulo real (jugador colisiona)
     * - Si tieneColision = false: Retorna rectángulo vacío (jugador atraviesa)
     * 
     * Esta es LA ÚNICA diferencia entre Bloque y TileVisual
     */
    @Override
    public Rectangle getBounds() {
        if (tieneColision) {
            // Comportamiento de Bloque (CON colisión)
            return new Rectangle(
                (int)getX(), 
                (int)getY(), 
                (int)getWidth(), 
                (int)getHeight()
            );
        } else {
            // Comportamiento de TileVisual (SIN colisión)
            return new Rectangle(0, 0, 0, 0);
        }
    }
    
    /**
     * Obtiene el área visual del tile (sin importar si tiene colisión)
     * Útil para efectos visuales o detección de hover
     */
    public Rectangle getAreaVisual() {
        return new Rectangle(
            (int)getX(), 
            (int)getY(), 
            (int)getWidth(), 
            (int)getHeight()
        );
    }
    
    // ==================== MÉTODOS ÚTILES ====================
    
    /**
     * Cambia si el tile tiene colisión (útil para tiles dinámicos)
     * NOTA: Cambiar colisión en runtime puede causar bugs si no se usa con cuidado
     */
    public void setTieneColision(boolean tieneColision) {
        if (this.tieneColision != tieneColision) {
            this.tieneColision = tieneColision;
            System.out.println(String.format(
                "[TILE] TileID %d cambió colisión: %s → %s",
                tileID,
                !tieneColision ? "SÓLIDO" : "ATRAVESABLE",
                tieneColision ? "SÓLIDO" : "ATRAVESABLE"
            ));
        }
    }
    
    /**
     * Cambia el sprite dinámicamente
     * Útil para animaciones de tiles o cambios de estado
     */
    public void setSprite(BufferedImage nuevoSprite) {
        this.sprite = nuevoSprite;
    }
    
    /**
     * Recarga el sprite desde Texturas
     * Útil después de cambiar nivel
     */
    public void recargarSprite() {
        cargarSprite();
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Verifica si el tile es sólido (tiene colisión)
     */
    public boolean tieneColision() {
        return tieneColision;
    }
    
    /**
     * Verifica si el tile es puramente decorativo
     */
    public boolean esDecorativo() {
        return !tieneColision;
    }
    
    /**
     * Verifica si el tile es parte del fondo lejano
     */
    public boolean isEsFondo() {
        return esFondo;
    }
    
    /**
     * Establece si el tile es parte del fondo lejano
     */
    public void setEsFondo(boolean esFondo) {
        this.esFondo = esFondo;
    }
    
    /**
     * Obtiene el ID del tile
     */
    public int getTileID() {
        return tileID;
    }
    
    /**
     * Obtiene el sprite actual (puede ser null)
     */
    public BufferedImage getSprite() {
        return sprite;
    }
    
    // ==================== DEBUG ====================
    
    /**
     * Información de debug del tile
     */
    public String getInfo() {
        return String.format(
            "Tile[ID: %d, Pos: (%.0f,%.0f), Colisión: %s, Fondo: %s, Sprite: %s]",
            tileID,
            getX(), getY(),
            tieneColision ? "SÍ" : "NO",
            esFondo ? "SÍ" : "NO",
            sprite != null ? "OK" : "NULL"
        );
    }
    
    @Override
    public String toString() {
        return String.format(
            "Tile(tileID=%d, colision=%b, fondo=%b)",
            tileID, tieneColision, esFondo
        );
    }
}