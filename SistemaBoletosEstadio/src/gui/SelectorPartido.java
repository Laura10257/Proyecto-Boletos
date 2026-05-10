/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import javax.swing.*;
import javax.swing.Timer;

public class SelectorPartido extends JFrame {

    private static final Color BG       = new Color( 8, 12, 20);
    private static final Color SURFACE  = new Color(14, 20, 32);
    private static final Color CARD     = new Color(18, 26, 42);
    private static final Color CARD_HOV = new Color(24, 36, 58);
    private static final Color BORDER   = new Color(38, 52, 76);
    private static final Color TEXT     = new Color(215, 225, 238);
    private static final Color MUTED    = new Color(100, 122, 155);
    private static final Color ACCENT   = new Color( 60, 130, 210);
    private static final Color GREEN    = new Color( 40, 200,  90);

    private static final String[][] PARTIDOS = {
        {
            "Deportivo Toluca FC", "Club América",
            "Sábado 17 de Mayo, 2026", "20:00 hrs",
            "Liga MX — Clausura 2026  |  J17",
            "Estadio Nemesio Díez  ·  Toluca, Méx.",
            "CLÁSICO"
        },
        {
            "Deportivo Toluca FC", "Chivas de Guadalajara",
            "Domingo 25 de Mayo, 2026", "18:00 hrs",
            "Liga MX — Clausura 2026  |  J18",
            "Estadio Nemesio Díez  ·  Toluca, Méx.",
            "LIGA"
        },
        {
            "Deportivo Toluca FC", "Cruz Azul",
            "Martes 3 de Junio, 2026", "21:00 hrs",
            "Copa MX — Cuartos de Final",
            "Estadio Nemesio Díez  ·  Toluca, Méx.",
            "COPA"
        }
    };

    private static final Color[] TAG_COLORS = {
        new Color(255, 200,  50),   // CLÁSICO — dorado
        new Color( 88, 166, 255),   // LIGA    — azul
        new Color(126, 231, 135),   // COPA    — verde
    };

    private int partidoHover    = -1;
    private int partidoSelected = -1;
    private Image bgImage;

    private final float[] px     = new float[22];
    private final float[] py     = new float[22];
    private final float[] pspeed = new float[22];
    private final float[] palpha = new float[22];

    public SelectorPartido() {
        cargarImagen();
        initParticulas();
        configurarVentana();
        construirUI();
    }

    private void cargarImagen() {
        for (String ext : new String[]{".png", ".jpg"}) {
            URL url = getClass().getResource("/recursos/fondo_estadio" + ext);
            if (url != null) { bgImage = new ImageIcon(url).getImage(); return; }
            File f = new File("recursos/fondo_estadio" + ext);
            if (f.exists()) { bgImage = new ImageIcon(f.getAbsolutePath()).getImage(); return; }
        }
    }

