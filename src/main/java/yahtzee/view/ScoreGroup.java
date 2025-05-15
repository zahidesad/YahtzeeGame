package yahtzee.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UI component for a single scoring category: displays name and score with interactive styling.
 */
public class ScoreGroup extends JPanel implements Resettable {
    private static final long serialVersionUID = 1L;

    // Visual style constants
    private static final Color BG_DEFAULT = new Color(0xFAFAFA);
    private static final Color BG_HOVER = new Color(0xEEF6FF);
    private static final Color BG_SELECTED = new Color(0x007BFF);
    private static final Color FG_SELECTED = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xD0D0D0);
    private static final int ARC = 10;

    // Category state
    protected yahtzee.model.Categories.Category category; // Scoring logic
    protected boolean canBeSelected;                      // Eligibility for selection
    protected boolean chosen;                             // Selection state
    protected boolean usingOverrideScore;                 // Override rule flag
    protected String categoryName;                        // Display name
    protected JLabel text;                                // Label for category name or score hint
    protected JLabel score;                               // Label for chosen score

    /**
     * Create ScoreGroup for given category with default styling.
     */
    public ScoreGroup(yahtzee.model.Categories.Category category) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(6, 10, 6, 10));
        setMaximumSize(new Dimension(340, 34));

        this.category = category;
        this.categoryName = category.toString();
        this.canBeSelected = false;
        this.chosen = false;
        this.usingOverrideScore = false;

        // Initialize text and score labels
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

        // Hover effect triggers repaint
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

    /**
     * Custom painting for background, separator, and border.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine background color based on state
        Color bg = chosen ? BG_SELECTED
                : (getMousePosition() != null && isEnabled()) ? BG_HOVER
                : BG_DEFAULT;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

        // Separator line before score label
        int sepX = getWidth() - 70;
        g2.setColor(new Color(0xCCCCCC));
        g2.drawLine(sepX, 4, sepX, getHeight() - 4);

        // Draw border
        g2.setColor(BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        g2.dispose();

        super.paintComponent(g);

        // Update label colors when selected
        if (chosen) {
            text.setForeground(FG_SELECTED);
            score.setForeground(FG_SELECTED);
        } else {
            text.setForeground(Color.DARK_GRAY);
            score.setForeground(Color.BLACK);
        }
    }

    /* ---------- Helper methods for external control ---------- */
    public boolean getCanBeSelected() {
        return canBeSelected;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean getChosen() {
        return chosen;
    }

    public boolean getUsingOverrideScore() {
        return usingOverrideScore;
    }

    public boolean isYahtzeeScoreGroup() {
        return category instanceof yahtzee.model.Categories.Yahtzee;
    }

    /**
     * Reset visual and state flags to default.
     */
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
        repaint();
    }

    public void setChosen(boolean b) {
        chosen = b;
        repaint();
    }

    public void setScore(int s) {
        score.setText(Integer.toString(s));
        repaint();
    }

    public void setCanBeSelected(boolean b, boolean override) {
        canBeSelected = b;
        usingOverrideScore = override;
    }

    public void setTextToCategory() {
        text.setText(categoryName);
        score.setText("");
        repaint();
    }
}