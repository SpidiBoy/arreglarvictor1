/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaDeNiveles.Configuracion;
import Entidades.Escenario.PlataformaMovil;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuración específica por nivel - Patrón STRATEGY
 * Cada nivel tiene su propia configuración de enemigos, items, plataformas, etc.
 * 
 * @author LENOVO
 */
public abstract class ConfiguracionNivel {
    protected int numeroNivel;
    protected String rutaTMX;
    
    /**
     * Factory Method - Crea la configuración según el nivel
     */
    public static ConfiguracionNivel crear(int nivel) {
        switch (nivel) {
            case 1:
                return new Nivel1();
            case 2:
                return new Nivel2();
            case 3:
                return new Nivel3();
            default:
                System.err.println("[ERROR] Nivel " + nivel + " no existe. Usando Nivel 1.");
                return new Nivel1();
        }
    }
    
    // ==================== MÉTODOS ABSTRACTOS ====================
    
    public abstract String getRutaTMX();
    
    // Diego Kong y Princesa
    public abstract boolean tieneDiegoKong();
    public abstract Point getPosicionDK();
    public abstract boolean tienePrincesa();
    public abstract Point getPosicionPrincesa();
    
    // Barriles
    public abstract boolean tieneBarriles();
    public abstract List<Point> getBarrilSpawnPoints();
    public abstract boolean isBarrilesActivos();
    
    // Fuegos
    public abstract boolean tieneFuegos();
    public abstract List<Point> getFuegoSpawnPoints();
    public abstract int getMaxFuegos();
    public abstract boolean isFuegosActivos();
    
    // Items
    public abstract boolean tieneItems();
    public abstract List<Point> getItemSpawnPoints();
    public abstract boolean isItemsActivos();
    
    // Plataformas móviles
    public abstract boolean tienePlataformasMoviles();
    public abstract List<PlataformaConfig> getPlataformasMoviles();
    
    // Llamas estáticas
    public abstract boolean tieneLlamasEstaticas();
    public abstract List<Point> getPosicionesLlamasEstaticas();
    
    // ==================== NIVEL 1 ====================
    
    public static class Nivel1 extends ConfiguracionNivel {
        
        public Nivel1() {
            this.numeroNivel = 1;
            this.rutaTMX = "/Imagenes/Nivel1.tmx";
        }
        
        @Override
        public String getRutaTMX() {
            return rutaTMX;
        }
        
        @Override
        public boolean tieneDiegoKong() {
            return true;
        }
        
        @Override
        public Point getPosicionDK() {
            return new Point(90, 40);
        }
        
        @Override
        public boolean tienePrincesa() {
            return true;
        }
        
        @Override
        public Point getPosicionPrincesa() {
            return new Point(180, 7);
        }
        
        @Override
        public boolean tieneBarriles() {
            return true;
        }
        
        @Override
        public List<Point> getBarrilSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            spawns.add(new Point(90, 50));
            return spawns;
        }
        
        @Override
        public boolean isBarrilesActivos() {
            return true; // Activar manualmente si se desea
        }
        
        @Override
        public boolean tieneFuegos() {
            return true;
        }
        
