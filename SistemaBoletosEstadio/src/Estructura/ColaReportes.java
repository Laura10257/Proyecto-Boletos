/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructura;

import java.util.LinkedList;
import java.util.Queue;
import modelo.ReporteVenta;

public class ColaReportes {

	private final Queue<ReporteVenta> cola = new LinkedList<>();

	public void encolar(ReporteVenta reporte) {
		if (reporte == null) {
			throw new IllegalArgumentException("No se puede encolar un reporte nulo.");
		}
		cola.offer(reporte);
	}

	public ReporteVenta desencolar() {
		return cola.poll();
	}

	public ReporteVenta frente() {
		return cola.peek();
	}

	public boolean estaVacia() {
		return cola.isEmpty();
	}

	public int tamano() {
		return cola.size();
	}
}
