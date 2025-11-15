package SistemaGFX;
import java.awt.image.BufferedImage;
import java.util.HashMap;
/**
 * PARTRON SINGLETON
 *
 * 
 * @author LENOVO
 */
public final class Texturas {
    private static Texturas instancia;
    
    private final String folder = "/Imagenes";
    
    // Contadores de sprites
    private final int mario_S_count = 14;
    private final int mario_muerte_count = 6;
    private final int mario_martillo_count = 6;
    private final int barril_count = 8;
    private final int diegokong_count = 8;
    private final int princesa_count = 8;
    private final int fuego_count = 4;
    private final int llama_count = 4;
    private final int dk_agarra_count = 6;    // 6 frames de animación 
    
    private CargadorImagenes cargar;
    
    // HOJAS DE SPRITES GLOBALES
    private BufferedImage player_sheet, barril_sheet;
    private BufferedImage diegokong_sheet, princesaSheet, fuego_sheet, llama_sheet;
    private BufferedImage items_sheet;
    private BufferedImage victoria_sheet;
    
    // HOJAS DE SPRITES POR NIVEL
    private HashMap<Integer, BufferedImage> bloquesSheetsPorNivel;
    private HashMap<Integer, HashMap<Integer, BufferedImage>> tilesSpritesPorNivel;
    
    // ARRAYS DE SPRITES GLOBALES
    private BufferedImage[] mario_l, mario_s, mario_martillo;
    private BufferedImage[] barril_sprites, diegokong_sprites;
    private BufferedImage[] mario_muerte;
    private BufferedImage[] princesaSprites, fuego_sprites, llama_sprites;
    private BufferedImage[] martillo_sprites, paraguas_sprites, bolso_sprites, sombrero_sprites;
    
    // victoria
    private BufferedImage spriteCorazon;
    private BufferedImage spriteCorazonRoto;
    private BufferedImage[] spritesDKAgarra; 
    
    // NIVEL ACTUAL
    private int nivelActual = 1;
    
