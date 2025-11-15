package SistemaDeSoporte;
import java.util.concurrent.CopyOnWriteArrayList;
import Entidades.JuegoObjetos;
import Entidades.Jugador;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handler mejorado con sistema de capas de renderizado
 * 
 * CAPAS DE RENDERIZADO (de atrás hacia adelante):
 * 1. FONDO      - TileVisual con esFondo=true, decoraciones lejanas
 * 2. BLOQUES    - Bloques sólidos, escaleras, plataformas
 * 3. ENTIDADES  - Jugador, enemigos, barriles, NPCs
 * 4. EFECTOS    - Explosiones, partículas, efectos visuales
 * 
 * @author LENOVO
 */
public class Handler {
    // Lista principal de objetos
    private List<JuegoObjetos> gameobjs;
    private Jugador player;
    private EstadoJuego estadoJuego;

    
    public Handler(){
        gameobjs = new CopyOnWriteArrayList<JuegoObjetos>();
        this.estadoJuego = EstadoJuego.getInstance();
    }
    
    public void tick(){
        for(JuegoObjetos obj : gameobjs){ 
            obj.tick();
        }
        if (estadoJuego != null) {
        estadoJuego.tick();
    }
    }
    
    /**
     * Renderizado por capas para control correcto del Z-order
     * Orden: Fondo → Bloques → Entidades → Efectos
     */
    public void render(Graphics g){
        // CAPA 1: FONDO (TileVisual marcados como fondo)
        renderCapa(g, ObjetosID.Tile, true);
        
        // CAPA 2: BLOQUES Y ESTRUCTURAS (sólidos y decorativos de primer plano)
        renderBloques(g);
        
        // CAPA 3: ENTIDADES (jugador, enemigos, NPCs)
        renderEntidades(g);
        
        // CAPA 4: EFECTOS (explosiones, partículas)
        renderEfectos(g);
        
        renderItems(g);
        // Renderizar HUD
        if (estadoJuego != null) {
        estadoJuego.renderHUD(g, 
            mariotest.Juego.getVentanaWidth(), 
            mariotest.Juego.getVentanaHeight()
        );
    }
    }
    
    private void renderItems(Graphics g) {
    for (JuegoObjetos obj : new LinkedList<JuegoObjetos>(gameobjs)) {
        if (obj.getId() == ObjetosID.Item) {
            obj.render(g);
        }
    }
}
    
    /**
     * Renderiza una capa específica de TileVisual
     */
    private void renderCapa(Graphics g, ObjetosID id, boolean esFondo) {
        for (JuegoObjetos obj : new LinkedList<JuegoObjetos>(gameobjs)) {
            if (obj.getId() == id) {
                // Para TileVisual, verificar si es fondo o no
                if (id == ObjetosID.Tile) {
                    try {
                        Entidades.Escenario.Tile tile = (Entidades.Escenario.Tile) obj;
                        if (tile.isEsFondo() == esFondo) {
                            obj.render(g);
                        }
                    } catch (ClassCastException e) {
                        // Si falla el cast, renderizar normalmente
                        obj.render(g);
                    }
                } else {
                    obj.render(g);
                }
                
            }
        }
    }
    
    /**
     * Renderiza bloques sólidos y escaleras (CAPA 2)
     */
private void renderBloques(Graphics g) {
    for (JuegoObjetos obj : new LinkedList<JuegoObjetos>(gameobjs)) {
        ObjetosID id = obj.getId();
        
        // ✅ Renderizar tiles (sólidos y decorativos)
        if (id == ObjetosID.Tile) {
            Entidades.Escenario.Tile tile = (Entidades.Escenario.Tile) obj;
            if (!tile.isEsFondo()) {
                obj.render(g);
            }
        }
        
        // Escaleras y pipes (sin cambios)
        if (id == ObjetosID.Pipe || 
            id == ObjetosID.Escalera || 
            id == ObjetosID.EscaleraRota) {
            obj.render(g);
        }
    }
}
    
    /**
     * Renderiza entidades (jugador, enemigos, NPCs) - CAPA 3
     * Esta capa está ENCIMA de bloques y tiles visuales
     */
    private void renderEntidades(Graphics g) {
        for (JuegoObjetos obj : gameobjs) {
            ObjetosID id = obj.getId();
            
            if (id == ObjetosID.Jugador || 
                id == ObjetosID.DiegoKong || 
                id == ObjetosID.Barril || 
                id == ObjetosID.Princesa || 
                id == ObjetosID.Fuego) {
                obj.render(g);
            }
        }
    }
    
    /**
     * Renderiza efectos visuales (CAPA 4)
     * Siempre en primer plano
     */
    private void renderEfectos(Graphics g) {
        for (JuegoObjetos obj : new LinkedList<JuegoObjetos>(gameobjs)) {
            ObjetosID id = obj.getId();
            
            if (id == ObjetosID.Explosion || 
                id == ObjetosID.Particula || 
                id == ObjetosID.Puntos) {
                obj.render(g);
            }
        }
    }
    
    /**
     * Método alternativo: Renderizado simple por orden de adición
     * (Mantener como fallback si el sistema de capas falla)
     */
    public void renderSimple(Graphics g) {
        for(JuegoObjetos obj : new LinkedList<JuegoObjetos>(gameobjs)){
            obj.render(g);
        }
    }
    
    // ==================== MÉTODOS ORIGINALES ====================
    
    public void addObj(JuegoObjetos obj){
        gameobjs.add(obj);
    }
    
    public void removeObj(JuegoObjetos obj){
        gameobjs.remove(obj);
    }
    
    public List<JuegoObjetos> getGameObjs(){
        return gameobjs;
    } 
    
    public int setPlayer(Jugador player){
        if(this.player != null){
            return -1;
        }
      
        addObj(player);
        this.player = player;
        return 0;
    }
    
    public int removePlayer(){
        if(player == null){
            return -1;
        }
        
        removeObj(player);
        this.player = null;
        return 0;
    }
    
    public Jugador getPlayer(){
        return player;
    }
    
    // ==================== MÉTODOS ÚTILES ====================
    
    /**
     * Obtiene todos los objetos de un tipo específico
     */
    public List<JuegoObjetos> getObjetosPorTipo(ObjetosID tipo) {
        List<JuegoObjetos> resultado = new ArrayList<>();
        for (JuegoObjetos obj : gameobjs) {
            if (obj.getId() == tipo) {
                resultado.add(obj);
            }
        }
        return resultado;
    }
    
    /**
     * Cuenta cuántos objetos hay de un tipo
     */
    public int contarObjetosPorTipo(ObjetosID tipo) {
        int count = 0;
        for (JuegoObjetos obj : gameobjs) {
            if (obj.getId() == tipo) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Elimina todos los objetos de un tipo específico
     */
    public void eliminarObjetosPorTipo(ObjetosID tipo) {
        gameobjs.removeIf(obj -> obj.getId() == tipo);
    }
    
    /**
     * Información de debug del handler
     */
    public String getInfoDebug() {
        return String.format(
            "Handler [Total: %d | Jugador: %s | Bloques: %d | Enemigos: %d]",
            gameobjs.size(),
            player != null ? "✓" : "✗",
            contarObjetosPorTipo(ObjetosID.Tile),
            contarObjetosPorTipo(ObjetosID.Barril)
        );
    }
    
    public EstadoJuego getEstadoJuego() {
        return estadoJuego;
    }
}