    private void initParticulas() {
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < px.length; i++) {
            px[i]     = rnd.nextFloat() * 1100;
            py[i]     = rnd.nextFloat() * 115;
            pspeed[i] = 0.12f + rnd.nextFloat() * 0.25f;
            palpha[i] = 0.08f + rnd.nextFloat() * 0.38f;
        }
    }

    private void configurarVentana() {
        setTitle("Selección de Partido — Sistema de Boletos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setOpaque(true);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);

        Timer particleTimer = new Timer(40, e -> {
            for (int i = 0; i < px.length; i++) {
                px[i] += pspeed[i];
                if (px[i] > 1100) px[i] = -4;
            }
            repaint();
        });
        particleTimer.start();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);

                // Imagen de fondo con overlay oscuro
                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, -280, getWidth(), getWidth() / 2, null);
                    g2.setColor(new Color(0, 0, 0, 165));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(10, 22, 60),
                            getWidth(), getHeight(), new Color(4, 10, 28)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                // Vignette inferior
                g2.setPaint(new GradientPaint(0, getHeight() - 50,
                        new Color(8, 12, 20, 0), 0, getHeight(), new Color(8, 12, 20, 255)));
                g2.fillRect(0, getHeight() - 50, getWidth(), 50);

                // Líneas de acento superior izquierda y derecha
                g2.setPaint(new GradientPaint(0, 0, new Color(60, 130, 210, 0),
                        getWidth() / 2f, 0, new Color(60, 130, 210, 200)));
                g2.fillRect(0, 0, getWidth() / 2, 2);
                g2.setPaint(new GradientPaint(getWidth() / 2f, 0, new Color(60, 130, 210, 200),
                        getWidth(), 0, new Color(60, 130, 210, 0)));
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, 2);

                // Partículas flotantes
                for (int i = 0; i < px.length; i++) {
                    int alpha = (int)(palpha[i] * 150);
                    g2.setColor(new Color(88, 166, 255, alpha));
                    int sz = 2 + (i % 3);
                    g2.fillOval((int) px[i], (int) py[i], sz, sz);
                }

                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 125));

        JPanel hContent = new JPanel();
        hContent.setOpaque(false);
        hContent.setLayout(new BoxLayout(hContent, BoxLayout.Y_AXIS));
        hContent.setBorder(BorderFactory.createEmptyBorder(24, 40, 18, 40));

        // Breadcrumb
        JLabel crumb = new JLabel("INICIO   ›   SELECCIÓN DE PARTIDO");
        crumb.setFont(new Font("Segoe UI", Font.BOLD, 9));
        crumb.setForeground(new Color(70, 110, 170));
        crumb.setAlignmentX(LEFT_ALIGNMENT);

        // Fila del título
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(LEFT_ALIGNMENT);

        // Ícono de balón
        JPanel ballIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2, r = 18;
                // Halo
                g2.setColor(new Color(60, 130, 210, 25));
                g2.fillOval(cx - r - 6, cy - r - 6, (r + 6) * 2, (r + 6) * 2);
                // Fondo del círculo
                g2.setPaint(new GradientPaint(cx - r, cy - r, new Color(38, 80, 160),
                        cx + r, cy + r, new Color(18, 46, 108)));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                // Borde
                g2.setColor(new Color(80, 150, 230, 140));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                // Pentagono central del balón
                g2.setColor(new Color(195, 220, 255));
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(cx - 8, cy - 8, 16, 16);
                // Costuras
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(cx, cy - 8, cx - 5, cy - 2);
                g2.drawLine(cx, cy - 8, cx + 5, cy - 2);
                g2.drawLine(cx - 5, cy - 2, cx - 7, cy + 5);
                g2.drawLine(cx + 5, cy - 2, cx + 7, cy + 5);
                g2.drawLine(cx - 7, cy + 5, cx + 7, cy + 5);
                g2.dispose();
            }
        };
        ballIcon.setOpaque(false);
        ballIcon.setPreferredSize(new Dimension(46, 50));

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        JLabel mainTitle = new JLabel("Elige tu Partido");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subTitle = new JLabel("Temporada 2026  ·  Estadio Nemesio Díez  ·  Toluca, México");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitle.setForeground(new Color(105, 150, 210));
        subTitle.setAlignmentX(LEFT_ALIGNMENT);
        titleText.add(mainTitle);
        titleText.add(Box.createVerticalStrut(3));
        titleText.add(subTitle);

        titleRow.add(ballIcon);
        titleRow.add(titleText);

        hContent.add(crumb);
        hContent.add(Box.createVerticalStrut(10));
        hContent.add(titleRow);
        header.add(hContent, BorderLayout.CENTER);
        return header;
    }

    // ── Centro: grid de tarjetas ──────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(28, 40, 10, 40));

        JPanel sectionRow = new JPanel(new BorderLayout());
        sectionRow.setOpaque(false);
        sectionRow.setBorder(BorderFactory.createEmptyBorder(0, 2, 16, 0));

        JLabel sectionLbl = new JLabel("PRÓXIMOS PARTIDOS EN CASA");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sectionLbl.setForeground(MUTED);

        JLabel countLbl = new JLabel(PARTIDOS.length + " partidos disponibles");
        countLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        countLbl.setForeground(new Color(60, 95, 145));

        sectionRow.add(sectionLbl, BorderLayout.WEST);
        sectionRow.add(countLbl,  BorderLayout.EAST);

        JPanel grid = new JPanel(new GridLayout(1, 3, 18, 0));
        grid.setOpaque(false);
        for (int i = 0; i < PARTIDOS.length; i++) {
            grid.add(buildCardPartido(i));
        }

        center.add(sectionRow, BorderLayout.NORTH);
        center.add(grid,       BorderLayout.CENTER);
        return center;
    }

    // ── Tarjeta de partido ────────────────────────────────────────────────────
    private JPanel buildCardPartido(int idx) {
        String[] p     = PARTIDOS[idx];
        Color tagColor = TAG_COLORS[idx];

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean hov = (partidoHover    == idx);
                boolean sel = (partidoSelected == idx);

                // Fondo
                g2.setColor(hov || sel ? CARD_HOV : CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Glow de selección
                if (sel) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(),
                            ACCENT.getBlue(), 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                }

                // Borde
                Color bordeColor = sel ? ACCENT : (hov ? new Color(80, 150, 230, 160) : BORDER);
                g2.setColor(bordeColor);
                g2.setStroke(new BasicStroke(sel ? 2f : 1f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);

                // Barra superior de color del partido
                g2.setColor(tagColor);
                g2.fillRoundRect(0, 0, getWidth(), 5, 5, 5);
                g2.fillRect(0, 3, getWidth(), 2);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 20, 22));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ── Tag (CLÁSICO / LIGA / COPA) ───────────────────────────────────────
        JPanel tagWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tagWrap.setOpaque(false);
        tagWrap.setAlignmentX(LEFT_ALIGNMENT);
        tagWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel tagLbl = new JLabel(p[6]) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(tagColor.getRed(), tagColor.getGreen(),
                        tagColor.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(tagColor.getRed(), tagColor.getGreen(),
                        tagColor.getBlue(), 150));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tagLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        tagLbl.setForeground(tagColor);
        tagLbl.setBorder(BorderFactory.createEmptyBorder(3, 9, 3, 9));
        tagLbl.setOpaque(false);
        tagWrap.add(tagLbl);

        // ── VS panel ──────────────────────────────────────────────────────────
        JPanel vsPanel = buildVsPanel(p[0], p[1], tagColor, idx);
        vsPanel.setAlignmentX(LEFT_ALIGNMENT);

        // ── Separador punteado ────────────────────────────────────────────────
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(BORDER.getRed(), BORDER.getGreen(), BORDER.getBlue(), 180));
                g2.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                        0, new float[]{5, 5}, 0));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);

        // ── Info del partido ───────────────────────────────────────────────────
        JPanel infoPanel = buildInfoPanel(p, tagColor);
        infoPanel.setAlignmentX(LEFT_ALIGNMENT);

        // ── Botón seleccionar ──────────────────────────────────────────────────
        JButton btnSel = buildBtnSeleccionar(idx, tagColor);
        btnSel.setAlignmentX(LEFT_ALIGNMENT);
        btnSel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        card.add(tagWrap);
        card.add(Box.createVerticalStrut(16));
        card.add(vsPanel);
        card.add(Box.createVerticalStrut(18));
        card.add(sep);
        card.add(Box.createVerticalStrut(15));
        card.add(infoPanel);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(16));
        card.add(btnSel);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { partidoHover = idx; repaint(); }
            @Override public void mouseExited (MouseEvent e) { partidoHover = -1;  repaint(); }
            @Override public void mouseClicked(MouseEvent e) { seleccionarYContinuar(idx); }
        });

        return card;
    }

    // ── Panel VS ──────────────────────────────────────────────────────────────
    private JPanel buildVsPanel(String local, String visitante, Color accentColor, int idx) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

        JPanel escLocal  = buildEscudo(accentColor, true);
        JPanel escVisit  = buildEscudo(new Color(100, 120, 155), false);
        escLocal .setAlignmentX(CENTER_ALIGNMENT);
        escVisit .setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblLocal = new JLabel(truncar(local));
        lblLocal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLocal.setForeground(TEXT);
        lblLocal.setAlignmentX(CENTER_ALIGNMENT);

        JPanel vsRow = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int rr = 13;
                g2.setColor(new Color(30, 42, 66));
                g2.fillOval(cx - rr, cy - rr, rr * 2, rr * 2);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawOval(cx - rr, cy - rr, rr * 2, rr * 2);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.setColor(MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("VS", cx - fm.stringWidth("VS") / 2, cy + fm.getAscent() / 2 - 1);
                g2.dispose();
            }
        };
        vsRow.setOpaque(false);
        vsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        vsRow.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblVisit = new JLabel(truncar(visitante));
        lblVisit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVisit.setForeground(new Color(165, 185, 210));
        lblVisit.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(escLocal);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblLocal);
        panel.add(Box.createVerticalStrut(6));
        panel.add(vsRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(escVisit);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblVisit);

        return panel;
    }

    private String truncar(String s) {
        return s.length() > 22 ? s.substring(0, 20) + "…" : s;
    }

    // ── Escudo dibujado ───────────────────────────────────────────────────────
    private JPanel buildEscudo(Color color, boolean local) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2, r = 17;
                // Halo
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                g2.fillOval(cx - r - 5, cy - r - 5, (r + 5) * 2, (r + 5) * 2);
                // Círculo base
                g2.setPaint(new GradientPaint(cx - r, cy - r,
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 75),
                        cx + r, cy + r,
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 30)));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                // Borde
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 170));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                // Letra L/V
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.setColor(color);
                FontMetrics fm = g2.getFontMetrics();
                String l = local ? "L" : "V";
                g2.drawString(l, cx - fm.stringWidth(l) / 2, cy + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(44, 44));
        p.setMaximumSize(new Dimension(44, 44));
        return p;
    }

    // ── Panel de información ──────────────────────────────────────────────────
    private JPanel buildInfoPanel(String[] p, Color accentColor) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        panel.add(infoRow("📅", p[2], TEXT));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("🕗", p[3], accentColor));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("🏆", p[4], MUTED));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("📍", p[5], MUTED));

        return panel;
    }

    private JPanel infoRow(String emoji, String texto, Color color) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel e = new JLabel(emoji);
        e.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel t = new JLabel(texto);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        t.setForeground(color);
        row.add(e);
        row.add(t);
        return row;
    }

    // ── Botón seleccionar ─────────────────────────────────────────────────────
    private JButton buildBtnSeleccionar(int idx, Color accentColor) {
        JButton btn = new JButton("Seleccionar Partido") {
            boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                boolean sel  = (partidoSelected == idx);
                Color   base = sel ? GREEN : (hov ? accentColor.brighter() : accentColor);
                g2.setPaint(new GradientPaint(0, 0, base, 0, getHeight(), base.darker()));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                // Brillo superior
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, hov ? 55 : 25),
                        0, getHeight() / 2, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 2, 7, 7);
                // Texto
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String txt = sel ? "✓  Partido Seleccionado" : getText();
                g2.drawString(txt, getWidth() / 2 - fm.stringWidth(txt) / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> seleccionarYContinuar(idx));
        return btn;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, 58));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JLabel hint = new JLabel("Selecciona un partido para continuar con la compra de boletos");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(MUTED);

        JButton btnRegresar = new JButton("← Regresar") {
            boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(BORDER.getRed(), BORDER.getGreen(),
                        BORDER.getBlue(), hov ? 120 : 55));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(120, 150, 190, 150));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(new Color(155, 180, 215));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), getWidth() / 2 - fm.stringWidth(getText()) / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
        };
        btnRegresar.setPreferredSize(new Dimension(130, 36));
        btnRegresar.setBorderPainted(false);
        btnRegresar.setContentAreaFilled(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegresar.addActionListener(e -> regresarAInicio());

        JPanel left  = new JPanel(new FlowLayout(FlowLayout.LEFT,  0, 18));
        left.setOpaque(false);
        left.add(hint);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 11));
        right.setOpaque(false);
        right.add(btnRegresar);

        footer.add(left,  BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    // ── Navegación ────────────────────────────────────────────────────────────
    private void seleccionarYContinuar(int idx) {
        partidoSelected = idx;
        repaint();
        Timer t = new Timer(300, e -> {
            String[] partido = PARTIDOS[idx];
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(
                    partido[0], partido[1],
                    partido[2], partido[3],
                    partido[4], partido[6]
                );
                ventana.setVisible(true);
                dispose();
            });
        });
        t.setRepeats(false);
        t.start();
    }

    private void regresarAInicio() {
        SwingUtilities.invokeLater(() -> {
            PantallaInicio inicio = new PantallaInicio();
            inicio.setVisible(true);
            dispose();
        });
    }
}