    public Texturas() {
        inicializarArrays();
        cargar = new CargadorImagenes();
        bloquesSheetsPorNivel = new HashMap<>();
        tilesSpritesPorNivel = new HashMap<>();
        
        try {
            cargarSpritesGlobales();
            cargarSpritesVictoria();
            cargarTodosLosNiveles();
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar texturas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Texturas getInstance() {
        if (instancia == null) {
            instancia = new Texturas();
        }
        return instancia;
    }
    
    private void inicializarArrays() {
        mario_s = new BufferedImage[mario_S_count];
        mario_martillo = new BufferedImage[mario_martillo_count];
        barril_sprites = new BufferedImage[barril_count];
        diegokong_sprites = new BufferedImage[diegokong_count];
        princesaSprites = new BufferedImage[princesa_count];
        fuego_sprites = new BufferedImage[fuego_count];
        llama_sprites = new BufferedImage[llama_count];
        mario_muerte = new BufferedImage[mario_muerte_count];
        martillo_sprites = new BufferedImage[1];
        paraguas_sprites = new BufferedImage[1];
        bolso_sprites = new BufferedImage[1];
        sombrero_sprites = new BufferedImage[1];
        spritesDKAgarra = new BufferedImage[dk_agarra_count];
    }
    
    private void cargarSpritesGlobales() {
        System.out.println("[TEXTURAS] Cargando sprites globales...");
        
        player_sheet = cargar.loadImage(folder + "/testt.png");
        barril_sheet = cargar.loadImage(folder + "/testt.png");
        diegokong_sheet = cargar.loadImage(folder + "/testt.png");
        princesaSheet = cargar.loadImage(folder + "/testt.png");
        fuego_sheet = cargar.loadImage(folder + "/testt.png");
        llama_sheet = cargar.loadImage(folder + "/testt.png");
        items_sheet = cargar.loadImage(folder + "/testt.png");
        victoria_sheet = cargar.loadImage(folder + "/testt.png");
        
        getPlayerTexturas();
        getBarrilTexturas();
        getItemsTexturas();
        getPlayerMartilloTexturas();
        getDiegoKongTexturas();
        getPrincesaTexturas();
        getFuegoTexturas();
        getLlamaTexturas();
        getPlayerMuerteTexturas();
    }
    
    /**
     * Carga sprites de victoria 
     */
    private void cargarSpritesVictoria() {
        System.out.println("[TEXTURAS] Cargando sprites de victoria...");
        
        try {
            // ==================== CORAZONES (16x16) ====================
            spriteCorazon = victoria_sheet.getSubimage(109, 157, 16, 16);
            spriteCorazonRoto = victoria_sheet.getSubimage(127, 157, 16, 16);
            
            System.out.println("[TEXTURAS] Corazones cargados (16x16)");
            
            // ==================== DK AGARRA PRINCESA (48x40) ====================
            int x_dk_base = 1;
            int y_dk_agarra = 368;
            int w_dk = 48;
            int h_dk = 40;
            int spacing = 2;
            
            //Cargar 6 frames 
            for (int i = 0; i < dk_agarra_count; i++) {
                int x_actual = x_dk_base + i * (w_dk + spacing);
                
                // Verificar límites del sheet
                if (x_actual + w_dk > diegokong_sheet.getWidth() ||
                    y_dk_agarra + h_dk > diegokong_sheet.getHeight()) {
                    System.err.println("[ERROR] Frame " + i + " fuera de límites: x=" + 
                                     x_actual + ", y=" + y_dk_agarra);
                    
                    // Crear placeholder
                    spritesDKAgarra[i] = crearPlaceholder(w_dk, h_dk, 
                        new java.awt.Color(255, 140, 0));
                    continue;
                }
                
                try {
                    spritesDKAgarra[i] = diegokong_sheet.getSubimage(
                        x_actual, y_dk_agarra, w_dk, h_dk
                    );
                    
                    System.out.println("[TEXTURAS] Frame " + i + " cargado: (" + 
                                     x_actual + "," + y_dk_agarra + ") " + w_dk + "x" + h_dk);
                    
                } catch (Exception e) {
                    System.err.println("[ERROR] Fallo al cargar frame " + i + ": " + e.getMessage());
                    spritesDKAgarra[i] = crearPlaceholder(w_dk, h_dk, 
                        new java.awt.Color(255, 140, 0));
                }
            }
            
            // Verificar que todos los frames se cargaron
            int framesValidos = 0;
            for (BufferedImage frame : spritesDKAgarra) {
                if (frame != null) framesValidos++;
            }
            
            System.out.println("[TEXTURAS] DK Agarra: " + framesValidos + "/" + 
                             dk_agarra_count + " frames cargados (48x40)");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo crítico en sprites de victoria: " + e.getMessage());
            e.printStackTrace();
            
            for (int i = 0; i < dk_agarra_count; i++) {
                spritesDKAgarra[i] = crearPlaceholder(48, 40, 
                    new java.awt.Color(255, 140, 0));
            }
        }
    }
    
    private void cargarTodosLosNiveles() {
        System.out.println("[TEXTURAS] Cargando texturas de todos los niveles...");
        cargarNivel(1, "/bloques2.png");
        cargarNivel(2, "/bloques3.png");
        cargarNivel(3, "/bloques4.png");
        cambiarNivel(1);
    }
    
    private void cargarNivel(int numeroNivel, String nombreArchivo) {
        try {
            System.out.println("[TEXTURAS] Cargando nivel " + numeroNivel + ": " + nombreArchivo);
            
            BufferedImage sheet = cargar.loadImage(folder + nombreArchivo);
            
            if (sheet == null) {
                System.err.println("[ERROR] No se pudo cargar: " + nombreArchivo);
                return;
            }
            
            bloquesSheetsPorNivel.put(numeroNivel, sheet);
            
            HashMap<Integer, BufferedImage> spritesNivel = new HashMap<>();
            extraerSpritesNivel(sheet, spritesNivel);
            
            tilesSpritesPorNivel.put(numeroNivel, spritesNivel);
            
            System.out.println("[TEXTURAS] Nivel " + numeroNivel + " cargado: " + 
                             spritesNivel.size() + " tiles");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar nivel " + numeroNivel + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void extraerSpritesNivel(BufferedImage sheet, HashMap<Integer, BufferedImage> sprites) {
        final int x_off = 0;
        final int y_off = 0;
        final int tileWidth = 8;
        final int tileHeight = 8;
        final int firstgid = 1;
        final int NUM_FILAS = 5;
        
        int currentTileID = firstgid;
        
        for (int fila = 0; fila < NUM_FILAS; fila++) {
            int y = y_off + fila * tileHeight;
            
            if (y + tileHeight > sheet.getHeight()) {
                break;
            }
            
            for (int x = x_off; x + tileWidth <= sheet.getWidth(); x += tileWidth) {
                BufferedImage sprite = sheet.getSubimage(x, y, tileWidth, tileHeight);
                sprites.put(currentTileID, sprite);
                currentTileID++;
            }
        }
    }
    
    public void cambiarNivel(int numeroNivel) {
        if (!tilesSpritesPorNivel.containsKey(numeroNivel)) {
            System.err.println("[ERROR] Nivel " + numeroNivel + " no existe en texturas");
            return;
        }
        
        this.nivelActual = numeroNivel;
        System.out.println("[TEXTURAS] Cambiado a nivel " + numeroNivel);
    }
    
    public BufferedImage getSpritePorID(int tileID) {
        HashMap<Integer, BufferedImage> spritesActuales = tilesSpritesPorNivel.get(nivelActual);
        
        if (spritesActuales == null) {
            System.err.println("[ADVERTENCIA] No hay sprites para nivel " + nivelActual);
            return null;
        }
        
        BufferedImage sprite = spritesActuales.get(tileID);
        
        if (sprite == null && tileID > 0) {
            System.err.println("[ADVERTENCIA] No se encontró sprite para tileID: " + tileID + 
                             " en nivel " + nivelActual);
        }
        return sprite;
    }
    
    private void getPlayerTexturas() {
        int x_off = 1, y_off = 1, width = 16, height = 16;
        for (int i = 0; i < mario_S_count; i++) {
            mario_s[i] = player_sheet.getSubimage(x_off + i * (width + 2), y_off, width, height);
        }
    }
    
    private void getPlayerMartilloTexturas() {
        int x_off = 1, y_off = 73, width = 32, height = 32;
        try {
            for (int i = 0; i < mario_martillo_count; i++) {
                mario_martillo[i] = player_sheet.getSubimage(
                    x_off + i * (width + 2), y_off, width, height
                );
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Martillo: " + e.getMessage());
        }
    }
    
    private void getPlayerMuerteTexturas() {
        int x_off = 1, y_off = 37, width = 16, height = 16;
        for (int i = 0; i < mario_muerte_count; i++) {
            mario_muerte[i] = player_sheet.getSubimage(x_off + i * (width + 2), y_off, width, height);
        }
    }
    
    private void getItemsTexturas() {
        try {
            martillo_sprites[0] = items_sheet.getSubimage(1, 55, 16, 16);
            paraguas_sprites[0] = items_sheet.getSubimage(145, 157, 16, 16);
            bolso_sprites[0] = items_sheet.getSubimage(145 + 18*2, 157, 16, 16);
            sombrero_sprites[0] = items_sheet.getSubimage(145 + 18, 157, 16, 16);
        } catch (Exception e) {
            System.err.println("[ERROR] Items: " + e.getMessage());
        }
    }
    
    private BufferedImage crearPlaceholder(int width, int height, java.awt.Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.setColor(java.awt.Color.WHITE);
        g.drawRect(0, 0, width - 1, height - 1);
        g.dispose();
        return img;
    }
    
    private void getBarrilTexturas() {
        int x_off = 1, y_off = 229, width = 16, height = 16;
        for (int i = 0; i < barril_count; i++) {
            barril_sprites[i] = barril_sheet.getSubimage(
                x_off + i * (width + 2), y_off, width, height
            );
        }
    }
    
    private void getDiegoKongTexturas() {
        int width = 48, height = 32;
        int spritesPrimeraFila = 4;
        
        try {
            int x_off_fila1 = 1, y_off_fila1 = 258;
            for (int i = 0; i < spritesPrimeraFila && i < diegokong_count; i++) {
                diegokong_sprites[i] = diegokong_sheet.getSubimage(
                    x_off_fila1 + i * (width + 2), y_off_fila1, width, height
                );
            }
            
            int x_off_fila2 = 1, y_off_fila2 = 292;
            for (int i = spritesPrimeraFila; i < diegokong_count; i++) {
                int spriteIndexEnFila = i - spritesPrimeraFila;
                diegokong_sprites[i] = diegokong_sheet.getSubimage(
                    x_off_fila2 + spriteIndexEnFila * (width + 2), y_off_fila2, width, height
                );
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Diego Kong: " + e.getMessage());
        }
    }
    
    private void getPrincesaTexturas() {
        int x_off = 1, y_off = 141, width = 16, height = 32;
        for (int i = 0; i < princesa_count; i++) {
            princesaSprites[i] = princesaSheet.getSubimage(
                x_off + i * (width + 2), y_off, width, height
            );
        }
    }
    
    private void getFuegoTexturas() {
        int x_off = 1, y_off = 193, width = 16, height = 16;
        try {
            for (int i = 0; i < fuego_count; i++) {
                fuego_sprites[i] = fuego_sheet.getSubimage(
                    x_off + i * (width + 2), y_off, width, height
                );
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Fuego: " + e.getMessage());
        }
    }
    
    private void getLlamaTexturas() {
        int x_off = 163, y_off = 193, width = 16, height = 16;
        try {
            for (int i = 0; i < llama_count; i++) {
                llama_sprites[i] = llama_sheet.getSubimage(
                    x_off + i * (width + 2), y_off, width, height
                );
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Llama: " + e.getMessage());
        }
    }
    // ==================== GETTERS ====================
    public BufferedImage[] getMarioL() { return mario_l; }
    public BufferedImage[] getMarioS() { return mario_s; }
    public BufferedImage[] getMarioMartillo() { return mario_martillo; }
    public BufferedImage[] getMarioMuerte() { return mario_muerte; }
    public BufferedImage[] getBarrilSprites() { return barril_sprites; }
    public BufferedImage[] getDiegoKongSprites() { return diegokong_sprites; }
    public BufferedImage[] getPrincesaSprites() { return princesaSprites; }
    public BufferedImage[] getFuegoSprites() { return fuego_sprites; }
    public BufferedImage[] getLlamaSprites() { return llama_sprites; }
    public BufferedImage[] getMartilloSprites() { return martillo_sprites; }
    public BufferedImage[] getParaguasSprites() { return paraguas_sprites; }
    public BufferedImage[] getBolsoSprites() { return bolso_sprites; }
    public BufferedImage[] getSombreroSprites() { return sombrero_sprites; }
    //GETTERS DE VICTORIA
    public BufferedImage getCorazonSprite() { return spriteCorazon; }
    public BufferedImage getCorazonRotoSprite() { return spriteCorazonRoto; }
    public BufferedImage[] getDKAgarraSprites() { return spritesDKAgarra; }
    public int getNivelActual() { return nivelActual; }
}