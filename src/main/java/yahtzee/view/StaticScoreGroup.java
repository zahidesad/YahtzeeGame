package yahtzee.view;

public class StaticScoreGroup extends ScoreGroup implements Resettable {
    private static final long serialVersionUID = 1L;

    public StaticScoreGroup() {
        super(yahtzee.model.Categories.Category.getCategory(14));
        this.chosen = true;
        this.setTextToCategory();
        this.score.setText("0");
    }

    public StaticScoreGroup(String name) {
        this();
        this.categoryName = name;
        this.text.setText(categoryName);
    }

    @Override
    public void reset() {
        super.reset();
        this.setTextToCategory();
        this.score.setText("0");
    }

    public void setScore(int score) {
        this.score.setText(Integer.toString(score));
    }
}
