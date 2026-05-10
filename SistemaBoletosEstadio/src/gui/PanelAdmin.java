package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import modelo.Categoria;
import modelo.ReporteVenta;
import servicio.VentaServicio;

public class PanelAdmin extends JDialog {

    private static final Color BG       = new Color(10,  14,  22);
    private static final Color SURFACE  = new Color(16,  22,  34);
    private static final Color CARD     = new Color(22,  30,  46);
    private static final Color BORDER   = new Color(38,  50,  70);
    private static final Color TEXT     = new Color(210, 218, 228);
    private static final Color MUTED    = new Color(110, 130, 160);
    private static final Color VIP_C    = new Color(255, 215,   0);
    private static final Color GEN_C    = new Color( 88, 166, 255);
    private static final Color PRE_C    = new Color(126, 231, 135);
    private static final Color GREEN    = new Color( 35, 134,  54);
    private static final Color DANGER   = new Color(200,  50,  50);
    private static final Color ACCENT   = new Color( 60, 130, 210);
    private static final Color ACCENT2  = new Color( 99, 179, 237);
    private static final Color BADGE_BG = new Color( 22,  48, 100);

    private final VentaServicio servicio;
    private final DefaultListModel<String> listModel;

    private JTextField txtVip, txtGen, txtPre;
    private JLabel lblIngresoTotal, lblVentasCount;