        @Override
        public List<Point> getFuegoSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            spawns.add(new Point(90, 40));
            spawns.add(new Point(90, 50));
            return spawns;
        }
        
        @Override
        public int getMaxFuegos() {
            return 10;
        }
        
        @Override
        public boolean isFuegosActivos() {
            return true;
        }
        
        @Override
        public boolean tieneItems() {
            return true;
        }
        
        @Override
        public List<Point> getItemSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            // Puntos de spawn vacíos - los items aparecen 
            spawns.add(new Point(0, 118));
            spawns.add(new Point(418, 68));
            spawns.add(new Point(418, 140));
            spawns.add(new Point(418, 200));
            return spawns;
        }
        
        @Override
        public boolean isItemsActivos() {
            return true;
        }
        
        @Override
        public boolean tienePlataformasMoviles() {
            return true;
        }
        
        @Override
        public List<PlataformaConfig> getPlataformasMoviles() {
            List<PlataformaConfig> plataformas = new ArrayList<>();
            
            // Plataforma vertical 1
            plataformas.add(new PlataformaConfig(
                60, 80,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.VERTICAL,   // Tipo
                1.5f,                                      // Velocidad
                190, 670,                                  // Límites
                900, 120                                   // Duraciones
            ));
            
            // Plataforma vertical 2
            plataformas.add(new PlataformaConfig(
                124, 80,
                8, 8,
                3, 1,
                PlataformaMovil.TipoMovimiento.VERTICAL,
                -1.5f,
                190, 670,
                980, 120
            ));
            
            return plataformas;
        }
        
        @Override
        public boolean tieneLlamasEstaticas() {
            return true;
        }
        
        @Override
        public List<Point> getPosicionesLlamasEstaticas() {
            List<Point> posiciones = new ArrayList<>();
            posiciones.add(new Point(81, 322));
            posiciones.add(new Point(94, 322));
            posiciones.add(new Point(177, 322));
            posiciones.add(new Point(192, 322));
            return posiciones;
        }
    }
    
    // ==================== NIVEL 2 ====================
    
    public static class Nivel2 extends ConfiguracionNivel {
        
        public Nivel2() {
            this.numeroNivel = 2;
            this.rutaTMX = "/Imagenes/Nivel2.tmx";
        }
        
        @Override
        public String getRutaTMX() {
            return rutaTMX;
        }
        
        @Override
        public boolean tieneDiegoKong() {
            return true;
        }
        
        @Override
        public Point getPosicionDK() {
            return new Point(90, 40); // Ajustar según el nivel 2
        }
        
        @Override
        public boolean tienePrincesa() {
            return true;
        }
        
        @Override
        public Point getPosicionPrincesa() {
            return new Point(180, 7); // Ajustar según el nivel 2
        }
        
        @Override
        public boolean tieneBarriles() {
            return true;
        }
        
        @Override
        public List<Point> getBarrilSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            spawns.add(new Point(100, 50));
            return spawns;
        }
        
        @Override
        public boolean isBarrilesActivos() {
            return true; // Nivel 2 tiene barriles activos
        }
        
        @Override
        public boolean tieneFuegos() {
            return true;
        }
        
        @Override
        public List<Point> getFuegoSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            spawns.add(new Point(150, 60));
            spawns.add(new Point(250, 80));
            return spawns;
        }
        
        @Override
        public int getMaxFuegos() {
            return 15; // Más fuegos en nivel 2
        }
        
        @Override
        public boolean isFuegosActivos() {
            return true;
        }
        
        @Override
        public boolean tieneItems() {
            return true;
        }
        
        @Override
        public List<Point> getItemSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            return spawns;
        }
        
        @Override
        public boolean isItemsActivos() {
            return true;
        }
        
        @Override
        public boolean tienePlataformasMoviles() {
            return true; // Nivel 2 NO tiene plataformas móviles
        }
        
        @Override
        public List<PlataformaConfig> getPlataformasMoviles() {
            List<PlataformaConfig> plataformas2 = new ArrayList<>();
            
            // Plataforma vertical 1
            plataformas2.add(new PlataformaConfig(
                60, 150,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.HORIZONTAL,   // Tipo
                1.5f,                                      // Velocidad
                80, 800,                                  // Límites
                1900, 120                                   // Duraciones
            ));
            
            plataformas2.add(new PlataformaConfig(
                68, 150,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.HORIZONTAL,   // Tipo
                1.5f,                                      // Velocidad
                80, 800,                                  // Límites
                1900, 120                                   // Duraciones
            ));
            
            plataformas2.add(new PlataformaConfig(
                170, 150,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.HORIZONTAL,   // Tipo
                1.5f,                                      // Velocidad
                80, 800,                                  // Límites
                1900, 120                                   // Duraciones
            ));
            plataformas2.add(new PlataformaConfig(
                178, 150,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.HORIZONTAL,   // Tipo
                1.5f,                                      // Velocidad
                80, 800,                                  // Límites
                1900, 120                                   // Duraciones
            ));
            
            plataformas2.add(new PlataformaConfig(
                60, 110,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.HORIZONTAL,   // Tipo
                -1.5f,                                      // Velocidad
                80, 800,                                  // Límites
                1900, 120                                   // Duraciones
            ));
            
            plataformas2.add(new PlataformaConfig(
                68, 110,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.HORIZONTAL,   // Tipo
                -1.5f,                                      // Velocidad
                80, 800,                                  // Límites
                1900, 120                                   // Duraciones
            ));
            
            plataformas2.add(new PlataformaConfig(
                170, 110,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.HORIZONTAL,   // Tipo
                -1.5f,                                      // Velocidad
                80, 800,                                  // Límites
                1900, 120                                   // Duraciones
            ));
            
            plataformas2.add(new PlataformaConfig(
                178, 110,                                    // Posición inicial
                8, 8,                                      // Tamaño
                3, 1,                                      // Scale, TileID
                PlataformaMovil.TipoMovimiento.HORIZONTAL,   // Tipo
                -1.5f,                                      // Velocidad
                80, 800,                                  // Límites
                1900, 120                                   // Duraciones
            ));
            return plataformas2;
        }
        
        @Override
        public boolean tieneLlamasEstaticas() {
            return true; // Nivel 2 tiene fuegos móviles en su lugar
        }
        
        @Override
        public List<Point> getPosicionesLlamasEstaticas() {
            List<Point> posiciones2 = new ArrayList<>();
            posiciones2.add(new Point(122, 342));
            posiciones2.add(new Point(127, 342));
            posiciones2.add(new Point(198, 342));
            posiciones2.add(new Point(193, 342));
            posiciones2.add(new Point(265, 342));
            posiciones2.add(new Point(270, 342));
            posiciones2.add(new Point(349, 342));
            posiciones2.add(new Point(354, 342));
            return posiciones2;
        }
    }

    private static class Nivel3 extends ConfiguracionNivel {

        public Nivel3() {
            this.numeroNivel = 3;
            this.rutaTMX = "/Imagenes/Nivel3.tmx";
        }

        @Override
        public String getRutaTMX() {
            return rutaTMX;
        }

        @Override
        public boolean tieneDiegoKong() {
            return true;
        }

        @Override
        public Point getPosicionDK() {
            return new Point(90, 89);
        }

        @Override
        public boolean tienePrincesa() {
            return true;
        }

        @Override
        public Point getPosicionPrincesa() {
            return new Point(170, 43);
        }

        @Override
        public boolean tieneBarriles() {
            return true;
        }

        @Override
        public List<Point> getBarrilSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            spawns.add(new Point(90, 89));
            return spawns;
        }

        @Override
        public boolean isBarrilesActivos() {
          return true;            
        }

        @Override
        public boolean tieneFuegos() {
          return true;
        }

        @Override
        public List<Point> getFuegoSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            spawns.add(new Point(150, 155));
            spawns.add(new Point(250, 155));
            return spawns;
        }

        @Override
        public int getMaxFuegos() {
            return 25;
        }

        @Override
        public boolean isFuegosActivos() {
            return true;
        }

        @Override
        public boolean tieneItems() {
            return true;
        }

        @Override
        public List<Point> getItemSpawnPoints() {
            List<Point> spawns = new ArrayList<>();
            spawns.add(new Point(150, 230));
            return spawns;
        }

        @Override
        public boolean isItemsActivos() {
            return true;
        }

        @Override
        public boolean tienePlataformasMoviles() {
            return false;
        }

        @Override
        public List<PlataformaConfig> getPlataformasMoviles() {
            List<PlataformaConfig> plataformas3 = new ArrayList<>();
            
            return plataformas3;
        }

        @Override
        public boolean tieneLlamasEstaticas() {
            return true;
        }

        @Override
        public List<Point> getPosicionesLlamasEstaticas() {
            List<Point> posicionesllamas = new ArrayList<>();
            posicionesllamas.add(new Point(202, 155));
            posicionesllamas.add(new Point(190, 155));
            posicionesllamas.add(new Point(155, 155));
            posicionesllamas.add(new Point(165, 155));
            posicionesllamas.add(new Point(215, 155));
            posicionesllamas.add(new Point(225, 155));
            posicionesllamas.add(new Point(250, 155));
            posicionesllamas.add(new Point(262, 155));
            return posicionesllamas;
        }
    }
}
