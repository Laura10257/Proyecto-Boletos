package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import modelo.Categoria;

/**
 * Mapa unificado del estadio.
 * El paso (STEP) se calcula dinámicamente para que GENERAL llene todo el ancho.
 * PREF ocupa los laterales, VIP la parte inferior.
 * El área central queda libre para la foto del estadio.
 *
 *  GENERAL  (5 filas × 20 cols — ancho completo)
 *  PREF │          FOTO          │ PREF    (5 filas × 3 cols cada lateral)
 *       VIP  (3 filas × 20 cols — ancho completo)
 *            [ CAMPO / ESTADIO ]
 */
public class PanelAsientos extends JPanel {

    // ── Colores ───────────────────────────────────────────────────────────────
    private static final Color BG      = new Color(13, 17, 23);
    private static final Color OCC     = new Color(48, 55, 65);
    private static final Color SEL     = new Color(210, 153, 34);
    private static final Color HOV     = new Color(56, 139, 253);
    private static final Color VIP_C   = new Color(155, 128,  0);
    private static final Color GEN_C   = new Color( 28,  90, 165);
    private static final Color PRE_C   = new Color( 22, 105,  50);
    private static final Color VIP_LBL = new Color(255, 215,   0);
    private static final Color GEN_LBL = new Color( 88, 166, 255);
    private static final Color PRE_LBL = new Color(126, 231, 135);

    // ── Dimensiones de cada zona (sincronizadas con VentaServicio) ─────────────
    static final int GEN_ROWS = 5,  GEN_COLS = 20;
    static final int PRE_ROWS = 5,  PRE_HALF = 3;   // 3 cols por lateral
    static final int VIP_ROWS = 3,  VIP_COLS = 20;

    private static final int GAP = 3;
    private static final int PAD = 10;

    // ── Estado ────────────────────────────────────────────────────────────────
    private final Map<String, Boolean>   occ   = new LinkedHashMap<>();
    private final Set<String>            sel   = new LinkedHashSet<>();
    private final Map<String, Rectangle> rects = new HashMap<>();

    private String    hover    = null;
    private Categoria catActiva = Categoria.VIP;
    private Runnable  onChange;

    private float   pulse = 0f;
    private boolean pUp   = true;

