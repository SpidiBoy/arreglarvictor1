package Entidades;

import SistemaDeSoporte.ObjetosID;
import java.awt.Graphics;
import java.awt.Rectangle;
/**
 *
 * @author LENOVO
 */
public abstract class JuegoObjetos {
    private float x;
    private float y;
    private ObjetosID id;
    private float velX , vely; //velocidad
    private float width , height;
    private int scale;

    public JuegoObjetos(float x, float y, ObjetosID id, float width, float height, int scale) {
        this.x = x *scale;
        this.y = y *scale;
        this.id = id;
        this.width = width * scale;
        this.height = height * scale;
        this.scale = scale;
    }
    
    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();
    
    public void aplicarGravedad(){
         vely += 0.5F;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }


    public float getY() {
        return y;
    }


    public void setY(float y) {
        this.y = y;
    }


    public ObjetosID getId() {
        return id;
    }

    public void setId(ObjetosID id) {
        this.id = id;
    }

    public float getVelX() {
        return velX;
    }


    public void setVelX(float velX) {
        this.velX = velX;
    }


    public float getVely() {
        return vely;
    }

    public void setVely(float vely) {
        this.vely = vely;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }


    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
