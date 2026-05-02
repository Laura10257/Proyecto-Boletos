package servicio;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import modelo.Categoria;

public class Validador {

	public static void validarCategoria(Categoria categoria) {
		if (categoria == null) {
			throw new IllegalArgumentException("Debes seleccionar una categoria.");
		}
	}

	public static void validarAsientosSeleccionados(List<String> asientos) {
		if (asientos == null || asientos.isEmpty()) {
			throw new IllegalArgumentException("Debes seleccionar al menos un asiento.");
		}
	}

	public static void validarAsientosNoDuplicados(List<String> asientos) {
		Set<String> unicos = new HashSet<>();
		for (String asiento : asientos) {
			String normalizado = asiento == null ? "" : asiento.trim().toUpperCase();
			if (normalizado.isBlank()) {
				throw new IllegalArgumentException("Hay asientos invalidos en la seleccion.");
			}
			if (!unicos.add(normalizado)) {
				throw new IllegalArgumentException("No puedes seleccionar el mismo asiento dos veces: " + normalizado);
			}
		}
	}
}
