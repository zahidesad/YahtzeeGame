package yahtzee.view;

import yahtzee.YahtzeeFrame;
import yahtzee.model.Game;
import yahtzee.controller.GameController;
import yahtzee.network.NetworkClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel combining dice controls, scoreboard view, and action buttons for a game round.
 */
public class GamePanel extends JPanel {

    // UI components
    private final YahtzeeDice[] diceComponents;   // Array of dice widgets
    private final ScoreBoard scoreBoard;          // Score table UI
    private final JButton rollDiceButton;         // Button to roll dice
    private final JButton concedeButton;          // Button to concede game
    private final GameController controller;      // Handles game logic and events
    private final JLabel timerLabel;              // Displays remaining time
    private final JLabel rollsLeftLabel;          // Displays rolls left count

    /**
     * Construct panel with game model, network client, and parent frame.
     */
    public GamePanel(Game game, NetworkClient net, YahtzeeFrame frame) {
        // Root panel setup
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(12, 16, 12, 16));
        setOpaque(false);

        // Top bar: dice row and roll button
        JPanel topBar = new JPanel();
        topBar.setOpaque(false);
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));
        topBar.setBorder(new EmptyBorder(0, 0, 6, 0));

        diceComponents = new YahtzeeDice[5];
        for (int i = 0; i < 5; i++) {
            diceComponents[i] = new YahtzeeDice(90);  // Create each die widget
            topBar.add(diceComponents[i]);
            if (i < 4) topBar.add(Box.createHorizontalStrut(10));
        }

        topBar.add(Box.createHorizontalStrut(20));
        rollDiceButton = modernButton("Roll Dice");
        topBar.add(rollDiceButton);
        topBar.add(Box.createHorizontalGlue());
        add(topBar, BorderLayout.NORTH);

        // Center: scoreboard in scroll pane
        scoreBoard = new ScoreBoard();
        JScrollPane scoreScroll = new JScrollPane(
                scoreBoard,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scoreScroll.setBorder(null);
        scoreScroll.setPreferredSize(new Dimension(780, 420));
        add(scoreScroll, BorderLayout.CENTER);

        // Bottom bar: concede button, timer, and roll count
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

        // Initialize controller to wire up game logic and network events
        controller = new GameController(
                game, frame, diceComponents,
                scoreBoard.getScoreGroups(),
                scoreBoard.getUpperSectionBonus(),
                scoreBoard.getUpperSectionTotal(),
                scoreBoard.getLowerSectionYahtzeeBonus(),
                scoreBoard.getGrandTotal(),
                rollDiceButton, concedeButton,
                timerLabel, rollsLeftLabel, net
        );
    }

    /**
     * Create a styled button with modern appearance.
     */
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

    // Getter methods for external components
    public GameController getController() { return controller; }
    public YahtzeeDice[] getDiceComponents() { return diceComponents; }
    public ScoreGroup[] getOpponentScoreGroups() { return scoreBoard.getOpponentScoreGroups(); }
    public JButton getConcedeButton() { return concedeButton; }
    public ScoreGroup[] getScoreGroups() { return scoreBoard.getScoreGroups(); }
    public StaticScoreGroup getUpperSectionBonus() { return scoreBoard.getUpperSectionBonus(); }
    public StaticScoreGroup getUpperSectionTotal() { return scoreBoard.getUpperSectionTotal(); }
    public StaticScoreGroup getLowerSectionYahtzeeBonus() { return scoreBoard.getLowerSectionYahtzeeBonus(); }
    public StaticScoreGroup getGrandTotal() { return scoreBoard.getGrandTotal(); }
    public JButton getRollDiceButton() { return rollDiceButton; }
    public JLabel getTimerLabel() { return timerLabel; }
    public JLabel getRollsLeftLabel() { return rollsLeftLabel; }
    public StaticScoreGroup getOpponentUpperSectionTotal() { return scoreBoard.getOpponentUpperSectionTotal(); }
    public StaticScoreGroup getOpponentUpperSectionBonus() { return scoreBoard.getOpponentUpperSectionBonus(); }
    public StaticScoreGroup getOpponentLowerSectionYahtzeeBonus() { return scoreBoard.getOpponentLowerSectionYahtzeeBonus(); }
    public StaticScoreGroup getOpponentGrandTotal() { return scoreBoard.getOpponentGrandTotal(); }
}