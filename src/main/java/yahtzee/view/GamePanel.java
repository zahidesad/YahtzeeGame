package yahtzee.view;

import yahtzee.YahtzeeFrame;
import yahtzee.model.Game;
import yahtzee.controller.GameController;
import yahtzee.network.NetworkClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel combining dice widgets, action buttons, and the redesigned ScoreBoard.
 * The dice area is now vertically centered so the left side never looks empty.
 */
public class GamePanel extends JPanel {

    private final YahtzeeDice[] diceComponents;
    private final ScoreBoard scoreBoard;
    private final JButton rollDiceButton;
    private final JButton concedeButton;
    private final JLabel timerLabel;
    private final JLabel rollsLeftLabel;
    private final GameController controller;

    public GamePanel(Game game, NetworkClient net, YahtzeeFrame frame) {

        /* ---------- overall layout ---------- */
        setLayout(new BorderLayout(24, 0));        // gap between play-field & scoreboard
        setBorder(new EmptyBorder(12, 16, 12, 16));
        setOpaque(false);

        /* ---------- left / centre section ---------- */
        JPanel playField = new JPanel(new BorderLayout());
        playField.setOpaque(false);

        /* vertical box that will be CENTERed, so it sits mid-screen */
        JPanel vbox = new JPanel();
        vbox.setOpaque(false);
        vbox.setLayout(new BoxLayout(vbox, BoxLayout.Y_AXIS));

        /* row of dice, horizontally centred */
        JPanel diceRow = new JPanel();
        diceRow.setOpaque(false);
        diceRow.setLayout(new BoxLayout(diceRow, BoxLayout.X_AXIS));

        diceComponents = new YahtzeeDice[5];
        for (int i = 0; i < 5; i++) {
            diceComponents[i] = new YahtzeeDice(90);
            diceRow.add(diceComponents[i]);
            if (i < 4) diceRow.add(Box.createHorizontalStrut(10));
        }

        /* action buttons row */
        rollDiceButton = modernButton("Roll Dice");
        concedeButton  = modernButton("Concede");

        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.setLayout(new BoxLayout(btnRow, BoxLayout.X_AXIS));
        btnRow.add(rollDiceButton);
        btnRow.add(Box.createHorizontalStrut(14));
        btnRow.add(concedeButton);

        /* info row (small timer + rolls-left labels) */
        timerLabel     = new JLabel("MOVE TIME: 1:30");
        rollsLeftLabel = new JLabel("ROLLS LEFT: 3");
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.PLAIN, 13f));
        rollsLeftLabel.setFont(rollsLeftLabel.getFont().deriveFont(Font.PLAIN, 13f));

        JPanel infoRow = new JPanel();
        infoRow.setOpaque(false);
        infoRow.setLayout(new BoxLayout(infoRow, BoxLayout.X_AXIS));
        infoRow.add(timerLabel);
        infoRow.add(Box.createHorizontalStrut(18));
        infoRow.add(rollsLeftLabel);

        /* assemble the centred vertical box */
        vbox.add(Box.createVerticalGlue());
        vbox.add(diceRow);
        vbox.add(Box.createVerticalStrut(20));
        vbox.add(btnRow);
        vbox.add(Box.createVerticalStrut(12));
        vbox.add(infoRow);
        vbox.add(Box.createVerticalGlue());

        playField.add(vbox, BorderLayout.CENTER);
        add(playField, BorderLayout.CENTER);

        /* ---------- right-hand ScoreBoard ---------- */
        scoreBoard = new ScoreBoard();
        JScrollPane scoreScroll = new JScrollPane(scoreBoard,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scoreScroll.setBorder(null);
        scoreScroll.getViewport().setBackground(getBackground());
        add(scoreScroll, BorderLayout.EAST);

        /* ---------- hook up controller ---------- */
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

    /* modern flat button helper */
    private JButton modernButton(String text) {
        JButton b = new JButton(text);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        b.setBackground(new Color(0x007BFF));
        b.setForeground(Color.WHITE);
        return b;
    }

    /* getters for external use (unchanged) */
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
