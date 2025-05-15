package yahtzee.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import yahtzee.YahtzeeFrame;
import yahtzee.model.Dice;
import yahtzee.model.Game;
import yahtzee.network.Message;
import yahtzee.network.MessageType;
import yahtzee.network.NetworkClient;
import yahtzee.view.YahtzeeDice;
import yahtzee.view.ScoreGroup;
import yahtzee.view.StaticScoreGroup;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Coordinates game model, view updates, and network communication.
 */
public class GameController {
    private Game game;                              // Core game logic
    private YahtzeeFrame view;                      // Main UI frame
    private YahtzeeDice[] diceComponents;           // UI dice controls
    private ScoreGroup[] scoreGroups;               // UI score categories
    private StaticScoreGroup upperSectionBonus;     // UI upper section bonus
    private StaticScoreGroup upperSectionTotal;     // UI upper section total
    private StaticScoreGroup lowerSectionYahtzeeBonus; // UI Yahtzee bonus
    private StaticScoreGroup grandTotal;            // UI grand total display
    private JButton rollDiceButton;                 // Button to roll dice
    private JButton concedeButton;                  // Button to concede game
    private List<Integer> normalSelectable = java.util.Collections.emptyList();   // Categories selectable normally
    private List<Integer> overrideSelectable = java.util.Collections.emptyList(); // Categories selectable as override
    private int opponentGrandTotal = 0;             // Tracks opponent's total
    private final NetworkClient net;                // Handles network messages
    private boolean myTurn = false;                 // Flag for player's turn
    private JLabel timerLabel;                      // UI timer display
    private JLabel rollsLeftLabel;                  // UI rolls left display
    private Timer timer;                            // Turn timer
    private int timeLeft = 90;                      // Seconds per turn

    /**
     * Initialize controller with model, view, and UI components.
     */
    public GameController(Game game, YahtzeeFrame view, YahtzeeDice[] diceComponents, ScoreGroup[] scoreGroups,
                          StaticScoreGroup upperSectionBonus, StaticScoreGroup upperSectionTotal,
                          StaticScoreGroup lowerSectionYahtzeeBonus, StaticScoreGroup grandTotal,
                          JButton rollDiceButton, JButton concedeButton,
                          JLabel timerLabel, JLabel rollsLeftLabel, NetworkClient net) {
        this.game = game;
        this.view = view;
        this.net = net;
        this.diceComponents = diceComponents;
        this.scoreGroups = scoreGroups;
        this.upperSectionBonus = upperSectionBonus;
        this.upperSectionTotal = upperSectionTotal;
        this.lowerSectionYahtzeeBonus = lowerSectionYahtzeeBonus;
        this.grandTotal = grandTotal;
        this.rollDiceButton = rollDiceButton;
        this.concedeButton = concedeButton;
        this.timerLabel = timerLabel;
        this.rollsLeftLabel = rollsLeftLabel;
        timer = new Timer(1000, e -> updateTimer());  // Update timer every second

        addDiceListeners();        // Enable holding dice
        addScoreGroupListeners();  // Enable selecting score categories
        rollDiceButton.addActionListener(e -> rollDice());     // Roll action
        concedeButton.addActionListener(e -> concedeTurn());    // Concede action
    }