    public PanelAdmin(Frame padre, VentaServicio servicio) {
        super(padre, "Panel de Administración", true);
        this.servicio  = servicio;
        this.listModel = new DefaultListModel<>();
        setSize(570, 690);
        setResizable(false);
        setLocationRelativeTo(padre);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
        actualizarVista();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(18, 28, 80),
                        getWidth(), 0, new Color(8, 14, 44)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, new Color(60, 130, 210, 0),
                        getWidth() / 2f, 0, new Color(60, 130, 210, 210)));
                g2.fillRect(0, getHeight() - 2, getWidth() / 2, 2);
                g2.setPaint(new GradientPaint(getWidth() / 2f, 0, new Color(60, 130, 210, 210),
                        getWidth(), 0, new Color(60, 130, 210, 0)));
                g2.fillRect(getWidth() / 2, getHeight() - 2, getWidth() / 2, 2);
                g2.dispose();
            }
        };
        h.setOpaque(false);
        h.setPreferredSize(new Dimension(0, 74));
        h.setBorder(new EmptyBorder(0, 20, 0, 20));

        JPanel shieldIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setPaint(new RadialGradientPaint(cx, cy, 20, new float[]{0f, 1f},
                        new Color[]{new Color(60, 130, 210, 55), new Color(0, 0, 0, 0)}));
                g2.fillOval(cx - 20, cy - 20, 40, 40);
                g2.setPaint(new GradientPaint(cx - 14, cy - 14, new Color(38, 82, 160),
                        cx + 14, cy + 14, new Color(18, 48, 108)));
                g2.fillOval(cx - 16, cy - 16, 32, 32);
                g2.setColor(new Color(80, 150, 230, 140));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(cx - 16, cy - 16, 32, 32);
                int[] sx = {cx-7,cx-7,cx,cx+7,cx+7};
                int[] sy = {cy-8,cy,cy+10,cy,cy-8};
                g2.setColor(new Color(175, 215, 255));
                g2.fillPolygon(sx, sy, 5);
                g2.setColor(new Color(18, 48, 108));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 3, cy + 1, cx, cy + 4);
                g2.drawLine(cx, cy + 4, cx + 4, cy - 2);
                g2.dispose();
            }
        };
        shieldIcon.setOpaque(false);
        shieldIcon.setPreferredSize(new Dimension(44, 74));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Panel de Administración");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Gestión de precios y cierre de caja");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(new Color(110, 155, 220));
        sub.setAlignmentX(LEFT_ALIGNMENT);
        textBlock.add(Box.createVerticalGlue());
        textBlock.add(titulo);
        textBlock.add(Box.createVerticalStrut(3));
        textBlock.add(sub);
        textBlock.add(Box.createVerticalGlue());

        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(BADGE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(60, 130, 210, 180));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.setColor(ACCENT2);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("ADMIN", getWidth()/2 - fm.stringWidth("ADMIN")/2,
                        getHeight()/2 + fm.getAscent()/2 - 1);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(54, 22));

        JPanel rightH = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 26));
        rightH.setOpaque(false);
        rightH.add(badge);

        JPanel leftH = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftH.setOpaque(false);
        leftH.add(shieldIcon);
        leftH.add(textBlock);

        h.add(leftH,  BorderLayout.WEST);
        h.add(rightH, BorderLayout.EAST);
        return h;
    }

    // ── Cuerpo ────────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(18, 18, 10, 18));
        body.add(buildStatsRow());
        body.add(Box.createVerticalStrut(18));
        body.add(buildSectionTitle("ACTUALIZACIÓN DE PRECIOS  —  HashMap"));
        body.add(Box.createVerticalStrut(8));
        body.add(buildPricesCard());
        body.add(Box.createVerticalStrut(18));
        body.add(buildSectionTitle("COLA DE REPORTES  —  FIFO"));
        body.add(Box.createVerticalStrut(8));
        body.add(buildQueueCard());
        return body;
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        lblIngresoTotal = new JLabel("$0.00");
        lblIngresoTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblIngresoTotal.setForeground(new Color(126, 231, 135));
        lblVentasCount = new JLabel("0");
        lblVentasCount.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVentasCount.setForeground(new Color(88, 166, 255));
        row.add(statCard("INGRESO ACUMULADO",     lblIngresoTotal, new Color(35, 134, 54)));
        row.add(statCard("VENTAS EN COLA (FIFO)", lblVentasCount,  new Color(60, 130, 210)));
        return row;
    }

    private JPanel statCard(String label, JLabel valor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 3, getHeight(), 3, 3);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 18, 12, 14));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(5));
        card.add(valor);
        return card;
    }

    private JPanel buildPricesCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));
        txtVip = styledField(servicio.obtenerPrecioActual(Categoria.VIP));
        txtGen = styledField(servicio.obtenerPrecioActual(Categoria.GENERAL));
        txtPre = styledField(servicio.obtenerPrecioActual(Categoria.PREFERENCIAL));
        card.add(priceRow("VIP",          txtVip, VIP_C));
        card.add(Box.createVerticalStrut(9));
        card.add(priceRow("PREFERENCIAL", txtPre, PRE_C));
        card.add(Box.createVerticalStrut(9));
        card.add(priceRow("GENERAL",      txtGen, GEN_C));
        card.add(Box.createVerticalStrut(14));
        JButton btnGuardar = buildStyledBtn("Guardar cambios en HashMap", GREEN, true);
        btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
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
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 2, color, 10, 12, color.darker()));
                g2.fillOval(0, 2, 10, 10);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillOval(2, 3, 4, 4);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(12, 14));
        dot.setOpaque(false);
        JLabel lbl = new JLabel(nombre);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);
        left.add(dot); left.add(lbl);
        row.add(left,  BorderLayout.WEST);
        row.add(campo, BorderLayout.EAST);
        campo.setPreferredSize(new Dimension(115, 27));
        return row;
    }

    private JPanel buildQueueCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        JList<String> lista = new JList<>(listModel);
        lista.setBackground(new Color(14, 20, 32));
        lista.setForeground(TEXT);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lista.setSelectionBackground(new Color(38, 58, 88));
        lista.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBackground(new Color(14, 20, 32));
        scroll.getViewport().setBackground(new Color(14, 20, 32));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.setPreferredSize(new Dimension(0, 115));
        JButton btnCierre = buildStyledBtn("EJECUTAR CIERRE DE CAJA  —  Desencolar FIFO", DANGER, true);
        btnCierre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnCierre.addActionListener(e -> ejecutarCierreCaja());
        card.add(scroll,    BorderLayout.CENTER);
        card.add(btnCierre, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JButton btnCerrar = buildStyledBtn("Cerrar", BORDER, false);
        btnCerrar.setPreferredSize(new Dimension(115, 33));
        btnCerrar.addActionListener(e -> dispose());
        f.add(btnCerrar);
        return f;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JPanel buildSectionTitle(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(80, 120, 180));
        p.add(lbl);
        return p;
    }

    private JPanel roundCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setAlignmentX(LEFT_ALIGNMENT);
        return card;
    }

    private JTextField styledField(double valor) {
        JTextField f = new JTextField(String.valueOf((int) valor));
        f.setBackground(new Color(14, 20, 32));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(2, 8, 2, 8)));
        f.setHorizontalAlignment(JTextField.RIGHT);
        return f;
    }

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
                    g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, hovered ? 45 : 20),
                            0, getHeight() / 2, new Color(255, 255, 255, 0)));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 2, 6, 6);
                } else {
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), hovered ? 50 : 18));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(Math.min(255, c.getRed() + 50),
                            Math.min(255, c.getGreen() + 50), Math.min(255, c.getBlue() + 50), 160));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), getWidth()/2 - fm.stringWidth(getText())/2,
                    getHeight()/2 + fm.getAscent()/2 - 2);
                g2.dispose();
            }
        };
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Lógica ────────────────────────────────────────────────────────────────
    private void guardarPreciosEnHashMap() {
        try {
            servicio.actualizarPrecioCategoria(Categoria.VIP,          Double.parseDouble(txtVip.getText()));
            servicio.actualizarPrecioCategoria(Categoria.GENERAL,      Double.parseDouble(txtGen.getText()));
            servicio.actualizarPrecioCategoria(Categoria.PREFERENCIAL, Double.parseDouble(txtPre.getText()));
            // ✅ REEMPLAZA: JOptionPane.showMessageDialog(this, "HashMap de precios actualizado con éxito.");
            DialogoMensaje.exito(this, "HashMap de precios actualizado con éxito.");
            actualizarVista();
        } catch (NumberFormatException e) {
            // ✅ REEMPLAZA: JOptionPane.showMessageDialog(this, "Error: Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            DialogoMensaje.error(this, "Error: Ingrese valores numéricos válidos.");
        }
    }

    private void actualizarVista() {
        int count = servicio.getColaReportes().tamano();
        lblVentasCount.setText(String.valueOf(count));
        listModel.clear();
        if (count == 0) listModel.addElement("  Sin reportes en cola");
    }

    private void ejecutarCierreCaja() {
        if (servicio.getColaReportes().estaVacia()) {
            // ✅ REEMPLAZA: JOptionPane.showMessageDialog(this, "No hay reportes en la cola para procesar.");
            DialogoMensaje.aviso(this, "No hay reportes en la cola para procesar.");
            return;
        }
        double totalSesion = 0; int boletosTotales = 0;
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
        // ✅ REEMPLAZA: JOptionPane.showMessageDialog(this, detalle.toString(), "Cierre de Caja Exitoso", JOptionPane.INFORMATION_MESSAGE);
        DialogoMensaje.info(this, detalle.toString(), "Cierre de Caja Exitoso");
        actualizarVista();
    }
}

