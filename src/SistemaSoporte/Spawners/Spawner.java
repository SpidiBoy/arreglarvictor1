package SistemaSoporte.Spawners;

import Entidades.JuegoObjetos;
import SistemaDeSoporte.Handler;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author LENOVO
 */
public abstract class Spawner <T extends JuegoObjetos > {
    protected final Handler handler;
    protected final List<Point> spawnPoints;
    protected final Random random;
    
    // Control de spawn
    protected int ticksDesdeUltimoSpawn;
    protected int ticksEntreSpawns;
    protected boolean activo;
    
    // Configuración (valores por defecto)
    protected static final int TICKS_MIN_DEFAULT = 120;  // 2 segundos
    protected static final int TICKS_MAX_DEFAULT = 300;  // 5 segundos
    
    protected int ticksMinSpawn;
    protected int ticksMaxSpawn;
    
    // ==================== CONSTRUCTOR ====================
    
    /**
     * Constructor base protegido
     * 
     * @param handler Handler del juego
     * @param spawnPoints Lista de puntos de spawn
     */
    protected Spawner(Handler handler, List<Point> spawnPoints) {
        this.handler = handler;
        this.spawnPoints = new ArrayList<>(spawnPoints);
        this.random = new Random();
        
        this.ticksMinSpawn = TICKS_MIN_DEFAULT;
        this.ticksMaxSpawn = TICKS_MAX_DEFAULT;
        
        this.ticksDesdeUltimoSpawn = 0;
        this.ticksEntreSpawns = generarTiempoAleatorio();
        this.activo = false;
        
        System.out.println("[" + getNombreSpawner() + "] Inicializado con " + 
                          spawnPoints.size() + " spawn points");
    }
        
    /**
     * TEMPLATE METHOD - Define el algoritmo de spawn
     * 
     * Este método NO debe ser sobrescrito.
     * Las subclases implementan los "hooks" específicos.
     */
    public final void tick() {
        if (!activo || spawnPoints.isEmpty()) {
            return;
        }
        
        ticksDesdeUltimoSpawn++;
        
        // Verificar si es momento de spawn
        if (ticksDesdeUltimoSpawn >= ticksEntreSpawns) {
            // Hook method 1: Verificar condiciones específicas
            if (puedeSpawnear()) {
                spawnEntidad();
            }
            
            // Resetear timer
            ticksDesdeUltimoSpawn = 0;
            ticksEntreSpawns = generarTiempoAleatorio();
        }
    }
    
    /**
     * Realiza el spawn de una entidad
     */
    private void spawnEntidad() {
        if (spawnPoints.isEmpty()) {
            return;
        }
        
        // Seleccionar spawn point aleatorio
        Point spawnPoint = seleccionarSpawnPoint();
        
        // Hook method 2: Crear la entidad específica
        T entidad = crearEntidad(spawnPoint);
        
        if (entidad != null) {
            // Agregar al handler
            handler.addObj(entidad);
            
            // Hook method 3: Callback post-spawn
            onEntidadSpawneada(entidad, spawnPoint);
            
            System.out.println("[" + getNombreSpawner() + "] Spawneado en (" + 
                             spawnPoint.x + ", " + spawnPoint.y + ")");
        }
    }
    
    // ==================== MÉTODOS ABSTRACTOS (Hook Methods) ====================
    
    /**
     * Crea la entidad específica del spawner
     * 
     * HOOK METHOD - Implementado por subclases
     * 
     * @param spawnPoint Punto donde se creará la entidad
     * @return Entidad creada o null si falla
     */
    protected abstract T crearEntidad(Point spawnPoint);
    
    /**
     * Nombre del spawner (para logging)
     * 
     * HOOK METHOD - Implementado por subclases
     */
    protected abstract String getNombreSpawner();
    
    // ==================== MÉTODOS CON IMPLEMENTACIÓN POR DEFECTO ====================
    
