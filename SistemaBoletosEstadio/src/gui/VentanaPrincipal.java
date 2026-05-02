package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import modelo.Categoria;
import modelo.ReporteVenta;
import servicio.VentaServicio;

public class VentanaPrincipal extends JFrame {

    // Constantes de diseño originales
    private static final Color BG_DEEP    = new Color(13,  17,  23);
    private static final Color BG_SURFACE = new Color(22,  27,  34);
    private static final Color BORDER     = new Color(48,  54,  61);
    private static final Color TEXT       = new Color(201, 209, 217);
    private static final Color MUTED      = new Color(139, 148, 158);
    private static final Color VIP_COLOR  = new Color(255, 215,   0);
    private static final Color GEN_COLOR  = new Color( 88, 166, 255);
    private static final Color PRE_COLOR  = new Color(126, 231, 135);
    private static final Color BG_CARD    = new Color( 30,  38,  52);

    private final VentaServicio ventaServicio;
    private final PanelAsientos panelAsientos;
    private final PanelCompra   panelCompra;

    private Categoria categoriaActual = Categoria.VIP;
    private JButton btnVIP, btnGeneral, btnPreferencial;
    private JLabel  lblCatNombre;
    private JLabel  lblDisponibles;

    // Componentes nuevos para la gestión de precios
    private JLabel lblPriceVIP, lblPriceGen, lblPricePre;

    public VentanaPrincipal() {
        super("Sistema de Boletos — Estadio");
        this.ventaServicio = new VentaServicio();
        this.panelAsientos = new PanelAsientos();
        this.panelCompra   = new PanelCompra();

        configurarLookAndFeel();
        configurarVentana();
        ensamblarUI();
        enlazarEventos();
        cargarCategoriaSeleccionada();
    }

