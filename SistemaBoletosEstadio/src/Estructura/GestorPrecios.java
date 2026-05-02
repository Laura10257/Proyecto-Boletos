/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructura;

import java.util.EnumMap;
import java.util.Map;
import modelo.Categoria;

public class GestorPrecios {

	private final Map<Categoria, Double> precios = new EnumMap<>(Categoria.class);

	public GestorPrecios() {
        // Precios iniciales configurados por defecto
		precios.put(Categoria.VIP, 1500.0);
		precios.put(Categoria.GENERAL, 800.0);
		precios.put(Categoria.PREFERENCIAL, 1100.0);
	}

	public double obtenerPrecio(Categoria categoria) {
		Double precio = precios.get(categoria);
		if (precio == null) {
			throw new IllegalArgumentException("No hay precio configurado para: " + categoria);
		}
		return precio;
	}

    /**
     * REQUISITO: Actualizar precio.
     * Modifica el valor asociado a la clave correspondiente en el mapa.
     */
	public void actualizarPrecio(Categoria categoria, double nuevoPrecio) {
		if (nuevoPrecio <= 0) {
			throw new IllegalArgumentException("El precio debe ser mayor a 0.");
		}
        // Uso de put() para actualizar el valor en el HashMap (Punto solicitado)
		precios.put(categoria, nuevoPrecio);
	}

	public Map<Categoria, Double> listarPrecios() {
		return Map.copyOf(precios);
	}
}
