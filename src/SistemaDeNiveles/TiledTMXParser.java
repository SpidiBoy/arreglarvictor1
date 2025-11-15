package SistemaDeNiveles;

import SistemaDeSoporte.Handler;
import Entidades.NPCs.DiegoKong;
import Entidades.NPCs.Princesa;
import Entidades.Jugador;
import Entidades.Escenario.Escalera;
import Entidades.Escenario.Tile;
import SistemaDeSoporte.ObjetosID;

import java.awt.Point;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parser completo para archivos TMX (mapa) y TSX (tileset)
 * Lee colisiones desde el TSX y las aplica correctamente
 * 
 * CARACTERÍSTICAS:
 * - Lee TMX (mapa principal)
 * - Lee TSX externo (tileset con colisiones)
 * - Detecta tiles con colisión desde <objectgroup>
 * - Solo crea bloques sólidos donde hay colisión definida
 * - Maneja escaleras, spawns y objetos especiales
 * 
 * @author LENOVO
 */
public class TiledTMXParser {
    
    private Handler handler;
    private java.awt.Point playerSpawnPoint;
    // Configuración del mapa
    public static final int TILE_SIZE = 8;
    public static final int ESCALA_VISUAL = 3;
    public static final int TILE_RENDER_SIZE = TILE_SIZE * ESCALA_VISUAL;
    
    // Dimensiones del mapa
    private int mapaAncho;
    private int mapaAlto;
    private int tileWidth;
    private int tileHeight;
    
    // Mapeo de tiles con colisión (clave = tileID global, valor = tiene colisión)
    private Map<Integer, Boolean> tilesConColision;
    
    // NUEVO: Mapeo de tiles especiales
    private Map<Integer, String> tilesEspeciales; // tileID → tipo ("escalera", "escalera_rota", etc.)
    
    // Mapeo de firstgid por tileset
    private Map<String, Integer> tilesetFirstGids;
    
    // IDs de tiles especiales (DEPRECADO - ahora se usa tilesEspeciales)
     private static final int TILE_ESCALERA = 2;
     private static final int TILE_SPAWN_MARIO = 3;
    
    // Estructuras de datos
    private List<Point> barrilSpawns;
    private List<Point> escalerasPos;
    private Point posicionInicioDK;
    private Point posicionPrincesa;
    private boolean[][] tilesSolidos;
    
    public TiledTMXParser(Handler handler) {
        this.handler = handler;
        this.barrilSpawns = new ArrayList<>();
        this.escalerasPos = new ArrayList<>();
        this.tilesConColision = new HashMap<>();
        this.tilesetFirstGids = new HashMap<>();
    }
    
