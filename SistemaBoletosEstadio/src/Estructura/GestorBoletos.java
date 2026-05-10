/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructura;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import modelo.Boleto;
import modelo.Categoria;

public class GestorBoletos {

    // Listas dinámicas USADAS: Disponibles y Vendidos
    private final Map<Categoria, LinkedList<Boleto>> disponiblesPorCategoria = new EnumMap<>(Categoria.class);
    private final Map<Categoria, LinkedList<Boleto>> vendidosPorCategoria = new EnumMap<>(Categoria.class);
    private int contadorBoletos = 1;

    public GestorBoletos() {
        for (Categoria categoria : Categoria.values()) {
            disponiblesPorCategoria.put(categoria, new LinkedList<>());
            vendidosPorCategoria.put(categoria, new LinkedList<>());
        }
    }

    public void inicializarBoletos(Categoria categoria, List<String> asientos, double precio) {
        LinkedList<Boleto> listaDisp = disponiblesPorCategoria.get(categoria);
        listaDisp.clear();
        vendidosPorCategoria.get(categoria).clear();

        for (String asiento : asientos) {
            listaDisp.add(new Boleto(contadorBoletos++, categoria, precio, asiento));
        }
    }

    /**
     * Implementación de la Gestión Dinámica: 
     * Elimina el boleto de la lista de disponibles y lo mueve a vendidos.
     */
    public void registrarVentaDinamica(Categoria categoria, String etiquetaAsiento) {
        LinkedList<Boleto> disponibles = disponiblesPorCategoria.get(categoria);
        Boleto boletoEncontrado = null;

        // Operación de Búsqueda y Eliminación en LinkedList
        for (Boleto b : disponibles) {
            if (b.getAsiento().equalsIgnoreCase(etiquetaAsiento)) {
                boletoEncontrado = b;
                break;
            }
        }
//BUSCA OBJETO RECORRIENDO LO NODOS Y UNA VEZ ENCONTRADO UTILIZA EL .REMOVE PARA DESCONECTAR EL NODO Y CONECTA EL NODO PASADO CON SIGUIENTE
        if (boletoEncontrado != null) {
            disponibles.remove(boletoEncontrado); // ELIMINAR de la lista (Requisito Maestra)
            boletoEncontrado.vender();
            vendidosPorCategoria.get(categoria).add(boletoEncontrado); // MOVER a otra lista
        } else {
            throw new IllegalStateException("El boleto para el asiento " + etiquetaAsiento + " no está en la lista de disponibles.");
        }
    }

    public Boleto buscarBoletoDisponible(Categoria categoria, String asiento) {
        for (Boleto boleto : disponiblesPorCategoria.get(categoria)) {
            if (boleto.getAsiento().equalsIgnoreCase(asiento)) {
                return boleto;
            }
        }
        return null;
    }

    public List<Boleto> listarDisponibles(Categoria categoria) {
        // Retornamos una copia de la lista de disponibles
        return new LinkedList<>(disponiblesPorCategoria.get(categoria));
    }

    public List<Boleto> listarVendidos(Categoria categoria) {
        return new LinkedList<>(vendidosPorCategoria.get(categoria));
    }
}