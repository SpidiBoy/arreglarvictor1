package SistemaDeNiveles;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import Entidades.NPCs.DiegoKong;
import Entidades.NPCs.Princesa;
import Entidades.JuegoObjetos;
import Entidades.Jugador;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import mariotest.Juego;

/**
 * Patrón STATE para manejar estados del nivel
 * 
 */
public abstract class EstadoNivel {
    
    protected Juego juego;
    protected GestorNiveles gestorNiveles;
    
    public EstadoNivel(Juego juego, GestorNiveles gestorNiveles) {
        this.juego = juego;
        this.gestorNiveles = gestorNiveles;
    }
    
    
    
    public abstract void entrar();
    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract void salir();
    
    public abstract boolean permitirMovimientoJugador();
    public abstract boolean permitirSpawnEnemigos();
    
    // ==================== ESTADO: JUGANDO ====================
    
    public static class Jugando extends EstadoNivel {
        
        public Jugando(Juego juego, GestorNiveles gestorNiveles) {
            super(juego, gestorNiveles);
        }
        
        @Override
        public void entrar() {
            System.out.println("[ESTADO NIVEL] → JUGANDO");
        }
        
        @Override
        public void tick() {
            if (gestorNiveles.verificarVictoria()) {
                gestorNiveles.cambiarEstado(
                    new Victoria(juego, gestorNiveles)
                );
            }
        }
        
        @Override
        public void render(Graphics g) {
            // Renderizado normal
        }
        
        @Override
        public void salir() {
            System.out.println("[ESTADO NIVEL] JUGANDO → saliendo");
        }
        
        @Override
        public boolean permitirMovimientoJugador() {
            return true;
        }
        
        @Override
        public boolean permitirSpawnEnemigos() {
            return true;
        }
    }
    // ==================== ESTADO: VICTORIA ====================

public static class Victoria extends EstadoNivel {
    
    private int ticksAnimacion;
    private static final int DURACION_ANIMACION_NIVEL_1_2 = 240; // 4 segundos
    private static final int DURACION_ANIMACION_NIVEL_3 = 180;   // 3 segundos (más rápido)
    
    private int faseActual;
    private static final int FASE_CORAZON = 0;
    private static final int FASE_ACCION = 1;
    private static final int FASE_MOVIMIENTO = 2;
    private static final int FASE_FINAL = 3;
    
    private DiegoKong diegoKong;
    private Princesa princesa;
    private TipoVictoria tipoVictoria;
    
    public Victoria(Juego juego, GestorNiveles gestorNiveles) {
        super(juego, gestorNiveles);
        this.ticksAnimacion = 0;
        this.faseActual = FASE_CORAZON;
        
        int nivel = gestorNiveles.getNivelActual();
        this.tipoVictoria = (nivel >= 3) ? TipoVictoria.DERROTA_FINAL : TipoVictoria.ESCAPE_PRINCESA;
        
        System.out.println("[VICTORIA] Tipo: " + tipoVictoria + " (Nivel " + nivel + ")");
    }
    
    @Override
    public void entrar() {
        System.out.println("[ESTADO NIVEL] → VICTORIA (" + tipoVictoria + ")");
        
        gestorNiveles.detenerSpawners();
        
        if (juego.getHandler().getPlayer() != null) {
            juego.getHandler().getPlayer().detenerMovimiento();
        }
        
        obtenerReferenciasEntidades();
        gestorNiveles.iniciarAnimacionVictoria();
    }
    
    private void obtenerReferenciasEntidades() {
        Handler handler = juego.getHandler();
        
        for (JuegoObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.DiegoKong) {
                diegoKong = (DiegoKong) obj;
            } else if (obj.getId() == ObjetosID.Princesa) {
                princesa = (Princesa) obj;
            }
        }
    }
    
