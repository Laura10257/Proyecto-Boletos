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

        JButton btnAdmin = new JButton("⚙") {
            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                setForeground(MUTED);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
        btnAdmin.addActionListener(e -> abrirPanelAdministracion());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        right.setOpaque(false);
        right.add(btnAdmin);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

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

        inner.add(buildPriceCard());
        sidebar.add(inner, BorderLayout.NORTH);
        return sidebar;
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
        p.add(microLabel("PRECIOS (HASHMAP)"));
        p.add(Box.createVerticalStrut(8));
        
        lblPriceVIP = new JLabel();
        lblPricePre = new JLabel();
        lblPriceGen = new JLabel();
        
        actualizarEtiquetasPrecios();

        p.add(priceRow("VIP", lblPriceVIP, VIP_COLOR));
        p.add(Box.createVerticalStrut(5));
        p.add(priceRow("PREF", lblPricePre, PRE_COLOR));
        p.add(Box.createVerticalStrut(5));
        p.add(priceRow("GEN", lblPriceGen, GEN_COLOR));
        return p;
    }

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

    private void enlazarEventos() {
        panelAsientos.setOnSeleccionCambiada(this::actualizarResumen);
        panelCompra.getBtnConfirmar().addActionListener(e -> confirmarCompra());
        panelCompra.getBtnLimpiar().addActionListener(e -> {
            panelAsientos.limpiarSeleccion();
            actualizarResumen();
        });
    }

    private void confirmarCompra() {
        List<String> seleccionados = panelAsientos.getAsientosSeleccionados();
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona al menos un asiento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ReporteVenta reporte = ventaServicio.confirmarCompra(categoriaActual, seleccionados, "reportes");
            DialogoTicket ticket = new DialogoTicket(this, reporte, seleccionados);
            ticket.setVisible(true);
            cargarCategoriaSeleccionada();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirPanelAdministracion() {
        String pass = JOptionPane.showInputDialog(this, "Contraseña Admin:", "Acceso", JOptionPane.QUESTION_MESSAGE);
        if ("1234".equals(pass)) {
            PanelAdmin admin = new PanelAdmin(this, ventaServicio);
            admin.setVisible(true);
            actualizarEtiquetasPrecios();
            actualizarResumen();
        } else if (pass != null) {
            JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEtiquetasPrecios() {
        lblPriceVIP.setText("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.VIP)));
        lblPricePre.setText("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.PREFERENCIAL)));
        lblPriceGen.setText("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.GENERAL)));
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
                    g2.setColor(color);
                    g2.fillRoundRect(0, 9, 3, getHeight() - 18, 2, 2);
                } else if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 10));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(active ? Color.WHITE : TEXT);
                g2.drawString(name, 29, getHeight() / 2 - 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(168, 52));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.addActionListener(e -> seleccionarCategoria(cat));
        return btn;
    }

    private void seleccionarCategoria(Categoria cat) {
        categoriaActual = cat;
        cargarCategoriaSeleccionada();
    }

    private void cargarCategoriaSeleccionada() {
        List<String> disponibles = ventaServicio.obtenerAsientosDisponibles(categoriaActual);
        panelAsientos.refrescar(categoriaActual, disponibles);
        lblCatNombre.setText(categoriaActual.toString());
        actualizarResumen();
    }

    private void actualizarResumen() {
        List<String> seleccionados = panelAsientos.getAsientosSeleccionados();
        double total = seleccionados.isEmpty() ? 0 : ventaServicio.calcularTotal(categoriaActual, seleccionados);
        panelCompra.actualizarSeleccion(seleccionados, total);
        lblDisponibles.setText(panelAsientos.getDisponiblesActuales().size() + " disponibles");
    }

    private JPanel priceRow(String cat, JLabel lblPrice, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel l = new JLabel(cat);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblPrice.setForeground(Color.WHITE);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 10));
        row.add(l, BorderLayout.WEST);
        row.add(lblPrice, BorderLayout.EAST);
        return row;
    }

    private JLabel microLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(MUTED);
        return lbl;
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout());
        p.setOpaque(false);
        p.add(new JLabel("Leyenda: [D] Disponible [S] Seleccionado [O] Ocupado"));
        return p;
    }

    private JPanel buildStatusBar() {
        JPanel sb = new JPanel(new BorderLayout());
        sb.setBackground(BG_SURFACE);
        sb.setPreferredSize(new Dimension(0, 26));
        sb.add(new JLabel(" v2.0 - Sistema de Gestión de Estadio"), BorderLayout.WEST);
        return sb;
    }
}