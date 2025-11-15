package Entidades;

import Entidades.EfectosVisuales.PoderMartillo;
import SistemaDeSoporte.*;
import SistemaGFX.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
/**
 * 
 * 
 */
public class Jugador extends JuegoObjetos {
    private static final float WIDTH = 16;
    private static final float HEIGHT = 16;
    
    // DEPENDENCIAS INYECTADAS 
    private final AdministradorEventos eventManager;
    private final Handler handler;
    private final Texturas textura;
    
    // COMPONENTES (Component Pattern) 
    private final ComponenteFisicasJugador physics;
    private final ComponenteEscaleraJugador ladder;
    private final ComponenteColisionJugador collision;
    
    //  ESTADO (State Pattern)
    private EstadoVidaPlayer estadoVida;
    private Point puntoSpawn;
    private boolean invulnerable;
    
    // POWER-UPS 
    private PoderMartillo poderMartillo;
    private boolean tieneMartillo;
    
    // ANIMACIONES
    private Animacion playerCaminaS;
    private Animacion playerSubeEscalera;
    private Animacion playerBajaEscalera;
    private Animacion playerCaminaMartillo;
    private Animacion muerteAnimacion;
    private Animacion currAnimacion;
    
    private BufferedImage[] spriteS;
    private BufferedImage[] spriteMartillo;
    private BufferedImage[] spriteMuerte;
    private boolean adelante = true;
    
    // ==================== CONSTRUCTOR CON INYECCIÓN ====================
    
    /**
     * Constructor con inyección de dependencias
     * Patrón: Dependency Injection
     */
    public Jugador(float x, float y, int scale, Handler handler, AdministradorEventos eventManager) {
        super(x, y, ObjetosID.Jugador, WIDTH, HEIGHT, scale);
        
        // Inyección de dependencias
        this.handler = handler;
        this.eventManager = eventManager;
        this.textura = mariotest.Juego.getTextura();
        
        // Inicializar componentes (Component Pattern)
        this.physics = new ComponenteFisicasJugador(this, handler);
        this.ladder = new ComponenteEscaleraJugador(this, handler);
        this.collision = new ComponenteColisionJugador(this, handler, ladder, physics);
        
        // Inicializar estado
        this.estadoVida = new EstadoVidaPlayer.Vivo(this);
        this.estadoVida.entrar();
        
        this.puntoSpawn = new Point((int)x, (int)y);
        this.invulnerable = false;
        
        // Inicializar power-ups
        this.poderMartillo = new PoderMartillo(this, handler);
        this.tieneMartillo = false;
        
        // Inicializar animaciones
        inicializarAnimaciones();
        
        System.out.println("[PLAYER] Creado con sistema de eventos y componentes");
    }
    
    /**
     * Constructor legacy (compatibilidad)
     */
    public Jugador(float x, float y, int scale, Handler handler) {
        this(x, y, scale, handler, AdministradorEventos.getInstance());
    }
    
    // ==================== INICIALIZACIÓN ====================
    
    private void inicializarAnimaciones() {
        spriteMuerte = textura.getMarioMuerte();
        spriteMartillo = textura.getMarioMartillo();
        spriteS = textura.getMarioS();
        
        playerCaminaS = new Animacion(5, spriteS[1], spriteS[2], spriteS[3]);
        playerSubeEscalera = new Animacion(5, spriteS[5], spriteS[6], spriteS[7], 
                                           spriteS[8], spriteS[9], spriteS[10], spriteS[11]);
        playerBajaEscalera = new Animacion(5, spriteS[5], spriteS[6], spriteS[7], 
                                           spriteS[8], spriteS[9], spriteS[10], spriteS[11]);
        playerCaminaMartillo = new Animacion(5, spriteMartillo[0], spriteMartillo[1],
                                             spriteMartillo[2], spriteMartillo[3],
                                             spriteMartillo[4], spriteMartillo[5]);
        muerteAnimacion = new Animacion(50, spriteMuerte[0], spriteMuerte[1],
                                       spriteMuerte[2], spriteMuerte[3], spriteMuerte[4]);
        
        currAnimacion = playerCaminaS;
    }
    
    // ==================== TICK  ====================
    
