package yahtzee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import yahtzee.controller.GameController;
import yahtzee.model.Game;
import yahtzee.network.Message;
import yahtzee.network.NetworkClient;
import yahtzee.view.ScoreGroup;
import yahtzee.view.YahtzeeDice;
import yahtzee.view.ScoreBoard;
import yahtzee.view.StaticScoreGroup;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;

public class YahtzeeFrame extends JFrame {
    private GameController controller;
    private static final long serialVersionUID = 1L;
    private YahtzeeDice[] diceComponents;
    private ScoreBoard scoreBoard;
    private JButton rollDiceButton;
    private JButton newGameButton;

    public static void main(String[] args) throws IOException {
        YahtzeeFrame frame = new YahtzeeFrame();
        frame.setVisible(true);
        frame.setSize(815, 545);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    public YahtzeeFrame() throws IOException {
        super("Yahtzee Game");
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        Game game = new Game("Sebastian");
        String ip = JOptionPane.showInputDialog(this, "AWS IP:");
        String nick = JOptionPane.showInputDialog(this, "Nick:");
        NetworkClient net = new NetworkClient(ip, 55555, nick, this::handleNetwork);
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
        controller = new GameController(game, this, net);
    }

    private void handleNetwork(Message msg) {
        SwingUtilities.invokeLater(() -> {
            switch (msg.type()) {
                case MATCHED -> {
                    boolean iStart = new Gson().fromJson(msg.payload().toString(),
                            JsonObject.class).get("yourTurn").getAsBoolean();
                    controller.setMyTurn(iStart);
                    JOptionPane.showMessageDialog(this,
                            iStart ? "Eşleştin, sen başlıyorsun!" : "Eşleştin, rakibi bekle...");
                }
                case ROLL, SELECT, UPDATE -> controller.applyRemote(msg);
                case END -> {
                    JsonObject result = new Gson().fromJson(msg.payload().toString(), JsonObject.class);
                    String message = "Oyun bitti!\n" +
                            "Senin puanın: " + result.get("yourScore").getAsInt() + "\n" +
                            "Rakibin puanı: " + result.get("opponentScore").getAsInt() + "\n" +
                            "Kazanan: " + result.get("winner").getAsString();
                    JOptionPane.showMessageDialog(this, message);
                }
                default -> {
                }
            }

        });
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
