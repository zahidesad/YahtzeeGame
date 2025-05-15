package yahtzee.view;

import yahtzee.model.Categories.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Displays player and opponent score categories and totals side by side.
 */
public class ScoreBoard extends JPanel implements Resettable {

    private ScoreGroup[] playerCategories;    // UI groups for player's scoring categories
    private ScoreGroup[] opponentCategories;  // UI groups for opponent's scoring categories
    private JPanel playerPanel;               // Container for player's column
    private JPanel opponentPanel;             // Container for opponent's column

    // Static score displays for various totals and bonuses
    private StaticScoreGroup playerUpperSectionBonus, playerUpperSectionTotal,
            playerLowerSectionYahtzeeBonus, playerGrandTotal,
            opponentUpperSectionBonus, opponentUpperSectionTotal,
            opponentLowerSectionYahtzeeBonus, opponentGrandTotal;

    /**
     * Initialize scoreboard layout, categories, and static score fields.
     */
    public ScoreBoard() {
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 20, 0));   // Two columns with horizontal gap
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setPreferredSize(new Dimension(900, 650));

        playerCategories = new ScoreGroup[13];
        opponentCategories = new ScoreGroup[13];

        // Create titled panels for player/opponent
        playerPanel = createColumnPanel("Your Scores");
        opponentPanel = createColumnPanel("Opponent Scores");

        // Instantiate category groups and static totals
        fillCategories();
        addCategories();

        // Add columns to main panel
        add(playerPanel);
        add(opponentPanel);
    }

    /**
     * Create a vertical panel with a title border.
     */
    private JPanel createColumnPanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(title));
        return panel;
    }

    /**
     * Instantiate ScoreGroup for each category and static score components.
     */
    private void fillCategories() {
        for (int i = 0; i < 13; i++) {
            playerCategories[i] = new ScoreGroup(Category.getCategory(i + 1));
            opponentCategories[i] = new ScoreGroup(Category.getCategory(i + 1));
            opponentCategories[i].setEnabled(false); // Opponent entries read-only
        }
        // Player static totals
        playerUpperSectionBonus = new StaticScoreGroup("Upper Section Bonus");
        playerUpperSectionTotal = new StaticScoreGroup("Upper Total");
        playerLowerSectionYahtzeeBonus = new StaticScoreGroup("Yahtzee Bonus");
        playerGrandTotal = new StaticScoreGroup("Grand Total");
        // Opponent static totals
        opponentUpperSectionBonus = new StaticScoreGroup("Upper Section Bonus");
        opponentUpperSectionTotal = new StaticScoreGroup("Upper Total");
        opponentLowerSectionYahtzeeBonus = new StaticScoreGroup("Yahtzee Bonus");
        opponentGrandTotal = new StaticScoreGroup("Grand Total");
    }

    /**
     * Add category and static score components to their respective panels.
     */
    private void addCategories() {
        // Player and opponent category rows
        for (int i = 0; i < 13; i++) {
            addCategory(playerPanel, playerCategories[i]);
            addCategory(opponentPanel, opponentCategories[i]);
        }
        // Player static score rows
        addCategory(playerPanel, playerUpperSectionBonus);
        addCategory(playerPanel, playerUpperSectionTotal);
        addCategory(playerPanel, playerLowerSectionYahtzeeBonus);
        addCategory(playerPanel, playerGrandTotal);
        // Opponent static score rows
        addCategory(opponentPanel, opponentUpperSectionBonus);
        addCategory(opponentPanel, opponentUpperSectionTotal);
        addCategory(opponentPanel, opponentLowerSectionYahtzeeBonus);
        addCategory(opponentPanel, opponentGrandTotal);
    }

    /**
     * Helper to add a component and spacing to a panel.
     */
    private void addCategory(JPanel panel, ScoreGroup group) {
        panel.add(group);
        panel.add(Box.createVerticalStrut(6)); // Space between entries
    }

    /* ---------- Getters for external access ---------- */
    public ScoreGroup[] getScoreGroups() {
        return playerCategories;
    }
    public ScoreGroup[] getOpponentScoreGroups() {
        return opponentCategories;
    }
    public StaticScoreGroup getUpperSectionBonus() {
        return playerUpperSectionBonus;
    }
    public StaticScoreGroup getUpperSectionTotal() {
        return playerUpperSectionTotal;
    }
    public StaticScoreGroup getLowerSectionYahtzeeBonus() {
        return playerLowerSectionYahtzeeBonus;
    }
    public StaticScoreGroup getGrandTotal() {
        return playerGrandTotal;
    }
    public StaticScoreGroup getOpponentUpperSectionBonus() {
        return opponentUpperSectionBonus;
    }
    public StaticScoreGroup getOpponentUpperSectionTotal() {
        return opponentUpperSectionTotal;
    }
    public StaticScoreGroup getOpponentLowerSectionYahtzeeBonus() {
        return opponentLowerSectionYahtzeeBonus;
    }
    public StaticScoreGroup getOpponentGrandTotal() {
        return opponentGrandTotal;
    }

    /**
     * Reset all category and static score displays to initial state.
     */
    @Override
    public void reset() {
        for (ScoreGroup g : playerCategories) g.reset();
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