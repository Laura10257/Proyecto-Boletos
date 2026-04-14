/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.StringJoiner;
import modelo.ReporteVenta;

public class ArchivoUtil {

	private ArchivoUtil() {
	}

	public static void guardarReporteDiario(ReporteVenta reporte, String carpeta) {
		if (reporte == null) {
			throw new IllegalArgumentException("El reporte no puede ser nulo.");
		}

		String directorio = (carpeta == null || carpeta.isBlank()) ? "reportes" : carpeta.trim();
		Path rutaCarpeta = Path.of(directorio);
		Path rutaArchivo = rutaCarpeta.resolve(FechaUtil.nombreArchivoDelDia());

		StringJoiner sj = new StringJoiner(System.lineSeparator());
		sj.add("--------------------------------------------------");
		sj.add("Fecha y hora: " + FechaUtil.formatearFechaHora(reporte.getFechaHora()));
		sj.add("Categoria: " + reporte.getCategoria());
		sj.add("Boletos vendidos: " + reporte.getCantidadBoletos());
		sj.add("Ingreso total: $" + String.format("%.2f", reporte.getIngresoTotal()));
		sj.add("Asientos: " + String.join(", ", reporte.getAsientosVendidos()));

		try {
			Files.createDirectories(rutaCarpeta);
			Files.writeString(
					rutaArchivo,
					sj.toString() + System.lineSeparator(),
					StandardCharsets.UTF_8,
					StandardOpenOption.CREATE,
					StandardOpenOption.APPEND
			);
		} catch (IOException e) {
			throw new RuntimeException("No se pudo guardar el reporte en archivo.", e);
		}
	}
}
