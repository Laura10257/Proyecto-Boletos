package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class PanelCompra extends JPanel {

    private static final Color BG     = new Color(16,  21,  32);
    private static final Color CARD   = new Color(24,  32,  48);
    private static final Color BORDER = new Color(40,  50,  65);
    private static final Color TEXT   = new Color(210, 218, 228);
    private static final Color MUTED  = new Color(110, 130, 155);
    private static final Color GREEN  = new Color( 35, 134,  54);
    private static final Color ACCENT = new Color( 60, 130, 210);

    private final JPanel  listPanel;
    private final JLabel  lblTotal;
    private final JLabel  lblCount;
    private final JButton btnConfirmar;
    private final JButton btnLimpiar;

    public PanelCompra() {
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER));

        JPanel inner = new JPanel();
        inner.setBackground(BG);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(22, 16, 22, 16));

        // Título de sección con línea decorativa
        inner.add(buildSectionHeader());
        inner.add(Box.createVerticalStrut(14));

        lblCount = new JLabel("0 asientos seleccionados");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCount.setForeground(MUTED);
        lblCount.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(lblCount);
        inner.add(Box.createVerticalStrut(14));

        inner.add(microLabel("ASIENTOS"));
        inner.add(Box.createVerticalStrut(6));

        listPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Borde sutil
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(0, 180));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // Estilizar scrollbar
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(55, 70, 95); trackColor = new Color(18, 24, 36);
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
            private JButton zeroButton() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b;
            }
        });
        inner.add(scroll);
        inner.add(Box.createVerticalStrut(22));

        // Separador
        inner.add(buildHSep());
        inner.add(Box.createVerticalStrut(14));

        inner.add(microLabel("TOTAL A PAGAR"));
        inner.add(Box.createVerticalStrut(5));

        lblTotal = new JLabel("$0.00") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Sombra del texto
                g2.setFont(getFont());
                g2.setColor(new Color(35, 134, 54, 80));
                g2.drawString(getText(), 2, getHeight() - 4);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(lblTotal);
        inner.add(Box.createVerticalStrut(24));

        btnConfirmar = buildBtn("CONFIRMAR COMPRA", GREEN, true);
        btnConfirmar.setAlignmentX(LEFT_ALIGNMENT);
        btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        inner.add(btnConfirmar);
        inner.add(Box.createVerticalStrut(9));

        btnLimpiar = buildBtn("Limpiar selección", BORDER, false);
        btnLimpiar.setAlignmentX(LEFT_ALIGNMENT);
        btnLimpiar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        inner.add(btnLimpiar);

        add(inner, BorderLayout.NORTH);
        resetList();
    }

    private JPanel buildSectionHeader() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        JLabel lbl = new JLabel("RESUMEN DE COMPRA");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(80, 120, 180));
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JPanel buildHSep() {
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(40, 50, 65, 0),
                        getWidth() / 2f, 0, new Color(60, 130, 210, 100)));
                g2.fillRect(0, 0, getWidth() / 2, 1);
                g2.setPaint(new GradientPaint(getWidth() / 2f, 0, new Color(60, 130, 210, 100),
                        getWidth(), 0, new Color(40, 50, 65, 0)));
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, 1);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    private JLabel microLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(80, 120, 180));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton buildBtn(String text, Color color, boolean filled) {
        JButton btn = new JButton(text) {
            boolean hovered = false;
            boolean pressed = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Color c = pressed ? color.darker() : hovered ? color.brighter() : color;
                if (filled) {
                    g2.setPaint(new GradientPaint(0, 0, c, 0, getHeight(), c.darker()));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                    g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255, pressed ? 0 : hovered ? 50 : 30),
                            0, getHeight() / 2, new Color(255, 255, 255, 0)));
                    g2.fillRoundRect(3, 2, getWidth() - 6, getHeight() / 2, 7, 7);
                } else {
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), hovered ? 45 : 15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                    g2.setColor(new Color(c.getRed() + 40, c.getGreen() + 40, c.getBlue() + 40, 160));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                }
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    getWidth() / 2 - fm.stringWidth(getText()) / 2,
                    getHeight() / 2 + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
        };
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void actualizarSeleccion(List<String> asientos, double total) {
        listPanel.removeAll();
        if (asientos == null || asientos.isEmpty()) { resetList(); return; }
        int n = asientos.size();
        lblCount.setText(n + " asiento" + (n != 1 ? "s" : "") + " seleccionado" + (n != 1 ? "s" : ""));
        for (int i = 0; i < asientos.size(); i++) {
            String a = asientos.get(i);
            final int idx = i;
            JPanel row = new JPanel(new BorderLayout(6, 0)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (idx % 2 == 0) {
                        g2.setColor(new Color(255, 255, 255, 6));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                    }
                    g2.dispose();
                }
            };
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
            row.setBorder(new EmptyBorder(2, 4, 2, 4));
            // Punto de asiento
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(210, 153, 34));
                    g2.fillOval(0, 2, 8, 8);
                    g2.dispose();
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(10, 12));
            JLabel lbl = new JLabel(a);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(TEXT);
            row.add(dot, BorderLayout.WEST);
            row.add(lbl, BorderLayout.CENTER);
            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(2));
        }
        lblTotal.setText(String.format("$%,.2f", total));
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void resetList() {
        listPanel.removeAll();
        JLabel empty = new JLabel("Ninguno seleccionado");
        empty.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        empty.setForeground(MUTED);
        empty.setAlignmentX(LEFT_ALIGNMENT);
        listPanel.add(empty);
        lblCount.setText("0 asientos seleccionados");
        lblTotal.setText("$0.00");
        listPanel.revalidate();
        listPanel.repaint();
    }

    public JButton getBtnConfirmar() { return btnConfirmar; }
    public JButton getBtnLimpiar()   { return btnLimpiar; }
}
