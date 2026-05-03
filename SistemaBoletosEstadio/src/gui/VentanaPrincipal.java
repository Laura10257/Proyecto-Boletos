package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
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

    // Colores de consola por estructura
    private static final Color LOG_HASHMAP  = new Color(255, 215,   0);  // dorado
    private static final Color LOG_LISTA    = new Color( 86, 217, 249);  // cyan
    private static final Color LOG_MATRIZ   = new Color(192, 132, 252);  // violeta
    private static final Color LOG_COLA     = new Color(253, 186, 116);  // naranja
    private static final Color LOG_ARCHIVO  = new Color(134, 239, 172);  // verde claro
    private static final Color LOG_INFO     = new Color(148, 163, 184);  // gris
    private static final Color LOG_OK       = new Color( 74, 222, 128);  // verde
    private static final Color LOG_ERROR    = new Color(248, 113, 113);  // rojo

    private final VentaServicio ventaServicio;
    private final PanelAsientos panelAsientos;
    private final PanelCompra   panelCompra;

    private Categoria categoriaActual = Categoria.VIP;
    private JButton btnVIP, btnGeneral, btnPreferencial;
    private JLabel  lblCatNombre, lblDisponibles;
    private JLabel  lblPriceVIP, lblPriceGen, lblPricePre;

    // Consola de actividad
    private final Deque<LogEntry> logEntries = new ArrayDeque<>();
    private static final int MAX_LOG = 5;
    private JPanel consolePanel;
    private Set<String> prevSeleccion = new HashSet<>();

    public VentanaPrincipal() {
        super("Sistema de Boletos — Estadio");
        this.ventaServicio = new VentaServicio();
        this.panelAsientos = new PanelAsientos();
        this.panelCompra   = new PanelCompra();

        configurarLookAndFeel();
        configurarVentana();
        ensamblarUI();
        enlazarEventos();
        log("[INFO   ] Sistema iniciado | VentaServicio creado | 3 LinkedList inicializadas", LOG_INFO);
        log("[HASHMAP] Precios cargados → VIP: $1,500 | PREF: $1,100 | GEN: $800", LOG_HASHMAP);
        cargarCategoriaSeleccionada();
    }

    private void configurarLookAndFeel() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 760);
        setMinimumSize(new Dimension(900, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DEEP);
        setLayout(new BorderLayout(0, 0));
    }

    private void ensamblarUI() {
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);
        add(buildCenter(),    BorderLayout.CENTER);
        add(panelCompra,      BorderLayout.EAST);
        add(buildConsole(),   BorderLayout.SOUTH);
        panelCompra.setPreferredSize(new Dimension(240, 0));
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(18, 26, 80), getWidth(), 0, new Color(10, 14, 40)));
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
        JLabel sub = new JLabel("Estadio Toluca  —  Gestion de Entradas");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(MUTED);
        titles.add(title); titles.add(sub);
        left.add(titles);

        JButton btnAdmin = new JButton("Admin") {
            { setContentAreaFilled(false); setBorderPainted(false);
              setForeground(MUTED); setFont(new Font("Segoe UI", Font.PLAIN, 11));
              setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
        };
        btnAdmin.addActionListener(e -> abrirPanelAdministracion());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 22));
        right.setOpaque(false);
        right.add(btnAdmin);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(BG_SURFACE);
        sidebar.setPreferredSize(new Dimension(188, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JPanel inner = new JPanel();
        inner.setBackground(BG_SURFACE);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(20, 10, 20, 10));

        inner.add(microLabel("CATEGORIAS"));
        inner.add(Box.createVerticalStrut(12));

        btnVIP          = buildCatBtn("VIP",          "Zona Premium",    VIP_COLOR, Categoria.VIP);
        btnGeneral      = buildCatBtn("GENERAL",      "Zona Estandar",   GEN_COLOR, Categoria.GENERAL);
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
            @Override protected void paintComponent(Graphics g) {
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

        lblPriceVIP = new JLabel(); lblPricePre = new JLabel(); lblPriceGen = new JLabel();
        actualizarEtiquetasPrecios();
        p.add(priceRow("VIP",  lblPriceVIP, VIP_COLOR)); p.add(Box.createVerticalStrut(5));
        p.add(priceRow("PREF", lblPricePre, PRE_COLOR)); p.add(Box.createVerticalStrut(5));
        p.add(priceRow("GEN",  lblPriceGen, GEN_COLOR));
        return p;
    }

    // ── Centro ────────────────────────────────────────────────────────────────
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
        p.add(legendItem("Ocupado",      new Color(55,  62,  72)));
        return p;
    }

    private JPanel legendItem(String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
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
        p.add(dot); p.add(lbl);
        return p;
    }

    // ── Consola de actividad ──────────────────────────────────────────────────
    private JPanel buildConsole() {
        consolePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);

                int lineH = 18;
                int startY = 10;
                Font mono = new Font("Consolas", Font.PLAIN, 11);
                g2.setFont(mono);
                FontMetrics fm = g2.getFontMetrics();

                LogEntry[] entries = logEntries.toArray(new LogEntry[0]);
                for (int i = 0; i < entries.length; i++) {
                    LogEntry e = entries[i];
                    int y = startY + i * lineH + fm.getAscent();
                    // Timestamp
                    g2.setColor(new Color(71, 85, 105));
                    g2.drawString(e.time + "  ", 12, y);
                    int tx = 12 + fm.stringWidth(e.time + "  ");
                    // Mensaje con color
                    g2.setColor(e.color);
                    g2.drawString(e.message, tx, y);
                }
                g2.dispose();
            }
        };

        consolePanel.setBackground(new Color(8, 12, 18));
        consolePanel.setPreferredSize(new Dimension(0, 112));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(8, 12, 18));
        wrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        // Header de la consola
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(12, 16, 24));
        header.setBorder(new EmptyBorder(3, 12, 3, 12));
        JLabel lbl = new JLabel("CONSOLA DE ACTIVIDAD");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(71, 85, 105));
        JLabel hint = new JLabel("Muestra las estructuras de datos en uso");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        hint.setForeground(new Color(51, 65, 85));
        header.add(lbl,  BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);

        wrapper.add(header,       BorderLayout.NORTH);
        wrapper.add(consolePanel, BorderLayout.CENTER);
        return wrapper;
    }

    // Agrega una entrada a la consola
    private void log(String mensaje, Color color) {
        String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        if (logEntries.size() >= MAX_LOG) logEntries.pollFirst();
        logEntries.addLast(new LogEntry(time, mensaje, color));
        if (consolePanel != null) consolePanel.repaint();
    }

    // ── Eventos ───────────────────────────────────────────────────────────────
    private void enlazarEventos() {
        panelAsientos.setOnSeleccionCambiada(this::actualizarResumen);
        panelCompra.getBtnConfirmar().addActionListener(e -> confirmarCompra());
        panelCompra.getBtnLimpiar().addActionListener(e -> {
            panelAsientos.limpiarSeleccion();
            log("[INFO   ] Seleccion limpiada por el usuario", LOG_INFO);
            actualizarResumen();
        });
    }

    private void seleccionarCategoria(Categoria cat) {
        categoriaActual = cat;
        prevSeleccion.clear();
        btnVIP.repaint(); btnGeneral.repaint(); btnPreferencial.repaint();
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

        int[] dim = switch (categoriaActual) {
            case VIP          -> new int[]{3, 5};
            case GENERAL      -> new int[]{6, 8};
            case PREFERENCIAL -> new int[]{4, 6};
        };
        double precio = ventaServicio.obtenerPrecioActual(categoriaActual);
        log(String.format("[HASHMAP] Categoria: %-12s | Precio consultado: $%.0f", categoriaActual, precio), LOG_HASHMAP);
        log(String.format("[MATRIZ ] Mapa cargado: %dx%d | %d asientos disponibles", dim[0], dim[1], disponibles.size()), LOG_MATRIZ);

        actualizarResumen();
    }

    private void actualizarResumen() {
        List<String> seleccionados = panelAsientos.getAsientosSeleccionados();
        Set<String>  actual        = new LinkedHashSet<>(seleccionados);

        // Detectar asientos recién seleccionados
        for (String s : actual) {
            if (!prevSeleccion.contains(s)) {
                int row = s.charAt(0) - 'A';
                int col = Integer.parseInt(s.substring(1)) - 1;
                log(String.format("[LISTA  ] Asiento %-3s buscado en LinkedList → estado: disponible", s), LOG_LISTA);
                log(String.format("[MATRIZ ] Posicion [%d][%d] = false (libre) → listo para ocupar", row, col), LOG_MATRIZ);
            }
        }

        // Detectar asientos deseleccionados
        for (String s : prevSeleccion) {
            if (!actual.contains(s)) {
                log("[INFO   ] Asiento " + s + " deseleccionado por el usuario", LOG_INFO);
            }
        }

        prevSeleccion = new HashSet<>(actual);

        double total = seleccionados.isEmpty() ? 0 : ventaServicio.calcularTotal(categoriaActual, seleccionados);
        if (!seleccionados.isEmpty()) {
            log(String.format("[HASHMAP] Total calculado: $%.2f (%d asiento(s) x $%.0f)",
                total, seleccionados.size(), total / seleccionados.size()), LOG_HASHMAP);
        }

        panelCompra.actualizarSeleccion(seleccionados, total);
        lblDisponibles.setText(panelAsientos.getDisponiblesActuales().size() + " disponibles");
    }

    private void confirmarCompra() {
        List<String> seleccionados = panelAsientos.getAsientosSeleccionados();
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona al menos un asiento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            ReporteVenta reporte = ventaServicio.confirmarCompra(categoriaActual, seleccionados, "reportes");

            log(String.format("[LISTA  ] LinkedList actualizada: %d boleto(s) marcado(s) como vendidos", seleccionados.size()), LOG_LISTA);
            log("[MATRIZ ] Posiciones marcadas como true (ocupadas) en boolean[][]", LOG_MATRIZ);
            log(String.format("[COLA   ] Reporte encolado en Cola FIFO | Total en cola: %d", ventaServicio.getColaReportes().tamano()), LOG_COLA);
            log("[ARCHIVO] Guardado en reportes/" + util.FechaUtil.nombreArchivoDelDia(), LOG_ARCHIVO);

            prevSeleccion.clear();
            DialogoTicket ticket = new DialogoTicket(this, reporte, seleccionados);
            ticket.setVisible(true);
            cargarCategoriaSeleccionada();
        } catch (Exception ex) {
            log("[ERROR  ] " + ex.getMessage(), LOG_ERROR);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirPanelAdministracion() {
        String pass = JOptionPane.showInputDialog(this, "Contrasena Admin:", "Acceso", JOptionPane.QUESTION_MESSAGE);
        if ("1234".equals(pass)) {
            log("[INFO   ] Acceso al panel de administracion concedido", LOG_OK);
            PanelAdmin admin = new PanelAdmin(this, ventaServicio);
            admin.setVisible(true);
            actualizarEtiquetasPrecios();
            log("[HASHMAP] Precios actualizados desde panel admin", LOG_HASHMAP);
            actualizarResumen();
        } else if (pass != null) {
            log("[ERROR  ] Contrasena incorrecta en intento de acceso admin", LOG_ERROR);
            JOptionPane.showMessageDialog(this, "Contrasena incorrecta.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEtiquetasPrecios() {
        lblPriceVIP.setText("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.VIP)));
        lblPricePre.setText("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.PREFERENCIAL)));
        lblPriceGen.setText("$" + String.format("%.0f", ventaServicio.obtenerPrecioActual(Categoria.GENERAL)));
        lblPriceVIP.setForeground(Color.WHITE); lblPriceVIP.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblPricePre.setForeground(Color.WHITE); lblPricePre.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblPriceGen.setForeground(Color.WHITE); lblPriceGen.setFont(new Font("Segoe UI", Font.BOLD, 10));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JButton buildCatBtn(String name, String sub, Color color, Categoria cat) {
        JButton btn = new JButton() {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = categoriaActual == cat;
                if (active) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 90));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    g2.setColor(color);
                    g2.fillRoundRect(0, 9, 3, getHeight()-18, 2, 2);
                } else if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 10));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(color);
                g2.fillOval(12, getHeight()/2 - 5, 10, 10);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(active ? Color.WHITE : TEXT);
                g2.drawString(name, 28, getHeight()/2 - 2);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(MUTED);
                g2.drawString(sub, 28, getHeight()/2 + 10);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(168, 52));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> seleccionarCategoria(cat));
        return btn;
    }

    private JPanel priceRow(String cat, JLabel lblPrice, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel l = new JLabel(cat);
        l.setForeground(color); l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        row.add(l, BorderLayout.WEST); row.add(lblPrice, BorderLayout.EAST);
        return row;
    }

    private JLabel microLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Log entry ─────────────────────────────────────────────────────────────
    private static class LogEntry {
        final String time, message;
        final Color  color;
        LogEntry(String t, String m, Color c) { time = t; message = m; color = c; }
    }
}
