package yahtzee.view;

import yahtzee.YahtzeeFrame;
import yahtzee.model.Game;
import yahtzee.controller.GameController;
import yahtzee.network.NetworkClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GamePanel extends JPanel {

    /* ---------- original fields ---------- */
    private final YahtzeeDice[] diceComponents;
    private final ScoreBoard scoreBoard;
    private final JButton rollDiceButton;
    private final JButton concedeButton;
    private final GameController controller;
    private final JLabel timerLabel;
    private final JLabel rollsLeftLabel;

    public GamePanel(Game game, NetworkClient net, YahtzeeFrame frame) {
        /* ---- root ---- */
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(12, 16, 12, 16));
        setOpaque(false);

        /* =========================================================
           TOP: dice row + Roll button (BoxLayout X)
         ========================================================= */
        JPanel topBar = new JPanel();
        topBar.setOpaque(false);
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));
        topBar.setBorder(new EmptyBorder(0, 0, 6, 0));

        diceComponents = new YahtzeeDice[5];
        for (int i = 0; i < 5; i++) {
            diceComponents[i] = new YahtzeeDice(90); // slightly smaller die so overall panel shrinks
            topBar.add(diceComponents[i]);
            if (i < 4) topBar.add(Box.createHorizontalStrut(10));
        }

        topBar.add(Box.createHorizontalStrut(20));

        rollDiceButton = modernButton("Roll Dice");
        topBar.add(rollDiceButton);
        topBar.add(Box.createHorizontalGlue());

        add(topBar, BorderLayout.NORTH);

        /* =========================================================
           CENTER: scoreboard inside a scroll pane (fixed height)
         ========================================================= */
        scoreBoard = new ScoreBoard();
        JScrollPane scoreScroll = new JScrollPane(scoreBoard,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scoreScroll.setBorder(null);
        scoreScroll.setPreferredSize(new Dimension(780, 420)); // trims vertical footprint
        add(scoreScroll, BorderLayout.CENTER);

        /* =========================================================
           BOTTOM: concede + timer info
         ========================================================= */
        JPanel bottomBar = new JPanel();
        bottomBar.setOpaque(false);
        bottomBar.setLayout(new BoxLayout(bottomBar, BoxLayout.X_AXIS));
        bottomBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        concedeButton = modernButton("Concede");

        timerLabel = new JLabel("MOVE TIME: 1:30");
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.PLAIN, 13f));
        rollsLeftLabel = new JLabel("ROLLS LEFT: 3");
        rollsLeftLabel.setFont(rollsLeftLabel.getFont().deriveFont(Font.PLAIN, 13f));

        bottomBar.add(concedeButton);
        bottomBar.add(Box.createHorizontalGlue());
        bottomBar.add(timerLabel);
        bottomBar.add(Box.createHorizontalStrut(18));
        bottomBar.add(rollsLeftLabel);

        add(bottomBar, BorderLayout.SOUTH);

        /* ---- controller ---- */
        controller = new GameController(
                game, frame, diceComponents,
                scoreBoard.getScoreGroups(),
                scoreBoard.getUpperSectionBonus(),
                scoreBoard.getUpperSectionTotal(),
                scoreBoard.getLowerSectionYahtzeeBonus(),
                scoreBoard.getGrandTotal(),
                rollDiceButton, concedeButton,
                timerLabel, rollsLeftLabel, net);
    }

    /* -------- helper -------- */
    private JButton modernButton(String text) {
        JButton b = new JButton(text);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        b.setBackground(new Color(0x007BFF));
        b.setForeground(Color.WHITE);
        return b;
    }

    /* ---------- getters (unchanged) ---------- */
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