    @Override
    public void tick() {
        // Validar estado
        if (estadoVida == null) {
            System.err.println("[ERROR] estadoVida NULL! Reinicializando...");
            estadoVida = new EstadoVidaPlayer.Vivo(this);
            estadoVida.entrar();
        }
        
        // Tick del estado de vida
        estadoVida.tick();
        
        // Si está muerto o muriendo, no ejecutar lógica normal
        if (estadoVida instanceof EstadoVidaPlayer.Muerto || 
            estadoVida instanceof EstadoVidaPlayer.Muriendo) {
            return;
        }
        
        // Tick de componentes
        ladder.tick();
        
        //  APLICAR FÍSICA CORRECTAMENTE
        if (ladder.isEnEscalera()) {
            // En escalera: NO aplicar gravedad
            // La velocidad vertical es controlada por el componente de escalera
        } else {
            // Fuera de escalera: aplicar gravedad
            physics.aplicarGravedad();
        }
        
        // Power-ups
        if (poderMartillo != null) {
            poderMartillo.tick();
            tieneMartillo = poderMartillo.isActivo();
        }
        
        //  MOVIMIENTO (debe ir DESPUÉS de aplicar gravedad)
        physics.tick();
        
        // Límite de seguridad
        if (getY() > 2000) {
            System.err.println("[EMERGENCIA] Player cayó fuera del mapa");
            if (estadoVida instanceof EstadoVidaPlayer.Vivo) {
                recibirDanio(null);
            }
            return;
        }
        
        // COLISIONES (debe ir DESPUÉS del movimiento)
        if (estadoVida.tieneColision()) {
            collision.procesarColisiones();
        }
        
        // Verificar colisiones con enemigos
        verificarColisionesEnemigos();
        
        // Actualizar animación
        actualizarAnimacion();
    }
    
    // ==================== RECIBIR DAÑO (Desacoplado con Observer) ====================
    
    /**
     * Recibe daño - REFACTORIZADO con Observer Pattern
     * 
     * ✅ SRP: Solo gestiona su propio estado
     * ✅ OCP: Listeners externos manejan consecuencias
     * ✅ DIP: No depende de implementaciones concretas
     */
    public void recibirDanio(JuegoObjetos enemigo) {
        // Verificar si puede recibir daño
        if (!estadoVida.puedeRecibirDanio() || invulnerable) {
            return;
        }
        
        System.out.println("[PLAYER] ¡Recibió daño de: " + 
                          (enemigo != null ? enemigo.getId() : "CAÍDA") + "!");
        
        //  EMITIR EVENTO (Notificar a observadores)
        EventoJugadorDañado event = new EventoJugadorDañado(this, enemigo);
        eventManager.firePlayerDamaged(event);
        
        //  Cambiar solo MI estado (SRP)
        cambiarEstadoVida(new EstadoVidaPlayer.Muriendo(this));
        
        // Emitir evento de muerte
        EventoMuerteJugador deathEvent = new EventoMuerteJugador(this, enemigo);
        eventManager.firePlayerDeath(deathEvent);
    }
    
    /**
     * Respawnea al jugador - REFACTORIZADO
     */
    public void respawnear() {
        if (puntoSpawn == null) {
            System.err.println("[ERROR] Punto de spawn no establecido!");
            puntoSpawn = new Point(100, 400);
        }
        
        System.out.println("[PLAYER] Respawneando en (" + 
                          puntoSpawn.x + ", " + puntoSpawn.y + ")");
        
        // Resetear posición y física
        setX(puntoSpawn.x);
        setY(puntoSpawn.y);
        setVelX(0);
        setVely(0);
        physics.setSalto(false);
        
        // Salir de escalera si estaba en una
        if (ladder.isEnEscalera()) {
            ladder.salirEscalera();
        }
        
        // Desactivar martillo
        if (tieneMartillo && poderMartillo != null) {
            poderMartillo.desactivar();
            tieneMartillo = false;
        }
        
        // Resetear animación
        currAnimacion = playerCaminaS;
        adelante = true;
        
        // ✅ EMITIR EVENTO de respawn
        EventoReaparicionJugador event = new EventoReaparicionJugador(
            this, puntoSpawn.x, puntoSpawn.y
        );
        eventManager.firePlayerRespawn(event);
        
        System.out.println("[PLAYER] Respawn completado");
    }
    
