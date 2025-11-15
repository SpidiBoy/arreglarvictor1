package SistemaDeNiveles;
import Entidades.Enemigos.*;
import SistemaDeNiveles.Configuracion.*;
import SistemaDeSoporte.Handler;
import SistemaSoporte.Spawners.*;
import SistemaDeSoporte.EstadoJuego;
import Entidades.NPCs.*;
import Entidades.Escenario.PlataformaMovil;
import Entidades.Jugador;
import Entidades.Items.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import mariotest.Juego;

/**
 * Gestor de Niveles - Patrón FACTORY + STRATEGY
 * Maneja carga, configuración y transiciones entre niveles
 * 
 * @author LENOVO
 */
public class GestorNiveles {
    
    private Juego juego;
    private Handler handler;
    private EstadoNivel estadoActual;
    
    // Configuración de niveles
    private int nivelActual;
    private static final int NIVEL_INICIAL = 1;
    private static final int NIVEL_MAXIMO = 3;
    
    // Configuración específica por nivel
    private ConfiguracionNivel configActual;
    
    // Referencias a sistemas de spawning
    private BarrilSpawner barrelSpawner;
    private FuegoSpawner fuegoSpawner;
    private ItemSpawner itemSpawner;
    private TiledTMXParser tmxParser;
    
    // Sprites de animación de victoria
    private BufferedImage spriteCorazon;
    private BufferedImage spriteCorazonRoto;
    private BufferedImage[] spritesDKAgarra;
    
    // Referencias a entidades clave
    private DiegoKong diegoKong;
    private Princesa princesa;
    
    // Estado de animación de victoria
    private boolean animacionVictoriaActiva;
    private int frameAnimacionDK;
    
    // CONSTANTE PARA MOVIMIENTO DE VICTORIA
    private static final float VELOCIDAD_ESCAPE_VICTORIA = -1.0f;
    
    /**
     * Constructor
     */
    public GestorNiveles(Juego juego, Handler handler) {
        this.juego = juego;
        this.handler = handler;
        this.nivelActual = NIVEL_INICIAL;
        this.estadoActual = new EstadoNivel.Jugando(juego, this);
        this.tmxParser = new TiledTMXParser(handler);
        this.animacionVictoriaActiva = false;
        
        cargarSpritesVictoria();
        
        System.out.println("[GESTOR] Inicializando nivel inicial...");
        inicializarNivel(NIVEL_INICIAL);
    }
    
    /**
     * Reinicia el juego al nivel 1
     */
    public void reiniciar() {
        this.nivelActual = 0; // Forzar recarga
        EstadoJuego.getInstance().reiniciar(); 
        
        inicializarNivel(3); 
        System.out.println("[GESTOR NIVELES] Reiniciado al Nivel 1.");
    }
    
    /**
     * Carga sprites de animación de victoria
     */
    private void cargarSpritesVictoria() {
        try {
            spriteCorazon = Juego.getTextura().getCorazonSprite();
            spriteCorazonRoto = Juego.getTextura().getCorazonRotoSprite();
            spritesDKAgarra = Juego.getTextura().getDKAgarraSprites();
            
            System.out.println("[GESTOR] Sprites de victoria cargados");
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar sprites de victoria: " + e.getMessage());
        }
    }
    
    /**
     * Inicializa el nivel actual
     */
    public void inicializarNivel(int nivel) {
        System.out.println("\n========================================");
        System.out.println("  INICIANDO NIVEL " + nivel);
        System.out.println("========================================");
        
        this.nivelActual = nivel;
        
        // Actualizar el estado global del juego
        EstadoJuego.getInstance().setNivelActual(nivel);
        
        // Cambiar texturas al nivel correspondiente
        Juego.getTextura().cambiarNivel(nivel);
        
        // Limpiar nivel anterior
        limpiarNivel();
        
        // Cargar configuración del nivel
        configActual = ConfiguracionNivel.crear(nivel);
        
        // Cargar mapa TMX
        cargarMapa(configActual.getRutaTMX());
        
        // Configurar elementos específicos del nivel
        configurarNivel(configActual);
        
        // Cambiar a estado JUGANDO
        cambiarEstado(new EstadoNivel.Jugando(juego, this));
        
        System.out.println("========================================");
        System.out.println("  NIVEL " + nivel + " CARGADO");
        System.out.println("========================================\n");
    }
    
