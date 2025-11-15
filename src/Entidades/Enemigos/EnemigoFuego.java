package Entidades.Enemigos;

import Entidades.JuegoObjetos;
import Entidades.Jugador;
import SistemaGFX.Animacion;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Clase Base Abstracta: EnemigoFuego
 * 
 * PATRÓN: TEMPLATE METHOD
 * Define el esqueleto del algoritmo para enemigos tipo fuego/llama.
 * Las subclases implementan los detalles específicos (sprites, dimensiones).
 * 
 * JERARQUÍA:
 * EnemigoFuego (abstracta)
 *    ├── Fuego (sprites de fuego, 16x16)
 *    └── Llama (sprites de llama, 16x24)
 * 
 * RESPONSABILIDADES:
 * - Física y movimiento común
 * - Colisiones estándar
 * - Sistema de comportamiento (STRATEGY)
 * - Gestión de dirección y velocidad
 * 
 * @author LENOVO
 * @version 2.0 - Refactorizada con Template Method
 */
public abstract class EnemigoFuego extends JuegoObjetos {
    
    // ==================== ENUMERACIÓN DE COMPORTAMIENTOS ====================
    
    /**
     * Patrón STRATEGY: Define los diferentes comportamientos de movimiento
     */
    public enum ComportamientoFuego {
        ESTATICO,       // No se mueve, solo anima
        PATRULLA,       // Se mueve horizontalmente
        RAPIDO,         // Velocidad aumentada
        PERSEGUIDOR,    // Persigue al jugador
        SALTARIN        // Puede saltar (solo Llama)
    }
    
    // ==================== CONSTANTES COMPARTIDAS ====================
    
    protected static final float VELOCIDAD_BASE = 2.0f;
    protected static final float VELOCIDAD_RAPIDA = 3.5f;
    protected static final float VELOCIDAD_CAIDA_MAX = 12f;
    protected static final float GRAVEDAD = 0.5f;
    protected static final float FUERZA_SALTO = -6f;
    
    // Comportamiento de salto (solo para tipos que saltan)
    protected static final int COOLDOWN_SALTO = 120;
    protected static final int PROBABILIDAD_SALTO = 5;
    
    // ==================== ATRIBUTOS COMPARTIDOS ====================
    
    protected Handler handler;
    protected BufferedImage[] sprites;
    protected Animacion animacion;
    
    // Estado de movimiento
    protected ComportamientoFuego comportamiento;
    protected boolean enSuelo = false;
    protected int direccion = 1; // 1 = derecha, -1 = izquierda
    protected float velocidadActual;
    
    // Control de salto (solo para tipos que saltan)
    protected int ticksEnSuelo = 0;
    protected boolean puedeSaltar = true;
    
    // Efectos y control
    protected int ticksVivo = 0;
    protected int ticksAnimacion = 0;
    protected boolean puedeGirar = true;
    
    // ==================== CONSTRUCTOR ====================
    
    /**
     * Constructor protegido (solo accesible por subclases)
     * 
     * @param x Posición X
     * @param y Posición Y
     * @param width Ancho del enemigo
     * @param height Alto del enemigo
     * @param scale Escala de renderizado
     * @param handler Handler del juego
     * @param comportamiento Tipo de comportamiento
     * @param direccion Dirección inicial
     */
    protected EnemigoFuego(float x, float y, float width, float height, int scale,
                           Handler handler, ComportamientoFuego comportamiento, int direccion) {
        super(x, y, ObjetosID.Fuego, width, height, scale);
        this.handler = handler;
        this.comportamiento = comportamiento;
        this.direccion = direccion;
        
        // Configurar velocidad según comportamiento
        configurarVelocidad();
        
        // Establecer velocidad inicial
        if (comportamiento != ComportamientoFuego.ESTATICO) {
            setVelX(velocidadActual * direccion);
        } else {
            setVelX(0);
            setVely(0);
        }
    }
    
    // ==================== MÉTODOS ABSTRACTOS (implementados por subclases) ====================
    
    /**
     * Carga los sprites específicos del enemigo
     * IMPLEMENTACIÓN: Cada subclase carga sus propios sprites
     */
    protected abstract void cargarSprites();
    
    /**
     * Inicializa la animación específica del enemigo
     * IMPLEMENTACIÓN: Cada subclase crea su animación
     */
    protected abstract void inicializarAnimacion();
    
    /**
     * Renderiza el placeholder cuando no hay sprites
     * IMPLEMENTACIÓN: Cada subclase define su forma visual
     */
    protected abstract void renderPlaceholder(Graphics g);
    
    /**
     * Genera efectos visuales específicos (opcional)
     * IMPLEMENTACIÓN: Fuego genera partículas, Llama puede no hacer nada
     */
    protected abstract void generarEfectosVisuales();
    
    /**
     * Retorna el nombre del tipo de enemigo para logging
     */
    protected abstract String getNombreTipo();
    
    /**
     * Define si este tipo puede saltar
     */
    protected abstract boolean puedeSaltar();
    
    // ==================== TEMPLATE METHOD (define el flujo general) ====================
    
