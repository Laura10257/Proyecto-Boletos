/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistemaboletosestadio;

import gui.VentanaPrincipal;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import modelo.Categoria;
import modelo.ReporteVenta;
import servicio.VentaServicio;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "cli".equalsIgnoreCase(args[0])) {
            ejecutarDemoConsola();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }

    private static void ejecutarDemoConsola() {
        VentaServicio ventaServicio = new VentaServicio();

        System.out.println("=== SISTEMA DE BOLETOS (LOGICA JAVA) ===");
        mostrarDisponibles(ventaServicio, Categoria.VIP);

        List<String> seleccion = Arrays.asList("A1", "A2");
        double total = ventaServicio.calcularTotal(Categoria.VIP, seleccion);
        System.out.println("Total de compra para " + seleccion + " (VIP): $" + String.format("%.2f", total));

        ReporteVenta reporte = ventaServicio.confirmarCompra(Categoria.VIP, seleccion, "reportes");
        System.out.println("Compra confirmada. Reporte generado:");
        System.out.println("Fecha y hora: " + reporte.getFechaHora());
        System.out.println("Categoria: " + reporte.getCategoria());
        System.out.println("Cantidad: " + reporte.getBoletosVendidos());
        System.out.println("Ingreso: $" + String.format("%.2f", reporte.getIngresoTotal()));
        System.out.println("Detalles: " + reporte.getDetalles());

        mostrarDisponibles(ventaServicio, Categoria.VIP);
        System.out.println("Reportes en cola: " + ventaServicio.getColaReportes().tamano());
        System.out.println("Archivo de reporte diario guardado en carpeta /reportes");
    }

    private static void mostrarDisponibles(VentaServicio ventaServicio, Categoria categoria) {
        List<String> disponibles = ventaServicio.obtenerAsientosDisponibles(categoria);
        int limite = Math.min(disponibles.size(), 10);
        System.out.println("Asientos disponibles " + categoria + " (primeros " + limite + "): " + disponibles.subList(0, limite));
    }
}
