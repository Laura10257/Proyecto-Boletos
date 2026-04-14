/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.Categoria;
import modelo.ReporteVenta;
import servicio.VentaServicio;

public class VentanaPrincipal extends JFrame {

	private final VentaServicio ventaServicio;
	private final JComboBox<Categoria> cmbCategoria;
	private final PanelAsientos panelAsientos;
	private final PanelCompra panelCompra;

	public VentanaPrincipal() {
		super("Sistema de Venta de Boletos - Estadio");

		this.ventaServicio = new VentaServicio();
		this.cmbCategoria = new JComboBox<>(Categoria.values());
		this.panelAsientos = new PanelAsientos();
		this.panelCompra = new PanelCompra();

		configurarVentana();
		enlazarEventos();
		cargarCategoriaSeleccionada();
	}

	private void configurarVentana() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		JPanel barraSuperior = new JPanel();
		barraSuperior.add(new JLabel("Categoria:"));
		barraSuperior.add(cmbCategoria);

		add(barraSuperior, BorderLayout.NORTH);
		add(panelAsientos, BorderLayout.CENTER);
		add(panelCompra, BorderLayout.EAST);

		panelCompra.setPreferredSize(new Dimension(320, 0));

		setSize(980, 620);
		setLocationRelativeTo(null);
	}

	private void enlazarEventos() {
		cmbCategoria.addActionListener(e -> cargarCategoriaSeleccionada());

		panelAsientos.setOnSeleccionCambiada(this::actualizarResumen);

		panelCompra.getBtnLimpiar().addActionListener(e -> {
			panelAsientos.limpiarSeleccion();
			actualizarResumen();
		});

		panelCompra.getBtnConfirmar().addActionListener(e -> confirmarCompra());
	}

	private void cargarCategoriaSeleccionada() {
		Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
		if (categoria == null) {
			return;
		}

		List<String> disponibles = ventaServicio.obtenerAsientosDisponibles(categoria);
		panelAsientos.refrescar(categoria, disponibles);
		actualizarResumen();
	}

	private void actualizarResumen() {
		Categoria categoria = panelAsientos.getCategoriaActual();
		List<String> seleccionados = panelAsientos.getAsientosSeleccionados();

		double total = 0;
		if (!seleccionados.isEmpty()) {
			total = ventaServicio.calcularTotal(categoria, seleccionados);
		}

		panelCompra.actualizarSeleccion(seleccionados, total);
	}

	private void confirmarCompra() {
		try {
			Categoria categoria = panelAsientos.getCategoriaActual();
			List<String> seleccionados = panelAsientos.getAsientosSeleccionados();

			ReporteVenta reporte = ventaServicio.confirmarCompra(categoria, seleccionados, "reportes");

			JOptionPane.showMessageDialog(
					this,
					"Compra exitosa.\nAsientos: " + String.join(", ", reporte.getAsientosVendidos())
					+ "\nTotal: $" + String.format("%.2f", reporte.getIngresoTotal()),
					"Compra Confirmada",
					JOptionPane.INFORMATION_MESSAGE
			);

			cargarCategoriaSeleccionada();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(
					this,
					ex.getMessage(),
					"Error en compra",
					JOptionPane.ERROR_MESSAGE
			);
		}
	}
}