    @Override
    public final void tick() {
        ticksVivo++;
        ticksAnimacion++;
        
        // SIEMPRE actualizar animación
        if (animacion != null) {
            animacion.runAnimacion();
        }
        
        // Aplicar física según comportamiento
        switch (comportamiento) {
            case ESTATICO:
                // Solo generar efectos visuales
                generarEfectosVisuales();
                break;
                
            case PATRULLA:
            case RAPIDO:
                tickPatrulla();
                break;
                
            case PERSEGUIDOR:
                tickPerseguidor();
                break;
                
            case SALTARIN:
                if (puedeSaltar()) {
                    tickSaltarin();
                } else {
                    tickPatrulla(); // Fallback si no puede saltar
                }
                break;
        }
        
        // Eliminar si cae fuera del mapa (excepto estáticos)
        if (comportamiento != ComportamientoFuego.ESTATICO && getY() > 1000) {
            destruir();
        }
    }
    
    // ==================== LÓGICA DE COMPORTAMIENTO ====================
    
    /**
     * Configura la velocidad según el comportamiento
     */
    private void configurarVelocidad() {
        switch (comportamiento) {
            case RAPIDO:
            case PERSEGUIDOR:
                velocidadActual = VELOCIDAD_RAPIDA;
                break;
            case ESTATICO:
                velocidadActual = 0;
                break;
            default:
                velocidadActual = VELOCIDAD_BASE;
                break;
        }
    }
    
    /**
     * Lógica para comportamiento PATRULLA (movimiento horizontal simple)
     */
    protected void tickPatrulla() {
        aplicarFisica();
        manejarColisiones();
        generarEfectosVisuales();
        
        // Mantener velocidad constante
        if (Math.abs(getVelX()) < velocidadActual && enSuelo) {
            setVelX(velocidadActual * direccion);
        }
    }
    
    /**
     * Lógica para comportamiento PERSEGUIDOR
     */
    protected void tickPerseguidor() {
        aplicarFisica();
        manejarColisiones();
        generarEfectosVisuales();
        
        Jugador player = handler.getPlayer();
        if (player == null || !enSuelo) return;
        
        // Solo perseguir si el jugador está cerca
        float distanciaX = player.getX() - getX();
        float distanciaY = Math.abs(player.getY() - getY());
        
        // Si está en la misma altura aproximada y cerca
        if (distanciaY < 50 && Math.abs(distanciaX) < 300) {
            int nuevaDireccion = distanciaX > 0 ? 1 : -1;
            
            if (nuevaDireccion != direccion && puedeGirar) {
                direccion = nuevaDireccion;
                setVelX(velocidadActual * direccion);
            }
        }
    }
    
    /**
     * Lógica para comportamiento SALTARIN (solo si puedeSaltar() == true)
     */
    protected void tickSaltarin() {
        aplicarFisica();
        manejarColisiones();
        generarEfectosVisuales();
        
        // Comportamiento en suelo
        if (enSuelo) {
            ticksEnSuelo++;
            
            // Recuperar capacidad de salto
            if (ticksEnSuelo >= COOLDOWN_SALTO) {
                puedeSaltar = true;
            }
            
            // Intentar saltar ocasionalmente
            if (puedeSaltar && Math.random() * 100 < PROBABILIDAD_SALTO) {
                ejecutarSalto();
            }
            
            // Mantener velocidad horizontal
            if (Math.abs(getVelX()) < velocidadActual) {
                setVelX(velocidadActual * direccion);
            }
        }
    }
    
    /**
     * Ejecuta el salto
     */
    protected void ejecutarSalto() {
        if (enSuelo && puedeSaltar) {
            setVely(FUERZA_SALTO);
            enSuelo = false;
            puedeSaltar = false;
            ticksEnSuelo = 0;
            System.out.println("[" + getNombreTipo() + "] ¡Saltó!");
        }
    }
    
    // ==================== FÍSICA ====================
    
    /**
     * Aplica física (gravedad y movimiento)
     */
    protected void aplicarFisica() {
        aplicarGravedad();
        
        // Aplicar movimiento
        setX(getX() + getVelX());
        setY(getY() + getVely());
        
        // Limitar velocidad de caída
        if (getVely() > VELOCIDAD_CAIDA_MAX) {
            setVely(VELOCIDAD_CAIDA_MAX);
        }
    }
    
    @Override
    public void aplicarGravedad() {
        if (comportamiento != ComportamientoFuego.ESTATICO && !enSuelo) {
            setVely(getVely() + GRAVEDAD);
        }
    }
    
    // ==================== COLISIONES ====================
    
