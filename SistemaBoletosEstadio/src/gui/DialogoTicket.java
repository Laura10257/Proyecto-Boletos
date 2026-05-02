/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.ReporteVenta;
import java.time.format.DateTimeFormatter;

public class DialogoTicket extends JDialog {

    private final ReporteVenta reporte;
    private final java.util.List<String> asientos;

    public DialogoTicket(Frame padre, ReporteVenta reporte, java.util.List<String> asientos) {
        super(padre, "Ticket de Compra", true);
        this.reporte = reporte;
        this.asientos = asientos;
        
        setUndecorated(true); // Elimina bordes estándar de Windows para un diseño limpio
        setBackground(new Color(0, 0, 0, 0)); // Transparencia para bordes redondeados
        
        initComponents();
        pack();
        setLocationRelativeTo(padre);
    }

    private void initComponents() {
        // Panel principal que dibuja el ticket físico
        JPanel ticketPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dibujar el cuerpo del ticket (color papel/crema)
                g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Línea de puntos decorativa (simulación de precorte)
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
                g2.drawLine(20, 110, getWidth() - 20, 110);
                
                g2.dispose();
            }
        };
        ticketPanel.setLayout(new BoxLayout(ticketPanel, BoxLayout.Y_AXIS));
        ticketPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        ticketPanel.setPreferredSize(new Dimension(380, 520));

        // Contenido del Ticket
        JLabel lblTitulo = new JLabel("ESTADIO TOLUCA");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("COMPROBANTE DE ACCESO");
        lblSubtitulo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

        // Espaciador para la línea de puntos
        ticketPanel.add(lblTitulo);
        ticketPanel.add(lblSubtitulo);
        ticketPanel.add(Box.createVerticalStrut(60));

        // Detalles de la venta
        ticketPanel.add(createDetailRow("FECHA:", reporte.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        ticketPanel.add(createDetailRow("HORA:", reporte.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        ticketPanel.add(Box.createVerticalStrut(15));
        ticketPanel.add(createDetailRow("CATEGORÍA:", reporte.getCategoria().toString()));
        ticketPanel.add(createDetailRow("ASIENTOS:", asientos.toString()));
        ticketPanel.add(Box.createVerticalStrut(25));
        
        // Total destacado
        JLabel lblTotal = new JLabel("TOTAL: $" + String.format("%.2f", reporte.getIngresoTotal()));
        lblTotal.setFont(new Font("Monospaced", Font.BOLD, 24));
        lblTotal.setAlignmentX(CENTER_ALIGNMENT);
        ticketPanel.add(lblTotal);

        ticketPanel.add(Box.createVerticalGlue());

        // Botón de cerrar con diseño minimalista
        JButton btnCerrar = new JButton("ACEPTAR");
        btnCerrar.setAlignmentX(CENTER_ALIGNMENT);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBackground(new Color(40, 40, 40));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.addActionListener(e -> dispose());
        ticketPanel.add(btnCerrar);

        add(ticketPanel);
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Monospaced", Font.BOLD, 13));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Monospaced", Font.PLAIN, 13));
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(300, 25));
        return row;
    }
}