    /**
     * Verifica si puede spawnear una entidad
     * 
     * HOOK METHOD - Puede ser sobrescrito por subclases
     * Por defecto, siempre retorna true
     * 
     * @return true si puede spawnear
     */
    protected boolean puedeSpawnear() {
        return true; // Implementación por defecto
    }
    
    /**
     * Callback ejecutado después de spawnear
     * 
     * HOOK METHOD - Puede ser sobrescrito por subclases
     * Por defecto, no hace nada
     */
    protected void onEntidadSpawneada(T entidad, Point spawnPoint) {
        // Implementación vacía por defecto
    }
    
    /**
     * Selecciona un spawn point aleatorio
     * 
     * Puede ser sobrescrito para lógica personalizada
     */
    protected Point seleccionarSpawnPoint() {
        return spawnPoints.get(random.nextInt(spawnPoints.size()));
    }
    
    /**
     * Genera tiempo aleatorio entre spawns
     */
    protected int generarTiempoAleatorio() {
        return ticksMinSpawn + random.nextInt(ticksMaxSpawn - ticksMinSpawn);
    }
    
    // ==================== GESTIÓN DE SPAWN POINTS ====================
    
    /**
     * Agrega un nuevo spawn point
     */
    public void agregarSpawnPoint(Point punto) {
        if (!spawnPoints.contains(punto)) {
            spawnPoints.add(punto);
            System.out.println("[" + getNombreSpawner() + "] Spawn point agregado: (" + 
                             punto.x + ", " + punto.y + ")");
        }
    }
    
    /**
     * Elimina un spawn point
     */
    public void eliminarSpawnPoint(Point punto) {
        spawnPoints.remove(punto);
        System.out.println("[" + getNombreSpawner() + "] Spawn point eliminado");
    }
    
    /**
     * Limpia todos los spawn points
     */
    public void limpiarSpawnPoints() {
        spawnPoints.clear();
        System.out.println("[" + getNombreSpawner() + "] Todos los spawn points eliminados");
    }
    
    // ==================== CONTROL DEL SPAWNER ====================
    
    /**
     * Activa el spawner
     */
    public void activar() {
        activo = true;
        ticksDesdeUltimoSpawn = 0;
        System.out.println("[" + getNombreSpawner() + "] Activado");
    }
    
    /**
     * Desactiva el spawner
     */
    public void desactivar() {
        activo = false;
        System.out.println("[" + getNombreSpawner() + "] Desactivado");
    }
    
    /**
     * Fuerza spawn inmediato
     */
    public void spawnInmediato() {
        if (puedeSpawnear()) {
            spawnEntidad();
        }
        ticksDesdeUltimoSpawn = 0;
        ticksEntreSpawns = generarTiempoAleatorio();
    }
    
    /**
     * Configura el rango de tiempo entre spawns
     */
    public void setRangoTiempoSpawn(int minTicks, int maxTicks) {
        if (minTicks > 0 && maxTicks > minTicks) {
            this.ticksMinSpawn = minTicks;
            this.ticksMaxSpawn = maxTicks;
            System.out.println("[" + getNombreSpawner() + "] Rango actualizado: " + 
                             minTicks + " - " + maxTicks + " ticks");
        }
    }
    
    // ==================== GETTERS ====================
    
    public boolean isActivo() {
        return activo;
    }
    
    public int getCantidadSpawnPoints() {
        return spawnPoints.size();
    }
    
    public List<Point> getSpawnPoints() {
        return new ArrayList<>(spawnPoints);
    }
    
    public int getTicksParaProximoSpawn() {
        return ticksEntreSpawns - ticksDesdeUltimoSpawn;
    }
    
    /**
     * Información de debug
     */
    public String getInfo() {
        return String.format(
            "%s [Activo: %s, Spawn Points: %d, Próximo spawn: %d ticks]",
            getNombreSpawner(),
            activo ? "SÍ" : "NO",
            spawnPoints.size(),
            getTicksParaProximoSpawn()
        );
    }
}
