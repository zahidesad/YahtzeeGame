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
    private JButton newGameButton;
    private GameController controller;

    public GamePanel(Game game, NetworkClient net, YahtzeeFrame frame) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        diceComponents = new YahtzeeDice[5];
        for (int i = 0; i < 5; i++) {
            diceComponents[i] = new YahtzeeDice(100);
            add(diceComponents[i]);
        }
        scoreBoard = new ScoreBoard();
        add(scoreBoard);
        rollDiceButton = new JButton("Zar At");
        rollDiceButton.setFont(new Font(rollDiceButton.getFont().getFontName(), Font.PLAIN, 20));
        add(rollDiceButton);
        newGameButton = new JButton("Yeni Oyun");
        add(newGameButton);
        controller = new GameController(game, frame, diceComponents, scoreBoard.getScoreGroups(),
                scoreBoard.getUpperSectionBonus(), scoreBoard.getUpperSectionTotal(),
                scoreBoard.getLowerSectionYahtzeeBonus(), scoreBoard.getGrandTotal(),
                rollDiceButton, newGameButton, net);
    }

    public GameController getController() {
        return controller;
    }

    public YahtzeeDice[] getDiceComponents() {
        return diceComponents;
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

    public JButton getNewGameButton() {
        return newGameButton;
    }
}