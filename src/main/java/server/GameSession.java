package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import yahtzee.network.Message;
import yahtzee.network.MessageType;

import java.net.Socket;
import java.io.IOException;

/**
 * Manages a two-player game session: matches players, routes actions,
 * handles concessions, and determines final results.
 */
final class GameSession implements Runnable {
    private final PlayerHandler p1;   // First player handler
    private final PlayerHandler p2;   // Second player handler
    private final Gson gson = new Gson();  // JSON utility
    private Integer p1FinalScore = null;   // First player's final score
    private Integer p2FinalScore = null;   // Second player's final score

    /**
     * Create a session with two connected player sockets.
     */
    GameSession(Socket a, Socket b) throws IOException {
        p1 = new PlayerHandler(a);
        p2 = new PlayerHandler(b);
    }

    /**
     * Main loop: send initial turn info, receive and forward actions,
     * process select events, handle concessions and conclude when both finish.
     */
    @Override
    public void run() {
        try (p1; p2) {
            sendInitialTurnInfo();

            while (true) {
                PlayerHandler sender;
                PlayerHandler receiver;

                // Check which player has a pending message
                if (p1.hasIncoming()) {
                    sender = p1;
                    receiver = p2;
                } else if (p2.hasIncoming()) {
                    sender = p2;
                    receiver = p1;
                } else {
                    Thread.sleep(10);
                    continue;
                }

                Message m = sender.read();

                // Handle concession at any time
                if (m.type() == MessageType.END &&
                        m.payload().getAsJsonObject().has("concede") &&
                        m.payload().getAsJsonObject().get("concede").getAsBoolean()) {
                    sendConcedeResults(sender, receiver);
                    break;
                }

                // Forward roll and select messages to the other player
                if (m.type() == MessageType.ROLL || m.type() == MessageType.SELECT) {
                    receiver.send(m);
                }

                // Update final score when a select indicates game over
                if (m.type() == MessageType.SELECT) {
                    handleSelect(sender, m);
                }

                // If both players have finished, send results and exit
                if (p1FinalScore != null && p2FinalScore != null) {
                    sendFinalResults();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------- Helper methods -------------------

    /**
     * Send initial matched message indicating which player starts.
     */
    private void sendInitialTurnInfo() throws IOException {
        JsonObject p1Payload = JsonParser.parseString("{\"yourTurn\":true}").getAsJsonObject();
        JsonObject p2Payload = JsonParser.parseString("{\"yourTurn\":false}").getAsJsonObject();
        p1.send(new Message(MessageType.MATCHED, p1Payload));
        p2.send(new Message(MessageType.MATCHED, p2Payload));
    }

    /**
     * Extract final score from select message if game over flag is set.
     */
    private void handleSelect(PlayerHandler current, Message m) throws IOException {
        JsonObject data = gson.fromJson(m.payload().toString(), JsonObject.class);
        boolean gameOver = data.get("gameOver").getAsBoolean();
        int finalScore = data.get("grand").getAsInt();

        if (gameOver) {
            if (current == p1) {
                p1FinalScore = finalScore;
            } else {
                p2FinalScore = finalScore;
            }
        }
    }

    /**
     * Notify both players of a concession outcome.
     */
    private void sendConcedeResults(PlayerHandler conceding, PlayerHandler opponent) throws IOException {
        int concedingScore = (conceding == p1 ?
                (p1FinalScore != null ? p1FinalScore : 0)
                : (p2FinalScore != null ? p2FinalScore : 0));

        int opponentScore  = (opponent == p1 ?
                (p1FinalScore != null ? p1FinalScore : 0)
                : (p2FinalScore != null ? p2FinalScore : 0));

        JsonObject concedeRes = new JsonObject();
        concedeRes.addProperty("yourScore", concedingScore);
        concedeRes.addProperty("opponentScore", opponentScore);
        concedeRes.addProperty("winner", "Opponent");
        concedeRes.addProperty("reason", "You conceded");

        JsonObject winRes = new JsonObject();
        winRes.addProperty("yourScore", opponentScore);
        winRes.addProperty("opponentScore", concedingScore);
        winRes.addProperty("winner", "You");
        winRes.addProperty("reason", "Opponent conceded");

        conceding.send(new Message(MessageType.END, concedeRes));
        opponent.send(new Message(MessageType.END, winRes));
    }

    /**
     * Send final results to both players when both have finished.
     */
    private void sendFinalResults() throws IOException {
        JsonObject res1 = new JsonObject();
        res1.addProperty("yourScore", p1FinalScore);
        res1.addProperty("opponentScore", p2FinalScore);
        res1.addProperty("winner",
                p1FinalScore > p2FinalScore ? "You" :
                        (p1FinalScore < p2FinalScore ? "Opponent" : "Tie")
        );

        JsonObject res2 = new JsonObject();
        res2.addProperty("yourScore", p2FinalScore);
        res2.addProperty("opponentScore", p1FinalScore);
        res2.addProperty("winner",
                p2FinalScore > p1FinalScore ? "You" :
                        (p2FinalScore < p1FinalScore ? "Opponent" : "Tie")
        );

        p1.send(new Message(MessageType.END, res1));
        p2.send(new Message(MessageType.END, res2));
    }
}