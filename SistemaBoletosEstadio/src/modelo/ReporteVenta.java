/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReporteVenta {

	private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private final LocalDateTime fechaHora;
	private final Categoria categoria;
	private final int boletosVendidos;
	private final double ingresoTotal;
	private final String detalles;

	public ReporteVenta(LocalDateTime fechaHora, Categoria categoria, int boletosVendidos, double ingresoTotal, String detalles) {
		if (fechaHora == null || categoria == null) {
			throw new IllegalArgumentException("La fecha y categoria son obligatorias.");
		}
		if (boletosVendidos <= 0) {
			throw new IllegalArgumentException("La cantidad de boletos debe ser mayor a 0.");
		}
		if (ingresoTotal <= 0) {
			throw new IllegalArgumentException("El ingreso total debe ser mayor a 0.");
		}
		if (detalles == null || detalles.isBlank()) {
			throw new IllegalArgumentException("Los detalles del reporte son obligatorios.");
		}

		this.fechaHora = fechaHora;
		this.categoria = categoria;
		this.boletosVendidos = boletosVendidos;
		this.ingresoTotal = ingresoTotal;
		this.detalles = detalles.trim();
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public int getBoletosVendidos() {
		return boletosVendidos;
	}

	public double getIngresoTotal() {
		return ingresoTotal;
	}

	public String getDetalles() {
		return detalles;
	}

	public String formatearLineaResumen() {
		return "Fecha y hora: " + fechaHora.format(FORMATO_FECHA_HORA)
				+ System.lineSeparator() + "Categoria: " + categoria
				+ System.lineSeparator() + "Boletos vendidos: " + boletosVendidos
				+ System.lineSeparator() + "Ingreso total: $" + String.format("%.2f", ingresoTotal)
				+ System.lineSeparator() + "Detalles: " + detalles;
	}

	@Override
	public String toString() {
		return "ReporteVenta{" + "fechaHora=" + fechaHora + ", categoria=" + categoria + ", boletosVendidos=" + boletosVendidos + ", ingresoTotal=" + ingresoTotal + ", detalles='" + detalles + '\'' + '}';
	}
}
