/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaDeNiveles.Configuracion;

import Entidades.Escenario.PlataformaMovil;

/**
 *
 * @author LENOVO
 */
public class PlataformaConfig {
    public int x, y;
    public int width, height;
    public int scale, tileID;
    public PlataformaMovil.TipoMovimiento tipo;
    public float velocidad;
    public float limiteMin, limiteMax;
    public int duracionVisible, duracionInvisible;
    
    public PlataformaConfig(int x, int y, int width, int height,
                           int scale, int tileID,
                           PlataformaMovil.TipoMovimiento tipo,
                           float velocidad,
                           float limiteMin, float limiteMax,
                           int duracionVisible, int duracionInvisible) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.tileID = tileID;
        this.tipo = tipo;
        this.velocidad = velocidad;
        this.limiteMin = limiteMin;
        this.limiteMax = limiteMax;
        this.duracionVisible = duracionVisible;
        this.duracionInvisible = duracionInvisible;
    }
}
