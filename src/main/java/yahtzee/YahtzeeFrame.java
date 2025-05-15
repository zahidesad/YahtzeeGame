package yahtzee;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yahtzee.controller.GameController;
import yahtzee.model.Game;
import yahtzee.network.Message;
import yahtzee.network.NetworkClient;
import yahtzee.view.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class YahtzeeFrame extends JFrame {
    public CardLayout cardLayout;
    public JPanel mainPanel;
    private LobbyPanel lobbyPanel;
    private GamePanel gamePanel;
    private Game game;
    private NetworkClient net;
    private GameController controller;


    public YahtzeeFrame() {
        super("Yahtzee Oyunu");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        lobbyPanel = new LobbyPanel();
        mainPanel.add(lobbyPanel, "lobby");
        add(mainPanel);
        cardLayout.show(mainPanel, "lobby");

        lobbyPanel.addFindGameListener(e -> {
            String ip = lobbyPanel.getIp();
            String nick = lobbyPanel.getNick();
            if (ip.isEmpty() || nick.isEmpty()) {
                lobbyPanel.setStatus("Lütfen IP ve takma ad girin.");
                return;
            }
            try {
                net = new NetworkClient(ip, 12345, nick, this::handleNetwork);
                lobbyPanel.setStatus("Başka bir oyuncu bekleniyor...");
            } catch (IOException ex) {
                lobbyPanel.setStatus("Bağlantı hatası: " + ex.getMessage());
            }
        });

        setSize(950, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleNetwork(Message msg) {
        SwingUtilities.invokeLater(() -> {
            switch (msg.type()) {
                case MATCHED -> {
                    boolean iStart = new Gson().fromJson(msg.payload().toString(),
                            JsonObject.class).get("yourTurn").getAsBoolean();
                    game = new Game(lobbyPanel.getNick());
                    gamePanel = new GamePanel(game, net, this);
                    mainPanel.add(gamePanel, "game");
                    cardLayout.show(mainPanel, "game");
                    controller = gamePanel.getController();
                    controller.setMyTurn(iStart);
                    JOptionPane.showMessageDialog(this,
                            iStart ? "Eşleştin, sen başlıyorsun!" : "Eşleştin, rakibi bekle...");
                }
                case ROLL, SELECT, UPDATE -> {
                    if (controller != null) {
                        controller.applyRemote(msg);
                    }
                }
                case END -> {
                    if (controller != null) {
                        JsonObject result = msg.payload().getAsJsonObject();
                        String message = "Oyun bitti!\n" +
                                "Senin puanın: " + result.get("yourScore").getAsDouble() + "\n" +
                                "Rakibin puanı: " + result.get("opponentScore").getAsDouble() + "\n" +
                                "Kazanan: " + result.get("winner").getAsString();
                        if (result.has("reason")) {
                            message += "\nSebep: " + result.get("reason").getAsString();
                        }
                        JOptionPane.showMessageDialog(this, message);
                        cardLayout.show(mainPanel, "lobby");
                        lobbyPanel.reset();
                    }
                }
                default -> {
                }
            }
        });
    }

    public YahtzeeDice[] getDiceComponents() {
        return gamePanel != null ? gamePanel.getDiceComponents() : null;
    }

    public ScoreGroup[] getOpponentScoreGroups() {
        return gamePanel != null ? gamePanel.getOpponentScoreGroups() : null;
    }

    public StaticScoreGroup getOpponentUpperSectionTotal() {
        return gamePanel != null ? gamePanel.getOpponentUpperSectionTotal() : null;
    }

    public StaticScoreGroup getOpponentUpperSectionBonus() {
        return gamePanel != null ? gamePanel.getOpponentUpperSectionBonus() : null;
    }

    public StaticScoreGroup getOpponentLowerSectionYahtzeeBonus() {
        return gamePanel != null ? gamePanel.getOpponentLowerSectionYahtzeeBonus() : null;
    }

    public StaticScoreGroup getOpponentGrandTotal() {
        return gamePanel != null ? gamePanel.getOpponentGrandTotal() : null;
    }

    public ScoreGroup[] getScoreGroups() {
        return gamePanel != null ? gamePanel.getScoreGroups() : null;
    }

    public StaticScoreGroup getUpperSectionBonus() {
        return gamePanel != null ? gamePanel.getUpperSectionBonus() : null;
    }

    public StaticScoreGroup getUpperSectionTotal() {
        return gamePanel != null ? gamePanel.getUpperSectionTotal() : null;
    }

    public StaticScoreGroup getLowerSectionYahtzeeBonus() {
        return gamePanel != null ? gamePanel.getLowerSectionYahtzeeBonus() : null;
    }

    public StaticScoreGroup getGrandTotal() {
        return gamePanel != null ? gamePanel.getGrandTotal() : null;
    }

    public JButton getRollDiceButton() {
        return gamePanel != null ? gamePanel.getRollDiceButton() : null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(YahtzeeFrame::new);
    }
}
