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

    private static final Color BG_DEEP    = new Color(10,  13,  20);
    private static final Color BG_SURFACE = new Color(18,  23,  32);
    private static final Color BORDER     = new Color(40,  48,  58);
    private static final Color TEXT       = new Color(210, 218, 228);
    private static final Color MUTED      = new Color(120, 132, 148);
    private static final Color VIP_COLOR  = new Color(255, 215,   0);
    private static final Color GEN_COLOR  = new Color( 88, 166, 255);
    private static final Color PRE_COLOR  = new Color(126, 231, 135);
    private static final Color BG_CARD    = new Color( 24,  32,  46);
    private static final Color ACCENT     = new Color( 60, 130, 210);

    private static final Color LOG_HASHMAP = new Color(255, 215,   0);
    private static final Color LOG_LISTA   = new Color( 86, 217, 249);
    private static final Color LOG_MATRIZ  = new Color(192, 132, 252);
    private static final Color LOG_COLA    = new Color(253, 186, 116);
    private static final Color LOG_ARCHIVO = new Color(134, 239, 172);
    private static final Color LOG_INFO    = new Color(148, 163, 184);
    private static final Color LOG_OK      = new Color( 74, 222, 128);
    private static final Color LOG_ERROR   = new Color(248, 113, 113);

    // ── Datos del partido seleccionado ────────────────────────────────────────
    private final String partidoLocal;
    private final String partidoVisitante;
    private final String partidoFecha;
    private final String partidoHora;
    private final String partidoTorneo;
    private final String partidoTag;

    private final VentaServicio ventaServicio;
    private final PanelAsientos panelAsientos;
    private final PanelCompra   panelCompra;

    private Categoria categoriaActual = Categoria.VIP;
    private JButton btnVIP, btnGeneral, btnPreferencial;
    private JLabel  lblCatNombre, lblDisponibles;
    private JLabel  lblPriceVIP, lblPriceGen, lblPricePre;

    private final Deque<LogEntry> logEntries = new ArrayDeque<>();
    private static final int MAX_LOG = 5;
    private JPanel consolePanel;
    private Set<String> prevSeleccion = new HashSet<>();

    private final float[] particleX     = new float[18];
    private final float[] particleY     = new float[18];
    private final float[] particleSpeed = new float[18];
    private final float[] particleAlpha = new float[18];
    private JPanel headerPanel;

    // ── Constructores ─────────────────────────────────────────────────────────
    /** Constructor de compatibilidad: valores por defecto si se invoca sin partido */
    public VentanaPrincipal() {
        this("Deportivo Toluca FC", "Club América",
             "Sábado 17 de Mayo, 2026", "20:00 hrs",
             "Liga MX — Clausura 2026", "CLÁSICO");
    }

    /** Constructor principal: recibe los datos del partido elegido en SelectorPartido */
    public VentanaPrincipal(String local, String visitante,
                             String fecha,  String hora,
                             String torneo, String tag) {
        super("Sistema de Boletos — Estadio");
        this.partidoLocal     = local;
        this.partidoVisitante = visitante;
        this.partidoFecha     = fecha;
        this.partidoHora      = hora;
        this.partidoTorneo    = torneo;
        this.partidoTag       = tag;

        this.ventaServicio = new VentaServicio();
        this.panelAsientos = new PanelAsientos();
        this.panelCompra   = new PanelCompra();

        initParticles();
        configurarLookAndFeel();
        configurarVentana();
        ensamblarUI();
        enlazarEventos();
        iniciarAnimaciones();
        log("[INFO   ] Sistema iniciado | VentaServicio creado | 3 LinkedList inicializadas", LOG_INFO);
        log("[HASHMAP] Precios cargados → VIP: $1,500 | PREF: $1,100 | GEN: $800", LOG_HASHMAP);
        log("[PARTIDO] " + local + " vs " + visitante + "  |  " + fecha + "  " + hora, LOG_OK);
        cargarCategoriaSeleccionada();
    }

    private void initParticles() {
        Random rnd = new Random();
        for (int i = 0; i < particleX.length; i++) {
            particleX[i]     = rnd.nextFloat() * 1100;
            particleY[i]     = rnd.nextFloat() * 68;
            particleSpeed[i] = 0.15f + rnd.nextFloat() * 0.3f;
            particleAlpha[i] = 0.1f  + rnd.nextFloat() * 0.4f;
        }
    }

    private void iniciarAnimaciones() {
        new javax.swing.Timer(50, e -> {
            for (int i = 0; i < particleX.length; i++) {
                particleX[i] -= particleSpeed[i];
                if (particleX[i] < 0) particleX[i] = 1100;
            }
            if (headerPanel != null) headerPanel.repaint();
        }).start();
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
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildCenter(),  BorderLayout.CENTER);
        add(panelCompra,    BorderLayout.EAST);
        add(buildConsole(), BorderLayout.SOUTH);
        panelCompra.setPreferredSize(new Dimension(248, 0));
    }

    // ── Header con partículas + banner del partido ────────────────────────────
    private JPanel buildHeader() {
        headerPanel = new JPanel(new BorderLayout(20, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setPaint(new GradientPaint(0, 0, new Color(12, 18, 55),
                        getWidth(), 0, new Color(6, 10, 32)));
                g2.fillRect(0, 0, getWidth(), getHeight());

                for (int i = 0; i < particleX.length; i++) {
                    int alpha = (int)(particleAlpha[i] * 180);
                    g2.setColor(new Color(80, 160, 255, alpha));
                    int sz = 2 + (i % 3);
                    g2.fillOval((int)particleX[i], (int)particleY[i], sz, sz);
                }

                g2.setPaint(new GradientPaint(0, 0, new Color(60, 130, 210, 0),
                        getWidth() / 2f, 0, new Color(60, 130, 210, 200)));
                g2.fillRect(0, getHeight() - 2, getWidth() / 2, 2);
                g2.setPaint(new GradientPaint(getWidth() / 2f, 0, new Color(60, 130, 210, 200),
                        getWidth(), 0, new Color(60, 130, 210, 0)));
                g2.fillRect(getWidth() / 2, getHeight() - 2, getWidth() / 2, 2);

                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(0, 92));
        headerPanel.setBorder(new EmptyBorder(0, 22, 0, 22));

        // ── Lado izquierdo: logo + título ──────────────────────────────────────
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JPanel iconPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setPaint(new GradientPaint(cx - 16, cy - 16, new Color(30, 80, 160),
                        cx + 16, cy + 16, new Color(15, 45, 100)));
                g2.fillOval(cx - 18, cy - 18, 36, 36);
                g2.setColor(new Color(80, 160, 255, 120));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(cx - 18, cy - 18, 36, 36);
                g2.setColor(new Color(160, 210, 255));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx - 10, cy - 8, 20, 16, 0, 180);
                g2.drawArc(cx - 14, cy - 10, 28, 20, 0, 180);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(cx - 14, cy + 2, cx + 14, cy + 2);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(44, 92));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        JLabel title = new JLabel("SISTEMA DE BOLETOS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Estadio Toluca  —  Gestión de Entradas");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(100, 140, 200));
        titles.add(title);
        titles.add(sub);
        left.add(iconPanel);
        left.add(titles);

        // ── Centro: banner del partido seleccionado ────────────────────────────
        JPanel centerBanner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int cx = getWidth() / 2, cy = getHeight() / 2;

                // Cápsula de fondo
                g2.setColor(new Color(20, 32, 58, 200));
                g2.fillRoundRect(cx - 220, cy - 28, 440, 56, 12, 12);
                g2.setColor(new Color(50, 88, 160, 120));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(cx - 220, cy - 28, 440, 56, 12, 12);

                // Tag del partido
                String tag = partidoTag;
                Color tagC = switch (tag) {
                    case "CLÁSICO" -> new Color(255, 200,  50);
                    case "COPA"    -> new Color(126, 231, 135);
                    default        -> new Color( 88, 166, 255);
                };
                g2.setColor(new Color(tagC.getRed(), tagC.getGreen(), tagC.getBlue(), 28));
                g2.fillRoundRect(cx - 214, cy - 22, 50, 18, 5, 5);
                g2.setColor(new Color(tagC.getRed(), tagC.getGreen(), tagC.getBlue(), 140));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(cx - 214, cy - 22, 50, 18, 5, 5);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
                g2.setColor(tagC);
                FontMetrics fmT = g2.getFontMetrics();
                g2.drawString(tag, cx - 214 + 25 - fmT.stringWidth(tag) / 2, cy - 22 + 12);

                // Nombre del partido (VS)
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(Color.WHITE);
                String vs = partidoLocal + "   vs   " + partidoVisitante;
                FontMetrics fmV = g2.getFontMetrics();
                g2.drawString(vs, cx - fmV.stringWidth(vs) / 2, cy - 3);

                // Fecha y hora
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(100, 145, 205));
                String info = partidoFecha + "   ·   " + partidoHora + "   ·   " + partidoTorneo;
                FontMetrics fmI = g2.getFontMetrics();
                g2.drawString(info, cx - fmI.stringWidth(info) / 2, cy + 16);

                g2.dispose();
            }
        };
        centerBanner.setOpaque(false);

        // ── Lado derecho: botón Admin ──────────────────────────────────────────
        JButton btnAdmin = buildAdminButton();
        btnAdmin.addActionListener(e -> abrirPanelAdministracion());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 29));
        right.setOpaque(false);
        right.add(btnAdmin);

        headerPanel.add(left,         BorderLayout.WEST);
        headerPanel.add(centerBanner, BorderLayout.CENTER);
        headerPanel.add(right,        BorderLayout.EAST);
        return headerPanel;
    }

    private JButton buildAdminButton() {
        JButton btn = new JButton("Admin") {
            private boolean hovered = false;
            private boolean pressed = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)   { hovered = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                Color c1 = pressed ? new Color(25, 60, 110) : hovered ? new Color(50, 110, 180) : new Color(35, 85, 150);
                Color c2 = pressed ? new Color(15, 40, 80)  : hovered ? new Color(30, 78,  145) : new Color(20, 55, 110);
                g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setColor(hovered ? new Color(99, 179, 237, 200) : new Color(60, 130, 200, 120));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, hovered ? 40 : 20),
                        0, h / 2, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(2, 2, w - 4, h / 2 - 2, 8, 8);
                int ix = 10, iy = (h - 14) / 2;
                Color ic = hovered ? new Color(180, 220, 255) : new Color(140, 195, 240);
                g2.setColor(ic);
                int[] sx = {ix+4,ix,ix,ix+4,ix+8,ix+8};
                int[] sy = {iy,iy+2,iy+7,iy+12,iy+7,iy+2};
                g2.fillPolygon(sx, sy, 6);
                g2.setColor(c2);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(ix+2, iy+6, ix+4, iy+8);
                g2.drawLine(ix+4, iy+8, ix+7, iy+4);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Admin", ix + 14, h / 2 + fm.getAscent() / 2 - 1);
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
        sidebar.setPreferredSize(new Dimension(196, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JPanel inner = new JPanel();
        inner.setBackground(BG_SURFACE);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(22, 12, 22, 12));

        inner.add(sectionLabel("CATEGORÍAS"));
        inner.add(Box.createVerticalStrut(12));

        btnVIP          = buildCatBtn("VIP",          "Zona Premium",    VIP_COLOR, Categoria.VIP);
        btnGeneral      = buildCatBtn("GENERAL",      "Zona Estándar",   GEN_COLOR, Categoria.GENERAL);
        btnPreferencial = buildCatBtn("PREFERENCIAL", "Zona Preferente", PRE_COLOR, Categoria.PREFERENCIAL);

        inner.add(btnVIP);          inner.add(Box.createVerticalStrut(7));
        inner.add(btnGeneral);      inner.add(Box.createVerticalStrut(7));
        inner.add(btnPreferencial); inner.add(Box.createVerticalStrut(28));

        inner.add(buildSeparator());
        inner.add(Box.createVerticalStrut(16));
        inner.add(buildPriceCard());
        inner.add(Box.createVerticalStrut(20));
        inner.add(buildInfoCard());
        sidebar.add(inner, BorderLayout.NORTH);
        return sidebar;
    }

    private JPanel buildSeparator() {
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(40, 48, 58, 0),
                        getWidth() / 2f, 0, new Color(60, 130, 210, 150)));
                g2.fillRect(0, 0, getWidth() / 2, 1);
                g2.setPaint(new GradientPaint(getWidth() / 2f, 0, new Color(60, 130, 210, 150),
                        getWidth(), 0, new Color(40, 48, 58, 0)));
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, 1);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(172, 1));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JPanel buildPriceCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setPaint(new GradientPaint(0, 0, new Color(60, 130, 210, 40),
                        getWidth(), getHeight(), new Color(0, 0, 0, 0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setAlignmentX(LEFT_ALIGNMENT);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel dot = new JLabel("●");
        dot.setForeground(ACCENT);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 7));
        JLabel lbl = sectionLabel("PRECIOS — HASHMAP");
        titleRow.add(dot);
        titleRow.add(lbl);
        p.add(titleRow);
        p.add(Box.createVerticalStrut(10));

        lblPriceVIP = new JLabel();
        lblPricePre = new JLabel();
        lblPriceGen = new JLabel();
        actualizarEtiquetasPrecios();
        p.add(priceRow("VIP",  lblPriceVIP, VIP_COLOR)); p.add(Box.createVerticalStrut(6));
        p.add(priceRow("PREF", lblPricePre, PRE_COLOR)); p.add(Box.createVerticalStrut(6));
        p.add(priceRow("GEN",  lblPriceGen, GEN_COLOR));
        return p;
    }

    private JPanel buildInfoCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 30, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(40, 80, 40, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(10, 14, 10, 14));
        p.setAlignmentX(LEFT_ALIGNMENT);
        JLabel hint = new JLabel("<html><center>Haz clic en un asiento<br>para seleccionarlo</center></html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        hint.setForeground(new Color(100, 150, 120));
        hint.setAlignmentX(CENTER_ALIGNMENT);
        p.add(hint);
        return p;
    }

    // ── Centro ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(BG_DEEP);
        center.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 8, 0));

        lblCatNombre = new JLabel("VIP — Zona Premium");
        lblCatNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCatNombre.setForeground(VIP_COLOR);

        lblDisponibles = new JLabel("");
        lblDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDisponibles.setForeground(MUTED);

        JPanel pill = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 35, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setBorder(new EmptyBorder(3, 10, 3, 10));
        JLabel dot = new JLabel("● ");
        dot.setForeground(PRE_COLOR);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        pill.add(dot);
        pill.add(lblDisponibles);
        new javax.swing.Timer(800, e -> {
            dot.setForeground(dot.getForeground().equals(PRE_COLOR)
                    ? new Color(PRE_COLOR.getRed(), PRE_COLOR.getGreen(), PRE_COLOR.getBlue(), 80)
                    : PRE_COLOR);
        }).start();

        topBar.add(lblCatNombre, BorderLayout.WEST);
        topBar.add(pill,         BorderLayout.EAST);

        center.add(topBar,        BorderLayout.NORTH);
        center.add(panelAsientos, BorderLayout.CENTER);
        center.add(buildLegend(), BorderLayout.SOUTH);
        return center;
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 6));
        p.setOpaque(false);
        p.add(legendItem("Disponible",   new Color(35, 134, 54)));
        p.add(legendItem("Seleccionado", new Color(210, 153, 34)));
        p.add(legendItem("Ocupado",      new Color(55, 62, 72)));
        return p;
    }

    private JPanel legendItem(String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() / 2, 2, 2);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(13, 13));
        dot.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(MUTED);
        p.add(dot);
        p.add(lbl);
        return p;
    }

    // ── Consola ───────────────────────────────────────────────────────────────
    private JPanel buildConsole() {
        consolePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int lineH = 18, startY = 10;
                Font mono = new Font("Consolas", Font.PLAIN, 11);
                g2.setFont(mono);
                FontMetrics fm = g2.getFontMetrics();
                LogEntry[] entries = logEntries.toArray(new LogEntry[0]);
                for (int i = 0; i < entries.length; i++) {
                    LogEntry e = entries[i];
                    int y = startY + i * lineH + fm.getAscent();
                    g2.setColor(new Color(55, 70, 90));
                    g2.drawString(e.time + "  ", 14, y);
                    int tx = 14 + fm.stringWidth(e.time + "  ");
                    g2.setColor(e.color);
                    g2.drawString(e.message, tx, y);
                }
                g2.dispose();
            }
        };
        consolePanel.setBackground(new Color(6, 9, 15));
        consolePanel.setPreferredSize(new Dimension(0, 110));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(6, 9, 15));
        wrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(10, 14, 22));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(ACCENT);
                g2.fillRect(0, 0, 2, getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(4, 14, 4, 14));
        JLabel lbl = new JLabel("CONSOLA DE ACTIVIDAD");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(60, 100, 160));
        JLabel hint = new JLabel("Estructuras de datos en uso");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        hint.setForeground(new Color(40, 60, 95));
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
            log("[INFO   ] Selección limpiada por el usuario", LOG_INFO);
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
            case GENERAL      -> "General — Zona Estándar";
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
        log(String.format("[HASHMAP] Categoria: %-12s | Precio consultado: $%.0f",
                categoriaActual, precio), LOG_HASHMAP);
        log(String.format("[MATRIZ ] Mapa cargado: %dx%d | %d asientos disponibles",
                dim[0], dim[1], disponibles.size()), LOG_MATRIZ);
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
            if (!actual.contains(s))
                log("[INFO   ] Asiento " + s + " deseleccionado por el usuario", LOG_INFO);
        }
        prevSeleccion = new HashSet<>(actual);
        double total  = seleccionados.isEmpty() ? 0
                : ventaServicio.calcularTotal(categoriaActual, seleccionados);
        if (!seleccionados.isEmpty())
            log(String.format("[HASHMAP] Total calculado: $%.2f (%d asiento(s) x $%.0f)",
                    total, seleccionados.size(), total / seleccionados.size()), LOG_HASHMAP);
        panelCompra.actualizarSeleccion(seleccionados, total);
        lblDisponibles.setText(panelAsientos.getDisponiblesActuales().size() + " disponibles");
    }

    private void confirmarCompra() {
        List<String> seleccionados = panelAsientos.getAsientosSeleccionados();
        if (seleccionados.isEmpty()) {
            DialogoMensaje.aviso(this, "Selecciona al menos un asiento.");
            return;
        }
        try {
            ReporteVenta reporte = ventaServicio.confirmarCompra(
                    categoriaActual, seleccionados, "reportes");
            log(String.format("[LISTA  ] LinkedList actualizada: %d boleto(s) marcado(s) como vendidos",
                    seleccionados.size()), LOG_LISTA);
            log("[MATRIZ ] Posiciones marcadas como true (ocupadas) en boolean[][]", LOG_MATRIZ);
            log(String.format("[COLA   ] Reporte encolado en Cola FIFO | Total en cola: %d",
                    ventaServicio.getColaReportes().tamano()), LOG_COLA);
            log("[ ARCHIVO] Guardado en reportes/" + util.FechaUtil.nombreArchivoDelDia(), LOG_ARCHIVO);
            prevSeleccion.clear();
            DialogoTicket ticket = new DialogoTicket(this, reporte, seleccionados);
            ticket.setVisible(true);
            cargarCategoriaSeleccionada();
        } catch (Exception ex) {
            log("[ERROR  ] " + ex.getMessage(), LOG_ERROR);
            DialogoMensaje.error(this, ex.getMessage());
        }
    }

    // ── Diálogo de contraseña ─────────────────────────────────────────────────
    private void abrirPanelAdministracion() {
        final Color D_BG     = new Color(12,  16,  26);
        final Color D_SURF   = new Color(20,  27,  42);
        final Color D_BORDER = new Color(38,  58,  98);
        final Color D_ACCENT = new Color(60, 130, 210);
        final Color D_ACCENT2= new Color(99, 179, 237);
        final Color D_TEXT   = new Color(210, 225, 245);
        final Color D_MUTED  = new Color(90, 120, 165);

        JDialog dialogo = new JDialog(this, "Acceso Administrativo", true);
        dialogo.setUndecorated(true);
        dialogo.setSize(370, 320);
        dialogo.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(16, 24, 48), 0, getHeight(), D_BG));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setPaint(new GradientPaint(0, 0, new Color(60, 130, 210, 0),
                        getWidth() / 2f, 0, new Color(60, 130, 210, 220)));
                g2.fillRect(0, 0, getWidth() / 2, 2);
                g2.setPaint(new GradientPaint(getWidth() / 2f, 0, new Color(60, 130, 210, 220),
                        getWidth(), 0, new Color(60, 130, 210, 0)));
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, 2);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createLineBorder(D_BORDER, 1));

        JPanel top = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(18, 30, 72),
                        getWidth(), 0, new Color(10, 18, 48)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(38, 58, 98));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 100));
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel iconPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setPaint(new RadialGradientPaint(cx, cy, 28, new float[]{0f, 1f},
                        new Color[]{new Color(60, 130, 210, 65), new Color(0, 0, 0, 0)}));
                g2.fillOval(cx - 28, cy - 28, 56, 56);
                g2.setPaint(new GradientPaint(cx - 20, cy - 20, new Color(38, 80, 155),
                        cx + 20, cy + 20, new Color(20, 52, 108)));
                g2.fillOval(cx - 20, cy - 20, 40, 40);
                g2.setColor(new Color(80, 150, 230, 150));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - 20, cy - 20, 40, 40);
                g2.setColor(new Color(175, 215, 255));
                g2.fillRoundRect(cx - 9, cy - 1, 18, 13, 4, 4);
                g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx - 7, cy - 10, 14, 14, 0, 180);
                g2.setColor(new Color(20, 52, 108));
                g2.fillOval(cx - 2, cy + 3, 4, 4);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(370, 60));
        iconPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lblTitle = new JLabel("Acceso Administrativo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(D_TEXT);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lblSub = new JLabel("Ingresa la contraseña para continuar");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(D_MUTED);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        top.add(iconPanel);
        top.add(lblTitle);
        top.add(Box.createVerticalStrut(2));
        top.add(lblSub);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(20, 32, 10, 32));

        JLabel lblField = new JLabel("Contraseña");
        lblField.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblField.setForeground(D_ACCENT2);
        lblField.setAlignmentX(LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(D_SURF);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(D_BORDER);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        passField.setOpaque(false);
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(D_ACCENT2);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBorder(new EmptyBorder(6, 12, 6, 12));
        passField.setEchoChar('●');
        passField.setAlignmentX(LEFT_ALIGNMENT);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

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

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        bottom.setOpaque(false);

        JButton btnCancelar = buildDialogBtn("Cancelar", false, D_BORDER, D_TEXT);
        JButton btnAceptar  = buildDialogBtn("Ingresar",  true, D_ACCENT, Color.WHITE);
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
                final int[] count = {0};
                final int origX = dialogo.getX();
                javax.swing.Timer shake = new javax.swing.Timer(30, null);
                shake.addActionListener(ev -> {
                    count[0]++;
                    dialogo.setLocation(origX + (count[0] % 2 == 0 ? 6 : -6), dialogo.getY());
                    if (count[0] >= 8) { shake.stop(); dialogo.setLocation(origX, dialogo.getY()); }
                });
                shake.start();
            }
        };

        btnAceptar.addActionListener(e  -> intentarAcceso.run());
        btnCancelar.addActionListener(e -> dialogo.dispose());
        passField.addActionListener(e   -> intentarAcceso.run());

        bottom.add(btnCancelar);
        bottom.add(btnAceptar);

        root.add(top,    BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        JPanel glass = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {}
        };
        glass.setOpaque(false);
        glass.add(root);
        dialogo.setContentPane(glass);
        dialogo.getRootPane().setDefaultButton(btnAceptar);
        SwingUtilities.invokeLater(passField::requestFocus);
        dialogo.setVisible(true);

        if (acceso[0]) {
            log("[INFO   ] Acceso al panel de administración concedido", LOG_OK);
            PanelAdmin admin = new PanelAdmin(this, ventaServicio);
            admin.setVisible(true);
            actualizarEtiquetasPrecios();
            log("[HASHMAP] Precios actualizados desde panel admin", LOG_HASHMAP);
            actualizarResumen();
        }
    }

    private JButton buildDialogBtn(String text, boolean filled, Color color, Color textColor) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Color base = hovered ? color.brighter() : color;
                if (filled) {
                    g2.setPaint(new GradientPaint(0, 0, base, 0, getHeight(), base.darker()));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, hovered ? 50 : 25),
                            0, getHeight() / 2, new Color(255, 255, 255, 0)));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 2, 6, 6);
                } else {
                    g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), hovered ? 40 : 15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(base.brighter());
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(textColor);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), getWidth() / 2 - fm.stringWidth(getText()) / 2,
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
        lblPriceVIP.setForeground(Color.WHITE); lblPriceVIP.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPricePre.setForeground(Color.WHITE); lblPricePre.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPriceGen.setForeground(Color.WHITE); lblPriceGen.setFont(new Font("Segoe UI", Font.BOLD, 11));
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
                    g2.setPaint(new GradientPaint(0, 0,
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 35),
                            getWidth(), 0,
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 10)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.setPaint(new GradientPaint(0, 6, color, 0, getHeight() - 12, color.darker()));
                    g2.fillRoundRect(0, 8, 3, getHeight() - 16, 2, 2);
                } else if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 8));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(255, 255, 255, 18));
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.setPaint(new GradientPaint(12, getHeight()/2 - 5, color,
                        22, getHeight()/2 + 5, color.darker()));
                g2.fillOval(12, getHeight()/2 - 5, 10, 10);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillOval(13, getHeight()/2 - 4, 4, 4);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(active ? Color.WHITE : TEXT);
                g2.drawString(name, 29, getHeight()/2 - 2);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(active
                        ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 180)
                        : MUTED);
                g2.drawString(sub, 29, getHeight()/2 + 10);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(172, 54));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> seleccionarCategoria(cat));
        return btn;
    }

    private JPanel priceRow(String cat, JLabel lblPrice, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel l = new JLabel(cat);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        row.add(l,        BorderLayout.WEST);
        row.add(lblPrice, BorderLayout.EAST);
        return row;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(80, 120, 180));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private static class LogEntry {
        final String time, message;
        final Color color;
        LogEntry(String t, String m, Color c) { time = t; message = m; color = c; }
    }
}

