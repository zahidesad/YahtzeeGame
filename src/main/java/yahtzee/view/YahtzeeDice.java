package yahtzee.view;

import java.awt.*;
import javax.swing.*;

/**
 * Displays a single die face with pips, shadow, and hold-overlay.
 */
public class YahtzeeDice extends JComponent implements Resettable {
    private static final long serialVersionUID = 1L;

    // Visual style constants
    private static final Color FACE_GRADIENT_TOP = new Color(0xFDFDFD);  // Top of face gradient
    private static final Color FACE_GRADIENT_BOTTOM = new Color(0xE8E8E8); // Bottom of face gradient
    private static final Color BORDER_COLOR = new Color(0xB5B5B5);      // Face border color
    private static final Color PIP_COLOR = new Color(0x222222);         // Pip (dot) color
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 40);    // Soft drop shadow
    private static final Color HOLD_OVERLAY_COLOR = new Color(0, 123, 255, 80); // Hold-state overlay

    // Component state
    private final Dimension size;   // Fixed component size (square)
    private final Dimension dot;    // Cell size for pip placement
    private final int arc;          // Corner rounding arc
    private int value = 1;          // Current pip count
    private boolean holdState = false; // Whether die is held

    /**
     * Create a die component of given pixel width.
     */
    public YahtzeeDice(int width) {
        this.size = new Dimension(width, width);
        this.dot = new Dimension(size.width / 3, size.height / 3);
        this.arc = (int) (size.width * 0.25f);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(true);
    }

    /**
     * Set the face value (1-6) and repaint.
     */
    public void setValue(int value) {
        this.value = value;
        repaint();
    }

    /**
     * Set hold state overlay and repaint.
     */
    public void setHoldState(boolean holdState) {
        this.holdState = holdState;
        repaint();
    }

    /**
     * Reset die to default face and release hold.
     */
    @Override
    public void reset() {
        value = 1;
        holdState = false;
        repaint();
    }

    /**
     * Paint die: shadow, face gradient, border, overlay, and pips.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw drop shadow
        g2.setColor(SHADOW_COLOR);
        g2.fillRoundRect(4, 4, w - 1, h - 1, arc, arc);

        // Draw face gradient
        GradientPaint gp = new GradientPaint(0, 0, FACE_GRADIENT_TOP, 0, h, FACE_GRADIENT_BOTTOM);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w - 4, h - 4, arc, arc);

        // Draw border
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, w - 4, h - 4, arc, arc);

        // Draw hold overlay if held
        if (holdState) {
            g2.setColor(HOLD_OVERLAY_COLOR);
            g2.fillRoundRect(0, 0, w - 4, h - 4, arc, arc);
        }

        // Draw pips based on value
        g2.setColor(PIP_COLOR);
        int pipSize = (int) (dot.width * 0.45);
        int[] xs = {dot.width / 2,
                w / 2 - pipSize / 2,
                w - dot.width + dot.width / 2 - pipSize};
        int[] ys = {dot.height / 2,
                h / 2 - pipSize / 2,
                h - dot.height + dot.height / 2 - pipSize};

        switch (value) {
            case 1 -> drawPip(g2, xs[1], ys[1], pipSize);
            case 2 -> {
                drawPip(g2, xs[2], ys[0], pipSize);
                drawPip(g2, xs[0], ys[2], pipSize);
            }
            case 3 -> {
                drawPip(g2, xs[2], ys[0], pipSize);
                drawPip(g2, xs[1], ys[1], pipSize);
                drawPip(g2, xs[0], ys[2], pipSize);
            }
            case 4 -> {
                drawPip(g2, xs[0], ys[0], pipSize);
                drawPip(g2, xs[0], ys[2], pipSize);
                drawPip(g2, xs[2], ys[0], pipSize);
                drawPip(g2, xs[2], ys[2], pipSize);
            }
            case 5 -> {
                drawPip(g2, xs[0], ys[0], pipSize);
                drawPip(g2, xs[0], ys[2], pipSize);
                drawPip(g2, xs[2], ys[0], pipSize);
                drawPip(g2, xs[2], ys[2], pipSize);
                drawPip(g2, xs[1], ys[1], pipSize);
            }
            case 6 -> {
                drawPip(g2, xs[0], ys[0], pipSize);
                drawPip(g2, xs[0], ys[1], pipSize);
                drawPip(g2, xs[0], ys[2], pipSize);
                drawPip(g2, xs[2], ys[0], pipSize);
                drawPip(g2, xs[2], ys[1], pipSize);
                drawPip(g2, xs[2], ys[2], pipSize);
            }
        }
        g2.dispose();
    }

    /**
     * Draw a single pip at given position and size.
     */
    private void drawPip(Graphics2D g2, int x, int y, int size) {
        g2.fillOval(x, y, size, size);
    }

    // Ensure component remains square
    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public Dimension getMinimumSize() {
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        return size;
    }
}