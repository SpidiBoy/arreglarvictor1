/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaSoporte.Spawners;
import Entidades.Enemigos.Barril;
import SistemaDeSoporte.Handler;
import java.awt.Point;
import java.util.List;
/**
 *
 * @author LENOVO
 */
public class BarrilSpawner extends Spawner<Barril> {
    
    /**
     * Constructor
     */
    public BarrilSpawner(Handler handler, List<Point> spawnPoints) {
        super(handler, spawnPoints);
    }
    
    @Override
    protected Barril crearEntidad(Point spawnPoint) {
        // Dirección aleatoria
        int probabilidad = random.nextInt(100);
        int direccion = (probabilidad < 71) ? 1 : -1;
        
        // Crear barril
        return new Barril(
            spawnPoint.x,
            spawnPoint.y,
            2,  // scale
            handler,
            direccion
        );
    }
    
    @Override
    protected String getNombreSpawner() {
        return "BARRIL SPAWNER";
    }
    
    @Override
    protected void onEntidadSpawneada(Barril barril, Point spawnPoint) {
        System.out.println("[BARRIL] Dirección: " + 
                          (barril.getDireccion() > 0 ? "DERECHA" : "IZQUIERDA"));
    }
}