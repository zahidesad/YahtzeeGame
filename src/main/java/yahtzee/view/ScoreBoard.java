package yahtzee.view;

import yahtzee.model.Categories.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ScoreBoard extends JPanel implements Resettable {

    private ScoreGroup[] playerCategories;
    private ScoreGroup[] opponentCategories;
    private JPanel playerPanel;
    private JPanel opponentPanel;

    private StaticScoreGroup playerUpperSectionBonus, playerUpperSectionTotal, playerLowerSectionYahtzeeBonus, playerGrandTotal, opponentUpperSectionBonus, opponentUpperSectionTotal, opponentLowerSectionYahtzeeBonus, opponentGrandTotal;

    public ScoreBoard() {
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 20, 0)); // two columns with gap
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setPreferredSize(new Dimension(900, 650));

        playerCategories = new ScoreGroup[13];
        opponentCategories = new ScoreGroup[13];

        playerPanel = createColumnPanel("Senin Puanların");
        opponentPanel = createColumnPanel("Rakip Puanları");

        fillCategories();
        addCategories();

        add(playerPanel);
        add(opponentPanel);
    }

    /* ---------- helpers ---------- */
    private JPanel createColumnPanel(String title) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new TitledBorder(title));
        return p;
    }

    private void fillCategories() {
        for (int i = 0; i < 13; i++) {
            playerCategories[i] = new ScoreGroup(Category.getCategory(i + 1));
            opponentCategories[i] = new ScoreGroup(Category.getCategory(i + 1));
            opponentCategories[i].setEnabled(false);
        }
        playerUpperSectionBonus = new StaticScoreGroup("Upper Section Bonus");
        playerUpperSectionTotal = new StaticScoreGroup("Upper Total");
        playerLowerSectionYahtzeeBonus = new StaticScoreGroup("Yahtzee Bonus");
        playerGrandTotal = new StaticScoreGroup("Grand Total");

        opponentUpperSectionBonus = new StaticScoreGroup("Upper Section Bonus");
        opponentUpperSectionTotal = new StaticScoreGroup("Upper Total");
        opponentLowerSectionYahtzeeBonus = new StaticScoreGroup("Yahtzee Bonus");
        opponentGrandTotal = new StaticScoreGroup("Grand Total");
    }

    private void addCategories() {
        for (int i = 0; i < 13; i++) {
            addCategory(playerPanel, playerCategories[i]);
            addCategory(opponentPanel, opponentCategories[i]);
        }

        addCategory(playerPanel, playerUpperSectionBonus);
        addCategory(playerPanel, playerUpperSectionTotal);
        addCategory(playerPanel, playerLowerSectionYahtzeeBonus);
        addCategory(playerPanel, playerGrandTotal);

        addCategory(opponentPanel, opponentUpperSectionBonus);
        addCategory(opponentPanel, opponentUpperSectionTotal);
        addCategory(opponentPanel, opponentLowerSectionYahtzeeBonus);
        addCategory(opponentPanel, opponentGrandTotal);
    }

    private void addCategory(JPanel panel, ScoreGroup g) {
        panel.add(g);
        panel.add(Box.createVerticalStrut(6));
    }

    /* ---------- getters (unchanged) ---------- */
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

    /* ---------- reset ---------- */
    @Override
    public void reset() {
        for (ScoreGroup c : playerCategories) c.reset();
        for (ScoreGroup c : opponentCategories) c.reset();
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