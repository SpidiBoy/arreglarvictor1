package Entidades.Escenario;

import SistemaDeSoporte.ObjetosID;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import mariotest.Juego;

/**
 * 
 * 
 * 
 * @author LENOVO
 */
public class PlataformaMovil extends Tile {
    
    // Movimiento
    private float velocidadX;
    private float velocidadY;
    private float limiteIzquierdo;
    private float limiteDerecho;
    private float limiteSuperior;
    private float limiteInferior;
    private TipoMovimiento tipoMovimiento;
    
    // Sistema de aparición/desaparición
    private boolean visible;
    private int ticksVisible;
    private int ticksInvisible;
    private int ticksActuales;
    private int duracionVisible;      // Cuánto tiempo está visible
    private int duracionInvisible;    // Cuánto tiempo está invisible
    
    // Efectos visuales
    private int alphaActual;          // Transparencia para efecto fade
    private boolean fadeOut;          // Si está desapareciendo
    private boolean fadeIn;           // Si está apareciendo
    private static final int FADE_SPEED = 5;
    
    // Configuración
    private boolean cicloActivo;      // Si el ciclo de aparecer/desaparecer está activo
    
    /**
     * Tipos de movimiento para la plataforma
     */
    public enum TipoMovimiento {
        HORIZONTAL,      // Se mueve de izquierda a derecha
        VERTICAL,        // Se mueve de arriba a abajo
        ESTATICA,        // No se mueve (solo aparece/desaparece)
        CIRCULAR         // Movimiento circular (avanzado)
    }
    
    /**
     * Constructor completo
     */
    public PlataformaMovil(int x, int y, int width, int height, int scale, int tileID,
                          TipoMovimiento tipo, float velocidad,
                          float limiteMin, float limiteMax,
                          int duracionVisible, int duracionInvisible) {
        super(x, y, width, height, scale, tileID,true,false);
        
        this.tipoMovimiento = tipo;
        this.duracionVisible = duracionVisible;
        this.duracionInvisible = duracionInvisible;
        
        // Configurar límites según tipo de movimiento
        switch (tipo) {
            case HORIZONTAL:
                this.velocidadX = velocidad;
                this.velocidadY = 0;
                this.limiteIzquierdo = limiteMin;
                this.limiteDerecho = limiteMax;
                break;
                
            case VERTICAL:
                this.velocidadX = 0;
                this.velocidadY = velocidad;
                this.limiteSuperior = limiteMin;
                this.limiteInferior = limiteMax;
                break;
                
            case ESTATICA:
                this.velocidadX = 0;
                this.velocidadY = 0;
                break;
                
            case CIRCULAR:
                // Implementación básica de circular
                this.velocidadX = velocidad;
                this.velocidadY = velocidad;
                break;
        }
        
        // Estado inicial
        this.visible = true;
        this.ticksActuales = 0;
        this.alphaActual = 255;
        this.fadeOut = false;
        this.fadeIn = false;
        this.cicloActivo = true;
        
        System.out.println("[PLATAFORMA MOVIL] Creada en (" + x + ", " + y + ") tipo: " + tipo);
    }
    
    /**
     * Constructor simplificado para plataforma horizontal
     */
    public PlataformaMovil(int x, int y, int width, int height, int scale,
                          float velocidad, float limiteIzq, float limiteDer,
                          int duracionVisible, int duracionInvisible) {
        this(x, y, width, height, scale, 1, TipoMovimiento.HORIZONTAL, 
             velocidad, limiteIzq, limiteDer, duracionVisible, duracionInvisible);
    }
    
    /**
     * Constructor para plataforma estática (solo aparece/desaparece)
     */
    public PlataformaMovil(int x, int y, int width, int height, int scale,
                          int duracionVisible, int duracionInvisible) {
        this(x, y, width, height, scale, 1, TipoMovimiento.ESTATICA, 
             0, 0, 0, duracionVisible, duracionInvisible);
    }
    
    @Override
    public void tick() {
        // Solo procesar si el ciclo está activo
        if (!cicloActivo) {
            return;
        }
        
        // Actualizar ciclo de visibilidad
        actualizarVisibilidad();
        
        // Actualizar movimiento solo si está visible
        if (visible && !fadeOut) {
            actualizarMovimiento();
        }
        
        // Actualizar efectos de fade
        actualizarFade();
    }
    