    /**
     * Limpia todos los objetos del nivel anterior
     */
    private void limpiarNivel() {
        System.out.println("[GESTOR] Limpiando nivel anterior...");
        
        // Detener y limpiar spawners
        detenerSpawners();
        barrelSpawner = null;
        fuegoSpawner = null;
        itemSpawner = null;
        
        // Limpiar todos los objetos excepto el jugador
        Jugador player = handler.getPlayer();
        handler.getGameObjs().clear();
        
        // Re-agregar jugador
        if (player != null) {
            handler.addObj(player);
        }
        
        // Resetear referencias
        diegoKong = null;
        princesa = null;
    }
    
    /**
     * Carga el mapa TMX del nivel
     */
    private void cargarMapa(String rutaTMX) {
        System.out.println("[GESTOR] Cargando mapa: " + rutaTMX);
        tmxParser.cargarMapa(rutaTMX);
    }
    
    /**
     * Configura elementos específicos del nivel
     */
    private void configurarNivel(ConfiguracionNivel config) {
        // Crear Diego Kong
        if (config.tieneDiegoKong()) {
            Point posDK = config.getPosicionDK();
            diegoKong = new DiegoKong(posDK.x, posDK.y, 2, handler);
            handler.addObj(diegoKong);
        }
        
        // Crear Princesa
        if (config.tienePrincesa()) {
            Point posPrincesa = config.getPosicionPrincesa();
            princesa = new Princesa(posPrincesa.x, posPrincesa.y, 2, handler);
            handler.addObj(princesa);
        }
        
        // Configurar spawner de barriles
        if (config.tieneBarriles()) {
            configurarSpawnerBarriles(config);
        }
        
        // Configurar spawner de fuegos
        if (config.tieneFuegos()) {
            configurarSpawnerFuegos(config);
        }
        
        // Martillo garantizado en nivel 3
        if (nivelActual == 3) {
            Martillo martillo = new Martillo(200, 280, 2, handler);
            Martillo martillo2 = new Martillo(350, 150, 2, handler);
            handler.addObj(martillo);
            handler.addObj(martillo2);
        }
        
        // Configurar spawner de items
        if (config.tieneItems()) {
            configurarSpawnerItems(config);
        }
        
        // Crear plataformas móviles específicas del nivel
        if (config.tienePlataformasMoviles()) {
            crearPlataformasMoviles(config);
        }
        
        // Crear llamas estáticas
        if (config.tieneLlamasEstaticas()) {
            crearLlamasEstaticas(config);
        }
    }
    
    /**
     * Configura el spawner de barriles
     */
    private void configurarSpawnerBarriles(ConfiguracionNivel config) {
        List<Point> spawnPoints = config.getBarrilSpawnPoints();
        barrelSpawner = new BarrilSpawner(handler, spawnPoints);
        
        if (config.isBarrilesActivos()) {
            barrelSpawner.activar();
            System.out.println("[GESTOR] Spawner de barriles activado");
        }
    }
    
    /**
     * Configura el spawner de fuegos
     */
    private void configurarSpawnerFuegos(ConfiguracionNivel config) {
        List<Point> spawnPoints = config.getFuegoSpawnPoints();
        fuegoSpawner = new FuegoSpawner(handler, spawnPoints);
        
        // Configurar límite de fuegos
        int maxFuegos = config.getMaxFuegos();
        if (maxFuegos > 0) {
            fuegoSpawner.setMaxFuegos(maxFuegos);
        }
        
        if (config.isFuegosActivos()) {
            fuegoSpawner.activar();
            System.out.println("[GESTOR] Spawner de fuegos activado (Max: " + maxFuegos + ")");
        }
    }
    
    /**
     * Configura el spawner de items
     */
    private void configurarSpawnerItems(ConfiguracionNivel config) {
        List<Point> spawnPoints = config.getItemSpawnPoints();
        itemSpawner = new ItemSpawner(handler, spawnPoints);
        
        if (config.isItemsActivos()) {
            itemSpawner.activar();
            System.out.println("[GESTOR] Spawner de items activado");
        }
    }
    
    /**
     * Crea plataformas móviles según configuración
     */
    private void crearPlataformasMoviles(ConfiguracionNivel config) {
        for (PlataformaConfig pConfig : config.getPlataformasMoviles()) {
            PlataformaMovil plataforma = new PlataformaMovil(
                pConfig.x, pConfig.y,
                pConfig.width, pConfig.height,
                pConfig.scale, pConfig.tileID,
                pConfig.tipo, pConfig.velocidad,
                pConfig.limiteMin, pConfig.limiteMax,
                pConfig.duracionVisible, pConfig.duracionInvisible
            );
            handler.addObj(plataforma);
        }
    }
    
    /**
     * Crea llamas estáticas según configuración
     */
    private void crearLlamasEstaticas(ConfiguracionNivel config) {
        for (Point pos : config.getPosicionesLlamasEstaticas()) {
            Llama llama = Llama.crearEstatica(pos.x, pos.y, 2, handler);
            handler.addObj(llama);
        }
    }
    
