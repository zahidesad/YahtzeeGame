package yahtzee.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScoreGroup extends JPanel implements Resettable {
    private static final long serialVersionUID = 1L;

    /* ---- visual constants ---- */
    private static final Color BG_DEFAULT = new Color(0xFAFAFA);
    private static final Color BG_HOVER = new Color(0xEEF6FF);
    private static final Color BG_SELECTED = new Color(0x007BFF);
    private static final Color FG_SELECTED = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xD0D0D0);
    private static final int ARC = 10;

    /* ---- state (same as before) ---- */
    protected yahtzee.model.Categories.Category category;
    protected boolean canBeSelected;
    protected boolean chosen;
    protected boolean usingOverrideScore;
    protected String categoryName;
    protected JLabel text;
    protected JLabel score;

    public ScoreGroup(yahtzee.model.Categories.Category category) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false); // we'll paint our own background
        setBorder(new EmptyBorder(6, 10, 6, 10));
        setMaximumSize(new Dimension(340, 34));

        /* ---- init state ---- */
        canBeSelected = false;
        chosen = false;
        usingOverrideScore = false;
        this.category = category;
        this.categoryName = category.toString();

        /* ---- labels ---- */
        text = new JLabel(categoryName);
        text.setFont(text.getFont().deriveFont(Font.PLAIN, 13f));
        text.setPreferredSize(new Dimension(170, 24));

        score = new JLabel("");
        score.setFont(score.getFont().deriveFont(Font.BOLD, 13f));
        score.setHorizontalAlignment(SwingConstants.RIGHT);
        score.setPreferredSize(new Dimension(50, 24));

        add(text);
        add(Box.createHorizontalGlue());
        add(score);

        /* ---- hover highlight (visual only) ---- */
        MouseAdapter hover = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        };
        addMouseListener(hover);
    }

    /* -------------------------------------------------- */

    public boolean getCanBeSelected() {
        return canBeSelected;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean getChosen() {
        return chosen;
    }

    public boolean getIsUpper() {
        return category.getCategoryIndex() <= 6;
    }

    public boolean getUsingOverrideScore() {
        return usingOverrideScore;
    }

    public boolean isYahtzeeScoreGroup() {
        return category instanceof yahtzee.model.Categories.Yahtzee;
    }

    /* -------------------------------------------------- */

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /* --- background --- */
        Color bg;
        if (chosen) bg = BG_SELECTED;
        else if (getMousePosition() != null && isEnabled()) bg = BG_HOVER;
        else bg = BG_DEFAULT;

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

        /* --- vertical separator line before score --- */
        int sepX = getWidth() - 70;
        g2.setColor(new Color(0xCCCCCC));
        g2.drawLine(sepX, 4, sepX, getHeight() - 4);

        /* --- border --- */
        g2.setColor(BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        g2.dispose();
        super.paintComponent(g); // paint children (labels)

        /* swap label colors if selected */
        if (chosen) {
            text.setForeground(FG_SELECTED);
            score.setForeground(FG_SELECTED);
        } else {
            text.setForeground(Color.DARK_GRAY);
            score.setForeground(Color.BLACK);
        }
    }

    /* ------------------ state helpers ------------------ */

    @Override
    public void reset() {
        chosen = false;
        canBeSelected = false;
        usingOverrideScore = false;
        text.setText(categoryName);
        score.setText("");
        repaint();
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        text.setEnabled(b);
        score.setEnabled(b);
        repaint();
    }

    public void setText(String s) {
        text.setText(s);
    }

    public void setChosen(boolean b) {
        chosen = b;
        repaint();
    }

    public void setScore(int s) {
        score.setText(Integer.toString(s));
        repaint();
    }

    public void setCanBeSelected(boolean b, boolean ignored) {
        canBeSelected = b;
    }

    public void setTextToCategory() {
        text.setText(categoryName);
        score.setText("");
    }
}