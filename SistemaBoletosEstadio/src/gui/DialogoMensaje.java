/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Diálogo de mensajes personalizado con diseño moderno.
 * Reemplaza JOptionPane.showMessageDialog en todo el proyecto.
 *
 * Tipos soportados:
 *   DialogoMensaje.EXITO   – ícono verde con checkmark
 *   DialogoMensaje.ERROR   – ícono rojo con X
 *   DialogoMensaje.AVISO   – ícono amarillo con !
 *   DialogoMensaje.INFO    – ícono azul con i
 */
public class DialogoMensaje extends JDialog {

    // ── Tipos ──────────────────────────────────────────────────────────────────
    public static final int EXITO = 0;
    public static final int ERROR = 1;
    public static final int AVISO = 2;
    public static final int INFO  = 3;

    // ── Paleta ─────────────────────────────────────────────────────────────────
    private static final Color BG       = new Color(12, 17, 27);
    private static final Color SURFACE  = new Color(18, 25, 40);
    private static final Color CARD     = new Color(24, 33, 52);
    private static final Color BORDER   = new Color(40, 55, 80);
    private static final Color TEXT     = new Color(215, 225, 235);
    private static final Color MUTED    = new Color(100, 125, 160);
    private static final Color BTN_BG   = new Color(35, 48, 75);

    // Colores por tipo
    private static final Color[] TYPE_COLOR = {
        new Color( 52, 199, 89),   // EXITO  – verde
        new Color(220,  55,  55),  // ERROR  – rojo
        new Color(255, 185,   0),  // AVISO  – amarillo
        new Color( 60, 140, 230),  // INFO   – azul
    };
    private static final String[] TYPE_TITLE = {
        "Operación Exitosa",
        "Error",
        "Aviso",
        "Información",
    };

