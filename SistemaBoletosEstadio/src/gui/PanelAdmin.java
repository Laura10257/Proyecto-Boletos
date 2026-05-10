package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import modelo.Categoria;
import modelo.ReporteVenta;
import servicio.VentaServicio;

public class PanelAdmin extends JDialog {

    // ── Paleta de colores ─────────────────────────────────────────────────────
    private static final Color BG       = new Color(13,  17,  23);
    private static final Color SURFACE  = new Color(22,  27,  34);
    private static final Color CARD     = new Color(30,  38,  52);
    private static final Color BORDER   = new Color(48,  54,  61);
    private static final Color TEXT     = new Color(201, 209, 217);
    private static final Color MUTED    = new Color(139, 148, 158);
    private static final Color VIP_C    = new Color(255, 215,   0);
    private static final Color GEN_C    = new Color( 88, 166, 255);
    private static final Color PRE_C    = new Color(126, 231, 135);
    private static final Color GREEN    = new Color( 35, 134,  54);
    private static final Color DANGER   = new Color(218,  54,  51);
    // Nuevos colores para el panel admin mejorado
    private static final Color ACCENT   = new Color( 60, 130, 210);
    private static final Color ACCENT2  = new Color( 99, 179, 237);
    private static final Color BADGE_BG = new Color( 30,  55, 110);

    private final VentaServicio servicio;
    private final DefaultListModel<String> listModel;

    private JTextField txtVip, txtGen, txtPre;
    private JLabel lblIngresoTotal;
    private JLabel lblVentasCount;

    public PanelAdmin(Frame padre, VentaServicio servicio) {
        super(padre, "Panel de Administracion", true);
        this.servicio  = servicio;
        this.listModel = new DefaultListModel<>();

        setSize(560, 680);
        setResizable(false);
        setLocationRelativeTo(padre);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        actualizarVista();
    }