    /**
     * Toggle hold state when dice clicked during player's turn.
     */
    private void addDiceListeners() {
        for (int i = 0; i < 5; i++) {
            final int index = i;
            diceComponents[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!myTurn) return;
                    boolean held = game.getDice()[index].isHeld();
                    game.getDice()[index].setHeld(!held);
                    diceComponents[index].setHoldState(!held);
                }
            });
        }
    }

    /**
     * Show possible scores on hover and select category on click.
     */
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
                    if (myTurn && scoreGroups[index].getCanBeSelected() && !scoreGroups[index].getChosen()) {
                        selectCategory(index);
                    }
                }
            });
        }
    }

    /**
     * Handle dice roll: update model, view, and notify opponent.
     */
    private void rollDice() {
        if (!myTurn) return;
        game.rollDice();
        updateDiceDisplays();
        normalSelectable = game.getNormalSelectableCategories();
        overrideSelectable = game.getOverrideSelectableCategories();
        updateSelectableCategories();
        updateStaticScores();
        rollsLeftLabel.setText("ROLLS LEFT: " + (3 - game.getRollCount()));

        JsonObject payload = new JsonObject();
        payload.addProperty("count", game.getRollCount());

        JsonArray diceValues = new JsonArray();
        for (Dice d : game.getDice()) {
            diceValues.add(d.getValue());
        }
        payload.add("dice", diceValues);
        JsonArray values = new JsonArray();
        for (Dice d : game.getDice()) values.add(d.getValue());
        net.send(new Message(MessageType.ROLL, payload));

        if (game.getRollCount() == 3) rollDiceButton.setEnabled(false);
    }

    /**
     * Handle category selection: update model, view, and notify opponent.
     */
    private void selectCategory(int index) {
        boolean useOverride = overrideSelectable.contains(index);
        game.selectCategory(index, useOverride);
        int scored = game.getScoreCard().getEntry(index).getScore();
        scoreGroups[index].setChosen(true);
        scoreGroups[index].setScore(scored);
        updateStaticScores();

        JsonObject payload = new JsonObject();
        payload.addProperty("category", index);
        payload.addProperty("override", useOverride);
        payload.addProperty("score", scored);
        payload.addProperty("upper", game.getPlayer().getUpperScore());
        payload.addProperty("upperBonus", game.getPlayer().getUpperBonus());
        payload.addProperty("yahtzeeBonus", game.getPlayer().getYahtzeeBonus());
        payload.addProperty("grand", game.getPlayer().getScore());
        payload.addProperty("gameOver", game.isGameOver());
        net.send(new Message(MessageType.SELECT, payload));

        setMyTurn(false);
        rollDiceButton.setEnabled(false);
        game.setRollCount(0);
        normalSelectable = game.getNormalSelectableCategories();
        overrideSelectable = game.getOverrideSelectableCategories();
        updateSelectableCategories();
    }

    /**
     * Refresh dice values and hold states in UI.
     */
    private void updateDiceDisplays() {
        for (int i = 0; i < 5; i++) {
            diceComponents[i].setValue(game.getDice()[i].getValue());
            diceComponents[i].setHoldState(game.getDice()[i].isHeld());
        }
    }

    /**
     * Enable/disable score categories based on selectability.
     */
    private void updateSelectableCategories() {
        for (int i = 0; i < 13; i++) {
            boolean normal = normalSelectable.contains(i);
            boolean override = overrideSelectable.contains(i);
            scoreGroups[i].setCanBeSelected(normal || override, override);
        }
    }

    /**
     * Update static score displays (upper, bonus, Yahtzee bonus, grand total).
     */
    private void updateStaticScores() {
        upperSectionTotal.setScore(game.getPlayer().getUpperScore());
        upperSectionBonus.setScore(game.getPlayer().getUpperBonus());
        lowerSectionYahtzeeBonus.setScore(game.getPlayer().getYahtzeeBonus());
        grandTotal.setScore(game.getPlayer().getScore());
    }

    /**
     * Countdown timer update; auto-concede when time expires.
     */
    private void updateTimer() {
        if (myTurn && timeLeft > 0) {
            timeLeft--;
            int mins = timeLeft / 60;
            int secs = timeLeft % 60;
            timerLabel.setText(String.format("MOVE TIME: %d:%02d", mins, secs));
            if (timeLeft == 0) concedeTurn();
        }
    }

    /**
     * Set turn state: enable or disable controls and timer.
     */
    public void setMyTurn(boolean t) {
        myTurn = t;
        rollDiceButton.setEnabled(t);
        concedeButton.setEnabled(true);
        for (ScoreGroup sg : scoreGroups) sg.setEnabled(t);
        if (t) {
            timeLeft = 90;
            timer.start();
        } else {
            timer.stop();
            timerLabel.setText("MOVE TIME: 1:30");
        }
        rollsLeftLabel.setText("ROLLS LEFT: " + (3 - game.getRollCount()));
    }

    /**
     * Prompt user to confirm concession and notify opponent.
     */
    private void concedeTurn() {
        int resp = JOptionPane.showConfirmDialog(
                view, "Are you sure you want to concede? Opponent will win.",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            concedeButton.setEnabled(false);
            JsonObject payload = new JsonObject();
            payload.addProperty("concede", true);
            net.send(new Message(MessageType.END, payload));
            view.setInGame(false);
            view.setController(null);
            try { if (view.getNet() != null) view.getNet().close(); } catch (Exception ignore) {}
            view.cardLayout.show(view.mainPanel, "lobby");
        }
    }

    /**
     * Apply opponent's actions received over network.
     */
    public void applyRemote(Message m) {
        Gson g = new Gson();
        switch (m.type()) {
            case ROLL -> {
                JsonObject o = g.fromJson(m.payload().toString(), JsonObject.class);
                JsonArray values = o.getAsJsonArray("dice");
                if (values == null || values.size() != 5) return;
                for (int i = 0; i < 5; i++) {
                    Dice d = game.getDice()[i];
                    d.setValue(values.get(i).getAsInt());
                    d.setHeld(false);
                }
                updateDiceDisplays();
            }
            case SELECT -> {
                JsonObject o = g.fromJson(m.payload().toString(), JsonObject.class);
                int cat = o.has("category") ? o.get("category").getAsInt() : -1;
                opponentGrandTotal = o.get("grand").getAsInt();
                if (cat != -1) {
                    view.getOpponentScoreGroups()[cat].setChosen(true);
                    view.getOpponentScoreGroups()[cat].setScore(o.get("score").getAsInt());
                    view.getOpponentUpperSectionTotal().setScore(o.get("upper").getAsInt());
                    view.getOpponentUpperSectionBonus().setScore(o.get("upperBonus").getAsInt());
                    view.getOpponentLowerSectionYahtzeeBonus().setScore(o.get("yahtzeeBonus").getAsInt());
                    view.getOpponentGrandTotal().setScore(opponentGrandTotal);
                }
                game.setRollCount(0);
                for (ScoreGroup sg : scoreGroups) sg.setCanBeSelected(false, false);
                setMyTurn(true);
            }
        }
    }
}