    @Override
    public void tick() {
        ticksAnimacion++;
        
        if (diegoKong != null) {
            diegoKong.tick();
        }
        
        if (princesa != null && faseActual >= FASE_MOVIMIENTO) {
            princesa.tick();
        }
        
        // Ejecutar fases de animación
        ejecutarFasesAnimacion();
        
        // Verificar si terminó
        int duracion = (tipoVictoria == TipoVictoria.DERROTA_FINAL) 
            ? DURACION_ANIMACION_NIVEL_3 
            : DURACION_ANIMACION_NIVEL_1_2;
        
        if (ticksAnimacion >= duracion) {
            finalizarVictoria();
        }
    }
    
    private void ejecutarFasesAnimacion() {
        if (tipoVictoria == TipoVictoria.DERROTA_FINAL) {
            // NIVEL 3: Animación de derrota
            if (ticksAnimacion == 20) {
                faseActual = FASE_CORAZON;
            } else if (ticksAnimacion == 60) {
                faseActual = FASE_ACCION;
                if (diegoKong != null) diegoKong.activarModoEnojado();
            } else if (ticksAnimacion == 100) {
                faseActual = FASE_MOVIMIENTO;
                if (princesa != null && juego.getHandler().getPlayer() != null) {
                    Jugador mario = juego.getHandler().getPlayer();
                    princesa.moverHacia(mario.getX() + 20, mario.getY());
                }
            } else if (ticksAnimacion == 140) {
                faseActual = FASE_FINAL;
            }
        } else {
            // NIVELES 1-2: Animación de escape
            if (ticksAnimacion == 30) {
                faseActual = FASE_CORAZON;
            } else if (ticksAnimacion == 90) {
                faseActual = FASE_ACCION;
                if (diegoKong != null && princesa != null) {
                    diegoKong.activarAnimacionAgarrar();
                    princesa.moverHacia(diegoKong.getX() + 10, diegoKong.getY() + 5);
                }
            } else if (ticksAnimacion == 120) {
                faseActual = FASE_MOVIMIENTO;
            } else if (ticksAnimacion == 180) {
                faseActual = FASE_FINAL;
            }
            
            if (faseActual == FASE_MOVIMIENTO && diegoKong != null) {
                diegoKong.setY(diegoKong.getY() - 1.0f);
                if (princesa != null && princesa.isMoviendose()) {
                    princesa.detenerMovimiento();
                    princesa.setX(diegoKong.getX() + 10);
                    princesa.setY(diegoKong.getY() + 5);
                }
            }
        }
    }
    
    private void finalizarVictoria() {
        int nivelActual = gestorNiveles.getNivelActual();
        
        System.out.println("[VICTORIA] Finalizando - Nivel: " + nivelActual);
        
        if (nivelActual >= 3) {
            // ✅ NIVEL 3: Ir directamente a pantalla de victoria
            System.out.println("[VICTORIA] → PantallaVictoria (juego completado)");
            
            // Cambiar estado del gestor
            if (juego.getGestorEstados() != null) {
                juego.getGestorEstados().cambiarEstado(UI.EstadoJuegoEnum.VICTORIA);
            }
        } else {
            // Niveles 1-2: Siguiente nivel
            System.out.println("[VICTORIA] → Siguiente nivel");
            gestorNiveles.cambiarEstado(new Transicion(juego, gestorNiveles));
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Renderizar overlay simple
        renderOverlay(g);
    }
    
    private void renderOverlay(Graphics g) {
        // Overlay semi-transparente
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Juego.getVentanaWidth(), Juego.getVentanaHeight());
        
        int centerX = Juego.getVentanaWidth() / 2;
        int centerY = Juego.getVentanaHeight() / 2;
        
        g.setFont(new Font("Arial", Font.BOLD, 32));
        
        String mensaje = "";
        Color color = Color.YELLOW;
        
        if (tipoVictoria == TipoVictoria.DERROTA_FINAL) {
            // Mensajes nivel 3
            switch (faseActual) {
                case FASE_CORAZON:
                    mensaje = "¡Llegaste hasta la princesa!";
                    color = new Color(255, 105, 180);
                    break;
                case FASE_ACCION:
                    mensaje = "¡Diego Kong ha sido derrotado!";
                    color = new Color(255, 215, 0);
                    break;
                case FASE_MOVIMIENTO:
                    mensaje = "¡La princesa es libre!";
                    color = new Color(0, 255, 127);
                    break;
                case FASE_FINAL:
                    mensaje = "¡VICTORIA TOTAL!";
                    color = new Color(255, 215, 0);
                    
                    // Estrellas de celebración
                    renderEstrellas(g);
                    break;
            }
        } else {
            // Mensajes niveles 1-2
            switch (faseActual) {
                case FASE_CORAZON:
                    mensaje = "¡Llegaste hasta la princesa!";
                    break;
                case FASE_ACCION:
                    mensaje = "¡Diego Kong la agarra!";
                    color = Color.RED;
                    break;
                case FASE_MOVIMIENTO:
                    mensaje = "¡La está llevando!";
                    color = Color.ORANGE;
                    break;
                case FASE_FINAL:
                    mensaje = "¡Ella escapa otra vez!";
                    color = Color.YELLOW;
                    break;
            }
        }
        
        g.setColor(color);
        int w = g.getFontMetrics().stringWidth(mensaje);
        g.drawString(mensaje, centerX - w/2, centerY);
        
        // Sombra
        g.setColor(Color.BLACK);
        g.drawString(mensaje, centerX - w/2 + 2, centerY + 2);
    }
    
