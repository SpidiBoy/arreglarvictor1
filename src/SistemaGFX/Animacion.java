package SistemaGFX;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
/**
 *
 * @author LENOVO
 */
public class Animacion {
    private int velocidad;
    private int frames;
    private int index = 0;
    private int count = 0;
    private BufferedImage[] images;
    private BufferedImage correrImagen;
    
    public Animacion(int velocidad ,BufferedImage... args){
        this.velocidad = velocidad;
        images = new BufferedImage[args.length];
        for(int i = 0 ; i < args.length ; i++){
            images[i] = args[i];
        }
        frames = args.length;
    }
    
    public void runAnimacion(){
        index++;
        if( index > velocidad){
            index = 0;
            nextFrame();
        }
    }

    private void nextFrame() {
        correrImagen = images[count];
        count++;
        
        if(count >= frames){
            count = 0;
        }
    }
    
    public void drawAnimacion(Graphics g,int x , int y , int scaleX , int scaleY){
        g.drawImage(correrImagen, x, y, scaleX , scaleY,null);   
    }
}