    // ── Constructor ────────────────────────────────────────────────────────────
    private DialogoMensaje(Window owner, String mensaje, String titulo, int tipo) {
        super(owner, titulo != null ? titulo : TYPE_TITLE[tipo],
              ModalityType.APPLICATION_MODAL);

        setUndecorated(true);
        getRootPane().putClientProperty("Window.shadow", Boolean.TRUE);
        setBackground(new Color(0, 0, 0, 0));

        Color accent = TYPE_COLOR[tipo];

        // ── Panel principal ────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra exterior simulada con fondo semitransparente
                g2.setColor(BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                // Borde fino
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                // Línea superior de acento
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(20, 0, getWidth()-20, 0);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setContentPane(root);

        // ── Ícono ──────────────────────────────────────────────────────────────
        JPanel iconPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int r = 22;
                // Halo exterior
                g2.setPaint(new RadialGradientPaint(cx, cy, r + 10,
                    new float[]{0f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(),
                                         accent.getBlue(), 35),
                                new Color(0, 0, 0, 0)}));
                g2.fillOval(cx - r - 10, cy - r - 10, (r + 10)*2, (r + 10)*2);
                // Círculo de fondo
                g2.setPaint(new GradientPaint(cx - r, cy - r,
                    new Color(accent.getRed(), accent.getGreen(),
                              accent.getBlue(), 40),
                    cx + r, cy + r,
                    new Color(accent.getRed(), accent.getGreen(),
                              accent.getBlue(), 15)));
                g2.fillOval(cx - r, cy - r, r*2, r*2);
                // Borde del círculo
                g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                                      accent.getBlue(), 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - r, cy - r, r*2, r*2);
                // Símbolo interior
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND,
                                              BasicStroke.JOIN_ROUND));
                switch (tipo) {
                    case EXITO -> {
                        // Checkmark
                        g2.drawLine(cx - 9, cy, cx - 3, cy + 8);
                        g2.drawLine(cx - 3, cy + 8, cx + 10, cy - 8);
                    }
                    case ERROR -> {
                        // X
                        g2.drawLine(cx - 8, cy - 8, cx + 8, cy + 8);
                        g2.drawLine(cx + 8, cy - 8, cx - 8, cy + 8);
                    }
                    case AVISO -> {
                        // Exclamación
                        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,
                                                      BasicStroke.JOIN_ROUND));
                        g2.drawLine(cx, cy - 9, cx, cy + 2);
                        g2.fillOval(cx - 2, cy + 7, 4, 4);
                    }
                    case INFO -> {
                        // i
                        g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND,
                                                      BasicStroke.JOIN_ROUND));
                        g2.drawLine(cx, cy + 2, cx, cy + 10);
                        g2.fillOval(cx - 2, cy - 9, 5, 5);
                    }
                }
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(76, 76));

        // ── Contenido de texto ─────────────────────────────────────────────────
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(BorderFactory.createEmptyBorder(18, 4, 12, 20));

        String titleText = titulo != null ? titulo : TYPE_TITLE[tipo];
        JLabel lblTitulo = new JLabel(titleText);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(accent);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSep = new JLabel(" ");
        lblSep.setPreferredSize(new Dimension(0, 4));

        // Soporte multi-línea con HTML
        String htmlMsg = "<html><body style='width:220px; font-family:Segoe UI;"
                + "font-size:11px; color:rgb("
                + TEXT.getRed() + "," + TEXT.getGreen() + "," + TEXT.getBlue()
                + ");'>" + mensaje.replace("\n", "<br>") + "</body></html>";
        JLabel lblMensaje = new JLabel(htmlMsg);
        lblMensaje.setAlignmentX(LEFT_ALIGNMENT);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(lblTitulo);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(lblMensaje);
        textPanel.add(Box.createVerticalGlue());

        // ── Fila superior (ícono + texto) ──────────────────────────────────────
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(iconPanel,  BorderLayout.WEST);
        topRow.add(textPanel,  BorderLayout.CENTER);

        // ── Footer con botón ───────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 16, 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        footer.setOpaque(false);

        JButton btnAceptar = buildBoton("  Aceptar  ", accent);
        btnAceptar.addActionListener(e -> dispose());
        footer.add(btnAceptar);

        root.add(topRow,  BorderLayout.CENTER);
        root.add(footer,  BorderLayout.SOUTH);

        // ── Tamaño y posición ──────────────────────────────────────────────────
        setSize(390, 160);
        setResizable(false);
        setLocationRelativeTo(owner instanceof Component c ? c : null);

        // Cerrar con ESC / Enter
        KeyStroke esc   = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,  0);
        getRootPane().registerKeyboardAction(e -> dispose(), esc,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> dispose(), enter,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(btnAceptar);
    }

    // ── Botón estilizado ───────────────────────────────────────────────────────
    private JButton buildBoton(String text, Color accent) {
        JButton btn = new JButton(text) {
            boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Color base = hov ? accent.brighter() : accent;
                g2.setPaint(new GradientPaint(0, 0, base,
                                              0, getHeight(), base.darker()));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Brillo superior
                g2.setPaint(new GradientPaint(0, 0,
                    new Color(255, 255, 255, hov ? 55 : 28),
                    0, getHeight()/2, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()/2, 6, 6);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    getWidth()/2 - fm.stringWidth(getText())/2,
                    getHeight()/2 + fm.getAscent()/2 - 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(110, 32));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── API pública estática (equivalente a JOptionPane) ───────────────────────

    /** Éxito / información positiva */
    public static void exito(Component parent, String mensaje) {
        mostrar(parent, mensaje, null, EXITO);
    }

    /** Mensaje de error */
    public static void error(Component parent, String mensaje) {
        mostrar(parent, mensaje, null, ERROR);
    }

    /** Aviso / advertencia */
    public static void aviso(Component parent, String mensaje) {
        mostrar(parent, mensaje, null, AVISO);
    }

    /** Información genérica */
    public static void info(Component parent, String mensaje, String titulo) {
        mostrar(parent, mensaje, titulo, INFO);
    }

    /** Método base flexible */
    public static void mostrar(Component parent, String mensaje,
                                String titulo, int tipo) {
        Window owner = parent == null ? null
                     : (parent instanceof Window w ? w
                        : SwingUtilities.getWindowAncestor(parent));
        DialogoMensaje d = new DialogoMensaje(owner, mensaje, titulo, tipo);
        d.setVisible(true);
    }
}