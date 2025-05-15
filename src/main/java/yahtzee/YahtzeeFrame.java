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

/**
 * Main application window: manages lobby and game panels, handles network events.
 */
public class YahtzeeFrame extends JFrame {
    public CardLayout cardLayout;   // Layout to switch between views
    public JPanel mainPanel;        // Container for lobby and game panels
    private LobbyPanel lobbyPanel;  // Initial connection UI
    private GamePanel gamePanel;    // Active game UI
    private Game game;              // Game model
    private NetworkClient net;      // Network client for server communication
    private GameController controller; // Controller for game interactions

    /**
     * Initialize frame, show lobby panel, and set up event handlers.
     */
    public YahtzeeFrame() {
        super("Yahtzee Game");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        lobbyPanel = new LobbyPanel();
        mainPanel.add(lobbyPanel, "lobby");
        add(mainPanel);
        cardLayout.show(mainPanel, "lobby");

        // Handle "Find Game" button in lobby
        lobbyPanel.addFindGameListener(e -> {
            String ip = lobbyPanel.getIp();
            String nick = lobbyPanel.getNick();
            if (ip.isEmpty() || nick.isEmpty()) {
                lobbyPanel.setStatus("Please enter IP and nickname.");
                return;
            }
            try {
                // Connect and send HELLO
                net = new NetworkClient(ip, 12345, nick, this::handleNetwork);
                lobbyPanel.setStatus("Waiting for another player...");
            } catch (IOException ex) {
                lobbyPanel.setStatus("Connection error: " + ex.getMessage());
            }
        });

        setSize(950, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Dispatch network messages on the Swing event thread.
     */
    private void handleNetwork(Message msg) {
        SwingUtilities.invokeLater(() -> {
            switch (msg.type()) {
                case MATCHED -> {
                    // Start game when matched
                    boolean iStart = new Gson()
                            .fromJson(msg.payload().toString(), JsonObject.class)
                            .get("yourTurn").getAsBoolean();
                    game = new Game(lobbyPanel.getNick());
                    gamePanel = new GamePanel(game, net, this);
                    mainPanel.add(gamePanel, "game");
                    cardLayout.show(mainPanel, "game");
                    controller = gamePanel.getController();
                    controller.setMyTurn(iStart);
                    JOptionPane.showMessageDialog(this,
                            iStart ? "Matched! You go first." : "Matched! Waiting for opponent...");
                }
                case ROLL, SELECT, UPDATE -> {
                    // Forward remote actions to controller
                    if (controller != null) {
                        controller.applyRemote(msg);
                    }
                }
                case END -> {
                    // Display results and return to lobby
                    if (controller != null) {
                        JsonObject res = msg.payload().getAsJsonObject();
                        String resultMsg = String.format(
                                "Game over!\nYour score: %.0f\nOpponent score: %.0f\nWinner: %s",
                                res.get("yourScore").getAsDouble(),
                                res.get("opponentScore").getAsDouble(),
                                res.get("winner").getAsString()
                        );
                        if (res.has("reason")) {
                            resultMsg += "\nReason: " + res.get("reason").getAsString();
                        }
                        JOptionPane.showMessageDialog(this, resultMsg);
                        cardLayout.show(mainPanel, "lobby");
                        lobbyPanel.reset();
                    }
                }
                default -> {
                    // Ignore other message types
                }
            }
        });
    }

    /* ---------- Getter proxies for testing or UI updates ---------- */
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

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(YahtzeeFrame::new);
    }
}