    /**
     * Verifica si el jugador llegó a la princesa (victoria)
     */
    public boolean verificarVictoria() {
        if (princesa == null || princesa.isRescatada()) {
            return false;
        }
        
        Jugador player = handler.getPlayer();
        if (player == null) return false;
        
        // Verificar distancia
        float distX = Math.abs(player.getX() - princesa.getX());
        float distY = Math.abs(player.getY() - princesa.getY());
        
        if (distX < 30 && distY < 30) {
            princesa.setRescatada(true);
            return true;
        }
        
        return false;
    }
    
    /**
     * Inicia la animación de victoria
     */
    public void iniciarAnimacionVictoria() {
        animacionVictoriaActiva = true;
        frameAnimacionDK = 0;
        
        System.out.println("[VICTORIA] Iniciando animación de victoria");
    }
    
    /**
     * Muestra sprite de corazón
     */
    public void mostrarCorazon() {
        System.out.println("[VICTORIA] Mostrando corazón");
    }
    
    /**
     * Anima a DK agarrando a la princesa
     */
    public void animarDKAgarraPrincesa() {
        System.out.println("[VICTORIA] DK agarra a la princesa");
        
        if (diegoKong != null && princesa != null) {
            // Activar animación especial de DK
            diegoKong.activarAnimacionAgarrar();
            
            // Mover princesa hacia DK
            float destinoX = diegoKong.getX() + 10;
            float destinoY = diegoKong.getY() + 5;
            princesa.moverHacia(destinoX, destinoY);
            
            System.out.println("[VICTORIA] Princesa moviéndose hacia DK");
        } else {
            System.err.println("[ERROR] DK o Princesa no encontrados para animación");
        }
    }
    
    /**
     * Mueve a DK y la princesa hacia arriba
     */
    public void moverDKYPrincesaHaciaArriba() {
        if (diegoKong != null) {
            // Mover DK hacia arriba
            diegoKong.setY(diegoKong.getY() + VELOCIDAD_ESCAPE_VICTORIA);
            
            // Forzar a la princesa a seguir a DK
            if (princesa != null) {
                // Detener cualquier movimiento automático restante
                if (princesa.isMoviendose()) {
                    princesa.detenerMovimiento();
                }
                
                // Anclar la posición de la princesa a DK
                float anclaX = diegoKong.getX() + 10;
                float anclaY = diegoKong.getY() + 5;
                princesa.setX(anclaX);
                princesa.setY(anclaY);
            }
        }
    }
    
    /**
     * Muestra sprite de corazón roto
     */
    public void mostrarCorazonRoto() {
        System.out.println("[VICTORIA] Mostrando corazón roto");
    }

 
    /**
 * Overlay para escape de princesa (niveles 1-2)
 */
private void renderOverlayEscapePrincesa(Graphics g, int fase, int ticks, int centerX, int centerY) {
    switch (fase) {
        case 0: // FASE_CORAZON
            renderCorazon(g, centerX, centerY, false);
            break;
            
        case 1: // FASE_DK_AGARRA
            renderTextoAnimacion(g, centerX, centerY, "¡DIEGO KONG AGARRA A LA PRINCESA!", Color.RED);
            break;
            
        case 2: // FASE_MOVIMIENTO
            renderTextoAnimacion(g, centerX, centerY, "¡LA ESTÁ LLEVANDO!", Color.ORANGE);
            break;
            
        case 3: // FASE_CORAZON_ROTO
            renderCorazon(g, centerX, centerY, true);
            renderTextoAnimacion(g, centerX, centerY + 60, "¡Ella se escapa otra vez!", Color.YELLOW);
            break;
    }
}

/**
 * Overlay para derrota final (nivel 3)
 */
private void renderOverlayDerrotaFinal(Graphics g, int fase, int ticks, int centerX, int centerY) {
    switch (fase) {
        case 0: // FASE_CORAZON
            renderCorazon(g, centerX, centerY, false);
            break;
            
        case 1: // FASE_DK_DERROTADO
            renderTextoAnimacion(g, centerX, centerY, "¡DIEGO KONG HA SIDO DERROTADO!", new Color(255, 215, 0));
            break;
            
        case 2: // FASE_PRINCESA_LIBRE
            renderTextoAnimacion(g, centerX, centerY, "¡LA PRINCESA ES LIBRE!", new Color(255, 105, 180));
            break;
            
        case 3: // FASE_VICTORIA
            // Corazón COMPLETO (no roto)
            renderCorazon(g, centerX, centerY, false);
            renderTextoAnimacion(g, centerX, centerY + 60, "¡VICTORIA TOTAL!", new Color(0, 255, 127));
            
            // Estrellas de celebración
            renderEstrellasCelebracion(g, ticks);
            break;
    }
}

/**
 * Renderiza estrellas de celebración
 */
private void renderEstrellasCelebracion(Graphics g, int ticks) {
    java.util.Random rand = new java.util.Random(42);
    
    for (int i = 0; i < 20; i++) {
        if ((ticks + i * 5) % 30 < 15) {
            int x = rand.nextInt(Juego.getVentanaWidth());
            int y = rand.nextInt(Juego.getVentanaHeight());
            
            g.setColor(new Color(255, 215, 0, 200));
            g.fillOval(x, y, 8, 8);
            
            g.setColor(Color.WHITE);
            g.fillOval(x + 2, y + 2, 4, 4);
        }
    }
}
    /**
     * Renderiza corazón o corazón roto
     */
    private void renderCorazon(Graphics g, int centerX, int centerY, boolean roto) {
        BufferedImage sprite = roto ? spriteCorazonRoto : spriteCorazon;
        
        if (sprite != null) {
            // Escala 4x para que sea grande y visible
            int size = 64;
            g.drawImage(sprite, centerX - size/2, centerY - size/2, size, size, null);
        } else {
            // Placeholder si no hay sprites
            g.setColor(roto ? new Color(139, 0, 0) : Color.RED);
            g.fillOval(centerX - 30, centerY - 30, 60, 60);
            
            if (roto) {
                g.setColor(Color.BLACK);
                g.drawLine(centerX - 30, centerY - 30, centerX + 30, centerY + 30);
                g.drawLine(centerX + 30, centerY - 30, centerX - 30, centerY + 30);
            }
        }
    }
    
