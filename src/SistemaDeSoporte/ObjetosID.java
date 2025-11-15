/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaDeSoporte;

import Entidades.Escenario.Tile;

/**
 *
 * @author LENOVO
 */
public enum ObjetosID {
    Jugador,
    // Elementos del escenario
    Tile,              // Plataformas y bloques sólidos o no
    Pipe,             // Tuberías (heredado de Mario, puede usarse para otros elementos)
    Escalera,// Escaleras normales
    Item,
    EscaleraRota,     // Escaleras rotas o dañadas
    
    // Enemigos y NPCs
    DiegoKong,       // Diego Kong
    Barril,           // Barriles que lanza DK
    Fuego,            // Fuego/llama enemiga
    Princesa,         // Princesa a rescatar
    
    // Elementos interactivos
    Martillo,         // Martillo power-up
    Puntos,           // Objetos que dan puntos
    
    // Elementos del sistema
    SpawnPoint,       // Puntos de aparición
    
    // Efectos visuales
    Explosion,       
    Particula;  

}