    // ── Header mejorado ───────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Degradado más rico
                g2.setPaint(new GradientPaint(0, 0, new Color(20, 32, 90), getWidth(), 0, new Color(10, 16, 50)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Línea de acento inferior
                g2.setPaint(new GradientPaint(0, 0, new Color(60, 130, 210, 180),
                        getWidth(), 0, new Color(30, 80, 160, 60)));
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        h.setOpaque(false);
        h.setPreferredSize(new Dimension(0, 72));
        h.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Icono de escudo (panel pintado)
        JPanel shieldIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;

                // Halo
                g2.setPaint(new RadialGradientPaint(cx, cy, 20,
                        new float[]{0f, 1f},
                        new Color[]{new Color(60, 130, 210, 55), new Color(0, 0, 0, 0)}));
                g2.fillOval(cx - 20, cy - 20, 40, 40);

                // Fondo circular
                g2.setPaint(new GradientPaint(cx - 14, cy - 14,
                        new Color(40, 85, 165), cx + 14, cy + 14, new Color(20, 50, 110)));
                g2.fillOval(cx - 16, cy - 16, 32, 32);
                g2.setColor(new Color(80, 150, 230, 140));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(cx - 16, cy - 16, 32, 32);

                // Escudo
                int[] sx = {cx - 7, cx - 7, cx, cx + 7, cx + 7};
                int[] sy = {cy - 8, cy,      cy + 10, cy, cy - 8};
                g2.setColor(new Color(175, 215, 255));
                g2.fillPolygon(sx, sy, 5);
                // Rayita interna
                g2.setColor(new Color(20, 50, 110));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 3, cy + 1, cx, cy + 4);
                g2.drawLine(cx, cy + 4, cx + 4, cy - 2);

                g2.dispose();
            }
        };
        shieldIcon.setOpaque(false);
        shieldIcon.setPreferredSize(new Dimension(44, 72));

        // Textos
        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel titulo = new JLabel("Panel de Administración");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Gestión de precios y cierre de caja");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(new Color(120, 160, 220));
        sub.setAlignmentX(LEFT_ALIGNMENT);

        textBlock.add(Box.createVerticalGlue());
        textBlock.add(titulo);
        textBlock.add(Box.createVerticalStrut(3));
        textBlock.add(sub);
        textBlock.add(Box.createVerticalGlue());

        // Badge "ADMIN"
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Fondo del badge
                g2.setColor(BADGE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(60, 130, 210, 180));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                // Texto
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.setColor(ACCENT2);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("ADMIN", getWidth() / 2 - fm.stringWidth("ADMIN") / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 1);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(54, 22));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 25));
        right.setOpaque(false);
        right.add(badge);

        JPanel leftBlock = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBlock.setOpaque(false);
        leftBlock.add(shieldIcon);
        leftBlock.add(textBlock);

        h.add(leftBlock, BorderLayout.WEST);
        h.add(right,     BorderLayout.EAST);
        return h;
    }

    // ── Cuerpo principal ──────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(18, 18, 10, 18));

        body.add(buildStatsRow());
        body.add(Box.createVerticalStrut(16));
        body.add(buildSectionTitle("ACTUALIZACION DE PRECIOS  —  HashMap"));
        body.add(Box.createVerticalStrut(8));
        body.add(buildPricesCard());
        body.add(Box.createVerticalStrut(16));
        body.add(buildSectionTitle("COLA DE REPORTES  —  FIFO"));
        body.add(Box.createVerticalStrut(8));
        body.add(buildQueueCard());

        return body;
    }

    // Fila de estadísticas
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        lblIngresoTotal = new JLabel("$0.00");
        lblIngresoTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblIngresoTotal.setForeground(new Color(126, 231, 135));

        lblVentasCount = new JLabel("0");
        lblVentasCount.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVentasCount.setForeground(new Color(88, 166, 255));

        row.add(statCard("INGRESO ACUMULADO",     lblIngresoTotal));
        row.add(statCard("VENTAS EN COLA (FIFO)", lblVentasCount));
        return row;
    }

    private JPanel statCard(String label, JLabel valor) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        valor.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valor);
        return card;
    }

    // Card de precios
    private JPanel buildPricesCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        txtVip = styledField(servicio.obtenerPrecioActual(Categoria.VIP));
        txtGen = styledField(servicio.obtenerPrecioActual(Categoria.GENERAL));
        txtPre = styledField(servicio.obtenerPrecioActual(Categoria.PREFERENCIAL));

        card.add(priceRow("VIP",          txtVip, VIP_C));
        card.add(Box.createVerticalStrut(8));
        card.add(priceRow("PREFERENCIAL", txtPre, PRE_C));
        card.add(Box.createVerticalStrut(8));
        card.add(priceRow("GENERAL",      txtGen, GEN_C));
        card.add(Box.createVerticalStrut(12));

        JButton btnGuardar = buildStyledBtn("Guardar cambios en HashMap", GREEN, true);
        btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnGuardar.addActionListener(e -> guardarPreciosEnHashMap());
        card.add(btnGuardar);

        return card;
    }

    private JPanel priceRow(String nombre, JTextField campo, Color color) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 2, 10, 10);
            }
        };
        dot.setPreferredSize(new Dimension(12, 14));
        dot.setOpaque(false);

        JLabel lbl = new JLabel(nombre);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);
        left.add(dot);
        left.add(lbl);

        row.add(left,  BorderLayout.WEST);
        row.add(campo, BorderLayout.EAST);
        campo.setPreferredSize(new Dimension(110, 26));
        return row;
    }

    // Card de cola
    private JPanel buildQueueCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JList<String> lista = new JList<>(listModel);
        lista.setBackground(new Color(20, 26, 36));
        lista.setForeground(TEXT);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lista.setSelectionBackground(new Color(48, 62, 85));
        lista.setBorder(new EmptyBorder(4, 6, 4, 6));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBackground(new Color(20, 26, 36));
        scroll.getViewport().setBackground(new Color(20, 26, 36));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.setPreferredSize(new Dimension(0, 110));

        JButton btnCierre = buildStyledBtn("EJECUTAR CIERRE DE CAJA  —  Desencolar FIFO", DANGER, true);
        btnCierre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnCierre.addActionListener(e -> ejecutarCierreCaja());

        card.add(scroll,    BorderLayout.CENTER);
        card.add(btnCierre, BorderLayout.SOUTH);
        return card;
    }

    // ── Footer mejorado ───────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, new Color(60, 130, 210, 60),
                        getWidth(), 0, new Color(0, 0, 0, 0)));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton btnCerrar = buildStyledBtn("Cerrar", BORDER, false);
        btnCerrar.setPreferredSize(new Dimension(110, 32));
        btnCerrar.addActionListener(e -> dispose());
        f.add(btnCerrar);
        return f;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JPanel buildSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(MUTED);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        p.add(lbl);
        return p;
    }

    private JPanel roundCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
        };
        card.setOpaque(false);
        card.setAlignmentX(LEFT_ALIGNMENT);
        return card;
    }

    private JTextField styledField(double valor) {
        JTextField f = new JTextField(String.valueOf((int) valor));
        f.setBackground(new Color(20, 26, 36));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(2, 6, 2, 6)
        ));
        f.setHorizontalAlignment(JTextField.RIGHT);
        return f;
    }

    /**
     * Botón con diseño consistente: degradado, hover, borde y texto centrado.
     */
    private JButton buildStyledBtn(String text, Color color, boolean filled) {
        JButton btn = new JButton(text) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Color c = hovered ? color.brighter() : color;
                if (filled) {
                    g2.setPaint(new GradientPaint(0, 0, c, 0, getHeight(), c.darker()));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    // Brillo superior
                    g2.setPaint(new GradientPaint(0, 0,
                            new Color(255, 255, 255, hovered ? 45 : 20),
                            0, getHeight() / 2, new Color(255, 255, 255, 0)));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 2, 6, 6);
                } else {
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), hovered ? 50 : 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(c.brighter());
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                }
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    getWidth()/2 - fm.stringWidth(getText())/2,
                    getHeight()/2 + fm.getAscent()/2 - 2);
                g2.dispose();
            }
        };
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Lógica (sin cambios) ──────────────────────────────────────────────────
    private void guardarPreciosEnHashMap() {
        try {
            servicio.actualizarPrecioCategoria(Categoria.VIP,          Double.parseDouble(txtVip.getText()));
            servicio.actualizarPrecioCategoria(Categoria.GENERAL,      Double.parseDouble(txtGen.getText()));
            servicio.actualizarPrecioCategoria(Categoria.PREFERENCIAL, Double.parseDouble(txtPre.getText()));
            JOptionPane.showMessageDialog(this, "HashMap de precios actualizado con exito.");
            actualizarVista();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese valores numericos validos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarVista() {
        int count = servicio.getColaReportes().tamano();
        lblVentasCount.setText(String.valueOf(count));

        listModel.clear();
        if (count == 0) {
            listModel.addElement("  Sin reportes en cola");
        }
    }

    private void ejecutarCierreCaja() {
        if (servicio.getColaReportes().estaVacia()) {
            JOptionPane.showMessageDialog(this, "No hay reportes en la cola para procesar.");
            return;
        }

        double totalSesion    = 0;
        int    boletosTotales = 0;
        StringBuilder detalle = new StringBuilder("=== RESUMEN DE CIERRE ===\n\n");

        while (!servicio.getColaReportes().estaVacia()) {
            ReporteVenta r = servicio.getColaReportes().desencolar();
            totalSesion    += r.getIngresoTotal();
            boletosTotales += r.getBoletosVendidos();
            detalle.append("• ").append(r.getCategoria())
                   .append(": $").append(String.format("%.2f", r.getIngresoTotal())).append("\n");
        }

        detalle.append("\nBoletos vendidos : ").append(boletosTotales);
        detalle.append("\nIngreso neto     : $").append(String.format("%.2f", totalSesion));

        lblIngresoTotal.setText(String.format("$%,.2f", totalSesion));
        JOptionPane.showMessageDialog(this, detalle.toString(), "Cierre de Caja Exitoso", JOptionPane.INFORMATION_MESSAGE);
        actualizarVista();
    }
}

