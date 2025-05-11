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
    private JButton concedeButton;
    private List<Integer> normalSelectable = java.util.Collections.emptyList();
    private List<Integer> overrideSelectable = java.util.Collections.emptyList();
    private int opponentGrandTotal = 0;
    private final NetworkClient net;
    private boolean myTurn = false;
    private JLabel timerLabel;
    private JLabel rollsLeftLabel;
    private Timer timer;
    private int timeLeft = 90;

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
        timer = new Timer(1000, e -> updateTimer());

        addDiceListeners();
        addScoreGroupListeners();
        rollDiceButton.addActionListener(e -> rollDice());
        concedeButton.addActionListener(e -> concedeTurn());
    }

    private void addDiceListeners() {
        for (int i = 0; i < 5; i++) {
            final int index = i;
            diceComponents[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!myTurn) return;
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
                    if (myTurn && scoreGroups[index].getCanBeSelected() && !scoreGroups[index].getChosen()) {
                        selectCategory(index);
                    }
                }
            });
        }
    }

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
        for (Dice d : game.getDice()) diceValues.add(d.getValue());
        payload.add("dice", diceValues);
        net.send(new Message(MessageType.ROLL, payload));

        if (game.getRollCount() == 3) rollDiceButton.setEnabled(false);
    }


    private void selectCategory(int index) {
        boolean useOverride = overrideSelectable.contains(index);

        game.selectCategory(index, useOverride);
        int scored = game.getScoreCard().getEntry(index).getScore();
        scoreGroups[index].setChosen(true);
        scoreGroups[index].setScore(scored);
        updateStaticScores();


        JsonObject p = new JsonObject();
        p.addProperty("category", index);
        p.addProperty("override", useOverride);
        p.addProperty("score", scored);
        p.addProperty("upper", game.getPlayer().getUpperScore());
        p.addProperty("upperBonus", game.getPlayer().getUpperBonus());
        p.addProperty("yahtzeeBonus", game.getPlayer().getYahtzeeBonus());
        p.addProperty("grand", game.getPlayer().getScore());
        p.addProperty("gameOver", game.isGameOver());
        net.send(new Message(MessageType.SELECT, p));


        setMyTurn(false);
        rollDiceButton.setEnabled(false);
        game.setRollCount(0);

        normalSelectable = game.getNormalSelectableCategories();
        overrideSelectable = game.getOverrideSelectableCategories();
        updateSelectableCategories();
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

    private void updateTimer() {
        if (myTurn && timeLeft > 0) {
            timeLeft--;
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;
            timerLabel.setText(String.format("MOVE TIME: %d:%02d", minutes, seconds));
            if (timeLeft == 0) {
                concedeTurn();
            }
        }
    }

    public void setMyTurn(boolean t) {
        myTurn = t;
        rollDiceButton.setEnabled(t);
        concedeButton.setEnabled(true);
        for (ScoreGroup sg : scoreGroups) {
            sg.setEnabled(t);
        }
        if (t) {
            timeLeft = 90;
            timer.start();
        } else {
            timer.stop();
            timerLabel.setText("MOVE TIME: 1:30");
        }
        rollsLeftLabel.setText("ROLLS LEFT: " + (3 - game.getRollCount()));
    }

    private void concedeTurn() {
        int response = JOptionPane.showConfirmDialog(view, "Oyunu terk etmek istediğinize emin misiniz? Bu durumda rakibiniz kazanır.", "Onay", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            JsonObject payload = new JsonObject();
            payload.addProperty("concede", true);
            net.send(new Message(MessageType.END, payload));
            view.cardLayout.show(view.mainPanel, "lobby");
        }
    }

    public void applyRemote(Message m) {
        Gson g = new Gson();
        switch (m.type()) {
            case ROLL -> {
                JsonObject o = g.fromJson(m.payload().toString(), JsonObject.class);
                int count = o.get("count").getAsInt();
                JsonArray diceValues = o.get("dice").getAsJsonArray();
                for (int i = 0; i < 5; i++) {
                    game.getDice()[i].setValue(diceValues.get(i).getAsInt());
                    game.getDice()[i].setHeld(false);
                }
                updateDiceDisplays();
            }
            case SELECT -> {
                JsonObject o = g.fromJson(m.payload().toString(), JsonObject.class);
                int category = o.has("category") ? o.get("category").getAsInt() : -1;
                int score = o.get("score").getAsInt();
                opponentGrandTotal = o.get("grand").getAsInt();
                if (category != -1) {
                    view.getOpponentScoreGroups()[category].setChosen(true);
                    view.getOpponentScoreGroups()[category].setScore(score);
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
