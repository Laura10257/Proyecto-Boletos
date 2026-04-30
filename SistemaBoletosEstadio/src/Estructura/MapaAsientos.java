/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructura;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import modelo.Categoria;

public class MapaAsientos {

	private final Map<Categoria, boolean[][]> mapa = new EnumMap<>(Categoria.class);

	public MapaAsientos(int filasVip, int columnasVip, int filasGeneral, int columnasGeneral, int filasPreferencial, int columnasPreferencial) {
		mapa.put(Categoria.VIP, new boolean[filasVip][columnasVip]);
		mapa.put(Categoria.GENERAL, new boolean[filasGeneral][columnasGeneral]);
		mapa.put(Categoria.PREFERENCIAL, new boolean[filasPreferencial][columnasPreferencial]);
	}

	public boolean estaDisponible(Categoria categoria, String etiquetaAsiento) {
		int[] pos = convertirEtiquetaAIndice(categoria, etiquetaAsiento);
		return !mapa.get(categoria)[pos[0]][pos[1]];
	}

	public void ocuparAsiento(Categoria categoria, String etiquetaAsiento) {
		int[] pos = convertirEtiquetaAIndice(categoria, etiquetaAsiento);
		if (mapa.get(categoria)[pos[0]][pos[1]]) {
			throw new IllegalStateException("El asiento " + etiquetaAsiento + " ya esta ocupado.");
		}
		mapa.get(categoria)[pos[0]][pos[1]] = true;
	}

	public List<String> listarAsientosDisponibles(Categoria categoria) {
		boolean[][] matriz = mapa.get(categoria);
		List<String> asientos = new ArrayList<>();

		for (int fila = 0; fila < matriz.length; fila++) {
			for (int columna = 0; columna < matriz[fila].length; columna++) {
				if (!matriz[fila][columna]) {
					asientos.add(formatearEtiqueta(fila, columna));
				}
			}
		}

		return asientos;
	}

	public int capacidad(Categoria categoria) {
		boolean[][] matriz = mapa.get(categoria);
		return matriz.length * matriz[0].length;
	}

	private int[] convertirEtiquetaAIndice(Categoria categoria, String etiquetaAsiento) {
		if (etiquetaAsiento == null || etiquetaAsiento.isBlank()) {
			throw new IllegalArgumentException("La etiqueta del asiento no puede estar vacia.");
		}

		String normalizada = etiquetaAsiento.trim().toUpperCase();
		char letra = normalizada.charAt(0);
		String numeroTexto = normalizada.substring(1);

		if (numeroTexto.isBlank()) {
			throw new IllegalArgumentException("Formato de asiento invalido: " + etiquetaAsiento);
		}

		int fila = letra - 'A';
		int columna;

		try {
			columna = Integer.parseInt(numeroTexto) - 1;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Formato de asiento invalido: " + etiquetaAsiento);
		}

		boolean[][] matriz = mapa.get(categoria);
		if (matriz == null) {
			throw new IllegalArgumentException("Categoria invalida: " + categoria);
		}

		if (fila < 0 || fila >= matriz.length || columna < 0 || columna >= matriz[fila].length) {
			throw new IllegalArgumentException("El asiento " + etiquetaAsiento + " no existe en " + categoria + '.');
		}

		return new int[]{fila, columna};
	}

	private String formatearEtiqueta(int fila, int columna) {
		return String.valueOf((char) ('A' + fila)) + (columna + 1);
	}
}
