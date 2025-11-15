package SistemaSoporte.Spawners;

import Entidades.Enemigos.EnemigoFuego;
import Entidades.Enemigos.Fuego;
import SistemaDeSoporte.Handler;
import SistemaDeSoporte.ObjetosID;
import java.awt.Point;
import java.util.List;

/**
 * 
 * 
 * 
 * @author LENOVO
 */
public class FuegoSpawner extends Spawner<Fuego> {
    
    private int maxFuegos;
    private static final int MAX_FUEGOS_DEFAULT = 4;
    
    /**
     * Constructor
     */
    public FuegoSpawner(Handler handler, List<Point> spawnPoints) {
        super(handler, spawnPoints);
        this.maxFuegos = MAX_FUEGOS_DEFAULT;
        
        // Configurar tiempos específicos para fuegos (más lentos)
        this.ticksMinSpawn = 180;  // 3 segundos
        this.ticksMaxSpawn = 420;  // 7 segundos
        this.ticksEntreSpawns = generarTiempoAleatorio();
    }
    
    @Override
    protected Fuego crearEntidad(Point spawnPoint) {
        int direccion = random.nextBoolean() ? 1 : -1;
        
        // Crear fuego según probabilidad
        int tipoRandom = random.nextInt(100);
        
        if (tipoRandom < 70) {
            // 70% - Fuego normal (patrulla)
            return new Fuego(spawnPoint.x, spawnPoint.y, 2, handler,
                           EnemigoFuego.ComportamientoFuego.PATRULLA, direccion);
        } else if (tipoRandom < 90) {
            // 20% - Fuego rápido
            return Fuego.crearRapido(spawnPoint.x, spawnPoint.y, 2, handler, direccion);
        } else {
            // 10% - Fuego perseguidor
            return Fuego.crearPerseguidor(spawnPoint.x, spawnPoint.y, 2, handler);
        }
    }
    
    @Override
    protected boolean puedeSpawnear() {
        // Solo spawnear si no se alcanzó el límite
        int fuegosActivos = contarFuegosActivos();
        return fuegosActivos < maxFuegos;
    }
    
    @Override
    protected String getNombreSpawner() {
        return "FUEGO SPAWNER";
    }
    
    @Override
    protected void onEntidadSpawneada(Fuego fuego, Point spawnPoint) {
        System.out.println("[FUEGO] Tipo: " + fuego.getComportamiento() + 
                          " | Activos: " + contarFuegosActivos() + "/" + maxFuegos);
    }
    
    /**
     * Cuenta fuegos activos en el juego
     */
    private int contarFuegosActivos() {
        return handler.contarObjetosPorTipo(ObjetosID.Fuego);
    }
    
    /**
     * Configura el límite de fuegos
     * @param max
     */
    public void setMaxFuegos(int max) {
        if (max > 0) {
            this.maxFuegos = max;
            System.out.println("[FUEGO SPAWNER] Límite actualizado: " + max);
        }
    }
    
    /**
     * Elimina todos los fuegos activos
     */
    public void eliminarTodosFuegos() {
        handler.eliminarObjetosPorTipo(ObjetosID.Fuego);
        System.out.println("[FUEGO SPAWNER] Todos los fuegos eliminados");
    }
    
    public int getMaxFuegos() {
        return maxFuegos;
    }
    
    public int getFuegosActivos() {
        return contarFuegosActivos();
    }
}