package mariotest;

import SistemaDeSoporte.EstadoJuego;
import Entidades.Jugador;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

/**
 * Patrón FACADE - Simplifica el Game Loop
 * Responsabilidad: Orquestar el bucle de actualización y renderizado
 * 
 * Principio: SINGLE RESPONSIBILITY
 * 
 * @author LENOVO
 */
public class FacadeMotorJuego implements Runnable {
    
    // Configuración del loop
    private static final int NANOS_PER_SEC = 1000000000;
    private static final double TARGET_TPS = 60.0;
    private static final int MILLIS_PER_SEC = 1000;
    
    // Componentes
    private final ContextoJuego contexto;
    private final Canvas canvas;
    private final Thread thread;
    
    // Estado del loop
    private volatile boolean running;
    private boolean debug;
    
    // Estadísticas
    private int fps;
    private int tps;
    
    /**
     * Constructor
     */
    public FacadeMotorJuego(ContextoJuego contexto, Canvas canvas) {
        this.contexto = contexto;
        this.canvas = canvas;
        this.thread = new Thread(this, "GameLoop-Thread");
        this.running = false;
        this.debug = false;
        this.fps = 0;
        this.tps = 0;
    }
    
    /**
     * Inicia el game loop
     */
    public synchronized void iniciar() {
        if (running) {
            System.err.println("[LOOP] Ya está corriendo");
            return;
        }
        
        System.out.println("[LOOP] Iniciando game loop...");
        running = true;
        thread.start();
    }
    
    /**
     * Detiene el game loop
     */
    public synchronized void detener() {
        if (!running) {
            return;
        }
        
        System.out.println("[LOOP] Deteniendo game loop...");
        running = false;
        
        try {
            thread.join();
            System.out.println("[LOOP] ✓ Game loop detenido correctamente");
        } catch (InterruptedException e) {
            System.err.println("[LOOP] Error deteniendo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Game loop principal - Fixed timestep a 60 TPS
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double ns = NANOS_PER_SEC / TARGET_TPS;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;
        
        // Dar foco al canvas
        canvas.requestFocus();
        
        System.out.println("[LOOP] ✓ Game loop activo (60 TPS)");
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            // Actualizar lógica a 60 TPS (fixed timestep)
            while (delta >= 1) {
                tick();
                updates++;
                delta--;
            }
            
            // Renderizar (ilimitado, V-Sync lo controla)
            if (running) {
                render();
                frames++;
            }
            
            // Actualizar estadísticas cada segundo
            if (System.currentTimeMillis() - timer > MILLIS_PER_SEC) {
                timer += MILLIS_PER_SEC;
                fps = frames;
                tps = updates;
                
                if (debug) {
                    imprimirStats();
                }
                
                frames = 0;
                updates = 0;
            }
        }
    }
    
    /**
     * Actualiza la lógica del juego
     */
    private void tick() {
        // 1. Actualizar gestor de niveles
        if (contexto.getGestorNiveles() != null) {
            contexto.getGestorNiveles().tick();
        }
        
        // 2. Actualizar handler (solo si el nivel lo permite)
        boolean permitirJuego = contexto.getGestorNiveles() == null 
            || contexto.getGestorNiveles().permitirMovimientoJugador();
        
        if (permitirJuego) {
            contexto.getHandler().tick();
        }
        
        // 3. Actualizar spawners (delegado al gestor de niveles)
        // Ya no se hace aquí, el gestor de niveles lo maneja
        
        // 4. Actualizar gestor de estados
        if (contexto.getGestorEstados() != null) {
            contexto.getGestorEstados().tick();
        }
        
        // 5. Verificar colisiones mortales
        verificarMuerte();
    }
    
/**
 * Renderiza el juego
 */
private void render() {
    BufferStrategy buffer = canvas.getBufferStrategy();
    
    if (buffer == null) {
        canvas.createBufferStrategy(3);
        return;
    }
    
    Graphics g = buffer.getDrawGraphics();
    
    try {
        // Limpiar pantalla
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // ✅ ORDEN CORRECTO DE RENDERIZADO
        
        // 1. Renderizar handler (objetos del juego) SOLO si estamos jugando
        GestorEstados gestor = contexto.getGestorEstados();
        if (gestor != null && gestor.estaJugando()) {
            contexto.getHandler().render(g);
            
            // 2. Renderizar overlay del gestor de niveles (victoria, transición)
            if (contexto.getGestorNiveles() != null) {
                contexto.getGestorNiveles().render(g);
            }
        }
        
        // 3. Renderizar gestor de estados (menús, victoria final) - SIEMPRE ENCIMA
        if (gestor != null) {
            gestor.render(g);
        }
        
        // 4. Debug info
        if (debug) {
            renderDebug(g);
        }
        
    } finally {
        g.dispose();
    }
    
    buffer.show();
}
    /**
     * Verifica condiciones de muerte del jugador
     */
    private void verificarMuerte() {
        Jugador player = contexto.getJugador();
        
        if (player == null || !player.estaVivo()) {
            return;
        }
        
        // Muerte por caída fuera del mapa
        if (player.getY() > canvas.getHeight() + 50) {
            System.out.println("[MUERTE] Jugador cayó fuera del mapa (Y=" + player.getY() + ")");
            player.recibirDanio(null); // null = muerte por caída
        }
    }
    
    /**
     * Renderiza información de debug
     */
    private void renderDebug(Graphics g) {
        g.setColor(Color.GREEN);
        int y = 20;
        
        g.drawString("FPS: " + fps + " | TPS: " + tps, 10, y);
        y += 15;
        
        g.drawString("Objetos: " + contexto.getHandler().getGameObjs().size(), 10, y);
        y += 15;
        
        if (contexto.getGestorNiveles() != null) {
            g.setColor(Color.CYAN);
            g.drawString("Nivel: " + contexto.getGestorNiveles().getNivelActual(), 10, y);
            y += 15;
            
            String estadoNivel = contexto.getGestorNiveles().getEstadoActual()
                .getClass().getSimpleName();
            g.drawString("Estado: " + estadoNivel, 10, y);
            y += 15;
        }
        
        Jugador player = contexto.getJugador();
        if (player != null) {
            g.setColor(Color.YELLOW);
            g.drawString(String.format("Pos: (%.0f, %.0f)", player.getX(), player.getY()), 10, y);
            y += 15;
            
            g.drawString(String.format("Vel: (%.1f, %.1f)", player.getVelX(), player.getVely()), 10, y);
            y += 15;
            
            // Estado de vida
            Color colorEstado = player.estaVivo() ? Color.GREEN : Color.RED;
            g.setColor(colorEstado);
            g.drawString("Estado: " + player.getEstadoVida().getClass().getSimpleName(), 10, y);
        }
    }
    
    /**
     * Imprime estadísticas en consola
     */
    private void imprimirStats() {
        Jugador player = contexto.getJugador();
        
        System.out.println(String.format(
            "[STATS] FPS: %d | TPS: %d | Objetos: %d | Player: (%.0f, %.0f)",
            fps, tps,
            contexto.getHandler().getGameObjs().size(),
            player != null ? player.getX() : 0,
            player != null ? player.getY() : 0
        ));
    }
    
    // ==================== CONTROL ====================
    
    public void toggleDebug() {
        debug = !debug;
        System.out.println("[DEBUG] Modo debug: " + (debug ? " ACTIVADO" : " DESACTIVADO"));
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public int getFPS() {
        return fps;
    }
    
    public int getTPS() {
        return tps;
    }
}