    private void configurarLookAndFeel() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DEEP);
        setLayout(new BorderLayout(0, 0));
    }

    private void ensamblarUI() {
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);
        add(buildCenter(),    BorderLayout.CENTER);
        add(panelCompra,      BorderLayout.EAST);
        add(buildStatusBar(), BorderLayout.SOUTH);
        panelCompra.setPreferredSize(new Dimension(240, 0));
    }

    //  Header ──────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(18, 26, 80), getWidth(), 0, new Color(10, 14, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 68));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(0, 22, 0, 22)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        JLabel title = new JLabel("SISTEMA DE BOLETOS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Estadio Municipal  —  Gestión de Entradas");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(MUTED);
        titles.add(title);
        titles.add(sub);
        left.add(titles);

        String fecha = new java.text.SimpleDateFormat(
            "EEEE dd 'de' MMMM yyyy", java.util.Locale.of("es", "MX"))
            .format(new java.util.Date());
        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFecha.setForeground(MUTED);

        header.add(left,     BorderLayout.WEST);
        header.add(lblFecha, BorderLayout.EAST);
        return header;
    }

    // Sidebar 
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(BG_SURFACE);
        sidebar.setPreferredSize(new Dimension(188, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JPanel inner = new JPanel();
        inner.setBackground(BG_SURFACE);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(20, 10, 20, 10));

        inner.add(microLabel("CATEGORÍAS"));
        inner.add(Box.createVerticalStrut(12));

        btnVIP          = buildCatBtn("VIP",          "Zona Premium",    VIP_COLOR, Categoria.VIP);
        btnGeneral      = buildCatBtn("GENERAL",      "Zona Estándar",   GEN_COLOR, Categoria.GENERAL);
        btnPreferencial = buildCatBtn("PREFERENCIAL", "Zona Preferente", PRE_COLOR, Categoria.PREFERENCIAL);

        inner.add(btnVIP);          inner.add(Box.createVerticalStrut(6));
        inner.add(btnGeneral);      inner.add(Box.createVerticalStrut(6));
        inner.add(btnPreferencial); inner.add(Box.createVerticalStrut(24));

        // PUNTO IMPORTANTE: Panel visual para mostrar precios del HashMap
        inner.add(buildPriceCard());
        
        // REQUISITO: Botón de Administración para actualizar precios
        inner.add(Box.createVerticalStrut(20));
        JButton btnAdmin = new JButton("Actualizar Precios");
        btnAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdmin.addActionListener(e -> mostrarDialogoActualizarPrecios());
        inner.add(btnAdmin);

        sidebar.add(inner, BorderLayout.NORTH);
        return sidebar;
    }

    private void mostrarDialogoActualizarPrecios() {
        JTextField fVip = new JTextField(String.valueOf(ventaServicio.obtenerPrecioActual(Categoria.VIP)));
        JTextField fGen = new JTextField(String.valueOf(ventaServicio.obtenerPrecioActual(Categoria.GENERAL)));
        JTextField fPre = new JTextField(String.valueOf(ventaServicio.obtenerPrecioActual(Categoria.PREFERENCIAL)));

        Object[] message = {
            "Nuevo Precio VIP:", fVip,
            "Nuevo Precio General:", fGen,
            "Nuevo Precio Preferencial:", fPre
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Actualizar HashMap de Precios", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                ventaServicio.actualizarPrecioCategoria(Categoria.VIP, Double.parseDouble(fVip.getText()));
                ventaServicio.actualizarPrecioCategoria(Categoria.GENERAL, Double.parseDouble(fGen.getText()));
                ventaServicio.actualizarPrecioCategoria(Categoria.PREFERENCIAL, Double.parseDouble(fPre.getText()));
                
                // Refrescar etiquetas visuales
                lblPriceVIP.setText("$" + fVip.getText());
                lblPriceGen.setText("$" + fGen.getText());
                lblPricePre.setText("$" + fPre.getText());
                actualizarResumen();
                
                JOptionPane.showMessageDialog(this, "Precios actualizados correctamente.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Ingrese valores válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton buildCatBtn(String name, String sub, Color color, Categoria cat) {
        JButton btn = new JButton() {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = categoriaActual == cat;
                if (active) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 90));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.setColor(color);
                    g2.fillRoundRect(0, 9, 3, getHeight() - 18, 2, 2);
                } else if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 10));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(color);
                g2.fillOval(12, getHeight() / 2 - 5, 10, 10);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(active ? Color.WHITE : TEXT);
                g2.drawString(name, 29, getHeight() / 2 - 2);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(MUTED);
                g2.drawString(sub, 29, getHeight() / 2 + 10);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(168, 52));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> seleccionarCategoria(cat));
        return btn;
    }

    private JPanel buildPriceCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(10, 12, 10, 12));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(microLabel("PRECIOS ACTUALES"));
        p.add(Box.createVerticalStrut(8));
        
        lblPriceVIP = new JLabel("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.VIP)));
        lblPricePre = new JLabel("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.PREFERENCIAL)));
        lblPriceGen = new JLabel("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.GENERAL)));

        p.add(priceRow("VIP", lblPriceVIP, VIP_COLOR));
        p.add(Box.createVerticalStrut(5));
        p.add(priceRow("PREF", lblPricePre, PRE_COLOR));
        p.add(Box.createVerticalStrut(5));
        p.add(priceRow("GEN", lblPriceGen, GEN_COLOR));
        return p;
    }

    private JPanel priceRow(String label, JLabel priceLabel, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lCat = new JLabel(label);
        lCat.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lCat.setForeground(color);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        priceLabel.setForeground(Color.WHITE);
        row.add(lCat,   BorderLayout.WEST);
        row.add(priceLabel, BorderLayout.EAST);
        return row;
    }

    // Center Status Bar 
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(BG_DEEP);
        center.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        lblCatNombre = new JLabel("VIP — Zona Premium");
        lblCatNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCatNombre.setForeground(VIP_COLOR);

        lblDisponibles = new JLabel("");
        lblDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDisponibles.setForeground(MUTED);

        topBar.add(lblCatNombre,   BorderLayout.WEST);
        topBar.add(lblDisponibles, BorderLayout.EAST);

        center.add(topBar,        BorderLayout.NORTH);
        center.add(panelAsientos, BorderLayout.CENTER);
        center.add(buildLegend(), BorderLayout.SOUTH);
        return center;
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 4));
        p.setOpaque(false);
        p.add(legendItem("Disponible",   new Color(35, 134, 54)));
        p.add(legendItem("Seleccionado", new Color(210, 153, 34)));
        p.add(legendItem("Ocupado",      new Color(55, 62, 72)));
        return p;
    }

    private JPanel legendItem(String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
            }
        };
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(MUTED);
        p.add(dot);
        p.add(lbl);
        return p;
    }

    private JPanel buildStatusBar() {
        JPanel sb = new JPanel(new BorderLayout());
        sb.setBackground(BG_SURFACE);
        sb.setPreferredSize(new Dimension(0, 26));
        sb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(0, 14, 0, 14)
        ));
        JLabel msg = new JLabel("Sistema listo  •  Haz clic en un asiento para seleccionarlo");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        msg.setForeground(MUTED);
        JLabel ver = new JLabel("v2.0");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ver.setForeground(new Color(65, 73, 85));
        sb.add(msg, BorderLayout.WEST);
        sb.add(ver, BorderLayout.EAST);
        return sb;
    }

    private JLabel microLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Events ───────────────────────────────────────────────────────────────
    private void enlazarEventos() {
        panelAsientos.setOnSeleccionCambiada(this::actualizarResumen);
        panelCompra.getBtnConfirmar().addActionListener(e -> confirmarCompra());
        panelCompra.getBtnLimpiar().addActionListener(e -> {
            panelAsientos.limpiarSeleccion();
            actualizarResumen();
        });
    }

    private void seleccionarCategoria(Categoria cat) {
        categoriaActual = cat;
        btnVIP.repaint();
        btnGeneral.repaint();
        btnPreferencial.repaint();
        cargarCategoriaSeleccionada();
    }

    private void cargarCategoriaSeleccionada() {
        List<String> disponibles = ventaServicio.obtenerAsientosDisponibles(categoriaActual);
        panelAsientos.refrescar(categoriaActual, disponibles);

        String nombre = switch (categoriaActual) {
            case VIP          -> "VIP — Zona Premium";
            case GENERAL      -> "General — Zona Estandar";
            case PREFERENCIAL -> "Preferencial — Zona Preferente";
        };
        Color color = switch (categoriaActual) {
            case VIP          -> VIP_COLOR;
            case GENERAL      -> GEN_COLOR;
            case PREFERENCIAL -> PRE_COLOR;
        };
        lblCatNombre.setText(nombre);
        lblCatNombre.setForeground(color);
        lblDisponibles.setText(disponibles.size() + " disponible" + (disponibles.size() != 1 ? "s" : ""));

        actualizarResumen();
    }

    private void actualizarResumen() {
        List<String> seleccionados = panelAsientos.getAsientosSeleccionados();
        double total = 0;
        if (!seleccionados.isEmpty()) {
            total = ventaServicio.calcularTotal(categoriaActual, seleccionados);
        }
        panelCompra.actualizarSeleccion(seleccionados, total);

        int disp = panelAsientos.getDisponiblesActuales().size();
        lblDisponibles.setText(disp + " disponible" + (disp != 1 ? "s" : ""));
    }

    private void confirmarCompra() {
        List<String> seleccionados = panelAsientos.getAsientosSeleccionados();
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selecciona al menos un asiento antes de confirmar.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            ReporteVenta reporte = ventaServicio.confirmarCompra(categoriaActual, seleccionados, "reportes");
            String msg = "<html><body style='font-family:Segoe UI;padding:6px'>"
                + "<b style='font-size:13px'>Compra exitosa</b><br><br>"
                + "Categoría: <b>" + reporte.getCategoria() + "</b><br>"
                + "Asientos: <b>" + seleccionados + "</b><br>"
                + "Total pagado: <b>$" + String.format("%,.2f", reporte.getIngresoTotal()) + "</b>"
                + "</body></html>";
            JOptionPane.showMessageDialog(this, msg, "Compra Confirmada", JOptionPane.INFORMATION_MESSAGE);
            cargarCategoriaSeleccionada();
        } catch (IllegalArgumentException | IllegalStateException | java.io.UncheckedIOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en compra", JOptionPane.ERROR_MESSAGE);
        }
    }
}
