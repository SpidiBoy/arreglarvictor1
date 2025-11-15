package UI;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;   
import mariotest.GestorEstados;
import mariotest.Juego;
/**
 *
 * @author LENOVO
 */
public class MenuPrincipal extends EstadoJuegoBase {
    // Opciones del menú
    private enum OpcionMenu {
        INICIAR_PARTIDA,
        CONTROLES,
        SALIR
    }
    
    private OpcionMenu opcionSeleccionada;
    private OpcionMenu[] opciones;
    
    private ImageIcon imagenTitulo;
    private int logoX, logoY;
    private int logoAncho, logoAlto;
    private boolean logoDisponible;
    
    private Font fuenteTitulo;
    private Font fuenteOpciones;
  
    // Colores
    private Color colorFondo;
    private Color colorTitulo;
    private Color colorOpcion;
    private Color colorSeleccionado;
    
    private final int tituloSize = 65; 
    
    /**
     * Constructor
     */
    public MenuPrincipal(GestorEstados gestorEstados, Juego juego) {
        super(gestorEstados, juego);
        
        // Inicializar opciones
        opciones = OpcionMenu.values();
        opcionSeleccionada = OpcionMenu.INICIAR_PARTIDA;
        
        // Configurar fuentes (simplificado como InterfazDeUsuario)
        fuenteTitulo = new Font("Arial", Font.BOLD, 48);
        fuenteOpciones = new Font("Arial", Font.BOLD, (int)36F);
        
        // Configurar colores
        colorFondo = new Color(0, 0, 0);
        colorTitulo = new Color(255, 215, 0); // Dorado
        colorOpcion = new Color(200, 200, 200);
        colorSeleccionado = new Color(255, 69, 0); // Rojo-naranja
        
        // CARGAR LOGO 
        cargarLogoEstiloUI(); 
    }
    
    private void cargarLogoEstiloUI() {
        logoDisponible = false;
        
        try {
            // RUTA EXACTA 
            var url = getClass().getResource("/imagenes/logo.gif");
            
            if (url != null) {
                imagenTitulo = new ImageIcon(url);
                System.out.println("[MENU] Logo cargado (Estilo UI): " + url.getPath());
                
                //Logica de tamaño
                logoAncho = tituloSize * 10;
                logoAlto = tituloSize * 10;  
                
                // Centrar horizontalmente
                logoX = (Juego.getVentanaWidth() / 2) - (logoAncho / 2);
                
                // Posición Y fija
                logoY = (int)(tituloSize * -0.3f); // -14
                
                logoDisponible = true;
                
                System.out.println("[MENU] Lógica de InterfazDeUsuario aplicada.");
                System.out.println("[MENU] Tamaño forzado a: " + logoAncho + "x" + logoAlto + "px");
                System.out.println("[MENU] Posición: (" + logoX + ", " + logoY + ")");
                
            } else {
                //MENSAJE DE ERROR de InterfazDeUsuario (según tu snippet)
                System.err.println("¡ERROR! No se pudo encontrar el archivo gif en la ruta especificada");
                System.err.println("[MENU] Ruta esperada: /imagenes/jugador/Titulo.gif");
                imagenTitulo = null;
            }
            
        } catch (Exception e) {
            // MENSAJE DE ERROR de InterfazDeUsuario (según tu snippet)
            System.err.println("¡ERROR! Ocurrio un problema al cargar el gif");
            e.printStackTrace();
            imagenTitulo = null;
            logoDisponible = false;
        }
    }
    
    @Override
    public void entrar() {
        System.out.println("[MENU] Menú Principal cargado");
        opcionSeleccionada = OpcionMenu.INICIAR_PARTIDA;
       
    }
    
    @Override
    public void tick() {
       
    }
    
    /**
     * RENDERIZADO APLICANDO LÓGICA DE InterfazDeUsuario
     */
    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        g.setColor(colorFondo);
        g.fillRect(0, 0, Juego.getVentanaWidth(), Juego.getVentanaHeight());
        
        // ==================== LOGO/GIF FORZADO ====================
        if (logoDisponible && imagenTitulo != null) {
            
            //  LÓGICA DE DIBUJADO EXACTA 
            // (Nota: 'x' y 'y' ya fueron calculados en cargarLogoEstiloUI)
            g2d.drawImage(
                imagenTitulo.getImage(),
                logoX,      // (int) x
                logoY,      // (int) y
                logoAncho,  // gp.tituloSize * 10
                logoAlto,   // gp.tituloSize * 10
                null
            );
            
        } else {
            // Fallback si no hay logo
            g.setFont(fuenteTitulo);
            g.setColor(colorTitulo);
            String titulo = "DIEGO KONG";
            int anchoTexto = g.getFontMetrics().stringWidth(titulo);
            int xTexto = (Juego.getVentanaWidth() - anchoTexto) / 2;
            g.setColor(colorTitulo);
            g.drawString(titulo, xTexto, 120);
        }
        
