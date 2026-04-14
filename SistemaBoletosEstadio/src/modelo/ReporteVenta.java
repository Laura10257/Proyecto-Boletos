/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReporteVenta {

	private final LocalDateTime fechaHora;
	private final Categoria categoria;
	private final int cantidadBoletos;
	private final double ingresoTotal;
	private final List<String> asientosVendidos;

	public ReporteVenta(LocalDateTime fechaHora, Categoria categoria, int cantidadBoletos, double ingresoTotal, List<String> asientosVendidos) {
		if (fechaHora == null || categoria == null) {
			throw new IllegalArgumentException("La fecha y categoria son obligatorias.");
		}
		if (cantidadBoletos <= 0) {
			throw new IllegalArgumentException("La cantidad de boletos debe ser mayor a 0.");
		}
		if (ingresoTotal <= 0) {
			throw new IllegalArgumentException("El ingreso total debe ser mayor a 0.");
		}
		if (asientosVendidos == null || asientosVendidos.isEmpty()) {
			throw new IllegalArgumentException("Debe incluir asientos vendidos.");
		}

		this.fechaHora = fechaHora;
		this.categoria = categoria;
		this.cantidadBoletos = cantidadBoletos;
		this.ingresoTotal = ingresoTotal;
		this.asientosVendidos = new ArrayList<>(asientosVendidos);
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public int getCantidadBoletos() {
		return cantidadBoletos;
	}

	public double getIngresoTotal() {
		return ingresoTotal;
	}

	public List<String> getAsientosVendidos() {
		return Collections.unmodifiableList(asientosVendidos);
	}
}