    /**
     * Renderiza texto de animación
     */
    private void renderTextoAnimacion(Graphics g, int centerX, int centerY, String texto, Color color) {
        g.setColor(color);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        
        // Centrar texto
        int textWidth = g.getFontMetrics().stringWidth(texto);
        g.drawString(texto, centerX - textWidth/2, centerY);
        
        // Sombra
        g.setColor(Color.BLACK);
        g.drawString(texto, centerX - textWidth/2 + 2, centerY + 2);
    }
    
    /**
     * Carga el siguiente nivel
     */
    public void cargarSiguienteNivel() {
        int siguienteNivel = nivelActual + 1;
        
        if (siguienteNivel > NIVEL_MAXIMO) {
            System.out.println("[GESTOR] ¡Juego completado!");
            siguienteNivel = NIVEL_INICIAL; // Reiniciar al nivel 1
        }
        
        inicializarNivel(siguienteNivel);
    }
    
    /**
     * Detiene todos los spawners
     */
    public void detenerSpawners() {
        if (barrelSpawner != null) {
            barrelSpawner.desactivar();
        }
        if (fuegoSpawner != null) {
            fuegoSpawner.desactivar();
        }
        if (itemSpawner != null) {
            itemSpawner.desactivar();
        }
    }
    
    /**
     * Cambia el estado del nivel
     */
    public void cambiarEstado(EstadoNivel nuevoEstado) {
        if (estadoActual != null) {
            estadoActual.salir();
        }
        
        estadoActual = nuevoEstado;
        estadoActual.entrar();
    }
    
    /**
     * Actualiza el gestor de niveles
     */
    public void tick() {
        if (estadoActual != null) {
            estadoActual.tick();
        }
        
        // Actualizar spawners solo si el estado lo permite
        if (estadoActual.permitirSpawnEnemigos()) {
            if (barrelSpawner != null) {
                barrelSpawner.tick();
            }
            if (fuegoSpawner != null) {
                fuegoSpawner.tick();
            }
            if (itemSpawner != null) {
                itemSpawner.tick();
            }
        }
    }
    
    /**
     * Renderiza el overlay del estado actual
     */
    public void render(Graphics g) {
        if (estadoActual != null) {
            estadoActual.render(g);
        }
    }
    
    // ==================== GETTERS ====================
    
    public int getNivelActual() {
        return nivelActual;
    }
    
    public EstadoNivel getEstadoActual() {
        return estadoActual;
    }
    
    public boolean permitirMovimientoJugador() {
        return estadoActual != null && estadoActual.permitirMovimientoJugador();
    }
    
    public BarrilSpawner getBarrelSpawner() {
        return barrelSpawner;
    }
    
    public FuegoSpawner getFuegoSpawner() {
        return fuegoSpawner;
    }
    
    public ItemSpawner getItemSpawner() {
        return itemSpawner;
    }
}