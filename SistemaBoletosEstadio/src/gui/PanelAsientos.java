package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import modelo.Categoria;

public class PanelAsientos extends JPanel {

    private static final Color BG            = new Color(13,  17,  23);
    private static final Color SEAT_AVAIL    = new Color(35,  134,  54);
    private static final Color SEAT_OCCUPIED = new Color(55,  62,  72);
    private static final Color SEAT_SELECTED = new Color(210, 153,  34);
    private static final Color SEAT_HOVER    = new Color(56,  139, 253);
    private static final Color LABEL_COLOR   = new Color(139, 148, 158);
    private static final Color STAGE_TEXT    = new Color(130, 140, 210);

    private Categoria categoriaActual;
    private List<String> disponiblesActuales = new ArrayList<>();
    private final Set<String> seleccionados  = new LinkedHashSet<>();
    private String seatHover = null;
    private Runnable onSeleccionCambiada;

    private int filas, columnas;
    private static final int SEAT_SIZE = 42;
    private static final int SEAT_GAP  =  8;

    private float pulseVal = 0f;
    private boolean pulseUp = true;
    private final Timer animTimer;

    public PanelAsientos() {
        setBackground(BG);
        setOpaque(true);

        animTimer = new Timer(40, e -> {
            if (pulseUp) { pulseVal = Math.min(1f, pulseVal + 0.06f); if (pulseVal >= 1f) pulseUp = false; }
            else         { pulseVal = Math.max(0f, pulseVal - 0.06f); if (pulseVal <= 0f) pulseUp = true;  }
            if (!seleccionados.isEmpty()) repaint();
        });
        animTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String seat = seatAt(e.getX(), e.getY());
                if (seat == null || !disponiblesActuales.contains(seat)) return;
                if (seleccionados.contains(seat)) seleccionados.remove(seat);
                else seleccionados.add(seat);
                repaint();
                if (onSeleccionCambiada != null) onSeleccionCambiada.run();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                String seat = seatAt(e.getX(), e.getY());
                if (!Objects.equals(seat, seatHover)) {
                    seatHover = seat;
                    repaint();
                }
                boolean overAvail = seat != null && disponiblesActuales.contains(seat);
                setCursor(Cursor.getPredefinedCursor(
                    overAvail ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public void refrescar(Categoria categoria, List<String> asientosDisponibles) {
        this.categoriaActual     = categoria;
        this.disponiblesActuales = new ArrayList<>(asientosDisponibles);
        this.seleccionados.clear();
        this.seatHover = null;

        switch (categoria) {
            case VIP          -> { filas = 3; columnas = 5; }
            case GENERAL      -> { filas = 6; columnas = 8; }
            case PREFERENCIAL -> { filas = 4; columnas = 6; }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (categoriaActual == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        int totalW = columnas * (SEAT_SIZE + SEAT_GAP) - SEAT_GAP;
        int totalH = filas   * (SEAT_SIZE + SEAT_GAP) - SEAT_GAP;
        int startX = (getWidth()  - totalW) / 2;
        int startY = (getHeight() - totalH) / 2 - 28;

        drawColumnLabels(g2, startX, startY);
        drawRowLabels(g2, startX, startY);
        drawSeats(g2, startX, startY);
        drawStage(g2, startX, startY + totalH + 16, totalW);

        g2.dispose();
    }

    private void drawColumnLabels(Graphics2D g2, int startX, int startY) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(LABEL_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        for (int c = 0; c < columnas; c++) {
            String lbl = String.valueOf(c + 1);
            int x = startX + c * (SEAT_SIZE + SEAT_GAP) + SEAT_SIZE / 2 - fm.stringWidth(lbl) / 2;
            g2.drawString(lbl, x, startY - 9);
        }
    }

    private void drawRowLabels(Graphics2D g2, int startX, int startY) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(LABEL_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        for (int r = 0; r < filas; r++) {
            String lbl = String.valueOf((char) ('A' + r));
            int y = startY + r * (SEAT_SIZE + SEAT_GAP) + SEAT_SIZE / 2 + fm.getAscent() / 2 - 2;
            g2.drawString(lbl, startX - 22, y);
        }
    }

    private void drawSeats(Graphics2D g2, int startX, int startY) {
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                String id = String.valueOf((char) ('A' + r)) + (c + 1);
                int x = startX + c * (SEAT_SIZE + SEAT_GAP);
                int y = startY + r * (SEAT_SIZE + SEAT_GAP);
                drawSeat(g2, x, y, id);
            }
        }
    }

    private void drawSeat(Graphics2D g2, int x, int y, String id) {
        boolean avail    = disponiblesActuales.contains(id);
        boolean selected = seleccionados.contains(id);
        boolean hover    = id.equals(seatHover);

        Color base;
        if (selected) {
            float pulse = 0.60f + 0.40f * pulseVal;
            base = new Color(
                Math.min(255, (int)(SEAT_SELECTED.getRed()   * pulse)),
                Math.min(255, (int)(SEAT_SELECTED.getGreen() * pulse)),
                Math.min(255, (int)(SEAT_SELECTED.getBlue()  * pulse))
            );
        } else if (!avail) {
            base = SEAT_OCCUPIED;
        } else if (hover) {
            base = SEAT_HOVER;
        } else {
            base = SEAT_AVAIL;
        }

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 55));
        g2.fillRoundRect(x + 2, y + 3, SEAT_SIZE, SEAT_SIZE, 10, 10);

        // Body
        g2.setColor(base);
        g2.fillRoundRect(x, y, SEAT_SIZE, SEAT_SIZE, 10, 10);

        // Top shine
        int alpha = selected ? 40 : (avail ? 28 : 12);
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.fillRoundRect(x + 3, y + 2, SEAT_SIZE - 6, SEAT_SIZE / 3, 5, 5);

        // Border for selected / hover
        if (selected || hover) {
            g2.setColor(selected ? new Color(255, 220, 70) : new Color(120, 180, 255));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, SEAT_SIZE - 1, SEAT_SIZE - 1, 10, 10);
            g2.setStroke(new BasicStroke(1f));
        }

        // Seat label
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.setColor(avail ? Color.WHITE : new Color(95, 103, 115));
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + SEAT_SIZE / 2 - fm.stringWidth(id) / 2;
        int ty = y + SEAT_SIZE / 2 + fm.getAscent() / 2 - 2;
        g2.drawString(id, tx, ty);
    }

