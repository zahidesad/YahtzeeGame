package yahtzee.view;

import yahtzee.YahtzeeFrame;
import yahtzee.model.Game;
import yahtzee.controller.GameController;
import yahtzee.network.NetworkClient;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private YahtzeeDice[] diceComponents;
    private ScoreBoard scoreBoard;
    private JButton rollDiceButton;
    private JButton concedeButton;
    private GameController controller;
    private JLabel timerLabel;
    private JLabel rollsLeftLabel;

    public GamePanel(Game game, NetworkClient net, YahtzeeFrame frame) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        diceComponents = new YahtzeeDice[5];
        for (int i = 0; i < 5; i++) {
            diceComponents[i] = new YahtzeeDice(100);
            add(diceComponents[i]);
        }
        scoreBoard = new ScoreBoard();
        add(scoreBoard);
        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.setFont(new Font(rollDiceButton.getFont().getFontName(), Font.PLAIN, 20));
        add(rollDiceButton);
        concedeButton = new JButton("Concede");
        concedeButton.setFont(new Font(rollDiceButton.getFont().getFontName(), Font.PLAIN, 20));
        add(concedeButton);
        timerLabel = new JLabel("MOVE TIME: 1:30");
        rollsLeftLabel = new JLabel("ROLLS LEFT: 3");
        add(timerLabel);
        add(rollsLeftLabel);
        controller = new GameController(game, frame, diceComponents, scoreBoard.getScoreGroups(),
                scoreBoard.getUpperSectionBonus(), scoreBoard.getUpperSectionTotal(),
                scoreBoard.getLowerSectionYahtzeeBonus(), scoreBoard.getGrandTotal(),
                rollDiceButton, concedeButton, timerLabel, rollsLeftLabel, net);
    }

    public GameController getController() {
        return controller;
    }

    public YahtzeeDice[] getDiceComponents() {
        return diceComponents;
    }

    public ScoreGroup[] getOpponentScoreGroups() {
        return scoreBoard.getOpponentScoreGroups();
    }

    public JButton getConcedeButton() {
        return concedeButton;
    }

    public ScoreGroup[] getScoreGroups() {
        return scoreBoard.getScoreGroups();
    }

    public StaticScoreGroup getUpperSectionBonus() {
        return scoreBoard.getUpperSectionBonus();
    }

    public StaticScoreGroup getUpperSectionTotal() {
        return scoreBoard.getUpperSectionTotal();
    }

    public StaticScoreGroup getLowerSectionYahtzeeBonus() {
        return scoreBoard.getLowerSectionYahtzeeBonus();
    }

    public StaticScoreGroup getGrandTotal() {
        return scoreBoard.getGrandTotal();
    }

    public JButton getRollDiceButton() {
        return rollDiceButton;
    }

    public JLabel getTimerLabel() {
        return timerLabel;
    }

    public JLabel getRollsLeftLabel() {
        return rollsLeftLabel;
    }

    public StaticScoreGroup getOpponentUpperSectionTotal() {
        return scoreBoard.getOpponentUpperSectionTotal();
    }

    public StaticScoreGroup getOpponentUpperSectionBonus() {
        return scoreBoard.getOpponentUpperSectionBonus();
    }

    public StaticScoreGroup getOpponentLowerSectionYahtzeeBonus() {
        return scoreBoard.getOpponentLowerSectionYahtzeeBonus();
    }


    public StaticScoreGroup getOpponentGrandTotal() {
        return scoreBoard.getOpponentGrandTotal();
    }
}