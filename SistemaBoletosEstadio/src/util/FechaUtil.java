/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FechaUtil {

	private static final DateTimeFormatter FORMATO_ARCHIVO = DateTimeFormatter.ofPattern("ddMMyyyy");
	private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter FORMATO_REPORTE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private FechaUtil() {
	}

	public static String nombreArchivoDelDia() {
		return "reporte_ventas_" + LocalDate.now().format(FORMATO_ARCHIVO) + ".txt";
	}

	public static String formatearFecha(LocalDate fecha) {
		return fecha.format(FORMATO_FECHA);
	}

	public static String formatearFechaHora(LocalDateTime fechaHora) {
		return fechaHora.format(FORMATO_REPORTE);
	}
}
