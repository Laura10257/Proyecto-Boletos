package Estructura;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import modelo.ReporteVenta;
import util.FechaUtil;

public class GestorReportes {

	private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private final ColaReportes colaReportes;

	public GestorReportes() {
		this.colaReportes = new ColaReportes();
	}

	public void registrarReporte(ReporteVenta reporte, String carpetaReportes) {
		if (reporte == null) {
			throw new IllegalArgumentException("El reporte no puede ser nulo.");
		}

		colaReportes.encolar(reporte);
		guardarReporteEnArchivo(reporte, carpetaReportes);
	}

	public ReporteVenta desencolarReporte() {
		return colaReportes.desencolar();
	}

	public ReporteVenta verSiguienteReporte() {
		return colaReportes.frente();
	}

	public boolean estaVacia() {
		return colaReportes.estaVacia();
	}

	public int tamano() {
		return colaReportes.tamano();
	}

	public ColaReportes getColaReportes() {
		return colaReportes;
	}

	public static void guardarReporteEnArchivo(ReporteVenta reporte, String carpetaReportes) {
		if (reporte == null) {
			throw new IllegalArgumentException("El reporte no puede ser nulo.");
		}

		String directorio = (carpetaReportes == null || carpetaReportes.isBlank()) ? "reportes" : carpetaReportes.trim();
		Path rutaCarpeta = Path.of(directorio);
		Path rutaArchivo = rutaCarpeta.resolve(FechaUtil.nombreArchivoDelDia());

		StringBuilder contenido = new StringBuilder();
		contenido.append("--------------------------------------------------").append(System.lineSeparator());
		contenido.append("Fecha y hora: ").append(reporte.getFechaHora().format(FORMATO_FECHA_HORA)).append(System.lineSeparator());
		contenido.append("Categoria: ").append(reporte.getCategoria()).append(System.lineSeparator());
		contenido.append("Boletos vendidos: ").append(reporte.getBoletosVendidos()).append(System.lineSeparator());
		contenido.append("Ingreso total: $").append(String.format("%.2f", reporte.getIngresoTotal())).append(System.lineSeparator());
		contenido.append("Detalles: ").append(reporte.getDetalles()).append(System.lineSeparator());
		contenido.append(System.lineSeparator());

		try {
			Files.createDirectories(rutaCarpeta);
			Files.writeString(
				rutaArchivo,
				contenido.toString(),
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
			);
		} catch (IOException e) {
			throw new UncheckedIOException("No se pudo guardar el reporte en archivo.", e);
		}
	}
}