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
    private static final Color ADMIN_COLOR = new Color(99, 179, 237);   // azul claro admin

    // Colores de consola por estructura
    private static final Color LOG_HASHMAP  = new Color(255, 215,   0);
    private static final Color LOG_LISTA    = new Color( 86, 217, 249);
    private static final Color LOG_MATRIZ   = new Color(192, 132, 252);
    private static final Color LOG_COLA     = new Color(253, 186, 116);
    private static final Color LOG_ARCHIVO  = new Color(134, 239, 172);
    private static final Color LOG_INFO     = new Color(148, 163, 184);
    private static final Color LOG_OK       = new Color( 74, 222, 128);
    private static final Color LOG_ERROR    = new Color(248, 113, 113);

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

        // ── Botón Admin mejorado ──────────────────────────────────────────────
        JButton btnAdmin = buildAdminButton();
        btnAdmin.addActionListener(e -> abrirPanelAdministracion());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 16));
        right.setOpaque(false);
        right.add(btnAdmin);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    /**
     * Botón de administrador con diseño mejorado: icono de escudo, degradado,
     * borde iluminado y efecto hover/press.
     */
    private JButton buildAdminButton() {
        JButton btn = new JButton("Admin") {
            private boolean hovered = false;
            private boolean pressed = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; pressed = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                // Fondo degradado
                Color c1, c2;
                if (pressed) {
                    c1 = new Color(30, 70, 120);
                    c2 = new Color(20, 50, 95);
                } else if (hovered) {
                    c1 = new Color(50, 110, 180);
                    c2 = new Color(30, 80, 145);
                } else {
                    c1 = new Color(35, 85, 150);
                    c2 = new Color(20, 58, 110);
                }
                g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
                g2.fillRoundRect(0, 0, w, h, 10, 10);

                // Borde luminoso
                Color borderColor = hovered
                        ? new Color(99, 179, 237, 200)
                        : new Color(60, 130, 200, 120);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);

                // Brillo superior (efecto cristal)
                g2.setPaint(new GradientPaint(0, 0,
                        new Color(255, 255, 255, hovered ? 40 : 20),
                        0, h / 2,
                        new Color(255, 255, 255, 0)));
                g2.fillRoundRect(2, 2, w - 4, h / 2 - 2, 8, 8);

                // Icono de escudo (dibujo vectorial simple)
                int iconX = 10, iconY = (h - 14) / 2;
                Color iconColor = hovered ? new Color(180, 220, 255) : new Color(140, 195, 240);
                g2.setColor(iconColor);
                // Cuerpo del escudo
                int[] shieldX = { iconX + 4, iconX, iconX, iconX + 4, iconX + 8, iconX + 8 };
                int[] shieldY = { iconY, iconY + 2, iconY + 7, iconY + 12, iconY + 7, iconY + 2 };
                g2.fillPolygon(shieldX, shieldY, 6);
                // Checkmark interior
                g2.setColor(pressed ? new Color(20, 50, 95) : c2);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(iconX + 2, iconY + 6, iconX + 4, iconY + 8);
                g2.drawLine(iconX + 4, iconY + 8, iconX + 7, iconY + 4);

                // Texto
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int textX = iconX + 14;
                int textY = h / 2 + fm.getAscent() / 2 - 1;
                g2.drawString("Admin", textX, textY);

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(105, 34));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Acceder al panel de administración");
        return btn;
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
                    g2.setColor(new Color(71, 85, 105));
                    g2.drawString(e.time + "  ", 12, y);
                    int tx = 12 + fm.stringWidth(e.time + "  ");
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

        for (String s : actual) {
            if (!prevSeleccion.contains(s)) {
                int row = s.charAt(0) - 'A';
                int col = Integer.parseInt(s.substring(1)) - 1;
                log(String.format("[LISTA  ] Asiento %-3s buscado en LinkedList → estado: disponible", s), LOG_LISTA);
                log(String.format("[MATRIZ ] Posicion [%d][%d] = false (libre) → listo para ocupar", row, col), LOG_MATRIZ);
            }
        }

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

    // ── Dialog de contraseña personalizado ───────────────────────────────────
    private void abrirPanelAdministracion() {
        // Colores del diálogo
        final Color DIALOG_BG       = new Color(15, 20, 30);
        final Color DIALOG_SURFACE  = new Color(22, 30, 45);
        final Color DIALOG_BORDER   = new Color(40, 60, 100);
        final Color DIALOG_ACCENT   = new Color(60, 130, 210);
        final Color DIALOG_ACCENT2  = new Color(99, 179, 237);
        final Color DIALOG_TEXT     = new Color(210, 225, 245);
        final Color DIALOG_MUTED    = new Color(100, 130, 170);

        JDialog dialogo = new JDialog(this, "Acceso Administrativo", true);
        dialogo.setUndecorated(true);
        dialogo.setSize(360, 310);
        dialogo.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo oscuro con leve degradado
                g2.setPaint(new GradientPaint(0, 0, new Color(18, 26, 50), 0, getHeight(), DIALOG_BG));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createLineBorder(DIALOG_BORDER, 1));

        // ── Zona superior con icono y título ──────────────────────────────────
        JPanel top = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(20, 35, 80), getWidth(), 0, new Color(12, 20, 55)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(40, 65, 120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 95));
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        // Icono de candado (dibujado a mano con Graphics2D)
        JPanel iconPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;

                // Halo exterior
                g2.setPaint(new RadialGradientPaint(cx, cy, 28,
                    new float[]{0f, 1f},
                    new Color[]{new Color(60, 130, 210, 60), new Color(0, 0, 0, 0)}));
                g2.fillOval(cx - 28, cy - 28, 56, 56);

                // Círculo de fondo del icono
                g2.setPaint(new GradientPaint(cx - 20, cy - 20, new Color(35, 75, 145),
                        cx + 20, cy + 20, new Color(20, 50, 100)));
                g2.fillOval(cx - 20, cy - 20, 40, 40);
                g2.setColor(new Color(80, 150, 230, 150));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - 20, cy - 20, 40, 40);

                // Cuerpo del candado
                g2.setColor(new Color(180, 215, 255));
                g2.fillRoundRect(cx - 9, cy - 1, 18, 13, 4, 4);

                // Arco del candado
                g2.setColor(new Color(180, 215, 255));
                g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx - 7, cy - 10, 14, 14, 0, 180);

                // Ojo del candado
                g2.setColor(new Color(20, 50, 100));
                g2.fillOval(cx - 2, cy + 3, 4, 4);

                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(360, 58));
        iconPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Acceso Administrativo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(DIALOG_TEXT);
        JLabel lblSub = new JLabel("Ingresa la contraseña para continuar");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(DIALOG_MUTED);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);
        titleBox.add(lblTitle);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(lblSub);
        titlePanel.add(titleBox);

        top.add(iconPanel);
        top.add(titlePanel);

        // ── Zona central: campo de contraseña ─────────────────────────────────
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(20, 30, 10, 30));

        JLabel lblField = new JLabel("Contraseña");
        lblField.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblField.setForeground(DIALOG_ACCENT2);
        lblField.setAlignmentX(LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DIALOG_SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(DIALOG_BORDER);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        passField.setOpaque(false);
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(DIALOG_ACCENT2);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBorder(new EmptyBorder(6, 12, 6, 12));
        passField.setEchoChar('●');
        passField.setAlignmentX(LEFT_ALIGNMENT);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Etiqueta de error (oculta inicialmente)
        JLabel lblError = new JLabel("⚠  Contraseña incorrecta. Intente de nuevo.");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblError.setForeground(new Color(248, 113, 113));
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        lblError.setVisible(false);

        center.add(lblField);
        center.add(Box.createVerticalStrut(6));
        center.add(passField);
        center.add(Box.createVerticalStrut(8));
        center.add(lblError);

        // ── Zona inferior: botones ────────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(0, 0, 6, 0));

        JButton btnCancelar = buildDialogBtn("Cancelar", false, DIALOG_BORDER, DIALOG_TEXT);
        JButton btnAceptar  = buildDialogBtn("Ingresar",  true, DIALOG_ACCENT, Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(120, 36));
        btnAceptar.setPreferredSize(new Dimension(140, 36));

        final boolean[] acceso = {false};

        Runnable intentarAcceso = () -> {
            String pass = new String(passField.getPassword());
            if ("1234".equals(pass)) {
                acceso[0] = true;
                dialogo.dispose();
            } else {
                lblError.setVisible(true);
                passField.setText("");
                passField.requestFocus();
                // Animación de shake
                final int[] count = {0};
                final int origX = dialogo.getX();
                javax.swing.Timer shake = new javax.swing.Timer(30, null);
                shake.addActionListener(ev -> {
                    count[0]++;
                    int offset = (count[0] % 2 == 0) ? 6 : -6;
                    dialogo.setLocation(origX + offset, dialogo.getY());
                    if (count[0] >= 8) {
                        shake.stop();
                        dialogo.setLocation(origX, dialogo.getY());
                    }
                });
                shake.start();
            }
        };

        btnAceptar.addActionListener(e -> intentarAcceso.run());
        btnCancelar.addActionListener(e -> dialogo.dispose());
        passField.addActionListener(e -> intentarAcceso.run());

        bottom.add(btnCancelar);
        bottom.add(btnAceptar);

        root.add(top,    BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        // Panel de contenido transparente para bordes redondeados
        JPanel glass = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {}
        };
        glass.setOpaque(false);
        glass.add(root);
        dialogo.setContentPane(glass);
        dialogo.getRootPane().setDefaultButton(btnAceptar);
        SwingUtilities.invokeLater(passField::requestFocus);

        dialogo.setVisible(true);

        // --- Post-diálogo ---
        if (acceso[0]) {
            log("[INFO   ] Acceso al panel de administracion concedido", LOG_OK);
            PanelAdmin admin = new PanelAdmin(this, ventaServicio);
            admin.setVisible(true);
            actualizarEtiquetasPrecios();
            log("[HASHMAP] Precios actualizados desde panel admin", LOG_HASHMAP);
            actualizarResumen();
        }
    }

    /**
     * Botón reutilizable para el diálogo de contraseña.
     */
    private JButton buildDialogBtn(String text, boolean filled, Color color, Color textColor) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Color base = hovered ? color.brighter() : color;
                if (filled) {
                    g2.setPaint(new GradientPaint(0, 0, base, 0, getHeight(),
                            base.darker()));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    // Brillo superior
                    g2.setPaint(new GradientPaint(0, 0,
                            new Color(255, 255, 255, hovered ? 50 : 25),
                            0, getHeight() / 2,
                            new Color(255, 255, 255, 0)));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 2, 6, 6);
                } else {
                    g2.setColor(hovered
                            ? new Color(base.getRed(), base.getGreen(), base.getBlue(), 40)
                            : new Color(base.getRed(), base.getGreen(), base.getBlue(), 15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(base.brighter());
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(textColor);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        getWidth() / 2 - fm.stringWidth(getText()) / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 1);
                g2.dispose();
            }
        };
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
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

