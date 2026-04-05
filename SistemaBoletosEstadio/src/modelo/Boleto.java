/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Boleto {

    private int id;
    private Categoria categoria;
    private double precio;
    private String asiento;
    private boolean vendido;

    //CONSTRUCTOR para inicializar 
    public Boleto(int id, Categoria categoria, double precio, String asiento) {
        this.id = id;
        this.categoria = categoria;
        this.precio = precio;
        this.asiento = asiento;
        this.vendido = false;
    }

    //METODOS
    public void vender() {
        this.vendido = true;
    }

    public boolean isVendido() {
        return vendido;
    }

    public String getAsiento() {
        return asiento;
    }

    public double getPrecio() {
        return precio;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
