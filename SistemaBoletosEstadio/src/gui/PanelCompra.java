/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PanelCompra extends JPanel {

	private final JTextArea txtSeleccion;
	private final JLabel lblTotal;
	private final JButton btnConfirmar;
	private final JButton btnLimpiar;

	public PanelCompra() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Resumen de Compra"));

		txtSeleccion = new JTextArea(8, 20);
		txtSeleccion.setEditable(false);
		txtSeleccion.setLineWrap(true);
		txtSeleccion.setWrapStyleWord(true);

		lblTotal = new JLabel("Total: $0.00");
		lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));

		btnConfirmar = new JButton("Confirmar Compra");
		btnLimpiar = new JButton("Limpiar Seleccion");

		JPanel botones = new JPanel();
		botones.add(btnConfirmar);
		botones.add(btnLimpiar);

		add(new JScrollPane(txtSeleccion), BorderLayout.CENTER);
		add(lblTotal, BorderLayout.NORTH);
		add(botones, BorderLayout.SOUTH);
	}

	public void actualizarSeleccion(List<String> asientos, double total) {
		if (asientos == null || asientos.isEmpty()) {
			txtSeleccion.setText("Sin asientos seleccionados.");
			lblTotal.setText("Total: $0.00");
			return;
		}

		txtSeleccion.setText("Asientos seleccionados:\n- " + String.join("\n- ", asientos));
		lblTotal.setText("Total: $" + String.format("%.2f", total));
	}

	public JButton getBtnConfirmar() {
		return btnConfirmar;
	}

	public JButton getBtnLimpiar() {
		return btnLimpiar;
	}
}
