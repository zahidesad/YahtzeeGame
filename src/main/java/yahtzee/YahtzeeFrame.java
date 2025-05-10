package yahtzee;

import yahtzee.controller.GameController;
import yahtzee.model.Game;
import yahtzee.view.ScoreGroup;
import yahtzee.view.YahtzeeDice;
import yahtzee.view.ScoreBoard;
import yahtzee.view.StaticScoreGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.awt.Font;

public class YahtzeeFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private YahtzeeDice[] diceComponents;
    private ScoreBoard scoreBoard;
    private JButton rollDiceButton;
    private JButton newGameButton;

    public static void main(String[] args) {
        YahtzeeFrame frame = new YahtzeeFrame();
        frame.setVisible(true);
        frame.setSize(815, 545);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    public YahtzeeFrame() {
        super("Yahtzee Game");
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        Game game = new Game("Sebastian");
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
        newGameButton = new JButton("New Game");
        add(newGameButton);
        new GameController(game, this);
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
