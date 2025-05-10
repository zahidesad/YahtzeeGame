package yahtzee.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScoreGroup extends JPanel implements Resettable {
    private static final long serialVersionUID = 1L;
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
        canBeSelected = false;
        chosen = false;
        usingOverrideScore = false;
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        text = new JLabel();
        text.setPreferredSize(new Dimension(150, 30));
        score = new JLabel();
        this.category = category;
        this.categoryName = category.toString();
        this.text.setText(categoryName);
        add(Box.createHorizontalStrut(4));
        add(text);
        add(score);
        add(Box.createGlue());
        repaint();
    }

    public boolean getCanBeSelected() {
        return canBeSelected;
    }

    public String getCategoryName() {
        return this.categoryName;
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (text != null) {
            int s = 130;
            g.drawLine(s, 0, s, (int) this.getSize().getHeight());
        }
    }

    @Override
    public void reset() {
        chosen = false;
        canBeSelected = false;
        usingOverrideScore = false;
        this.text.setText(category.toString());
        this.score.setText("");
    }

    public void setText(String s) {
        this.text.setText(s);
    }


    public void setChosen(boolean b) {
        this.chosen = b;
    }

    public void setScore(int score) {
        this.score.setText(Integer.toString(score));
    }

    public void setCanBeSelected(boolean b, boolean b1) {
        canBeSelected = b;
    }

    public void setTextToCategory() {
        this.text.setText(categoryName);
        this.score.setText("");
    }
}