    /**
     * Actualiza el ciclo de aparición/desaparición
     */
    private void actualizarVisibilidad() {
        ticksActuales++;
        
        if (visible) {
            // Mientras está visible
            ticksVisible++;
            
            // Tiempo de empezar a desaparecer (30 ticks antes)
            if (ticksVisible >= duracionVisible - 30 && !fadeOut) {
                fadeOut = true;
            }
            
            // Tiempo de desaparecer completamente
            if (ticksVisible >= duracionVisible) {
                visible = false;
                fadeOut = false;
                ticksVisible = 0;
                ticksInvisible = 0;
                alphaActual = 0;
            }
        } else {
            // Mientras está invisible
            ticksInvisible++;
            
            // Tiempo de empezar a aparecer (30 ticks antes)
            if (ticksInvisible >= duracionInvisible - 30 && !fadeIn) {
                fadeIn = true;
            }
            
            // Tiempo de aparecer completamente
            if (ticksInvisible >= duracionInvisible) {
                visible = true;
                fadeIn = false;
                ticksInvisible = 0;
                ticksVisible = 0;
                alphaActual = 255;
            }
        }
    }
    
    /**
     * Actualiza el movimiento según el tipo
     */
    private void actualizarMovimiento() {
        switch (tipoMovimiento) {
            case HORIZONTAL:
                moverHorizontal();
                break;
                
            case VERTICAL:
                moverVertical();
                break;
                
            case CIRCULAR:
                moverCircular();
                break;
                
            case ESTATICA:
                // No hacer nada
                break;
        }
    }
    
    /**
     * Movimiento horizontal con rebote en límites
     */
    private void moverHorizontal() {
        float nuevaX = getX() + velocidadX;
        
        // Verificar límites y rebotar
        if (nuevaX <= limiteIzquierdo) {
            nuevaX = limiteIzquierdo;
            velocidadX = Math.abs(velocidadX); // Cambiar dirección a derecha
        } else if (nuevaX + getWidth() >= limiteDerecho) {
            nuevaX = limiteDerecho - getWidth();
            velocidadX = -Math.abs(velocidadX); // Cambiar dirección a izquierda
        }
        
        setX(nuevaX);
    }
    
    /**
     * Movimiento vertical con rebote en límites
     */
    private void moverVertical() {
        float nuevaY = getY() + velocidadY;
        
        // Verificar límites y rebotar
        if (nuevaY <= limiteSuperior) {
            nuevaY = limiteSuperior;
            velocidadY = Math.abs(velocidadY); // Cambiar dirección a abajo
        } else if (nuevaY + getHeight() >= limiteInferior) {
            nuevaY = limiteInferior - getHeight();
            velocidadY = -Math.abs(velocidadY); // Cambiar dirección a arriba
        }
        
        setY(nuevaY);
    }
    
    /**
     * Movimiento circular (implementación básica)
     */
    private void moverCircular() {
        // Implementación simple: movimiento en patrón circular
        float centroX = (limiteIzquierdo + limiteDerecho) / 2;
        float centroY = (limiteSuperior + limiteInferior) / 2;
        float radio = (limiteDerecho - limiteIzquierdo) / 2;
        
        float angulo = (ticksActuales * velocidadX) % 360;
        float radianes = (float) Math.toRadians(angulo);
        
        float nuevaX = centroX + (float) Math.cos(radianes) * radio;
        float nuevaY = centroY + (float) Math.sin(radianes) * radio;
        
        setX(nuevaX);
        setY(nuevaY);
    }
    
    /**
     * Actualiza el efecto de fade in/out
     */
    private void actualizarFade() {
        if (fadeOut) {
            // Desvanecer (aparecer -> desaparecer)
            alphaActual -= FADE_SPEED;
            if (alphaActual < 0) {
                alphaActual = 0;
            }
        } else if (fadeIn) {
            // Aparecer (invisible -> visible)
            alphaActual += FADE_SPEED;
            if (alphaActual > 255) {
                alphaActual = 255;
            }
        }
    }
    
