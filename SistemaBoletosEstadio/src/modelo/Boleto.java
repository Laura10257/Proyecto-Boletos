/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Boleto {

    private final int id;
    private final Categoria categoria;
    private final double precio;
    private final String asiento;
    private boolean vendido;

    // CONSTRUCTOR para inicializar
    public Boleto(int id, Categoria categoria, double precio, String asiento) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de boleto debe ser mayor a 0.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria no puede ser nula.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (asiento == null || asiento.isBlank()) {
            throw new IllegalArgumentException("El asiento es obligatorio.");
        }

        this.id = id;
        this.categoria = categoria;
        this.precio = precio;
        this.asiento = asiento.trim().toUpperCase();
        this.vendido = false;
    }

    // METODOS
    public void vender() {
        if (vendido) {
            throw new IllegalStateException("El boleto ya fue vendido.");
        }
        this.vendido = true;
    }

    public int getId() {
        return id;
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

    @Override
    public String toString() {
        return "Boleto{" + "id=" + id + ", categoria=" + categoria + ", precio=" + precio + ", asiento=" + asiento + ", vendido=" + vendido + '}';
    }
}
