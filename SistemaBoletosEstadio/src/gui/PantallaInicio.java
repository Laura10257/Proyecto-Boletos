package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import javax.swing.*;
import javax.swing.Timer;

public class PantallaInicio extends JFrame {

    private Image bgImage;

    public PantallaInicio() {
        cargarImagen();
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

    private void configurarVentana() {
        setTitle("Sistema de Boletos - Estadio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        JPanel fondo = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,   RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo: imagen o gradiente de respaldo
                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 45, 10),
                        0, getHeight(), new Color(0, 12, 4));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                // Capa oscura para legibilidad
                g2.setColor(new Color(0, 0, 0, 115));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Halo verde central
                RadialGradientPaint halo = new RadialGradientPaint(
                    new Point(getWidth() / 2, getHeight() / 2 - 30),
                    getWidth() * 0.45f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0, 100, 20, 55), new Color(0, 0, 0, 0)}
                );
                g2.setPaint(halo);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        };

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);

        // Título principal con sombra
        JPanel panelTitulo = buildPanelTitulo("SISTEMA DE BOLETOS", 52,
            Color.WHITE, new Color(0, 180, 60, 120));
        panelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo
        JPanel panelSub = buildPanelTitulo("ESTADIO TOLUCA", 20,
            new Color(160, 255, 160), new Color(0, 80, 20, 100));
        panelSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Línea decorativa
        JPanel lineaVerde = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Simétrico: mitad izquierda -> mitad derecha
                GradientPaint left  = new GradientPaint(0, 0, new Color(0, 200, 80, 0), getWidth()/2, 0, new Color(0, 220, 90, 200));
                GradientPaint right = new GradientPaint(getWidth()/2, 0, new Color(0, 220, 90, 200), getWidth(), 0, new Color(0, 200, 80, 0));
                g2.setPaint(left);
                g2.fillRect(0, 0, getWidth() / 2, getHeight());
                g2.setPaint(right);
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, getHeight());
                g2.dispose();
            }
        };
        lineaVerde.setPreferredSize(new Dimension(420, 2));
        lineaVerde.setMaximumSize(new Dimension(420, 2));
        lineaVerde.setOpaque(false);
        lineaVerde.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón principal
        JButton btnComprar = buildBotonCompra();
        btnComprar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Texto inferior
        JPanel panelHint = buildPanelTitulo(
            "Selecciona tus asientos y disfruta del partido",
            12, new Color(180, 255, 180, 170), new Color(0, 0, 0, 0));
        panelHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(panelTitulo);
        contenido.add(Box.createVerticalStrut(6));
        contenido.add(panelSub);
        contenido.add(Box.createVerticalStrut(28));
        contenido.add(lineaVerde);
        contenido.add(Box.createVerticalStrut(42));
        contenido.add(btnComprar);
        contenido.add(Box.createVerticalStrut(22));
        contenido.add(panelHint);

        fondo.add(contenido);
        setContentPane(fondo);
    }

    // Panel que dibuja texto con sombra/glow
    private JPanel buildPanelTitulo(String texto, int size, Color colorTexto, Color colorSombra) {
        return new JPanel() {
            {
                setOpaque(false);
                Font f = new Font("Segoe UI", Font.BOLD, size);
                FontMetrics fm = getFontMetrics(f);
                setPreferredSize(new Dimension(fm.stringWidth(texto) + 20, size + 16));
                setMaximumSize(new Dimension(fm.stringWidth(texto) + 20, size + 16));
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                Font f = new Font("Segoe UI", Font.BOLD, size);
                g2.setFont(f);
                FontMetrics fm = g2.getFontMetrics();
                int x = getWidth()  / 2 - fm.stringWidth(texto) / 2;
                int y = getHeight() / 2 + fm.getAscent() / 2 - 2;
                // Sombra
                g2.setColor(colorSombra);
                g2.drawString(texto, x + 2, y + 3);
                // Texto principal
                g2.setColor(colorTexto);
                g2.drawString(texto, x, y);
                g2.dispose();
            }
        };
    }

    private JButton buildBotonCompra() {
        JButton btn = new JButton("REALIZAR COMPRA") {
            boolean hovered = false;
            float pulse  = 0f;
            boolean pulseUp = true;
            {
                Timer t = new Timer(35, e -> {
                    if (pulseUp) { pulse = Math.min(1f, pulse + 0.04f); if (pulse >= 1f) pulseUp = false; }
                    else         { pulse = Math.max(0f, pulse - 0.04f); if (pulse <= 0f) pulseUp = true;  }
                    repaint();
                });
                t.start();
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                float glow = 0.35f + 0.65f * pulse;

                // Glow exterior
                for (int i = 10; i >= 1; i--) {
                    int alpha = (int)(glow * 45 * ((float) i / 10));
                    g2.setColor(new Color(0, 210, 70, alpha));
                    int pad = i * 2;
                    g2.fillRoundRect(-pad, -pad, w + pad * 2, h + pad * 2, 24 + pad, 24 + pad);
                }

                // Cuerpo del botón
                Color top = hovered ? new Color(0, 225, 90) : new Color(0, 195, 70);
                Color bot = hovered ? new Color(0, 165, 60) : new Color(0, 135, 45);
                GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bot);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 20, 20);

                // Brillo superior
                g2.setColor(new Color(255, 255, 255, hovered ? 70 : 50));
                g2.fillRoundRect(4, 3, w - 8, h / 2 - 2, 14, 14);

                // Borde verde
                g2.setColor(new Color(120, 255, 160, 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, 20, 20);

                // Texto
                g2.setFont(new Font("Segoe UI", Font.BOLD, 19));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                g2.drawString(txt, w / 2 - fm.stringWidth(txt) / 2, h / 2 + fm.getAscent() / 2 - 2);

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(310, 64));
        btn.setMaximumSize(new Dimension(310, 64));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> abrirVentanaPrincipal());
        return btn;
    }

    private void abrirVentanaPrincipal() {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
            dispose();
        });
    }
}
