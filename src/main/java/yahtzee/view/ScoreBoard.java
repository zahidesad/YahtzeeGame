package yahtzee.view;


import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class ScoreBoard extends JPanel implements Resettable {
    private static final long serialVersionUID = 1L;
    private ScoreGroup[] categories;
    private JPanel higherCategories;
    private JPanel lowerCategories;
    private StaticScoreGroup upperSectionBonus;
    private StaticScoreGroup upperSectionTotal;
    private StaticScoreGroup lowerSectionYahtzeeBonus;
    private StaticScoreGroup grandTotal;

    public ScoreBoard() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        this.setPreferredSize(new Dimension(750, 300));
        categories = new ScoreGroup[13];

        higherCategories = new JPanel();
        higherCategories.setPreferredSize(new Dimension(350, 300));
        higherCategories.setLayout(new BoxLayout(higherCategories, BoxLayout.Y_AXIS));

        lowerCategories = new JPanel();
        lowerCategories.setPreferredSize(new Dimension(350, 300));
        lowerCategories.setLayout(new BoxLayout(lowerCategories, BoxLayout.Y_AXIS));

        fillCategories();
        addCategories();
    }

    private void fillCategories() {
        for (int i = 0; i < categories.length; i++) {
            categories[i] = new ScoreGroup(yahtzee.model.Categories.Category.getCategory(i + 1));
        }
        upperSectionBonus = new StaticScoreGroup("Upper Section Bonus");
        upperSectionTotal = new StaticScoreGroup("Upper Total");
        lowerSectionYahtzeeBonus = new StaticScoreGroup("Yahtzee Bonus");
        grandTotal = new StaticScoreGroup("Grand Total");
    }

    private void addCategories() {
        for (int i = 0; i < 6; i++) {
            addCategory(higherCategories, categories[i]);
        }
        addCategory(higherCategories, upperSectionBonus);
        addCategory(higherCategories, upperSectionTotal);
        for (int i = 6; i < categories.length; i++) {
            addCategory(lowerCategories, categories[i]);
        }
        addCategory(lowerCategories, lowerSectionYahtzeeBonus);
        addCategory(lowerCategories, grandTotal);
        this.add(higherCategories);
        this.add(lowerCategories);
    }

    private void addCategory(JPanel panel, ScoreGroup group) {
        panel.add(group);
        panel.add(Box.createVerticalStrut(7));
    }

    public ScoreGroup[] getScoreGroups() {
        return categories;
    }

    public StaticScoreGroup getUpperSectionBonus() {
        return upperSectionBonus;
    }

    public StaticScoreGroup getUpperSectionTotal() {
        return upperSectionTotal;
    }

    public StaticScoreGroup getLowerSectionYahtzeeBonus() {
        return lowerSectionYahtzeeBonus;
    }

    public StaticScoreGroup getGrandTotal() {
        return grandTotal;
    }

    @Override
    public void reset() {
        for (ScoreGroup category : categories) {
            category.reset();
        }
        upperSectionBonus.reset();
        upperSectionTotal.reset();
        lowerSectionYahtzeeBonus.reset();
        grandTotal.reset();
    }
}