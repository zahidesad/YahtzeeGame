package yahtzee.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import yahtzee.YahtzeeFrame;
import yahtzee.model.Game;
import yahtzee.network.Message;
import yahtzee.network.MessageType;
import yahtzee.network.NetworkClient;
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
    private List<Integer> normalSelectable = java.util.Collections.emptyList();
    private List<Integer> overrideSelectable = java.util.Collections.emptyList();
    private int opponentGrandTotal = 0;
    private final NetworkClient net;
    private boolean myTurn = false;

    public GameController(Game game, YahtzeeFrame view, YahtzeeDice[] diceComponents, ScoreGroup[] scoreGroups,
                          StaticScoreGroup upperSectionBonus, StaticScoreGroup upperSectionTotal,
                          StaticScoreGroup lowerSectionYahtzeeBonus, StaticScoreGroup grandTotal,
                          JButton rollDiceButton, JButton newGameButton, NetworkClient net) {
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
        this.newGameButton = newGameButton;

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

        JsonObject payload = new JsonObject();
        payload.addProperty("count", game.getRollCount());
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

    public void setMyTurn(boolean t) {
        myTurn = t;
        rollDiceButton.setEnabled(t);
        for (ScoreGroup sg : scoreGroups) {
            sg.setEnabled(t);
        }
    }

    public void applyRemote(Message m) {
        Gson g = new Gson();
        switch (m.type()) {
            case ROLL -> {
            }
            case SELECT -> {
                JsonObject o = m.payload() instanceof JsonObject
                        ? (JsonObject) m.payload()
                        : g.fromJson(m.payload().toString(), JsonObject.class);
                opponentGrandTotal = o.get("grand").getAsInt();

                game.setRollCount(0);
                for (ScoreGroup sg : scoreGroups) sg.setCanBeSelected(false, false);
                setMyTurn(true);
                rollDiceButton.setEnabled(true);

            }
            default -> {
            }
        }
    }


}
