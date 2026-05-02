/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Estructura.ColaReportes;
import Estructura.GestorBoletos;
import Estructura.GestorReportes;
import Estructura.GestorPrecios;
import Estructura.ControladorEstadio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelo.Boleto;
import modelo.Categoria;
import modelo.ReporteVenta;

public class VentaServicio {

    private final ControladorEstadio mapaAsientos;
    private final GestorPrecios gestorPrecios;
    private final GestorBoletos gestorBoletos;
    private final GestorReportes gestorReportes;

    public VentaServicio() {
        this.mapaAsientos = new ControladorEstadio(3, 5, 6, 8, 4, 6);
        this.gestorPrecios = new GestorPrecios();
        this.gestorBoletos = new GestorBoletos();
        this.gestorReportes = new GestorReportes();

        inicializarBoletosCategoria(Categoria.VIP);
        inicializarBoletosCategoria(Categoria.GENERAL);
        inicializarBoletosCategoria(Categoria.PREFERENCIAL);
    }

    private void inicializarBoletosCategoria(Categoria categoria) {
        List<String> asientosDisponibles = mapaAsientos.listarAsientosDisponibles(categoria);
        double precio = gestorPrecios.obtenerPrecio(categoria);
        gestorBoletos.inicializarBoletos(categoria, asientosDisponibles, precio);
    }

    public List<String> obtenerAsientosDisponibles(Categoria categoria) {
        Validador.validarCategoria(categoria);
        return mapaAsientos.listarAsientosDisponibles(categoria);
    }

    public double calcularTotal(Categoria categoria, List<String> asientos) {
        Validador.validarCategoria(categoria);
        Validador.validarAsientosSeleccionados(asientos);
        Validador.validarAsientosNoDuplicados(asientos);
        return gestorPrecios.obtenerPrecio(categoria) * asientos.size();
    }

    public ReporteVenta confirmarCompra(Categoria categoria, List<String> asientos, String carpetaReportes) {
        Validador.validarCategoria(categoria);
        Validador.validarAsientosSeleccionados(asientos);
        Validador.validarAsientosNoDuplicados(asientos);

        List<String> asientosVendidos = new ArrayList<>();
        double total = 0;

        for (String asiento : asientos) {
            String normalizado = asiento.trim().toUpperCase();
            
            // 1. Validar en el mapa visual (matriz)
            if (!mapaAsientos.estaDisponible(categoria, normalizado)) {
                throw new IllegalStateException("El asiento " + normalizado + " ya está ocupado.");
            }

            // 2. Ejecutar Gestión Dinámica en Listas Enlazadas (Eliminar de disponibles y mover a vendidos)
            try {
                gestorBoletos.registrarVentaDinamica(categoria, normalizado);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Inconsistencia: " + e.getMessage());
            }

            // 3. Ocupar en la matriz visual
            mapaAsientos.ocuparAsiento(categoria, normalizado);

            asientosVendidos.add(normalizado);
            total += gestorPrecios.obtenerPrecio(categoria);
        }

        ReporteVenta reporte = new ReporteVenta(
                LocalDateTime.now(),
                categoria,
                asientosVendidos.size(),
                total,
                "Asientos vendidos: " + String.join(", ", asientosVendidos)
        );

        gestorReportes.registrarReporte(reporte, carpetaReportes);
        return reporte;
    }

    public void actualizarPrecioCategoria(Categoria categoria, double nuevoPrecio) {
        gestorPrecios.actualizarPrecio(categoria, nuevoPrecio);
    }

    public ColaReportes getColaReportes() {
        return gestorReportes.getColaReportes();
    }
}