    @Override
    public void render(Graphics g) {
        // No renderizar si está completamente invisible
        if (!visible && !fadeIn) {
            return;
        }
        
        // Renderizar con transparencia
        if (alphaActual < 255) {
            // Crear color con transparencia
            Color colorBase = new Color(139, 69, 19); // Marrón
            Color colorTransparente = new Color(
                colorBase.getRed(),
                colorBase.getGreen(),
                colorBase.getBlue(),
                alphaActual
            );
            
            g.setColor(colorTransparente);
            g.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
            
            // Borde
            if (alphaActual > 100) {
                Color bordeTransparente = new Color(0, 0, 0, alphaActual);
                g.setColor(bordeTransparente);
                g.drawRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
            }
            
            // Indicador de estado (puntos parpadeantes)
            if (fadeOut) {
                // Puntos rojos cuando está desapareciendo
                g.setColor(new Color(255, 0, 0, alphaActual));
                g.fillOval((int)getX() + 2, (int)getY() + 2, 3, 3);
                g.fillOval((int)(getX() + getWidth() - 5), (int)getY() + 2, 3, 3);
            } else if (fadeIn) {
                // Puntos verdes cuando está apareciendo
                g.setColor(new Color(0, 255, 0, alphaActual));
                g.fillOval((int)getX() + 2, (int)getY() + 2, 3, 3);
                g.fillOval((int)(getX() + getWidth() - 5), (int)getY() + 2, 3, 3);
            }
        } else {
            // Renderizado normal (completamente visible)
            super.render(g);
            
            // Indicadores de dirección de movimiento
            g.setColor(Color.CYAN);
            switch (tipoMovimiento) {
                case HORIZONTAL:
                    // Flechas horizontales
                    if (velocidadX > 0) {
                        g.drawString("", (int)(getX() + getWidth() - 8), (int)(getY() + getHeight()/2));
                    } else {
                        g.drawString("", (int)(getX() + 2), (int)(getY() + getHeight()/2));
                    }
                    break;
                    
                case VERTICAL:
                    // Flechas verticales
                    if (velocidadY > 0) {
                        g.drawString("", (int)(getX() + getWidth()/2), (int)(getY() + getHeight() - 2));
                    } else {
                        g.drawString("", (int)(getX() + getWidth()/2), (int)(getY() + 8));
                    }
                    break;
            }
        }
    }
    
    @Override
    public Rectangle getBounds() {
        // Solo tiene colisión si está visible
        if (visible && alphaActual > 100) {
            return super.getBounds();
        } else {
            // Retornar rectángulo vacío (sin colisión)
            return new Rectangle(0, 0, 0, 0);
        }
    }
    
    // ==================== MÉTODOS PÚBLICOS ====================
    
    /**
     * Activa o desactiva el ciclo de aparición/desaparición
     */
    public void setCicloActivo(boolean activo) {
        this.cicloActivo = activo;
        System.out.println("[PLATAFORMA] Ciclo " + (activo ? "activado" : "desactivado"));
    }
    
    /**
     * Fuerza la plataforma a estar visible
     */
    public void forzarVisible() {
        this.visible = true;
        this.alphaActual = 255;
        this.fadeOut = false;
        this.fadeIn = false;
        this.ticksVisible = 0;
    }
    
    /**
     * Fuerza la plataforma a estar invisible
     */
    public void forzarInvisible() {
        this.visible = false;
        this.alphaActual = 0;
        this.fadeOut = false;
        this.fadeIn = false;
        this.ticksInvisible = 0;
    }
    
    /**
     * Reinicia el ciclo desde el principio
     */
    public void reiniciarCiclo() {
        this.ticksActuales = 0;
        this.ticksVisible = 0;
        this.ticksInvisible = 0;
        this.visible = true;
        this.alphaActual = 255;
        this.fadeOut = false;
        this.fadeIn = false;
    }
    
    /**
     * Cambia la velocidad de movimiento
     */
    public void setVelocidad(float velocidad) {
        switch (tipoMovimiento) {
            case HORIZONTAL:
                this.velocidadX = velocidad * (velocidadX < 0 ? -1 : 1);
                break;
            case VERTICAL:
                this.velocidadY = velocidad * (velocidadY < 0 ? -1 : 1);
                break;
            case CIRCULAR:
                this.velocidadX = velocidad;
                this.velocidadY = velocidad;
                break;
        }
    }
    
    /**
     * Cambia los tiempos de visible/invisible
     */
    public void setDuraciones(int visible, int invisible) {
        this.duracionVisible = visible;
        this.duracionInvisible = invisible;
    }
    
    // ==================== GETTERS ====================
    
    public boolean isVisible() {
        return visible;
    }
    
    public boolean isFadeOut() {
        return fadeOut;
    }
    
    public boolean isFadeIn() {
        return fadeIn;
    }
    
    public int getAlphaActual() {
        return alphaActual;
    }
    
    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }
    
    public float getVelocidadX() {
        return velocidadX;
    }
    
    public float getVelocidadY() {
        return velocidadY;
    }
    
    public boolean isCicloActivo() {
        return cicloActivo;
    }
    
    /**
     * Información de debug
     */
    public String getInfo() {
        return String.format(
            "PlataformaMovil[Pos: (%.0f,%.0f), Visible: %s, Alpha: %d, Tipo: %s]",
            getX(), getY(), visible, alphaActual, tipoMovimiento
        );
    }
}