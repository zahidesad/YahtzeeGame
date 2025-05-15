package yahtzee.view;

public class StaticScoreGroup extends ScoreGroup implements Resettable {
    private static final long serialVersionUID = 1L;

    public StaticScoreGroup() {
        super(yahtzee.model.Categories.Category.getCategory(14));
        this.chosen = true;           // always considered chosen / non-clickable
        setTextToCategory();
        score.setText("0");
        setEnabled(false);            // grey out by default
    }

    public StaticScoreGroup(String name) {
        this();
        this.categoryName = name;
        text.setText(categoryName);
    }

    @Override
    public void reset() {
        super.reset();
        setTextToCategory();
        score.setText("0");
    }

    public void setScore(int s) {
        score.setText(Integer.toString(s));
    }
}