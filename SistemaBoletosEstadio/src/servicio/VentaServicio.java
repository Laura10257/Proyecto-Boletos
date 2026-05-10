/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 * Clase de Servicio que orquesta la lógica de negocio del sistema.
 * Conecta las estructuras de datos (Matrices, HashMaps, Listas y Colas) con la interfaz.
 */
public class VentaServicio {

	private final ControladorEstadio mapaAsientos;
	private final GestorPrecios gestorPrecios;
	private final GestorBoletos gestorBoletos;
	private final GestorReportes gestorReportes;

	public VentaServicio() {
		// Inicialización de dimensiones del estadio (Filas, Columnas por categoría
		this.mapaAsientos = new ControladorEstadio(3, 5, 6, 8, 4, 6);
		this.gestorPrecios = new GestorPrecios();
		this.gestorBoletos = new GestorBoletos();
		this.gestorReportes = new GestorReportes();

		// Inicializar las listas enlazadas con los boletos base[cite: 4]
		inicializarBoletosCategoria(Categoria.VIP);
		inicializarBoletosCategoria(Categoria.GENERAL);
		inicializarBoletosCategoria(Categoria.PREFERENCIAL);
	}

	/**
	 * Se crearon los objetos Boletos y se almacenaron en la LinkedList correspondiente.
	 */
	private void inicializarBoletosCategoria(Categoria categoria) {
		List<String> asientosDisponibles = mapaAsientos.listarAsientosDisponibles(categoria);
		double precio = gestorPrecios.obtenerPrecio(categoria);
		gestorBoletos.inicializarBoletos(categoria, asientosDisponibles, precio);
	}

	public List<String> obtenerAsientosDisponibles(Categoria categoria) {
		Validador.validarCategoria(categoria);
		return mapaAsientos.listarAsientosDisponibles(categoria);
	}

	/**
	 * Se calculo el total consultando el precio actual en el HashMap.
	 */
	public double calcularTotal(Categoria categoria, List<String> asientos) {
		Validador.validarCategoria(categoria);
		Validador.validarAsientosSeleccionados(asientos);
		Validador.validarAsientosNoDuplicados(asientos);
		
		double precioActual = gestorPrecios.obtenerPrecio(categoria);
		return precioActual * asientos.size();
	}

	/**
	 * Proceso de compra: Ejecuta la Gestión Dinámica en Listas y el registro en Cola FIFO
	 */
	public ReporteVenta confirmarCompra(Categoria categoria, List<String> asientos, String carpetaReportes) {
		Validador.validarCategoria(categoria);
		Validador.validarAsientosSeleccionados(asientos);
		Validador.validarAsientosNoDuplicados(asientos);

		List<String> asientosVendidos = new ArrayList<>();
		double precioAlMomento = gestorPrecios.obtenerPrecio(categoria);
		double total = 0;

		for (String asiento : asientos) {
			String normalizado = asiento.trim().toUpperCase();
			
			// 1. Validar disponibilidad en la Matriz Visual
			if (!mapaAsientos.estaDisponible(categoria, normalizado)) {
				throw new IllegalStateException("El asiento " + normalizado + " ya está ocupado.");
			}

			// 2. GESTIÓN DINÁMICA: Eliminar de LinkedList de disponibles y mover a vendidos
			gestorBoletos.registrarVentaDinamica(categoria, normalizado);
			
			// 3. Actualizar estado en la Matriz
			mapaAsientos.ocuparAsiento(categoria, normalizado);

			asientosVendidos.add(normalizado);
			total += precioAlMomento;
		}

		// Crear el reporte de la transacción
		ReporteVenta reporte = new ReporteVenta(
				LocalDateTime.now(),
				categoria,
				asientosVendidos.size(),
				total,
				"Asientos: " + String.join(", ", asientosVendidos)
		);

		// 4. REGISTRO EN COLA (FIFO): Almacenar para cierre de caja o persistencia
		gestorReportes.registrarReporte(reporte, carpetaReportes);
		
		return reporte;
	}

	/**
	 * REQUISITO: Actualizar precio en el HashMap
	 */
	public void actualizarPrecioCategoria(Categoria categoria, double nuevoPrecio) {
		gestorPrecios.actualizarPrecio(categoria, nuevoPrecio);
	}

	public double obtenerPrecioActual(Categoria categoria) {
		return gestorPrecios.obtenerPrecio(categoria);
	}

	/**
	 * Acceso a la cola para el Panel de Administración
	 */
	public ColaReportes getColaReportes() {
		return gestorReportes.getColaReportes();
	}
}