    /**
     * Colecta un item - REFACTORIZADO
     */
    public void colectarItem(String itemType, int puntos) {
        System.out.println("[PLAYER] Colectó: " + itemType + " (+" + puntos + " pts)");
        
        // ✅ EMITIR EVENTO (Los listeners manejarán puntos, sonido, etc.)
        EventoJugadorRecogeObjeto event = new EventoJugadorRecogeObjeto(
            this, itemType, puntos
        );
        eventManager.firePlayerCollectItem(event);
    }
    
    // ==================== VERIFICAR COLISIONES ENEMIGOS ====================
    
    private void verificarColisionesEnemigos() {
        // No recibir daño si es invulnerable o no puede recibir daño
        if (!estadoVida.puedeRecibirDanio() || invulnerable) {
            return;
        }
        
        // Si tiene martillo activo, no recibe daño
        if (tieneMartillo && poderMartillo.isActivo()) {
            return;
        }
        
        try {
            for (JuegoObjetos obj : handler.getGameObjs()) {
                if (obj == null) continue;
                
                ObjetosID id = obj.getId();
                
                // Verificar colisión con enemigos
                boolean esEnemigo = (id == ObjetosID.Barril || 
                                    id == ObjetosID.Fuego || 
                                    id == ObjetosID.DiegoKong);
                
                if (esEnemigo && getBounds().intersects(obj.getBounds())) {
                    recibirDanio(obj);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Excepción en verificarColisionesEnemigos: " + e.getMessage());
        }
    }
    
    // ==================== ANIMACIÓN ====================
    
    private void actualizarAnimacion() {
        if (ladder.isEnEscalera()) {
            // En escalera (siempre usa animación normal)
            if (ladder.isSubiendoEscalera()) {
                currAnimacion = playerSubeEscalera;
                currAnimacion.runAnimacion();
            } else if (ladder.isBajandoEscalera()) {
                currAnimacion = playerBajaEscalera;
                currAnimacion.runAnimacion();
            }
            // Si está quieto en escalera, no correr animación
        } else {
            // Fuera de escalera
            
            // SELECCIONAR ANIMACIÓN SEGÚN SI TIENE MARTILLO
            if (tieneMartillo) {
                currAnimacion = playerCaminaMartillo;
            } else {
                currAnimacion = playerCaminaS;
            }
            
            // Ejecutar animación solo si se está moviendo horizontalmente Y NO ESTÁ SALTANDO
            if (getVelX() != 0 && !physics.hasSalto()) {
                currAnimacion.runAnimacion();
            }
        }
    }
    
    // ==================== RENDER ====================
    
    @Override
    public void render(Graphics g) {
        // Si está en estado MUERTO, no renderizar
        if (estadoVida instanceof EstadoVidaPlayer.Muerto) {
            return;
        }
        
        // Si está RESPAWNEANDO y parpadeando, alternar visibilidad
        if (estadoVida instanceof EstadoVidaPlayer.Respawneando) {
            EstadoVidaPlayer.Respawneando respawn = (EstadoVidaPlayer.Respawneando) estadoVida;
            if (respawn.isParpadeando()) {
                return;
            }
        }
        
        // Animación de muerte
        if (estadoVida instanceof EstadoVidaPlayer.Muriendo) {
            renderAnimacionMuerte(g);
            return;
        }
        
        // Renderizado normal
        BufferedImage[] spritesActuales;
        Animacion animacionCaminar;
        int anchoRender, altoRender;
        int xRender, yRender;
        
        if (tieneMartillo && !ladder.isEnEscalera()) {
            spritesActuales = spriteMartillo;
            animacionCaminar = playerCaminaMartillo;
            anchoRender = (int) getWidth() * 2;
            altoRender = (int) getHeight() * 2;
            xRender = (int) getX() - (int) getWidth() / 2;
            yRender = (int) getY() - (int) getHeight();
        } else {
            spritesActuales = spriteS;
            animacionCaminar = currAnimacion;
            anchoRender = (int) getWidth();
            altoRender = (int) getHeight();
            xRender = (int) getX();
            yRender = (int) getY();
        }
        
        if (spritesActuales == null || spritesActuales.length == 0) {
            spritesActuales = spriteS;
            animacionCaminar = currAnimacion;
            anchoRender = (int) getWidth();
            altoRender = (int) getHeight();
            xRender = (int) getX();
            yRender = (int) getY();
        }
        
        // Renderizar según estado
        if (ladder.isEnEscalera()) {
            if (ladder.isSubiendoEscalera() || ladder.isBajandoEscalera()) {
                currAnimacion.drawAnimacion(g, (int) getX(), (int) getY(), 
                                           (int) getWidth(), (int) getHeight());
            } else {
                g.drawImage(spriteS[5], (int) getX(), (int) getY(), 
                           (int) getWidth(), (int) getHeight(), null);
            }
        } else if (physics.hasSalto()) {
            BufferedImage spriteJump = spritesActuales[3];
            if (adelante) {
                g.drawImage(spriteJump, xRender, yRender, anchoRender, altoRender, null);
            } else {
                g.drawImage(spriteJump, xRender + anchoRender, yRender, 
                           -anchoRender, altoRender, null);
            }
        } else if (getVelX() > 0) {
            animacionCaminar.drawAnimacion(g, xRender, yRender, anchoRender, altoRender);
            adelante = true;
        } else if (getVelX() < 0) {
            animacionCaminar.drawAnimacion(g, xRender + anchoRender, yRender, 
                                           -anchoRender, altoRender);
            adelante = false;
        } else {
            BufferedImage spriteReposo = spritesActuales[0];
            if (adelante) {
                g.drawImage(spriteReposo, xRender, yRender, anchoRender, altoRender, null);
            } else {
                g.drawImage(spriteReposo, xRender + anchoRender, yRender, 
                           -anchoRender, altoRender, null);
            }
        }
        
        // Renderizar efecto del martillo
        if (poderMartillo != null && tieneMartillo) {
            poderMartillo.render(g);
        }
        
        // Indicador visual de invulnerabilidad
        if (invulnerable && !(estadoVida instanceof EstadoVidaPlayer.Respawneando)) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillOval((int)getX() - 5, (int)getY() - 5, 
                      (int)getWidth() + 10, (int)getHeight() + 10);
        }
    }
    
    private void renderAnimacionMuerte(Graphics g) {
        muerteAnimacion.runAnimacion();
        
        if (adelante) {
            muerteAnimacion.drawAnimacion(g, 
                (int)getX(), (int)getY(), 
                (int)getWidth(), (int)getHeight()
            );
        } else {
            muerteAnimacion.drawAnimacion(g, 
                (int)(getX() + getWidth()), (int)getY(), 
                (int)-getWidth(), (int)getHeight()
            );
        }
        
        EstadoVidaPlayer.Muriendo muriendo = (EstadoVidaPlayer.Muriendo) estadoVida;
        int frame = muriendo.getFrameActual();
        
        // Frame 0: Flash blanco de impacto
        if (frame == 1) {
            g.setColor(new Color(255, 255, 255, 80));
            g.fillRect((int)getX() - 2, (int)getY() - 2, 
                      (int)getWidth() + 4, (int)getHeight() + 4);
        }
    }
    
    // ==================== POWER-UPS ====================
    
    public void activarMartillo() {
        if (poderMartillo != null) {
            poderMartillo.activar();
            tieneMartillo = true;
            
            // ✅ EMITIR EVENTO
            EventoJugadorPoder event = new EventoJugadorPoder(this, "MARTILLO");
            eventManager.firePlayerPowerUp(event);
            
            System.out.println("[PLAYER] ¡Martillo activado!");
        }
    }

    public void activarMartillo(int duracionSegundos) {
        if (poderMartillo != null) {
            poderMartillo.activar(duracionSegundos * 60);
            tieneMartillo = true;
            
            // ✅ EMITIR EVENTO
            EventoJugadorPoder event = new EventoJugadorPoder(this, "MARTILLO");
            eventManager.firePlayerPowerUp(event);
        }
    }
    
    public void golpearConMartillo() {
        if (poderMartillo != null && tieneMartillo) {
            poderMartillo.golpear();
        }
    }
    
    public boolean tieneMartillo() {
        return tieneMartillo;
    }
    
    public PoderMartillo getPoderMartillo() {
        return poderMartillo;
    }
    
    // ==================== MOVIMIENTO (Delegado a componentes) ====================
    
    public void iniciarSalto() {
        if (!ladder.isEnEscalera()) {
            physics.iniciarSalto();
        }
    }
    
    public void subirEscalera() {
        if (tieneMartillo) return;
        ladder.subirEscalera(physics.getVelocidadEscalera());
    }
    
    public void bajarEscalera() {
        if (tieneMartillo) return;
        ladder.bajarEscalera(physics.getVelocidadEscalera());
    }
    
    public void detenerMovimientoVertical() {
        ladder.detenerMovimientoVertical();
    }
    
    public void moverIzquierda() {
        if (!ladder.isEnEscalera()) {
            physics.moverIzquierda();
        } else {
            ladder.salirEscalera();
            physics.moverIzquierda();
        }
    }
    
    public void moverDerecha() {
        if (!ladder.isEnEscalera()) {
            physics.moverDerecha();
        } else {
            ladder.salirEscalera();
            physics.moverDerecha();
        }
    }
    
    public void detenerMovimiento() {
        if (!ladder.isEnEscalera()) {
            physics.detenerMovimiento();
        }
    }
    
    // ==================== HITBOXES ====================
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(
            (int)(getX() + getWidth() / 4),
            (int)(getY() + getHeight() / 2),
            (int) getWidth() / 2,
            (int) getHeight() / 2
        );
    }
    