    /**
     * Maneja colisiones con bloques y plataformas
     */
    protected void manejarColisiones() {
        enSuelo = false;
        
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.Tile || obj.getId() == ObjetosID.Pipe) {
                
                // Colisión inferior (aterrizar en plataforma)
                if (getBounds().intersects(obj.getBounds())) {
                    setY(obj.getY() - getHeight());
                    setVely(0);
                    enSuelo = true;
                    
                    // Asegurar velocidad horizontal
                    if (Math.abs(getVelX()) < velocidadActual && comportamiento != ComportamientoFuego.ESTATICO) {
                        setVelX(velocidadActual * direccion);
                    }
                }
                
                // Colisión superior
                if (getBoundsTop().intersects(obj.getBounds())) {
                    setY(obj.getY() + obj.getHeight());
                    setVely(0);
                }
                
                // Colisión derecha (girar)
                if (getBoundsRight().intersects(obj.getBounds())) {
                    setX(obj.getX() - getWidth());
                    if (puedeGirar) {
                        direccion = -1;
                        setVelX(velocidadActual * direccion);
                    }
                }
                
                // Colisión izquierda (girar)
                if (getBoundsLeft().intersects(obj.getBounds())) {
                    setX(obj.getX() + obj.getWidth());
                    if (puedeGirar) {
                        direccion = 1;
                        setVelX(velocidadActual * direccion);
                    }
                }
            }
        }
    }
    
    // ==================== RENDERIZADO ====================
    
    @Override
    public final void render(Graphics g) {
        if (sprites != null && sprites[0] != null && animacion != null) {
            // Renderizar animación
            animacion.drawAnimacion(g, 
                (int) getX(), (int) getY(), 
                (int) getWidth(), (int) getHeight()
            );
        } else {
            // Usar placeholder específico de la subclase
            renderPlaceholder(g);
        }
    }
    
    // ==================== HITBOXES ====================
    
    @Override
    public Rectangle getBounds() {
        if (comportamiento == ComportamientoFuego.ESTATICO) {
            // Hitbox completa (trampa fija)
            return new Rectangle(
                (int)(getX() + 3),
                (int)(getY() + 3),
                (int)(getWidth() - 6),
                (int)(getHeight() - 6)
            );
        } else {
            // Hitbox ajustada para móviles
            return new Rectangle(
                (int)(getX() + 3),
                (int)(getY() + getHeight()/2),
                (int)(getWidth() - 6),
                (int)(getHeight()/2)
            );
        }
    }
    
    public Rectangle getBoundsTop() {
        return new Rectangle(
            (int)(getX() + 4),
            (int)getY(),
            (int)(getWidth() - 8),
            (int)(getHeight() / 2)
        );
    }
    
    public Rectangle getBoundsRight() {
        return new Rectangle(
            (int)(getX() + getWidth() - 5),
            (int)(getY() + 5),
            5,
            (int)(getHeight() - 10)
        );
    }
    
    public Rectangle getBoundsLeft() {
        return new Rectangle(
            (int)getX(),
            (int)(getY() + 5),
            5,
            (int)(getHeight() - 10)
        );
    }
    
    // ==================== GESTIÓN ====================
    
    public void destruir() {
        handler.removeObj(this);
        System.out.println("[" + getNombreTipo() + "] Destruido en (" + 
                          (int)getX() + ", " + (int)getY() + ")");
    }
    
    public boolean colisionaConJugador(Jugador player) {
        return getBounds().intersects(player.getBounds());
    }
    
    /**
     * Cambia el comportamiento dinámicamente
     */
    public void setComportamiento(ComportamientoFuego nuevoComportamiento) {
        ComportamientoFuego anterior = this.comportamiento;
        this.comportamiento = nuevoComportamiento;
        
        // Reconfigurar velocidad
        configurarVelocidad();
        
        // Reconfigurar movimiento si cambia de/a ESTATICO
        if (anterior == ComportamientoFuego.ESTATICO && nuevoComportamiento != ComportamientoFuego.ESTATICO) {
            setVelX(velocidadActual * direccion);
        } else if (nuevoComportamiento == ComportamientoFuego.ESTATICO) {
            setVelX(0);
            setVely(0);
        }
        
        System.out.println("[" + getNombreTipo() + "] Comportamiento cambiado: " + 
                          anterior + " → " + nuevoComportamiento);
    }
    
    /**
     * Fuerza un salto (solo si puedeSaltar() == true)
     */
    public void forzarSalto() {
        if (puedeSaltar() && enSuelo) {
            ejecutarSalto();
        }
    }
    
    // ==================== GETTERS Y SETTERS ====================
    
    public ComportamientoFuego getComportamiento() {
        return comportamiento;
    }
    
    public int getDireccion() {
        return direccion;
    }
    
    public void setDireccion(int direccion) {
        this.direccion = direccion;
        if (comportamiento != ComportamientoFuego.ESTATICO) {
            setVelX(velocidadActual * direccion);
        }
    }
    
    public boolean isEnSuelo() {
        return enSuelo;
    }
    
    public void setPuedeGirar(boolean puedeGirar) {
        this.puedeGirar = puedeGirar;
    }
    
    public int getTicksVivo() {
        return ticksVivo;
    }
    
    public boolean isEstatico() {
        return comportamiento == ComportamientoFuego.ESTATICO;
    }
    
    public boolean isPerseguidor() {
        return comportamiento == ComportamientoFuego.PERSEGUIDOR;
    }
    
    public boolean isSaltarin() {
        return comportamiento == ComportamientoFuego.SALTARIN;
    }
}