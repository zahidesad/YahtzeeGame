package yahtzee.view;

import yahtzee.model.Categories.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Displays player and opponent score categories and totals side by side.
 */
public class ScoreBoard extends JPanel implements Resettable {

    private ScoreGroup[] playerCategories;    // Player's category rows
    private ScoreGroup[] opponentCategories;  // Opponent's category rows
    private JPanel playerPanel;
    private JPanel opponentPanel;

    // Static total/bonus rows for each side
    private StaticScoreGroup playerUpperSectionBonus, playerUpperSectionTotal,
            playerLowerSectionYahtzeeBonus, playerGrandTotal;
    private StaticScoreGroup opponentUpperSectionBonus, opponentUpperSectionTotal,
            opponentLowerSectionYahtzeeBonus, opponentGrandTotal;

    /**
     * Initialize the scoreboard with two side-by-side columns.
     */
    public ScoreBoard() {
        setLayout(new GridLayout(1, 2, 20, 0));  // 2 columns with a 20px gap
        // Create the two column panels with headers
        playerPanel = createColumnPanel("Your Scores");
        opponentPanel = createColumnPanel("Opponent Scores");

        playerCategories = new ScoreGroup[13];
        opponentCategories = new ScoreGroup[13];
        fillCategories();
        addCategories();

        // Add the two columns to this panel
        add(playerPanel);
        add(opponentPanel);

        // Style the scoreboard panel: white background with gray border and padding
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                new EmptyBorder(10, 15, 10, 15)
        ));
    }

    /**
     * Create one of the two score columns with a title header.
     */
    private JPanel createColumnPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // Add a bold header label at the top of the column
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(8));  // spacing below header
        return panel;
    }

    /**
     * Instantiate all score category and static total objects.
     */
    private void fillCategories() {
        for (int i = 0; i < 13; i++) {
            playerCategories[i] = new ScoreGroup(Category.getCategory(i + 1));
            opponentCategories[i] = new ScoreGroup(Category.getCategory(i + 1));
            opponentCategories[i].setEnabled(false);  // opponent's categories not selectable by player
        }
        // Create static score rows for each section
        playerUpperSectionBonus   = new StaticScoreGroup("Upper Section Bonus");
        playerUpperSectionTotal   = new StaticScoreGroup("Upper Total");
        playerLowerSectionYahtzeeBonus = new StaticScoreGroup("Yahtzee Bonus");
        playerGrandTotal          = new StaticScoreGroup("Grand Total");
        opponentUpperSectionBonus = new StaticScoreGroup("Upper Section Bonus");
        opponentUpperSectionTotal = new StaticScoreGroup("Upper Total");
        opponentLowerSectionYahtzeeBonus = new StaticScoreGroup("Yahtzee Bonus");
        opponentGrandTotal        = new StaticScoreGroup("Grand Total");
    }

    /**
     * Add all categories and totals into the column panels, then apply styling.
     */
    private void addCategories() {
        // Add the 13 category rows
        for (int i = 0; i < 13; i++) {
            addCategory(playerPanel, playerCategories[i]);
            addCategory(opponentPanel, opponentCategories[i]);
        }
        // Add the static total/bonus rows
        addCategory(playerPanel, playerUpperSectionBonus);
        addCategory(playerPanel, playerUpperSectionTotal);
        addCategory(playerPanel, playerLowerSectionYahtzeeBonus);
        addCategory(playerPanel, playerGrandTotal);
        addCategory(opponentPanel, opponentUpperSectionBonus);
        addCategory(opponentPanel, opponentUpperSectionTotal);
        addCategory(opponentPanel, opponentLowerSectionYahtzeeBonus);
        addCategory(opponentPanel, opponentGrandTotal);

        // Apply consistent styling to each column
        styleScoreColumn(playerPanel);
        styleScoreColumn(opponentPanel);
    }

    /**
     * Helper to add a score row to a column panel (no extra spacing is added).
     */
    private void addCategory(JPanel panel, ScoreGroup group) {
        panel.add(group);
        // (Removed the 6px vertical strut to keep rows compact)
    }

    /**
     * Apply styling to all score rows in the given column:
     * make backgrounds transparent, add separators, bold text for totals.
     */
    private void styleScoreColumn(JPanel columnPanel) {
        Component[] comps = columnPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof ScoreGroup sg) {
                // Make the score group background transparent (inherits column background)
                sg.setOpaque(false);
                // Draw a light gray line below each row except the last
                if (i < comps.length - 1) {
                    sg.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xCCCCCC)));
                } else {
                    sg.setBorder(null);
                }
                // Highlight static total/bonus rows by using bold font
                if (sg instanceof StaticScoreGroup) {
                    sg.text.setFont(sg.text.getFont().deriveFont(Font.BOLD));
                    sg.score.setFont(sg.score.getFont().deriveFont(Font.BOLD));
                }
            }
        }
    }

    // Getters for external access
    public ScoreGroup[] getScoreGroups()              { return playerCategories; }
    public ScoreGroup[] getOpponentScoreGroups()      { return opponentCategories; }
    public StaticScoreGroup getUpperSectionBonus()    { return playerUpperSectionBonus; }
    public StaticScoreGroup getUpperSectionTotal()    { return playerUpperSectionTotal; }
    public StaticScoreGroup getLowerSectionYahtzeeBonus() { return playerLowerSectionYahtzeeBonus; }
    public StaticScoreGroup getGrandTotal()           { return playerGrandTotal; }
    public StaticScoreGroup getOpponentUpperSectionBonus()  { return opponentUpperSectionBonus; }
    public StaticScoreGroup getOpponentUpperSectionTotal()  { return opponentUpperSectionTotal; }
    public StaticScoreGroup getOpponentLowerSectionYahtzeeBonus() { return opponentLowerSectionYahtzeeBonus; }
    public StaticScoreGroup getOpponentGrandTotal()   { return opponentGrandTotal; }

    /**
     * Reset all score entries to initial state.
     */
    @Override
    public void reset() {
        for (ScoreGroup g : playerCategories)   g.reset();
        for (ScoreGroup g : opponentCategories) g.reset();
        playerUpperSectionBonus.reset();
        playerUpperSectionTotal.reset();
        playerLowerSectionYahtzeeBonus.reset();
        playerGrandTotal.reset();
        opponentUpperSectionBonus.reset();
        opponentUpperSectionTotal.reset();
        opponentLowerSectionYahtzeeBonus.reset();
        opponentGrandTotal.reset();
    }
}
