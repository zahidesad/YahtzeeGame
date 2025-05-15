package yahtzee.view;

public class StaticScoreGroup extends ScoreGroup {
    private static final long serialVersionUID = 1L;

    /**
     * Create static group with default name and zeroed score.
     */
    public StaticScoreGroup() {
        super(yahtzee.model.Categories.Category.getCategory(14)); // placeholder category
        chosen = true;               // Always marked as chosen
        setTextToCategory();
        setScore(0);                 // Display initial zero
        setEnabled(false);           // Disable interaction
    }

    /**
     * Set custom display name for this static field.
     */
    public StaticScoreGroup(String name) {
        this();
        categoryName = name;
        text.setText(categoryName);
    }

    /**
     * Reset score back to zero.
     */
    @Override
    public void reset() {
        super.reset();
        setTextToCategory();
        setScore(0);
    }

    /**
     * Update displayed static score.
     */
    @Override
    public void setScore(int s) {
        score.setText(Integer.toString(s));
    }
}