    private void renderEstrellas(Graphics g) {
        java.util.Random rand = new java.util.Random(42);
        for (int i = 0; i < 30; i++) {
            if ((ticksAnimacion + i * 5) % 20 < 10) {
                int x = rand.nextInt(Juego.getVentanaWidth());
                int y = rand.nextInt(Juego.getVentanaHeight());
                g.setColor(new Color(255, 215, 0, 200));
                g.fillOval(x, y, 6, 6);
            }
        }
    }
    
    @Override
    public void salir() {
        System.out.println("[ESTADO NIVEL] VICTORIA → saliendo");
    }
    
    @Override
    public boolean permitirMovimientoJugador() {
        return false;
    }
    
    @Override
    public boolean permitirSpawnEnemigos() {
        return false;
    }
}
    // ==================== ESTADO: TRANSICION ====================
    
    public static class Transicion extends EstadoNivel {
        
        private int ticksTransicion;
        private static final int DURACION_FADE = 60;
        private float alphaFade;
        
        public Transicion(Juego juego, GestorNiveles gestorNiveles) {
            super(juego, gestorNiveles);
            this.ticksTransicion = 0;
            this.alphaFade = 0f;
        }
        
        @Override
        public void entrar() {
            System.out.println("[ESTADO NIVEL] → TRANSICION");
        }
        
        @Override
        public void tick() {
            ticksTransicion++;
            alphaFade = Math.min(1f, (float)ticksTransicion / DURACION_FADE);
            
            if (ticksTransicion >= DURACION_FADE) {
                gestorNiveles.cambiarEstado(
                    new CargandoNivel(juego, gestorNiveles)
                );
            }
        }
        
        @Override
        public void render(Graphics g) {
            java.awt.Color colorFade = new java.awt.Color(
                0, 0, 0, (int)(alphaFade * 255)
            );
            g.setColor(colorFade);
            g.fillRect(0, 0, 
                Juego.getVentanaWidth(), 
                Juego.getVentanaHeight()
            );
        }
        
        @Override
        public void salir() {
            System.out.println("[ESTADO NIVEL] TRANSICION → saliendo");
        }
        
        @Override
        public boolean permitirMovimientoJugador() {
            return false;
        }
        
        @Override
        public boolean permitirSpawnEnemigos() {
            return false;
        }
    }
    
    // ==================== ESTADO: CARGANDO NIVEL ====================
    
    public static class CargandoNivel extends EstadoNivel {
        
        public CargandoNivel(Juego juego, GestorNiveles gestorNiveles) {
            super(juego, gestorNiveles);
        }
        
        @Override
        public void entrar() {
            System.out.println("[ESTADO NIVEL] → CARGANDO_NIVEL");
            gestorNiveles.cargarSiguienteNivel();
            gestorNiveles.cambiarEstado(
                new Jugando(juego, gestorNiveles)
            );
        }
        
        @Override
        public void tick() {
            // Se ejecuta solo una vez
        }
        
        @Override
        public void render(Graphics g) {
            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, 
                Juego.getVentanaWidth(), 
                Juego.getVentanaHeight()
            );
            
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
            String texto = "CARGANDO NIVEL...";
            int x = Juego.getVentanaWidth() / 2 - 100;
            int y = Juego.getVentanaHeight() / 2;
            g.drawString(texto, x, y);
        }
        
        @Override
        public void salir() {
            System.out.println("[ESTADO NIVEL] CARGANDO_NIVEL → saliendo");
        }
        
        @Override
        public boolean permitirMovimientoJugador() {
            return false;
        }
        
        @Override
        public boolean permitirSpawnEnemigos() {
            return false;
        }
    }
    
    public static class VictoriaTotal extends EstadoNivel {
    
   private int ticksEspera;
    private static final int DURACION_ESPERA = 120; // 2 segundos
    
    public VictoriaTotal(Juego juego, GestorNiveles gestorNiveles) {
        super(juego, gestorNiveles);
        this.ticksEspera = 0;
    }
    
    @Override
    public void entrar() {
        System.out.println("[ESTADO NIVEL] → VICTORIA TOTAL");
        
        // Detener spawners
        gestorNiveles.detenerSpawners();
        
        // Bonus final de tiempo
        if (juego.getHandler() != null && juego.getHandler().getEstadoJuego() != null) {
            juego.getHandler().getEstadoJuego().bonusTiempo();
        }
        
        System.out.println("[VICTORIA TOTAL] Esperando " + (DURACION_ESPERA / 60) + " segundos...");
    }
    
    @Override
    public void tick() {
        ticksEspera++;
        
        // Debug cada segundo
        if (ticksEspera % 60 == 0) {
            System.out.println("[VICTORIA TOTAL] Tick: " + ticksEspera + " / " + DURACION_ESPERA);
        }
        
        // Después de 2 segundos, cambiar a pantalla de victoria
        if (ticksEspera >= DURACION_ESPERA) {
            System.out.println("[VICTORIA TOTAL] ✅ Cambiando a pantalla de victoria...");
            
            // ✅ Verificar que GestorEstados existe
            if (juego.getGestorEstados() != null) {
                juego.getGestorEstados().cambiarEstado(UI.EstadoJuegoEnum.VICTORIA);
                System.out.println("[VICTORIA TOTAL] ✅ Cambio ejecutado");
            } else {
                System.err.println("[ERROR] GestorEstados es NULL!");
            }
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Overlay de transición
        g.setColor(new java.awt.Color(0, 0, 0, 150));
        g.fillRect(0, 0, mariotest.Juego.getVentanaWidth(), 
                         mariotest.Juego.getVentanaHeight());
        
        // Texto de espera
        g.setColor(java.awt.Color.YELLOW);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 32));
        String texto = "¡JUEGO COMPLETADO!";
        int w = g.getFontMetrics().stringWidth(texto);
        g.drawString(texto, 
                    (mariotest.Juego.getVentanaWidth() - w) / 2, 
                    mariotest.Juego.getVentanaHeight() / 2);
        
        // Debug
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        String debug = "Esperando: " + (ticksEspera / 60) + "s / " + (DURACION_ESPERA / 60) + "s";
        g.drawString(debug, 10, 20);
    }
    
    @Override
    public void salir() {
        System.out.println("[ESTADO NIVEL] VICTORIA TOTAL → saliendo");
    }
    
    @Override
    public boolean permitirMovimientoJugador() {
        return false;
    }
    
    @Override
    public boolean permitirSpawnEnemigos() {
        return false;
    }
    }
}