        // ==================== OPCIONES DEL MENÚ (LÓGICA InterfazDeUsuario) ====================
        
        int yInicial;
        if (logoDisponible) {
            // Lógica de InterfazDeUsuario:
            // y_opcion1 = y_logo_calculada + (tituloSize * 7.5f)
            yInicial = (int) (logoY + (tituloSize * 7.5f)); 
        } else {
            yInicial = 350; // Fallback
        }

        int espaciado = tituloSize;
        
        int y = yInicial;
        
        g.setFont(fuenteOpciones);
        
        for (OpcionMenu opcion : opciones) {
            boolean esSeleccionada = (opcion == opcionSeleccionada);
            
            g.setColor(esSeleccionada ? colorSeleccionado : colorOpcion);
            
            String texto = obtenerTextoOpcion(opcion);
            
            // LÓGICA DE CENTRADO (getXCentroTexto)
            int anchoTexto = g.getFontMetrics().stringWidth(texto);
            int x = (Juego.getVentanaWidth() - anchoTexto) / 2;
            
            // Indicador de selección (LÓGICA InterfazDeUsuario)
            if (esSeleccionada) {
                g.drawString(">", x - tituloSize, y);
            }
            
            g.drawString(texto, x, y);
            
            y += espaciado; 
        }
        
        // ==================== PIE DE PÁGINA ====================
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(new Color(150, 150, 150));
        
        String instrucciones = "Usa ↑/↓ Para Navegar | ENTER para Seleccionar | ESC Para Salir";
        String integrantes = "ALUMNOS: NECIOSUP FUENTES HAROL RAUL | ROJAS CASTRO EDWIN ALEXANDER | MENDOZA VARGAS DIEGO LUIS ALONSO";
        String integrantes2 = "SECLEN SANTISTEBAN ANTHONY SANTIAGO | CHIROQUE DIAZ JORGE EDERSSON";
        int anchoInstruc = g.getFontMetrics().stringWidth(instrucciones);
        g.drawString(instrucciones, 
                    (Juego.getVentanaWidth() - anchoInstruc) / 2, 
                    Juego.getVentanaHeight() - 130);
        
        int anchoIngrant = g.getFontMetrics().stringWidth(integrantes);
        g.drawString(integrantes, 
                    (Juego.getVentanaWidth() - anchoIngrant) / 2, 
                    Juego.getVentanaHeight() - 95);
        int anchoIngrant2 = g.getFontMetrics().stringWidth(integrantes2);
        g.drawString(integrantes2, 
                    (Juego.getVentanaWidth() - anchoIngrant2) / 2, 
                    Juego.getVentanaHeight() - 70);
    }
    
    /**
     * Obtiene el texto de una opción del menú
     */
    private String obtenerTextoOpcion(OpcionMenu opcion) {
        switch (opcion) {
            case INICIAR_PARTIDA:
                return "INICIAR JUEGO"; 
            case CONTROLES:
                return "CONTROLES";
            case SALIR:
                return "SALIR";
            default:
                return "";
        }
    }
    
    @Override
    public void salir() {
        System.out.println("[MENU] Saliendo del menú principal");
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        switch (key) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                int indiceActual = opcionSeleccionada.ordinal();
                indiceActual--;
                if (indiceActual < 0) {
                    indiceActual = opciones.length - 1;
                }
                opcionSeleccionada = opciones[indiceActual];
                break;
                
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                indiceActual = opcionSeleccionada.ordinal();
                indiceActual++;
                if (indiceActual >= opciones.length) {
                    indiceActual = 0;
                }
                opcionSeleccionada = opciones[indiceActual];
                break;
                
            case KeyEvent.VK_ENTER:
                ejecutarOpcion();
                break;
                
            case KeyEvent.VK_ESCAPE:
                System.out.println("[MENU] Saliendo del juego...");
                System.exit(0);
                break;
        }
    }
    
    /**
     * Ejecuta la opción seleccionada
     */
    private void ejecutarOpcion() {
        switch (opcionSeleccionada) {
            case INICIAR_PARTIDA:
                System.out.println("[MENU] Iniciando partida...");
                gestorEstados.cambiarEstado(EstadoJuegoEnum.JUGANDO);
                break;
                
            case CONTROLES:
                System.out.println("[MENU] Mostrando controles...");
                gestorEstados.cambiarEstado(EstadoJuegoEnum.CONTROLES);
                break;
                
            case SALIR:
                System.out.println("[MENU] Saliendo del juego...");
                System.exit(0);
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    }
}