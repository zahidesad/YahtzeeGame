package yahtzee.view;

import yahtzee.model.Categories.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UI component for a single scoring category row.
 * Keeps existing public API so GameController and ScoreBoard continue to work unchanged.
 */
public class ScoreGroup extends JPanel implements Resettable {
    private static final long serialVersionUID = 1L;

    /* ---------- visual constants ---------- */
    private static final Color BG_DEFAULT  = new Color(0xFAFAFA);
    private static final Color BG_HOVER    = new Color(0xEEF6FF);
    private static final Color BG_SELECTED = new Color(0x80007BFF, true);   // bright blue
    private static final Color FG_SELECTED = Color.WHITE;
    private static final Color FG_NORMAL   = new Color(0x333333);
    private static final Color FG_SCORE    = Color.BLACK;
    private static final Color BORDER_CLR  = new Color(0xD0D0D0);
    private static final Color SEP_CLR     = new Color(0xCCCCCC);
    private static final int   ARC         = 10;

    /* ---------- category state ---------- */
    protected final Category category;
    protected String   categoryName;
    protected boolean canBeSelected     = false;
    protected boolean chosen            = false;
    protected boolean usingOverrideScore= false;

    /* ---------- swing components ---------- */
    protected final JLabel text;
    protected final JLabel score;

    public ScoreGroup(Category category) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);                       // we paint background ourselves
        setBorder(new EmptyBorder(6, 10, 6, 10));
        setMaximumSize(new Dimension(340, 34));

        this.category     = category;
        this.categoryName = category.toString();

        /* ----- labels ----- */
        text  = new JLabel(categoryName);
        text.setFont(text.getFont().deriveFont(Font.PLAIN, 13f));
        text.setPreferredSize(new Dimension(170, 24));

        score = new JLabel("");
        score.setFont(score.getFont().deriveFont(Font.BOLD, 13f));
        score.setHorizontalAlignment(SwingConstants.RIGHT);
        score.setPreferredSize(new Dimension(50, 24));

        add(text);
        add(Box.createHorizontalGlue());
        add(score);

        /* hover repaint */
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { repaint(); }
            @Override public void mouseExited (MouseEvent e) { repaint(); }
        });
    }

    /* ---------- custom painting ---------- */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        /* pick background colour */
        Color bg = chosen ? BG_SELECTED
                : (getMousePosition() != null && isEnabled()) ? BG_HOVER
                : getBackground().equals(BG_SELECTED) ? BG_SELECTED   // static rows set by ScoreBoard
                : BG_DEFAULT;

        /* rounded fill */
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

        /* separator  – distance from right = score label prefWidth + 12 px padding */
        int sepX = getWidth() - score.getPreferredSize().width - 12;
        g2.setColor(SEP_CLR);
        g2.drawLine(sepX, 4, sepX, getHeight() - 4);

        /* border */
        g2.setColor(BORDER_CLR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        g2.dispose();

        /* update label colours */
        boolean onBlue = bg.equals(BG_SELECTED);
        text .setForeground(onBlue ? FG_SELECTED : FG_NORMAL);
        score.setForeground(onBlue ? FG_SELECTED : FG_SCORE);

        super.paintComponent(g);   // paints children (labels)
    }

    /* ---------- getters used elsewhere ---------- */
    public boolean getCanBeSelected()      { return canBeSelected; }
    public String  getCategoryName()       { return categoryName; }
    public boolean getChosen()             { return chosen; }


    /* ---------- setters called by GameController ---------- */
    public void setText(String s)          { text.setText(s); repaint(); }
    public void setChosen(boolean b)       { chosen = b; repaint(); }
    public void setScore(int s)            { score.setText(Integer.toString(s)); repaint(); }
    public void setCanBeSelected(boolean b, boolean override) {
        canBeSelected = b;
        usingOverrideScore = override;
    }
    public void setTextToCategory()        { text.setText(categoryName); score.setText(""); repaint(); }

    /* ---------- reset ---------- */
    @Override
    public void reset() {
        chosen = false;
        canBeSelected = false;
        usingOverrideScore = false;
        text.setText(categoryName);
        score.setText("");
        repaint();
    }

    /* keep GameController’s enable/disable behaviour */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        text .setEnabled(b);
        score.setEnabled(b);
        repaint();
    }
}
