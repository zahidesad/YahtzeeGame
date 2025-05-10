package yahtzee.view;

import yahtzee.view.Resettable;

import java.awt.*;
import javax.swing.JComponent;

public class YahtzeeDice extends JComponent implements Resettable {
    private static final long serialVersionUID = 1L;
    private Dimension size;
    private Dimension dot;
    private Dimension arc;
    private int value;
    private boolean holdState;

    public YahtzeeDice(int width) {
        super();
        size = new Dimension(width, width);
        dot = new Dimension(size.width / 3, size.height / 3);
        arc = new Dimension((int) Math.sqrt(size.width), (int) Math.sqrt(size.height));
        setSize(this.size.width, size.height);
        setFocusable(true);
        holdState = false;
        value = 1;
    }

    public void setValue(int value) {
        this.value = value;
        repaint();
    }

    public void setHoldState(boolean holdState) {
        this.holdState = holdState;
        repaint();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D antiAlias = (Graphics2D) g;
        antiAlias.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (holdState)
            g.setColor(Color.RED);
        else
            g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc.width, arc.height);
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc.width, arc.height);
        g.setColor(Color.decode("#c0c0c0"));
        g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc.width, arc.height);
        int height;
        int width = height = dot.height * 2 / 3;
        int left = getWidth() * 1 / 3 - dot.width / 2 - width / 4;
        int center = getWidth() * 2 / 3 - dot.width / 2 - width / 2;
        int right = getWidth() * 3 / 3 - dot.width / 2 - width * 3 / 4;
        int top = getHeight() * 1 / 3 - dot.height / 2 - height / 4;
        int middle = getHeight() * 2 / 3 - dot.height / 2 - height / 2;
        int bottom = getHeight() * 3 / 3 - dot.height / 2 - height * 3 / 4;
        g.setColor(Color.BLACK);
        switch (value) {
            case 0: break;
            case 1: g.fillOval(center, middle, width, height); break;
            case 2:
                g.fillOval(right, top, width, height);
                g.fillOval(left, bottom, width, height);
                break;
            case 3:
                g.fillOval(right, top, width, height);
                g.fillOval(center, middle, width, height);
                g.fillOval(left, bottom, width, height);
                break;
            case 4:
                g.fillOval(left, top, width, height);
                g.fillOval(left, bottom, width, height);
                g.fillOval(right, top, width, height);
                g.fillOval(right, bottom, width, height);
                break;
            case 5:
                g.fillOval(left, top, width, height);
                g.fillOval(left, bottom, width, height);
                g.fillOval(right, top, width, height);
                g.fillOval(right, bottom, width, height);
                g.fillOval(center, middle, width, height);
                break;
            case 6:
                g.fillOval(left, top, width, height);
                g.fillOval(left, middle, width, height);
                g.fillOval(left, bottom, width, height);
                g.fillOval(right, top, width, height);
                g.fillOval(right, middle, width, height);
                g.fillOval(right, bottom, width, height);
                break;
        }
    }

    @Override
    public void reset() {
        value = 1;
        holdState = false;
        repaint();
    }
}
