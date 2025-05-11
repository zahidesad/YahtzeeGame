package yahtzee;

import com.google.gson.Gson;
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
    private CardLayout cardLayout;
    private JPanel mainPanel;
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

        setSize(815, 545);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
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
                        JsonObject result = new Gson().fromJson(msg.payload().toString(), JsonObject.class);
                        String message = "Oyun bitti!\n" +
                                "Senin puanın: " + result.get("yourScore").getAsInt() + "\n" +
                                "Rakibin puanı: " + result.get("opponentScore").getAsInt() + "\n" +
                                "Kazanan: " + result.get("winner").getAsString();
                        JOptionPane.showMessageDialog(this, message);
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

    public JButton getNewGameButton() {
        return gamePanel != null ? gamePanel.getNewGameButton() : null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(YahtzeeFrame::new);
    }
}