    /**
     * Carga un mapa TMX desde recursos
     * 
     * @param rutaArchivo Ruta al TMX (ej: "/Imagenes/test1.tmx")
     */
    public void cargarMapa(String rutaArchivo) {
        try {
            System.out.println("\n========================================");
            System.out.println("  CARGANDO MAPA TILED");
            System.out.println("========================================");
            System.out.println("[TMX] Archivo: " + rutaArchivo);
            
            // Cargar y parsear el XML del TMX
            InputStream is = getClass().getResourceAsStream(rutaArchivo);
            if (is == null) {
                throw new Exception("No se encontró el archivo: " + rutaArchivo);
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            
            // Parsear el mapa
            parsearMapa(doc);
            
            System.out.println("\n========================================");
            System.out.println("  MAPA CARGADO EXITOSAMENTE");
            System.out.println("========================================");
            System.out.println("[STATS] Dimensiones: " + mapaAncho + "x" + mapaAlto);
            System.out.println("[STATS] Escaleras: " + escalerasPos.size());
            System.out.println("[STATS] Spawns barriles: " + barrilSpawns.size());
            System.out.println("[STATS] Tiles con colisión: " + contarTilesConColision());
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo cargar el TMX: " + e.getMessage());
            e.printStackTrace();
            crearMapaPorDefecto();
        }
    }
    
    /**
     * Parsea el documento XML del mapa TMX
     */
    private void parsearMapa(Document doc) {
        // Obtener el elemento raíz <map>
        Element mapElement = doc.getDocumentElement();
        
        // Leer dimensiones del mapa
        mapaAncho = Integer.parseInt(mapElement.getAttribute("width"));
        mapaAlto = Integer.parseInt(mapElement.getAttribute("height"));
        tileWidth = Integer.parseInt(mapElement.getAttribute("tilewidth"));
        tileHeight = Integer.parseInt(mapElement.getAttribute("tileheight"));
        
        tilesSolidos = new boolean[mapaAncho][mapaAlto];
        
        System.out.println("[TMX] Dimensiones: " + mapaAncho + "x" + mapaAlto + " tiles");
        System.out.println("[TMX] Tamaño tile: " + tileWidth + "x" + tileHeight + " px");
        
        // PASO 1: Parsear tilesets (CRÍTICO - debe ir primero)
        System.out.println("\n[PASO 1] Parseando tilesets...");
        parsearTilesets(doc);
        
        // PASO 2: Parsear capas de tiles
        System.out.println("\n[PASO 2] Parseando capas de tiles...");
        NodeList capas = doc.getElementsByTagName("layer");
        for (int i = 0; i < capas.getLength(); i++) {
            Node nodo = capas.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element capa = (Element) nodo;
                parsearCapa(capa);
            }
        }
        
        // PASO 3: Parsear capas de objetos (spawns, triggers, etc.)
        System.out.println("\n[PASO 3] Parseando capas de objetos...");
        NodeList capasObjetos = doc.getElementsByTagName("objectgroup");
        for (int i = 0; i < capasObjetos.getLength(); i++) {
            Node nodo = capasObjetos.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element capaObjetos = (Element) nodo;
                parsearCapaObjetos(capaObjetos);
            }
        }
    }
    
    /**
     * Parsea los tilesets y carga los TSX externos
     * CRÍTICO: Detecta colisiones desde los archivos .tsx
     */
    private void parsearTilesets(Document doc) {
        NodeList tilesets = doc.getElementsByTagName("tileset");
        
        for (int i = 0; i < tilesets.getLength(); i++) {
            Node nodo = tilesets.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element tileset = (Element) nodo;
                
                int firstGid = Integer.parseInt(tileset.getAttribute("firstgid"));
                String source = tileset.getAttribute("source");
                
                System.out.println("[TILESET] firstgid=" + firstGid + ", source=" + source);
                
                // Si el tileset es externo (.tsx), cargarlo
                if (source != null && !source.isEmpty()) {
                    cargarTSXExterno(source, firstGid);
                }
            }
        }
    }
    
    /**
     * Carga un archivo TSX externo y detecta colisiones
     * 
     * @param rutaTSX Ruta relativa al TSX (ej: "bloques21.tsx")
     * @param firstGid Primer GID del tileset
     */
    private void cargarTSXExterno(String rutaTSX, int firstGid) {
        try {
            System.out.println("[TSX] Cargando: " + rutaTSX);
            
            // Construir ruta completa (asumiendo que está en /Imagenes/)
            String rutaCompleta = "/Imagenes/" + rutaTSX;
            
            InputStream is = getClass().getResourceAsStream(rutaCompleta);
            if (is == null) {
                System.err.println("[ERROR] No se encontro TSX: " + rutaCompleta);
                return;
            }
            
            // Parsear el TSX
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document tsxDoc = builder.parse(is);
            tsxDoc.getDocumentElement().normalize();
            
            Element tilesetElement = tsxDoc.getDocumentElement();
            
            // Leer información del tileset
            String nombre = tilesetElement.getAttribute("name");
            int tilecount = Integer.parseInt(tilesetElement.getAttribute("tilecount"));
            
            System.out.println("[TSX] Nombre: " + nombre);
            System.out.println("[TSX] Total tiles: " + tilecount);
            System.out.println("[TSX] firstgid: " + firstGid);
            
            // Guardar firstgid para este tileset
            tilesetFirstGids.put(nombre, firstGid);
            
            // DETECTAR COLISIONES: Buscar todos los <tile> con <objectgroup>
            NodeList tiles = tsxDoc.getElementsByTagName("tile");
            int tilesConColisionEncontrados = 0;
            
            for (int i = 0; i < tiles.getLength(); i++) {
                Node nodo = tiles.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element tile = (Element) nodo;
                    
                    // ID local del tile (dentro del tileset)
                    int localId = Integer.parseInt(tile.getAttribute("id"));
                    
                    // ID global del tile (sumando firstgid)
                    int globalId = firstGid + localId;
                    
                    // Buscar si tiene <objectgroup> (indica colisión)
                    NodeList objectgroups = tile.getElementsByTagName("objectgroup");
                    
                    if (objectgroups.getLength() > 0) {
                        // Este tile tiene colisión
                        tilesConColision.put(globalId, true);
                        tilesConColisionEncontrados++;
                        
                        System.out.println("[TSX] Tile con colisión: localID=" + localId + 
                                         " → globalID=" + globalId);
                    }
                }
            }
            
            System.out.println("[TSX] Tiles con colisión encontrados: " + tilesConColisionEncontrados);
            
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar TSX '" + rutaTSX + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Parsea una capa de tiles
     */
    private void parsearCapa(Element capa) {
        String nombreCapa = capa.getAttribute("name");
        System.out.println("[CAPA] Procesando: " + nombreCapa);
        
        // Obtener los datos de la capa
        NodeList dataNodes = capa.getElementsByTagName("data");
        if (dataNodes.getLength() == 0) return;
        
        Element dataElement = (Element) dataNodes.item(0);
        String encoding = dataElement.getAttribute("encoding");
        
        if (encoding.equals("csv")) {
            parsearCapaCSV(dataElement, nombreCapa);
        } else {
            System.err.println("[ERROR] Solo se soporta encoding='csv'");
        }
    }
    
    /**
     * Parsea una capa con encoding CSV
     * CRÍTICO: Crea bloques sólidos O tiles visuales según colisión del TSX
     */
   private void parsearCapaCSV(Element dataElement , String nombreCapa) {
        String csvData = dataElement.getTextContent().trim();
        String[] valores = csvData.split(",");
        
        int bloquesCreados = 0;
        int tilesVisualesCreados = 0;
        int index = 0;
        
        boolean estaCapaEsFondo = nombreCapa.toLowerCase().contains("fondo");
        if (estaCapaEsFondo) {
        System.out.println("[CSV] Detectada capa de fondo: " + nombreCapa);
        }
        
        for (int y = 0; y < mapaAlto; y++) {
        for (int x = 0; x < mapaAncho; x++) {
            int tileID = Integer.parseInt(valores[index].trim());
            index++;
            
            if (tileID == 0) continue;
            
            int worldX = x * TILE_RENDER_SIZE;
            int worldY = y * TILE_RENDER_SIZE;
            
            // Verificar si tiene colisión
            boolean tieneColision = tilesConColision.getOrDefault(tileID, false);
            
            // ✅ CREAR TILE UNIFICADO
            crearTile(worldX, worldY, tileID, tieneColision, estaCapaEsFondo);
            
            procesarTileEspecial(tileID, worldX, worldY);
        }
    }
    
    System.out.println("[CSV] Tiles creados: " + 
                      "(sólidos + decorativos = total)");
}
   
   private void crearTile(int x, int y, int tileID, boolean tieneColision , boolean esFondo) {
    Tile tile = Tile.crear(
        x, y, 
        TILE_RENDER_SIZE, TILE_RENDER_SIZE, 
        1, 
        tileID, 
        tieneColision, 
        esFondo  // esFondo = false (primer plano por defecto)
    );
    handler.addObj(tile);
}
    
    /**
     * Procesa tiles especiales (escaleras, spawns, etc.)
     */
    private void procesarTileEspecial(int tileID, int worldX, int worldY) {
        // Escalera (ajusta el ID según tu tileset)
        
        if (tileID == TILE_ESCALERA) {
            crearEscalera(worldX, worldY, false);
            escalerasPos.add(new Point(worldX / TILE_RENDER_SIZE, worldY / TILE_RENDER_SIZE));
        }
        
        // Spawn de Mario (ajusta el ID según tu tileset)
        if (tileID == TILE_SPAWN_MARIO) {
            posicionarMario(worldX, worldY);
        }
    }
    
    /**
     * Parsea una capa de objetos (spawns, triggers, etc.)
     */
    private void parsearCapaObjetos(Element capaObjetos) {
        String nombreCapa = capaObjetos.getAttribute("name");
        System.out.println("[OBJETOS] Procesando capa: " + nombreCapa);
        
        NodeList objetos = capaObjetos.getElementsByTagName("object");
        
        for (int i = 0; i < objetos.getLength(); i++) {
            Node nodo = objetos.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element objeto = (Element) nodo;
                
                String tipo = objeto.getAttribute("type");
                float x = Float.parseFloat(objeto.getAttribute("x")) * ESCALA_VISUAL;
                float y = Float.parseFloat(objeto.getAttribute("y")) * ESCALA_VISUAL;
                
                // Procesar según el tipo
                switch (tipo.toLowerCase()) {
                    case "spawn_barril":
                        barrilSpawns.add(new Point((int)x, (int)y));
                        System.out.println("[OBJETO] Spawn barril: (" + x + ", " + y + ")");
                        break;
                        
                    case "spawn_mario":
                        posicionarMario((int)x, (int)y);
                        break;
                        
                    case "spawn_dk":
                        crearDiegoKong((int)x, (int)y);
                        break;
                        
                    case "princesa":
                        posicionPrincesa = new Point((int)x, (int)y);
                        crearPrincesa((int)x, (int)y);
                        break;
                }
            }
        }
    }
    
    
    /**
     * Crea una escalera
     */
    private void crearEscalera(int x, int y, boolean esRota) {
        Escalera escalera = new Escalera(x, y, TILE_RENDER_SIZE, TILE_RENDER_SIZE, esRota);
        handler.addObj(escalera);
    }
    
    /**
     * Posiciona a Mario
     */
   private void posicionarMario(int x, int y) {
        Jugador player = handler.getPlayer();
        if (player != null) {
            // ✅ ESTABLECER POSICIÓN INICIAL
            player.setX(x);
            player.setY(y - TILE_RENDER_SIZE);
            
            // ✅ ESTABLECER PUNTO DE SPAWN (CRÍTICO PARA RESPAWN)
            player.setPuntoSpawn(x, y - TILE_RENDER_SIZE);
            
            System.out.println("[SPAWN]  Mario posicionado en: (" + x + ", " + (y - TILE_RENDER_SIZE) + ")");
            System.out.println("[SPAWN]  Punto de respawn establecido");
        } else {
            System.err.println("[ERROR] No se pudo posicionar a Mario - player es null");
        }
    }
    
    /**
     * Crea a Diego Kong
     */
    private void crearDiegoKong(int x, int y) {
        DiegoKong dk = new DiegoKong(x, y - TILE_RENDER_SIZE, 2, handler);
        handler.addObj(dk);
        posicionInicioDK = new Point(x, y);
        System.out.println("[SPAWN] Diego Kong creado en: (" + x + ", " + y + ")");
    }
    
    /**
     * Crea a la Princesa
     */
    private void crearPrincesa(int x, int y) {
        Princesa princesa = new Princesa(x, y - TILE_RENDER_SIZE, 2, handler);
        handler.addObj(princesa);
        System.out.println("[SPAWN] Princesa creada en: (" + x + ", " + y + ")");
    }
    
    /**
     * Crea un mapa por defecto si falla la carga
     */
    private void crearMapaPorDefecto() {
        System.out.println("[DEFAULT] Creando mapa por defecto...");
        
        mapaAncho = 32;
        mapaAlto = 30;
        tilesSolidos = new boolean[mapaAncho][mapaAlto];
        
        // Plataforma base
        for (int i = 0; i < mapaAncho; i++) {
            int x = i * TILE_RENDER_SIZE;
            int y = (mapaAlto - 1) * TILE_RENDER_SIZE;
            crearTile(x, y, 1,true,false);
            tilesSolidos[i][mapaAlto - 1] = true;
        }
        
        if (handler.getPlayer() != null) {
            handler.getPlayer().setX(100);
            handler.getPlayer().setY(400);
        }
    }
    
    /**
     * Cuenta cuántos tiles tienen colisión
     */
    private int contarTilesConColision() {
        int count = 0;
        for (int y = 0; y < mapaAlto; y++) {
            for (int x = 0; x < mapaAncho; x++) {
                if (tilesSolidos[x][y]) count++;
            }
        }
        return count;
    }
    
    // ==================== SISTEMA DE COLISIONES ====================
    
    public boolean esTileSolido(int worldX, int worldY) {
        int tileX = worldX / TILE_RENDER_SIZE;
        int tileY = worldY / TILE_RENDER_SIZE;
        
        if (tileX < 0 || tileX >= mapaAncho || tileY < 0 || tileY >= mapaAlto) {
            return false;
        }
        
        return tilesSolidos[tileX][tileY];
    }
    
    public List<Point> getTilesSolidosEnArea(int x, int y, int width, int height) {
        List<Point> tiles = new ArrayList<>();
        
        int startTileX = Math.max(0, x / TILE_RENDER_SIZE);
        int endTileX = Math.min(mapaAncho - 1, (x + width) / TILE_RENDER_SIZE);
        int startTileY = Math.max(0, y / TILE_RENDER_SIZE);
        int endTileY = Math.min(mapaAlto - 1, (y + height) / TILE_RENDER_SIZE);
        
        for (int ty = startTileY; ty <= endTileY; ty++) {
            for (int tx = startTileX; tx <= endTileX; tx++) {
                if (tilesSolidos[tx][ty]) {
                    tiles.add(new Point(tx * TILE_RENDER_SIZE, ty * TILE_RENDER_SIZE));
                }
            }
        }
        
        return tiles;
    }
    
    private void procesarObjetosSpawn(org.w3c.dom.Document doc) {
    try {
        // Buscar capa de objetos "Spawns" o "Objetos"
        org.w3c.dom.NodeList capasObjetos = doc.getElementsByTagName("objectgroup");
        
        for (int i = 0; i < capasObjetos.getLength(); i++) {
            org.w3c.dom.Element capa = (org.w3c.dom.Element) capasObjetos.item(i);
            String nombreCapa = capa.getAttribute("name");
            
            // Solo procesar capa de spawns
            if (!nombreCapa.equalsIgnoreCase("spawns") && 
                !nombreCapa.equalsIgnoreCase("objetos")) {
                continue;
            }
            
            // Procesar objetos dentro de la capa
            org.w3c.dom.NodeList objetos = capa.getElementsByTagName("object");
            
            for (int j = 0; j < objetos.getLength(); j++) {
                org.w3c.dom.Element objeto = (org.w3c.dom.Element) objetos.item(j);
                
                String tipo = objeto.getAttribute("type");
                String nombre = objeto.getAttribute("name");
                
                int x = (int)(Float.parseFloat(objeto.getAttribute("x")) * ESCALA_VISUAL);
                int y = (int)(Float.parseFloat(objeto.getAttribute("y")) * ESCALA_VISUAL);
                
                // Detectar spawn point del jugador
                if (tipo.equalsIgnoreCase("PlayerSpawn") || 
                    nombre.equalsIgnoreCase("PlayerSpawn")) {
                    
                    playerSpawnPoint = new java.awt.Point(x, y);
                    System.out.println("[TMX] Player spawn detectado: (" + x + ", " + y + ")");
                    
                    // Reposicionar jugador si ya existe
                    if (handler.getPlayer() != null) {
                        handler.getPlayer().setX(x);
                        handler.getPlayer().setY(y);
                        handler.getPlayer().setPuntoSpawn(x, y);
                    }
                }
            }
        }
        
    } catch (Exception e) {
        System.err.println("[ERROR] Fallo procesando spawns: " + e.getMessage());
    }
}
    
    // ==================== GETTERS ====================
    
    public List<Point> getBarrilSpawns() {
        return new ArrayList<>(barrilSpawns);
    }
    
    public List<Point> getEscalerasPos() {
        return new ArrayList<>(escalerasPos);
    }
    
    public Point getPosicionInicioDK() {
        return posicionInicioDK;
    }
    
    public Point getPosicionPrincesa() {
        return posicionPrincesa;
    }
    
    public int getMapaAncho() {
        return mapaAncho;
    }
    
    public int getMapaAlto() {
        return mapaAlto;
    }
    
    public int getMapaAnchoPixels() {
        return mapaAncho * TILE_RENDER_SIZE;
    }
    
    public int getMapaAltoPixels() {
        return mapaAlto * TILE_RENDER_SIZE;
    }
    
    public boolean[][] getTilesSolidos() {
        return tilesSolidos;
    }
    
    public java.awt.Point getPlayerSpawnPoint() {
    return playerSpawnPoint;
}
    
    public String getInfoMapa() {
        return String.format(
            "Mapa TMX: %dx%d tiles (%dx%d px) | Tile: %dx%d | Escala: %dx | Colisiones: %d",
            mapaAncho, mapaAlto,
            getMapaAnchoPixels(), getMapaAltoPixels(),
            tileWidth, tileHeight,
            ESCALA_VISUAL,
            contarTilesConColision()
        );
    }
}