    public Rectangle getBoundsTop() {
        return new Rectangle(
            (int) (getX() + getWidth() / 4),
            (int) getY(),
            (int) getWidth() / 2,
            (int) getHeight() / 2
        );
    }
    
    public Rectangle getBoundsRight() {
        return new Rectangle(
            (int) (getX() + getWidth() - 5),
            (int) getY() + 5,
            5,
            (int) getHeight() - 10
        );
    }
    
    public Rectangle getBoundsLeft() {
        return new Rectangle(
            (int) getX(),
            (int) (getY() + 5),
            5,
            (int) (getHeight() - 10)
        );
    }
    
    // ==================== ESTADO DE VIDA ====================
    
    public void cambiarEstadoVida(EstadoVidaPlayer nuevoEstado) {
        if (estadoVida != null) {
            estadoVida.salir();
        }
        estadoVida = nuevoEstado;
        estadoVida.entrar();
    }
    
    public EstadoVidaPlayer getEstadoVida() {
        return estadoVida;
    }
    
    public boolean estaVivo() {
        return estadoVida instanceof EstadoVidaPlayer.Vivo || 
               estadoVida instanceof EstadoVidaPlayer.Respawneando;
    }
    
    public boolean estaMuriendo() {
        return estadoVida instanceof EstadoVidaPlayer.Muriendo;
    }
    
    public boolean estaMuerto() {
        return estadoVida instanceof EstadoVidaPlayer.Muerto;
    }
    
    // ==================== GETTERS/SETTERS ====================
    
    public void setPuntoSpawn(int x, int y) {
        puntoSpawn = new Point(x, y);
        System.out.println("[PLAYER] Nuevo punto de spawn: (" + x + ", " + y + ")");
    }
    
    public Point getPuntoSpawn() {
        return puntoSpawn;
    }
    
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
    
    public boolean isInvulnerable() {
        return invulnerable;
    }
    
    public boolean hasSalto() {
        return physics.hasSalto();
    }
    
    public void setSalto(boolean salto) {
        physics.setSalto(salto);
    }
    
    public boolean isEnEscalera() {
        return ladder.isEnEscalera();
    }
    
    public boolean isSubiendoEscalera() {
        return ladder.isSubiendoEscalera();
    }
    
    public boolean isBajandoEscalera() {
        return ladder.isBajandoEscalera();
    }
    
    @Override
    public void aplicarGravedad() {
        if (!ladder.isEnEscalera()) {
            physics.aplicarGravedad();
        }
    }
}