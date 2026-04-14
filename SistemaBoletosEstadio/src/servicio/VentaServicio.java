/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Estructura.ColaReportes;
import Estructura.GestorBoletos;
import Estructura.GestorPrecios;
import Estructura.MapaAsientos;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelo.Boleto;
import modelo.Categoria;
import modelo.ReporteVenta;
import util.ArchivoUtil;

public class VentaServicio {

	private final MapaAsientos mapaAsientos;
	private final GestorPrecios gestorPrecios;
	private final GestorBoletos gestorBoletos;
	private final ColaReportes colaReportes;

	public VentaServicio() {
		this.mapaAsientos = new MapaAsientos(3, 5, 6, 8, 4, 6);
		this.gestorPrecios = new GestorPrecios();
		this.gestorBoletos = new GestorBoletos();
		this.colaReportes = new ColaReportes();

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
		Validaciones.validarCategoria(categoria);
		return mapaAsientos.listarAsientosDisponibles(categoria);
	}

	public double calcularTotal(Categoria categoria, List<String> asientos) {
		Validaciones.validarCategoria(categoria);
		Validaciones.validarAsientosSeleccionados(asientos);
		Validaciones.validarAsientosNoDuplicados(asientos);
		return gestorPrecios.obtenerPrecio(categoria) * asientos.size();
	}

	public ReporteVenta confirmarCompra(Categoria categoria, List<String> asientos, String carpetaReportes) {
		Validaciones.validarCategoria(categoria);
		Validaciones.validarAsientosSeleccionados(asientos);
		Validaciones.validarAsientosNoDuplicados(asientos);

		List<String> asientosVendidos = new ArrayList<>();
		double total = 0;

		for (String asiento : asientos) {
			String normalizado = asiento.trim().toUpperCase();
			if (!mapaAsientos.estaDisponible(categoria, normalizado)) {
				throw new IllegalStateException("El asiento " + normalizado + " ya esta ocupado.");
			}

			Boleto boleto = gestorBoletos.buscarBoletoPorAsiento(categoria, normalizado);
			if (boleto == null) {
				throw new IllegalStateException("No existe un boleto para el asiento " + normalizado + " en " + categoria + '.');
			}
			if (boleto.isVendido()) {
				throw new IllegalStateException("El boleto del asiento " + normalizado + " ya fue vendido.");
			}

			mapaAsientos.ocuparAsiento(categoria, normalizado);
			boleto.vender();

			asientosVendidos.add(normalizado);
			total += boleto.getPrecio();
		}

		ReporteVenta reporte = new ReporteVenta(
				LocalDateTime.now(),
				categoria,
				asientosVendidos.size(),
				total,
				asientosVendidos
		);

		colaReportes.encolar(reporte);
		ArchivoUtil.guardarReporteDiario(reporte, carpetaReportes);
		return reporte;
	}

	public void actualizarPrecioCategoria(Categoria categoria, double nuevoPrecio) {
		gestorPrecios.actualizarPrecio(categoria, nuevoPrecio);
	}

	public ColaReportes getColaReportes() {
		return colaReportes;
	}
}
