package yahtzee.controller;

import yahtzee.YahtzeeFrame;
import yahtzee.model.Game;
import yahtzee.view.YahtzeeDice;
import yahtzee.view.ScoreGroup;
import yahtzee.view.StaticScoreGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class GameController {
    private Game game;
    private YahtzeeFrame view;
    private YahtzeeDice[] diceComponents;
    private ScoreGroup[] scoreGroups;
    private StaticScoreGroup upperSectionBonus;
    private StaticScoreGroup upperSectionTotal;
    private StaticScoreGroup lowerSectionYahtzeeBonus;
    private StaticScoreGroup grandTotal;
    private JButton rollDiceButton;
    private JButton newGameButton;
    private List<Integer> normalSelectable;
    private List<Integer> overrideSelectable;

    public GameController(Game game, YahtzeeFrame view) {
        this.game = game;
        this.view = view;
        diceComponents = view.getDiceComponents();
        scoreGroups = view.getScoreGroups();
        upperSectionBonus = view.getUpperSectionBonus();
        upperSectionTotal = view.getUpperSectionTotal();
        lowerSectionYahtzeeBonus = view.getLowerSectionYahtzeeBonus();
        grandTotal = view.getGrandTotal();
        rollDiceButton = view.getRollDiceButton();
        newGameButton = view.getNewGameButton();

        addDiceListeners();
        addScoreGroupListeners();
        rollDiceButton.addActionListener(e -> rollDice());
        newGameButton.addActionListener(e -> newGame());
    }

    private void addDiceListeners() {
        for (int i = 0; i < 5; i++) {
            final int index = i;
            diceComponents[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    game.getDice()[index].setHeld(!game.getDice()[index].isHeld());
                    diceComponents[index].setHoldState(game.getDice()[index].isHeld());
                }
            });
        }
    }

    private void addScoreGroupListeners() {
        for (int i = 0; i < 13; i++) {
            final int index = i;
            scoreGroups[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (scoreGroups[index].getCanBeSelected() && !scoreGroups[index].getChosen()) {
                        boolean useOverride = overrideSelectable.contains(index);
                        int score = game.getPossibleScore(index, useOverride);
                        scoreGroups[index].setText(scoreGroups[index].getCategoryName() + " (" + score + ")");
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!scoreGroups[index].getChosen()) {
                        scoreGroups[index].setTextToCategory();
                    }
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    if (scoreGroups[index].getCanBeSelected() && !scoreGroups[index].getChosen()) {
                        selectCategory(index);
                    }
                }
            });
        }
    }

    private void rollDice() {
        game.rollDice();
        updateDiceDisplays();
        normalSelectable = game.getNormalSelectableCategories();
        overrideSelectable = game.getOverrideSelectableCategories();
        updateSelectableCategories();
        updateStaticScores();
        if (game.getRollCount() == 3) {
            rollDiceButton.setEnabled(false);
        }
    }

    private void selectCategory(int index) {
        boolean useOverride = overrideSelectable.contains(index);
        game.selectCategory(index, useOverride);
        scoreGroups[index].setChosen(true);
        scoreGroups[index].setScore(game.getScoreCard().getEntry(index).getScore());
        updateStaticScores();
        if (game.isGameOver()) {
            showFinalScorePrompt();
        } else {
            for (ScoreGroup sg : scoreGroups) {
                sg.setCanBeSelected(false, false);
            }
            rollDiceButton.setEnabled(true);
            game.setRollCount(0);
        }
    }

    private void newGame() {
        game.reset();
        for (YahtzeeDice d : diceComponents) {
            d.reset();
        }
        for (ScoreGroup sg : scoreGroups) {
            sg.reset();
        }
        upperSectionBonus.reset();
        upperSectionTotal.reset();
        lowerSectionYahtzeeBonus.reset();
        grandTotal.reset();
        rollDiceButton.setEnabled(true);
    }

    private void updateDiceDisplays() {
        for (int i = 0; i < 5; i++) {
            diceComponents[i].setValue(game.getDice()[i].getValue());
            diceComponents[i].setHoldState(game.getDice()[i].isHeld());
        }
    }

    private void updateSelectableCategories() {
        for (int i = 0; i < 13; i++) {
            if (normalSelectable.contains(i)) {
                scoreGroups[i].setCanBeSelected(true, false);
            } else if (overrideSelectable.contains(i)) {
                scoreGroups[i].setCanBeSelected(true, true);
            } else {
                scoreGroups[i].setCanBeSelected(false, false);
            }
        }
    }

    private void updateStaticScores() {
        upperSectionTotal.setScore(game.getPlayer().getUpperScore());
        upperSectionBonus.setScore(game.getPlayer().getUpperBonus());
        lowerSectionYahtzeeBonus.setScore(game.getPlayer().getYahtzeeBonus());
        grandTotal.setScore(game.getPlayer().getScore());
    }

    private void showFinalScorePrompt() {
        if (JOptionPane.showOptionDialog(view, "Your final score is " + game.getPlayer().getScore() + ". Would you like to start a new game?", "Game End", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == 0) {
            newGame();
        }
    }
}
