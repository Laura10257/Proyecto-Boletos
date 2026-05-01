package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class PanelCompra extends JPanel {

    private static final Color BG     = new Color(22,  27,  34);
    private static final Color CARD   = new Color(30,  38,  52);
    private static final Color BORDER = new Color(48,  54,  61);
    private static final Color TEXT   = new Color(201, 209, 217);
    private static final Color MUTED  = new Color(139, 148, 158);
    private static final Color GREEN  = new Color(35,  134,  54);

    private final JPanel listPanel;
    private final JLabel lblTotal;
    private final JLabel lblCount;
    private final JButton btnConfirmar;
    private final JButton btnLimpiar;

    public PanelCompra() {
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER));

        JPanel inner = new JPanel();
        inner.setBackground(BG);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(20, 14, 20, 14));

        inner.add(microLabel("RESUMEN DE COMPRA"));
        inner.add(Box.createVerticalStrut(14));

        lblCount = new JLabel("0 asientos seleccionados");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCount.setForeground(MUTED);
        lblCount.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(lblCount);
        inner.add(Box.createVerticalStrut(12));

        inner.add(microLabel("ASIENTOS"));
        inner.add(Box.createVerticalStrut(6));

        listPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
        };
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(0, 170));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inner.add(scroll);
        inner.add(Box.createVerticalStrut(20));

        inner.add(microLabel("TOTAL A PAGAR"));
        inner.add(Box.createVerticalStrut(5));

        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(lblTotal);
        inner.add(Box.createVerticalStrut(22));

        btnConfirmar = buildBtn("CONFIRMAR COMPRA", GREEN, true);
        btnConfirmar.setAlignmentX(LEFT_ALIGNMENT);
        btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        inner.add(btnConfirmar);
        inner.add(Box.createVerticalStrut(8));

        btnLimpiar = buildBtn("Limpiar seleccion", BORDER, false);
        btnLimpiar.setAlignmentX(LEFT_ALIGNMENT);
        btnLimpiar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 33));
        inner.add(btnLimpiar);

        add(inner, BorderLayout.NORTH);
        resetList();
    }

    private JLabel microLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton buildBtn(String text, Color color, boolean filled) {
        JButton btn = new JButton(text) {
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
                Color c = hovered ? color.brighter() : color;
                if (filled) {
                    g2.setColor(c);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), hovered ? 50 : 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(c.brighter());
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
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
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void actualizarSeleccion(List<String> asientos, double total) {
        listPanel.removeAll();
        if (asientos == null || asientos.isEmpty()) {
            resetList();
            return;
        }
        int n = asientos.size();
        lblCount.setText(n + " asiento" + (n != 1 ? "s" : "") + " seleccionado" + (n != 1 ? "s" : ""));
        for (String a : asientos) {
            JLabel lbl = new JLabel("  " + a);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(TEXT);
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            listPanel.add(lbl);
            listPanel.add(Box.createVerticalStrut(3));
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
