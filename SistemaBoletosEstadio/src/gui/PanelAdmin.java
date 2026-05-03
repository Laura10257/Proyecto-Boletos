package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import modelo.Categoria;
import modelo.ReporteVenta;
import servicio.VentaServicio;

public class PanelAdmin extends JDialog {

    // Paleta
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

    private final VentaServicio servicio;
    private final DefaultListModel<String> listModel;

    private JTextField txtVip, txtGen, txtPre;
    private JLabel lblIngresoTotal;
    private JLabel lblVentasCount;

    public PanelAdmin(Frame padre, VentaServicio servicio) {
        super(padre, "Panel de Administracion", true);
        this.servicio  = servicio;
        this.listModel = new DefaultListModel<>();

        setSize(560, 660);
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
                g2.setPaint(new GradientPaint(0, 0, new Color(18, 26, 80), getWidth(), 0, new Color(10, 14, 40)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        h.setOpaque(false);
        h.setPreferredSize(new Dimension(0, 60));
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(0, 20, 0, 20)
        ));

        JLabel titulo = new JLabel("PANEL DE ADMINISTRACION");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Gestion de precios y cierre de caja");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(MUTED);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        left.add(titulo);
        left.add(sub);
        h.add(left, BorderLayout.CENTER);
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

        row.add(statCard("INGRESO ACUMULADO", lblIngresoTotal));
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

        JButton btnGuardar = styledBtn("Guardar cambios en HashMap", GREEN, true);
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

        JButton btnCierre = styledBtn("EJECUTAR CIERRE DE CAJA  —  Desencolar FIFO", DANGER, true);
        btnCierre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnCierre.addActionListener(e -> ejecutarCierreCaja());

        card.add(scroll,    BorderLayout.CENTER);
        card.add(btnCierre, BorderLayout.SOUTH);
        return card;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        f.setBackground(SURFACE);
        f.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton btnCerrar = styledBtn("Cerrar", BORDER, false);
        btnCerrar.setPreferredSize(new Dimension(100, 30));
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

    private JButton styledBtn(String text, Color color, boolean filled) {
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
                    g2.setColor(c);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
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
                g2.drawString(getText(), getWidth()/2 - fm.stringWidth(getText())/2,
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

        double totalSesion   = 0;
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
