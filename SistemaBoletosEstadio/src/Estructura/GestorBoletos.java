/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructura;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import modelo.Boleto;
import modelo.Categoria;

public class GestorBoletos {

	private final Map<Categoria, LinkedList<Boleto>> boletosPorCategoria = new EnumMap<>(Categoria.class);
	private int contadorBoletos = 1;

	public GestorBoletos() {
		for (Categoria categoria : Categoria.values()) {
			boletosPorCategoria.put(categoria, new LinkedList<>());
		}
	}

	public void inicializarBoletos(Categoria categoria, List<String> asientos, double precio) {
		LinkedList<Boleto> lista = boletosPorCategoria.get(categoria);
		lista.clear();

		for (String asiento : asientos) {
			lista.add(new Boleto(contadorBoletos++, categoria, precio, asiento));
		}
	}

	public Boleto buscarBoletoPorAsiento(Categoria categoria, String asiento) {
		for (Boleto boleto : boletosPorCategoria.get(categoria)) {
			if (boleto.getAsiento().equalsIgnoreCase(asiento)) {
				return boleto;
			}
		}
		return null;
	}

	public List<Boleto> listarDisponibles(Categoria categoria) {
		List<Boleto> disponibles = new ArrayList<>();
		for (Boleto boleto : boletosPorCategoria.get(categoria)) {
			if (!boleto.isVendido()) {
				disponibles.add(boleto);
			}
		}
		return disponibles;
	}

	public List<Boleto> listarTodos(Categoria categoria) {
		return List.copyOf(boletosPorCategoria.get(categoria));
	}
}
