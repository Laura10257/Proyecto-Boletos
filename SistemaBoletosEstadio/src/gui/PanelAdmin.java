/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import modelo.Categoria;
import modelo.ReporteVenta;
import servicio.VentaServicio;

public class PanelAdmin extends JDialog {

    private final VentaServicio servicio;
    private final JLabel lblIngresoTotal;
    private final JLabel lblVentasCount;
    private final DefaultListModel<String> listModel;

    // Campos para la actualización de precios (HashMap)
    private JTextField txtPrecioVip, txtPrecioGen, txtPrecioPre;

    public PanelAdmin(Frame padre, VentaServicio servicio) {
        super(padre, "Panel de Administración y Configuración", true);
        this.servicio = servicio;
        this.listModel = new DefaultListModel<>();
        
        setSize(550, 650);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(13, 17, 23));

        // --- Panel Superior: Estadísticas Rápidas ---
        JPanel pnlStats = new JPanel(new GridLayout(2, 1, 10, 10));
        pnlStats.setBackground(new Color(22, 27, 34));
        pnlStats.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(48, 54, 61)), 
                "Resumen de Sesión", TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 12), new Color(201, 209, 217)));
        
        lblIngresoTotal = new JLabel("Ingreso Total Acumulado: $0.00");
        lblIngresoTotal.setForeground(Color.WHITE);
        lblVentasCount = new JLabel("Ventas en Cola (FIFO): 0");
        lblVentasCount.setForeground(new Color(139, 148, 158));
        
        pnlStats.add(lblIngresoTotal);
        pnlStats.add(lblVentasCount);

        // --- Panel Central: Gestión de Precios (HASHMAP) ---
        JPanel pnlHashMap = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlHashMap.setBackground(new Color(22, 27, 34));
        pnlHashMap.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(48, 54, 61)), 
                "Actualización de Precios (HashMap)", TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 12), new Color(201, 209, 217)));

        txtPrecioVip = new JTextField(String.valueOf(servicio.obtenerPrecioActual(Categoria.VIP)));
        txtPrecioGen = new JTextField(String.valueOf(servicio.obtenerPrecioActual(Categoria.GENERAL)));
        txtPrecioPre = new JTextField(String.valueOf(servicio.obtenerPrecioActual(Categoria.PREFERENCIAL)));

        pnlHashMap.add(crearLabelBlanco("Precio VIP:")); pnlHashMap.add(txtPrecioVip);
        pnlHashMap.add(crearLabelBlanco("Precio General:")); pnlHashMap.add(txtPrecioGen);
        pnlHashMap.add(crearLabelBlanco("Precio Preferencial:")); pnlHashMap.add(txtPrecioPre);
        
        JButton btnActualizarHashMap = new JButton("Guardar Cambios en HashMap");
        btnActualizarHashMap.addActionListener(e -> guardarPreciosEnHashMap());
        pnlHashMap.add(new JLabel()); // Espaciador
        pnlHashMap.add(btnActualizarHashMap);

        // --- Panel Inferior: Historial y Cierre ---
        JPanel pnlInferior = new JPanel(new BorderLayout(0, 10));
        pnlInferior.setOpaque(false);

        JList<String> listaCola = new JList<>(listModel);
        listaCola.setBackground(new Color(30, 38, 52));
        listaCola.setForeground(new Color(201, 209, 217));
        JScrollPane scroll = new JScrollPane(listaCola);
        scroll.setPreferredSize(new Dimension(0, 150));
        scroll.setBorder(BorderFactory.createTitledBorder("Historial de Cola"));

        JButton btnCierre = new JButton("EJECUTAR CIERRE DE CAJA (DESENCOLAR FIFO)");
        btnCierre.setBackground(new Color(35, 134, 54));
        btnCierre.setForeground(Color.WHITE);
        btnCierre.addActionListener(e -> ejecutarCierreCaja());

        pnlInferior.add(scroll, BorderLayout.CENTER);
        pnlInferior.add(btnCierre, BorderLayout.SOUTH);

        // Ensamblado final
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(pnlStats, BorderLayout.NORTH);
        mainPanel.add(pnlHashMap, BorderLayout.CENTER);
        mainPanel.add(pnlInferior, BorderLayout.SOUTH);

        add(mainPanel);
        actualizarVista();
    }

    private JLabel crearLabelBlanco(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        return l;
    }

    private void guardarPreciosEnHashMap() {
        try {
            servicio.actualizarPrecioCategoria(Categoria.VIP, Double.parseDouble(txtPrecioVip.getText()));
            servicio.actualizarPrecioCategoria(Categoria.GENERAL, Double.parseDouble(txtPrecioGen.getText()));
            servicio.actualizarPrecioCategoria(Categoria.PREFERENCIAL, Double.parseDouble(txtPrecioPre.getText()));
            JOptionPane.showMessageDialog(this, "HashMap de precios actualizado con éxito.");
            actualizarVista();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese valores numéricos válidos.");
        }
    }

    private void actualizarVista() {
        int count = servicio.getColaReportes().tamano();
        lblVentasCount.setText("Ventas en Cola (FIFO): " + count);
        // El ingreso total se calcula sumando los reportes sin sacarlos de la cola
        // Para la demo, lo dejamos estático o implementamos un visor
    }

    private void ejecutarCierreCaja() {
        if (servicio.getColaReportes().estaVacia()) {
            JOptionPane.showMessageDialog(this, "No hay reportes en la cola para procesar.");
            return;
        }

        double totalSesion = 0;
        int boletosTotales = 0;
        StringBuilder detalle = new StringBuilder("=== RESUMEN DE CIERRE ===\n");

        // REQUISITO: Desencolar elementos de la Cola FIFO[cite: 1, 2]
        while (!servicio.getColaReportes().estaVacia()) {
            ReporteVenta r = servicio.getColaReportes().desencolar(); // Se saca de la estructura
            totalSesion += r.getIngresoTotal();
            boletosTotales += r.getBoletosVendidos();
            detalle.append("• ").append(r.getCategoria()).append(": $").append(r.getIngresoTotal()).append("\n");
        }

        detalle.append("\nBoletos vendidos: ").append(boletosTotales);
        detalle.append("\nIngreso Neto: $").append(String.format("%.2f", totalSesion));

        JOptionPane.showMessageDialog(this, detalle.toString(), "Cierre de Caja Exitoso", JOptionPane.INFORMATION_MESSAGE);
        actualizarVista();
    }
}