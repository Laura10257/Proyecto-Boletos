/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import Estructura.GestorReportes;
import modelo.ReporteVenta;

public class ArchivoUtil {

	private ArchivoUtil() {
	}

	public static void guardarReporteDiario(ReporteVenta reporte, String carpeta) {
		GestorReportes.guardarReporteEnArchivo(reporte, carpeta);
	}
}
