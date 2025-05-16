package yahtzee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import yahtzee.controller.GameController;
import yahtzee.model.Game;
import yahtzee.network.Message;
import yahtzee.network.NetworkClient;
import yahtzee.view.*;
import com.formdev.flatlaf.FlatLightLaf;

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
    private boolean inGame = false;

    /**
     * Initialize frame, show lobby panel, and set up event handlers.
     */
    public YahtzeeFrame() {

        super("Yahtzee Game");
        // Set a modern FlatLaf look-and-feel for the UI
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf: " + ex.getMessage());
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        lobbyPanel = new LobbyPanel();
        mainPanel.add(lobbyPanel, "lobby");
        add(mainPanel);
        mainPanel.setBackground(new Color(0xF0F0F0));
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
                setNet(new NetworkClient(ip, 12345, nick, this::handleNetwork));
                lobbyPanel.setStatus("Waiting for another player...");
                lobbyPanel.setFindEnabled(false);
                lobbyPanel.setNickEnabled(false);
            } catch (IOException ex) {
                lobbyPanel.setStatus("Connection error: " + ex.getMessage());
                lobbyPanel.setFindEnabled(true);
                lobbyPanel.setNickEnabled(true);
            }
        });

        setSize(1080, 690);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // If there is an active game, inform the opponent first
                if (inGame) {
                    int resp = JOptionPane.showConfirmDialog(
                            YahtzeeFrame.this,
                            "Quitting ends the game and the opponent wins.\nDo you want to continue?",
                            "Confirm Exit", JOptionPane.YES_NO_OPTION);

                    if (resp != JOptionPane.YES_OPTION) {
                        return; // User gave up
                    }

                    try {
                        // Send a concede message
                        com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
                        payload.addProperty("concede", true);
                        if (getNet() != null) {
                            getNet().send(new yahtzee.network.Message(
                                    yahtzee.network.MessageType.END, payload));
                            getNet().close(); // cleanly close the connection
                        }
                    } catch (Exception ignore) { }

                    // Close window – game over
                    dispose();
                    System.exit(0);
                } else {
                    // Close directly when in the lobby
                    dispose();
                    System.exit(0);
                }
            }
        });

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
                    gamePanel = new GamePanel(game, getNet(), this);
                    mainPanel.add(gamePanel, "game");
                    cardLayout.show(mainPanel, "game");
                    setController(gamePanel.getController());
                    getController().setMyTurn(iStart);
                    JOptionPane.showMessageDialog(this,
                            iStart ? "Matched! You go first." : "Matched! Waiting for opponent...");
                    setInGame(true);
                }
                case ROLL, SELECT, UPDATE -> {
                    // Forward remote actions to controller
                    if (getController() != null) {
                        getController().applyRemote(msg);
                    }
                }
                case END -> {
                    JsonObject p = msg.payload().getAsJsonObject();

                    StringBuilder sb = new StringBuilder("Game over. ");
                    if (p.has("reason")) {
                        sb.append(p.get("reason").getAsString());
                    } else {     // geriye dönük uyumluluk
                        boolean timedOut        = p.has("timeout")  && p.get("timeout").getAsBoolean();
                        boolean opponentConcede = p.has("concede") && p.get("concede").getAsBoolean();

                        sb.append( timedOut         ? "Opponent ran out of time."
                                : opponentConcede ? "Opponent has conceded."
                                : "Opponent disconnected.");
                    }

                    if (p.has("yourScore") && p.has("opponentScore") && p.has("winner")) {
                        sb.append(String.format(
                                "\nYour score: %.0f\nOpponent score: %.0f\nWinner: %s",
                                p.get("yourScore").getAsDouble(),
                                p.get("opponentScore").getAsDouble(),
                                p.get("winner").getAsString()));
                    }

                    returnToLobby(sb.toString());
                    try { if (getNet() != null) getNet().close(); } catch (Exception ignore) {}
                }
                default -> {
                    // Ignore other message types
                }
            }
        });
    }

    //  YahtzeeFrame.java  (simple forwarder)
    public void resetLobbyPanel() {
        lobbyPanel.resetWaitingState();
    }

    /*  Bring both UI and state back to the Lobby in a single call.  */
    public void returnToLobby(String message) {
        JOptionPane.showMessageDialog(this, message);

        if (getController() != null) {
            getController().stopTimer();         // make sure no timer keeps ticking
        }
        setInGame(false);

        if (gamePanel != null) {                // remove old game UI
            mainPanel.remove(gamePanel);
            gamePanel = null;
        }

        lobbyPanel.resetWaitingState();          // clear “Waiting …” text/animation
        cardLayout.show(mainPanel, "lobby");
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

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public GameController getController() {
        return controller;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setNet(NetworkClient net) {
        this.net = net;
    }

    public NetworkClient getNet() {
        return net;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(YahtzeeFrame::new);
    }
}