    private void drawStage(Graphics2D g2, int x, int y, int width) {
        GradientPaint gp = new GradientPaint(x, y, new Color(22, 30, 88), x + width, y, new Color(10, 14, 50));
        g2.setPaint(gp);
        g2.fillRoundRect(x, y, width, 24, 12, 12);

        g2.setColor(new Color(70, 100, 210, 80));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x, y, width, 24, 12, 12);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(STAGE_TEXT);
        String txt = "CAMPO / ESCENARIO";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(txt, x + width / 2 - fm.stringWidth(txt) / 2, y + 16);
    }

    private String seatAt(int mx, int my) {
        if (categoriaActual == null) return null;
        int totalW = columnas * (SEAT_SIZE + SEAT_GAP) - SEAT_GAP;
        int totalH = filas   * (SEAT_SIZE + SEAT_GAP) - SEAT_GAP;
        int startX = (getWidth()  - totalW) / 2;
        int startY = (getHeight() - totalH) / 2 - 28;
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                int x = startX + c * (SEAT_SIZE + SEAT_GAP);
                int y = startY + r * (SEAT_SIZE + SEAT_GAP);
                if (mx >= x && mx <= x + SEAT_SIZE && my >= y && my <= y + SEAT_SIZE) {
                    return String.valueOf((char) ('A' + r)) + (c + 1);
                }
            }
        }
        return null;
    }

    // Public API
    public void setOnSeleccionCambiada(Runnable cb)  { this.onSeleccionCambiada = cb; }
    public Categoria getCategoriaActual()             { return categoriaActual; }
    public List<String> getAsientosSeleccionados()    { return new ArrayList<>(seleccionados); }
    public List<String> getDisponiblesActuales()      { return new ArrayList<>(disponiblesActuales); }

    public void limpiarSeleccion() {
        seleccionados.clear();
        repaint();
        if (onSeleccionCambiada != null) onSeleccionCambiada.run();
    }
}