    public PanelAsientos() {
        setBackground(BG);
        setOpaque(true);

        new Timer(40, e -> {
            if (!sel.isEmpty()) {
                if (pUp) { pulse = Math.min(1f, pulse + 0.06f); if (pulse >= 1f) pUp = false; }
                else     { pulse = Math.max(0f, pulse - 0.06f); if (pulse <= 0f) pUp = true;  }
                repaint();
            }
        }).start();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                String k = hit(e.getX(), e.getY());
                if (!Objects.equals(k, hover)) { hover = k; repaint(); }
                setCursor(Cursor.getPredefinedCursor(
                    k != null && !occ.getOrDefault(k, true)
                        ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                String k = hit(e.getX(), e.getY());
                if (k == null || occ.getOrDefault(k, true)) return;
                Categoria c = catOf(k);
                if (c != catActiva) { sel.clear(); catActiva = c; }
                if (sel.contains(k)) sel.remove(k); else sel.add(k);
                repaint();
                if (onChange != null) onChange.run();
            }
            @Override public void mouseExited(MouseEvent e) { hover = null; repaint(); }
        });
    }

    // ── API pública ───────────────────────────────────────────────────────────

    public void refrescarTodo(Map<Categoria, List<String>> por) {
        occ.clear(); sel.clear(); hover = null;
        for (Categoria cat : Categoria.values()) {
            Set<String> d = new HashSet<>(por.getOrDefault(cat, List.of()));
            for (int r = 0; r < rows(cat); r++)
                for (int c = 0; c < cols(cat); c++)
                    occ.put(key(cat, r, c), !d.contains(lbl(r, c)));
        }
        repaint();
    }

    public void refrescar(Categoria cat, List<String> disponibles) {
        Set<String> d = new HashSet<>(disponibles);
        sel.clear(); hover = null;
        for (int r = 0; r < rows(cat); r++)
            for (int c = 0; c < cols(cat); c++)
                occ.put(key(cat, r, c), !d.contains(lbl(r, c)));
        repaint();
    }

    public void setOnSeleccionCambiada(Runnable cb) { onChange = cb; }
    public Categoria getCategoriaActual()            { return catActiva; }

    public List<String> getAsientosSeleccionados() {
        List<String> r = new ArrayList<>();
        for (String k : sel) r.add(labelOf(k));
        return r;
    }

    public List<String> getDisponiblesActuales() {
        List<String> r = new ArrayList<>();
        for (int row = 0; row < rows(catActiva); row++)
            for (int col = 0; col < cols(catActiva); col++) {
                String k = key(catActiva, row, col);
                if (!occ.getOrDefault(k, true)) r.add(lbl(row, col));
            }
        return r;
    }

    public void limpiarSeleccion() {
        sel.clear(); repaint();
        if (onChange != null) onChange.run();
    }

    // ── Pintura ───────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        rects.clear();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();
        if (W < 80 || H < 80) { g2.dispose(); return; }

        int campoH = 18;

        // ── Paso dinámico: acotado por ANCHO y por ALTO ───────────────────────
        // Para que todas las filas quepan sin solaparse se calcula el step
        // máximo que cabe en la altura disponible para los asientos.
        int availW     = W - 2 * PAD;
        int stepW      = (availW + GAP) / GEN_COLS;                // llena el ancho

        int nonSeatH   = 2 * PAD + 14 + 6 + 8 + campoH + 3 * 10; // márgenes, etiquetas, campo
        int totalRows  = GEN_ROWS + PRE_ROWS + VIP_ROWS;
        int stepH      = (H - nonSeatH + GAP) / totalRows;         // llena el alto

        int step = Math.max(16, Math.min(42, Math.min(stepW, stepH)));
        int s    = step - GAP;

        // ── Dimensiones calculadas ───────────────────────────────────────────
        int genW  = GEN_COLS * step - GAP;
        int genH  = GEN_ROWS * step - GAP;
        int preW  = PRE_HALF * step - GAP;
        int preH  = PRE_ROWS * step - GAP;
        int vipW  = VIP_COLS * step - GAP;
        int vipH  = VIP_ROWS * step - GAP;

        // ── Posiciones ───────────────────────────────────────────────────────
        int genX   = (W - genW) / 2;             // centrado
        int vipX   = (W - vipW) / 2;
        int genY   = PAD + 14;
        int vipY   = H - PAD - campoH - 8 - vipH;
        int campoY = vipY + vipH + 6;

        int midY = genY + genH + 10;
        int midH = vipY - midY - 10;

        int preLeftX  = PAD;
        int preRightX = W - PAD - preW;
        // Centrar PREF en el espacio medio y garantizar que no pise VIP
        int preY = midY + Math.max(0, (midH - preH) / 2);
        preY = Math.min(preY, vipY - preH - 4);   // clamp de seguridad

        int imgX = preLeftX + preW + 8;
        int imgW = preRightX - imgX - 8;
        int imgY = midY;

        // ── Fondo único del estadio ──────────────────────────────────────────
        g2.setColor(new Color(18, 24, 32));
        g2.fillRoundRect(2, 2, W - 4, H - 4, 14, 14);
        g2.setColor(new Color(42, 52, 64));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(2, 2, W - 4, H - 4, 14, 14);

        // ── Imagen / campo central ────────────────────────────────────────────
        drawCentro(g2, imgX, imgY, imgW, midH);

        // ── Etiquetas de zona ─────────────────────────────────────────────────
        zoneLbl(g2, genX + genW / 2, PAD + 10,     "GENERAL",      GEN_LBL);
        zoneLbl(g2, vipX + vipW / 2, vipY - 6,     "VIP",          VIP_LBL);
        zoneLblV(g2, preLeftX  + preW / 2, preY + preH / 2, "PREF", PRE_LBL);
        zoneLblV(g2, preRightX + preW / 2, preY + preH / 2, "PREF", PRE_LBL);

        // ── Asientos GENERAL (llena todo el ancho) ────────────────────────────
        for (int r = 0; r < GEN_ROWS; r++)
            for (int c = 0; c < GEN_COLS; c++)
                seat(g2, s, genX + c * step, genY + r * step, Categoria.GENERAL, r, c);

        // ── Asientos PREF izquierda (cols 0-2) ────────────────────────────────
        for (int r = 0; r < PRE_ROWS; r++)
            for (int c = 0; c < PRE_HALF; c++)
                seat(g2, s, preLeftX + c * step, preY + r * step, Categoria.PREFERENCIAL, r, c);

        // ── Asientos PREF derecha (cols 3-5) ──────────────────────────────────
        for (int r = 0; r < PRE_ROWS; r++)
            for (int c = 0; c < PRE_HALF; c++)
                seat(g2, s, preRightX + c * step, preY + r * step, Categoria.PREFERENCIAL, r, c + PRE_HALF);

        // ── Asientos VIP (llena todo el ancho) ───────────────────────────────
        for (int r = 0; r < VIP_ROWS; r++)
            for (int c = 0; c < VIP_COLS; c++)
                seat(g2, s, vipX + c * step, vipY + r * step, Categoria.VIP, r, c);

        // ── Barra CAMPO ───────────────────────────────────────────────────────
        g2.setPaint(new GradientPaint(vipX, campoY, new Color(22, 30, 88), vipX + vipW, campoY, new Color(10, 14, 50)));
        g2.fillRoundRect(vipX, campoY, vipW, campoH, 8, 8);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
        g2.setColor(new Color(130, 140, 210));
        String ct = "CAMPO";
        FontMetrics fmc = g2.getFontMetrics();
        g2.drawString(ct, vipX + vipW / 2 - fmc.stringWidth(ct) / 2, campoY + campoH / 2 + fmc.getAscent() / 2 - 2);

        g2.dispose();
    }

    private void drawCentro(Graphics2D g2, int x, int y, int w, int h) {
        if (w < 10 || h < 10) return;
        g2.setColor(new Color(6, 10, 6));
        g2.fillRoundRect(x, y, w, h, 10, 10);
        g2.setPaint(new GradientPaint(x, y, new Color(12, 65, 20), x, y + h, new Color(6, 40, 10)));
        g2.fillRoundRect(x + 3, y + 3, w - 6, h - 6, 7, 7);

        int cx = x + w / 2, cy = y + h / 2;
        g2.setColor(new Color(255, 255, 255, 18));
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(x + 7, y + 7, w - 14, h - 14, 5, 5);
        g2.drawLine(cx, y + 9, cx, y + h - 9);
        int cr = Math.min(w, h) / 7;
        g2.drawOval(cx - cr, cy - cr, cr * 2, cr * 2);
        g2.setColor(new Color(255, 255, 255, 28));
        g2.fillOval(cx - 3, cy - 3, 6, 6);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        g2.setColor(new Color(255, 255, 255, 32));
        String t = "[ FOTO DEL ESTADIO ]";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t, cx - fm.stringWidth(t) / 2, y + h - 8);

        g2.setColor(new Color(50, 130, 60, 65));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x, y, w, h, 10, 10);
    }

    private void zoneLbl(Graphics2D g2, int cx, int y, String txt, Color c) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.setColor(c);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(txt, cx - fm.stringWidth(txt) / 2, y);
    }

    private void zoneLblV(Graphics2D g2, int cx, int cy, String txt, Color c) {
        Graphics2D g3 = (Graphics2D) g2.create();
        g3.setFont(new Font("Segoe UI", Font.BOLD, 8));
        g3.setColor(c);
        FontMetrics fm = g3.getFontMetrics();
        g3.rotate(-Math.PI / 2, cx, cy);
        g3.drawString(txt, cx - fm.stringWidth(txt) / 2, cy + fm.getAscent() / 2 - 1);
        g3.dispose();
    }

    private void seat(Graphics2D g2, int sz, int px, int py, Categoria cat, int r, int c) {
        String  k     = key(cat, r, c);
        boolean isOcc = occ.getOrDefault(k, false);
        boolean isSel = sel.contains(k);
        boolean isHov = k.equals(hover);

        rects.put(k, new Rectangle(px, py, sz, sz));

        Color base;
        if (isSel) {
            float p = 0.55f + 0.45f * pulse;
            base = new Color(
                Math.min(255, (int)(SEL.getRed()   * p)),
                Math.min(255, (int)(SEL.getGreen() * p)),
                Math.min(255, (int)(SEL.getBlue()  * p)));
        } else if (isOcc) {
            base = OCC;
        } else if (isHov) {
            base = HOV;
        } else {
            base = switch (cat) {
                case VIP          -> VIP_C;
                case GENERAL      -> GEN_C;
                case PREFERENCIAL -> PRE_C;
            };
        }

        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(px + 1, py + 2, sz, sz, 6, 6);
        g2.setColor(base);
        g2.fillRoundRect(px, py, sz, sz, 6, 6);
        g2.setColor(new Color(255, 255, 255, isOcc ? 5 : (isSel ? 28 : 15)));
        g2.fillRoundRect(px + 2, py + 1, sz - 4, sz / 3, 3, 3);

        if (isSel || isHov) {
            g2.setColor(isSel ? new Color(255, 220, 70, 200) : new Color(120, 180, 255, 180));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(px, py, sz - 1, sz - 1, 6, 6);
            g2.setStroke(new BasicStroke(1f));
        }

        // Etiqueta solo si el asiento es suficientemente grande
        if (sz >= 18) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(6, sz / 4)));
            g2.setColor(isOcc ? new Color(65, 72, 82) : new Color(210, 228, 245));
            FontMetrics fm = g2.getFontMetrics();
            String t = lbl(r, c);
            int tx = px + sz / 2 - fm.stringWidth(t) / 2;
            int ty = py + sz / 2 + fm.getAscent() / 2 - 2;
            g2.drawString(t, tx, ty);
        }
    }

    // ── Hit testing ───────────────────────────────────────────────────────────

    private String hit(int mx, int my) {
        for (Map.Entry<String, Rectangle> e : rects.entrySet())
            if (e.getValue().contains(mx, my)) return e.getKey();
        return null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String key(Categoria cat, int r, int c) { return cat.name() + "_" + r + "_" + c; }
    private static String lbl(int r, int c)                 { return String.valueOf((char)('A' + r)) + (c + 1); }

    private static String labelOf(String k) {
        String[] p = k.split("_");
        return lbl(Integer.parseInt(p[p.length - 2]), Integer.parseInt(p[p.length - 1]));
    }

    private static Categoria catOf(String k) {
        if (k.startsWith("VIP"))          return Categoria.VIP;
        if (k.startsWith("GENERAL"))      return Categoria.GENERAL;
        if (k.startsWith("PREFERENCIAL")) return Categoria.PREFERENCIAL;
        throw new IllegalArgumentException(k);
    }

    private static int rows(Categoria cat) { return switch (cat) { case VIP -> VIP_ROWS; case GENERAL -> GEN_ROWS; case PREFERENCIAL -> PRE_ROWS; }; }
    private static int cols(Categoria cat) { return switch (cat) { case VIP -> VIP_COLS; case GENERAL -> GEN_COLS; case PREFERENCIAL -> PRE_HALF * 2; }; }
}
