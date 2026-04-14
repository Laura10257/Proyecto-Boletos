/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import modelo.Categoria;

public class PanelAsientos extends JPanel {

	private final Map<Categoria, int[]> dimensiones = new HashMap<>();
	private final List<JToggleButton> botones = new ArrayList<>();
	private Categoria categoriaActual = Categoria.VIP;
	private List<String> disponiblesActuales = new ArrayList<>();
	private Runnable onSeleccionCambiada;

	public PanelAsientos() {
		dimensiones.put(Categoria.VIP, new int[]{3, 5});
		dimensiones.put(Categoria.GENERAL, new int[]{6, 8});
		dimensiones.put(Categoria.PREFERENCIAL, new int[]{4, 6});

		setBorder(BorderFactory.createTitledBorder("Mapa de Asientos"));
		setBackground(Color.WHITE);
	}

	public void setOnSeleccionCambiada(Runnable onSeleccionCambiada) {
		this.onSeleccionCambiada = onSeleccionCambiada;
	}

	public void refrescar(Categoria categoria, List<String> asientosDisponibles) {
		this.categoriaActual = categoria;
		this.disponiblesActuales = new ArrayList<>(asientosDisponibles);

		removeAll();
		botones.clear();

		int[] dimension = dimensiones.get(categoria);
		int filas = dimension[0];
		int columnas = dimension[1];

		setLayout(new GridLayout(filas, columnas, 5, 5));

		Set<String> disponibles = new HashSet<>();
		for (String asiento : asientosDisponibles) {
			disponibles.add(asiento.toUpperCase());
		}

		for (int f = 0; f < filas; f++) {
			for (int c = 0; c < columnas; c++) {
				String asiento = etiquetaAsiento(f, c);
				JToggleButton btn = new JToggleButton(asiento);
				btn.setFocusPainted(false);

				boolean disponible = disponibles.contains(asiento);
				btn.setEnabled(disponible);
				btn.setBackground(disponible ? new Color(190, 246, 206) : new Color(255, 180, 180));

				btn.addActionListener(e -> {
					btn.setBackground(btn.isSelected() ? new Color(255, 235, 152) : new Color(190, 246, 206));
					if (onSeleccionCambiada != null) {
						onSeleccionCambiada.run();
					}
				});

				botones.add(btn);
				add(btn);
			}
		}

		revalidate();
		repaint();
	}

	public Categoria getCategoriaActual() {
		return categoriaActual;
	}

	public List<String> getAsientosSeleccionados() {
		List<String> seleccionados = new ArrayList<>();
		for (JToggleButton btn : botones) {
			if (btn.isEnabled() && btn.isSelected()) {
				seleccionados.add(btn.getText());
			}
		}
		return seleccionados;
	}

	public void limpiarSeleccion() {
		for (JToggleButton btn : botones) {
			if (btn.isEnabled() && btn.isSelected()) {
				btn.setSelected(false);
				btn.setBackground(new Color(190, 246, 206));
			}
		}
	}

	public List<String> getDisponiblesActuales() {
		return new ArrayList<>(disponiblesActuales);
	}

	private String etiquetaAsiento(int fila, int columna) {
		return String.valueOf((char) ('A' + fila)) + (columna + 1);
	}
}
