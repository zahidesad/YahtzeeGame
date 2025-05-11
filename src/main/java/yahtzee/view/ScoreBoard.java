package yahtzee.view;


import yahtzee.model.Categories.Category;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

public class ScoreBoard extends JPanel implements Resettable {
    private ScoreGroup[] playerCategories; // Kendi puanlarım
    private ScoreGroup[] opponentCategories; // Rakibin puanları
    private JPanel playerPanel;
    private JPanel opponentPanel;
    private StaticScoreGroup playerUpperSectionBonus, playerUpperSectionTotal;
    private StaticScoreGroup playerLowerSectionYahtzeeBonus, playerGrandTotal;
    private StaticScoreGroup opponentUpperSectionBonus, opponentUpperSectionTotal;
    private StaticScoreGroup opponentLowerSectionYahtzeeBonus, opponentGrandTotal;

    public ScoreBoard() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        this.setPreferredSize(new Dimension(750, 500));

        playerCategories = new ScoreGroup[13];
        opponentCategories = new ScoreGroup[13];

        playerPanel = new JPanel();
        playerPanel.setPreferredSize(new Dimension(350, 500));
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

        opponentPanel = new JPanel();
        opponentPanel.setPreferredSize(new Dimension(350, 500));
        opponentPanel.setLayout(new BoxLayout(opponentPanel, BoxLayout.Y_AXIS));

        fillCategories();
        addCategories();
    }

    private void fillCategories() {
        for (int i = 0; i < 13; i++) {
            playerCategories[i] = new ScoreGroup(Category.getCategory(i + 1));
            opponentCategories[i] = new ScoreGroup(Category.getCategory(i + 1));
            opponentCategories[i].setEnabled(false); // Rakip puanları tıklanamaz
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

        this.add(playerPanel);
        this.add(opponentPanel);
    }

    private void addCategory(JPanel panel, ScoreGroup group) {
        panel.add(group);
        panel.add(Box.createVerticalStrut(7));
    }

    public ScoreGroup[] getScoreGroups() {return playerCategories;}
    public ScoreGroup[] getOpponentScoreGroups() {return opponentCategories;}

    public StaticScoreGroup getUpperSectionBonus() { return playerUpperSectionBonus; }
    public StaticScoreGroup getUpperSectionTotal() { return playerUpperSectionTotal; }
    public StaticScoreGroup getLowerSectionYahtzeeBonus() { return playerLowerSectionYahtzeeBonus; }
    public StaticScoreGroup getGrandTotal() { return playerGrandTotal; }

    public StaticScoreGroup getOpponentUpperSectionBonus() { return opponentUpperSectionBonus; }
    public StaticScoreGroup getOpponentUpperSectionTotal() { return opponentUpperSectionTotal; }
    public StaticScoreGroup getOpponentLowerSectionYahtzeeBonus() { return opponentLowerSectionYahtzeeBonus; }
    public StaticScoreGroup getOpponentGrandTotal() { return opponentGrandTotal; }

    @Override
    public void reset() {
        for (ScoreGroup category : playerCategories) category.reset();
        for (ScoreGroup category : opponentCategories) category.reset();
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