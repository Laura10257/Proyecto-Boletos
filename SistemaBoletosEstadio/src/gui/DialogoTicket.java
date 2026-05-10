/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import modelo.ReporteVenta;

public class DialogoTicket extends JDialog {

    private static final Color TICKET_BG   = new Color(248, 248, 245);
    private static final Color TICKET_DARK = new Color(30,  30,  30);
    private static final Color TICKET_MUTED= new Color(110, 110, 110);
    private static final Color TICKET_LINE = new Color(200, 200, 200);
    private static final Color ACCENT_GREEN= new Color( 22, 140,  60);
    private static final Color ACCENT_BLUE = new Color( 30,  80, 160);

    private final ReporteVenta reporte;
    private final List<String> asientos;

    public DialogoTicket(Frame padre, ReporteVenta reporte, List<String> asientos) {
        super(padre, "Ticket de Compra", true);
        this.reporte  = reporte;
        this.asientos = asientos;
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        initComponents();
        pack();
        setLocationRelativeTo(padre);
    }

    private void initComponents() {
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra exterior
                for (int i = 8; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, 10 * i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 20, 20);
                }
                g2.dispose();
            }
        };
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel ticket = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Cuerpo blanco crema
                g2.setColor(TICKET_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Franja superior de color
                g2.setPaint(new GradientPaint(0, 0, ACCENT_BLUE, getWidth(), 0, new Color(30, 120, 80)));
                g2.fillRoundRect(0, 0, getWidth(), 62, 16, 16);
                g2.fillRect(0, 46, getWidth(), 16);

                // Línea de puntos (corte)
                g2.setColor(TICKET_LINE);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                        0, new float[]{6, 4}, 0));
                g2.drawLine(20, 125, getWidth() - 20, 125);

                // Círculos de semicorte en los lados
                g2.setColor(new Color(230, 230, 230));
                g2.fillOval(-12, 119, 24, 12);
                g2.fillOval(getWidth() - 12, 119, 24, 12);

                // Borde exterior
                g2.setColor(new Color(220, 220, 215));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

                g2.dispose();
            }
        };
        ticket.setLayout(new BoxLayout(ticket, BoxLayout.Y_AXIS));
        ticket.setBorder(new EmptyBorder(0, 0, 28, 0));
        ticket.setPreferredSize(new Dimension(390, 540));
        ticket.setOpaque(false);

        // ── Zona de franja superior (blanca sobre azul) ───────────────────────
        JPanel topZone = new JPanel();
        topZone.setOpaque(false);
        topZone.setLayout(new BoxLayout(topZone, BoxLayout.Y_AXIS));
        topZone.setBorder(new EmptyBorder(14, 28, 14, 28));
        topZone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel lblEstadio = new JLabel("ESTADIO TOLUCA");
        lblEstadio.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblEstadio.setForeground(Color.WHITE);
        lblEstadio.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("COMPROBANTE DE ACCESO");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(new Color(200, 230, 255));
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        topZone.add(lblEstadio);
        topZone.add(lblSub);

        // ── Zona de datos (bajo la línea de puntos) ───────────────────────────
        JPanel dataZone = new JPanel();
        dataZone.setOpaque(false);
        dataZone.setLayout(new BoxLayout(dataZone, BoxLayout.Y_AXIS));
        dataZone.setBorder(new EmptyBorder(24, 32, 16, 32));

        // Fecha y hora
        String fecha = reporte.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String hora  = reporte.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        dataZone.add(buildDetailRow("FECHA",       fecha,                      false));
        dataZone.add(Box.createVerticalStrut(8));
        dataZone.add(buildDetailRow("HORA",        hora,                       false));
        dataZone.add(Box.createVerticalStrut(14));
        dataZone.add(buildDivider());
        dataZone.add(Box.createVerticalStrut(14));
        dataZone.add(buildDetailRow("CATEGORÍA",   reporte.getCategoria().toString(), false));
        dataZone.add(Box.createVerticalStrut(8));

        // Asientos en chips
        JLabel lblAsLbl = new JLabel("ASIENTOS");
        lblAsLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblAsLbl.setForeground(TICKET_MUTED);
        lblAsLbl.setAlignmentX(LEFT_ALIGNMENT);
        dataZone.add(lblAsLbl);
        dataZone.add(Box.createVerticalStrut(6));
        dataZone.add(buildChipsPanel());
        dataZone.add(Box.createVerticalStrut(18));
        dataZone.add(buildDivider());
        dataZone.add(Box.createVerticalStrut(16));

        // Total
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lblTotalTxt = new JLabel("TOTAL PAGADO");
        lblTotalTxt.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTotalTxt.setForeground(TICKET_MUTED);
        JLabel lblTotalVal = new JLabel("$" + String.format("%,.2f", reporte.getIngresoTotal()));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalVal.setForeground(ACCENT_GREEN);
        totalRow.add(lblTotalTxt, BorderLayout.WEST);
        totalRow.add(lblTotalVal, BorderLayout.EAST);
        totalRow.setAlignmentX(LEFT_ALIGNMENT);
        dataZone.add(totalRow);

        // Botón aceptar
        dataZone.add(Box.createVerticalStrut(22));
        JButton btnCerrar = buildTicketBtn();
        btnCerrar.setAlignmentX(CENTER_ALIGNMENT);
        btnCerrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnCerrar.addActionListener(e -> dispose());
        dataZone.add(btnCerrar);

        ticket.add(topZone);
        // Espacio para la zona de puntos (pintada en paintComponent)
        ticket.add(Box.createVerticalStrut(64));
        ticket.add(dataZone);

        outer.add(ticket, BorderLayout.CENTER);
        add(outer);
    }

    private JPanel buildDetailRow(String label, String value, boolean highlight) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(TICKET_MUTED);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", highlight ? Font.BOLD : Font.PLAIN, 13));
        val.setForeground(highlight ? ACCENT_GREEN : TICKET_DARK);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel buildChipsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        for (String a : asientos) {
            JLabel chip = new JLabel(a) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(220, 240, 255));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    g2.setColor(new Color(30, 80, 160, 80));
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            chip.setFont(new Font("Segoe UI", Font.BOLD, 11));
            chip.setForeground(ACCENT_BLUE);
            chip.setBorder(new EmptyBorder(3, 10, 3, 10));
            chip.setOpaque(false);
            p.add(chip);
        }
        return p;
    }

    private JPanel buildDivider() {
        JPanel d = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(TICKET_LINE);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        d.setOpaque(false);
        d.setPreferredSize(new Dimension(0, 1));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setAlignmentX(LEFT_ALIGNMENT);
        return d;
    }

    private JButton buildTicketBtn() {
        JButton btn = new JButton("ACEPTAR") {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0,
                        hovered ? new Color(25, 160, 75) : ACCENT_GREEN,
                        0, getHeight(),
                        hovered ? new Color(15, 110, 50) : new Color(15, 100, 45)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, hovered ? 45 : 25),
                        0, getHeight() / 2, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(3, 2, getWidth() - 6, getHeight() / 2, 7, 7);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    getWidth() / 2 - fm.stringWidth(getText()) / 2,
                    getHeight() / 2 + fm.getAscent() / 2 - 1);
                g2.dispose();
            }
        };
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
