package SistemaSoporte.Spawners;

import Entidades.Items.*;
import SistemaDeSoporte.Handler;
import java.awt.Point;
import java.util.*;

public class ItemSpawner extends Spawner<Item> {
    
    // Probabilidades de spawn (0-100)
    private static final int PROB_MARTILLO = 5;
    private static final int PROB_PARAGUAS = 30;
    private static final int PROB_BOLSO = 25;
    private static final int PROB_SOMBRERO = 35;
    
    /**
     * Constructor
     */
    public ItemSpawner(Handler handler, List<Point> spawnPoints) {
        super(handler, spawnPoints);
        
        // Configurar tiempos específicos para items (más lentos)
        this.ticksMinSpawn = 300;  // 5 segundos
        this.ticksMaxSpawn = 600;  // 10 segundos
        this.ticksEntreSpawns = generarTiempoAleatorio();
    }
    
    @Override
    protected Item crearEntidad(Point spawnPoint) {
        int prob = random.nextInt(100);
        
        // Seleccionar tipo de item según probabilidad
        if (prob < PROB_MARTILLO) {
            return new Martillo(spawnPoint.x, spawnPoint.y, 2, handler);
        } else if (prob < PROB_MARTILLO + PROB_PARAGUAS) {
            return new Paraguas(spawnPoint.x, spawnPoint.y, 2, handler);
        } else if (prob < PROB_MARTILLO + PROB_PARAGUAS + PROB_BOLSO) {
            return new BolsoDama(spawnPoint.x, spawnPoint.y, 2, handler);
        } else {
            return new Sombrero(spawnPoint.x, spawnPoint.y, 2, handler);
        }
    }
    
    @Override
    protected String getNombreSpawner() {
        return "ITEM SPAWNER";
    }
    
    @Override
    protected void onEntidadSpawneada(Item item, Point spawnPoint) {
        System.out.println("[ITEM] Tipo: " + item.getClass().getSimpleName() + 
                          " | Puntos: " + item.getValorPuntos());
    }
}