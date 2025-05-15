package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import yahtzee.network.*;
import com.google.gson.Gson;

import java.net.Socket;
import java.io.*;

/**
 * Manages a two-player game session, routing messages and determining results.
 */
final class GameSession implements Runnable {
    private final PlayerHandler p1, p2;  // Player connections
    private final Gson gson = new Gson(); // JSON serializer/deserializer
    private boolean p1Finished = false;
    private boolean p2Finished = false;
    private Integer p1FinalScore = null;
    private Integer p2FinalScore = null;

    /**
     * Initialize session with two player sockets.
     */
    GameSession(Socket a, Socket b) throws IOException {
        p1 = new PlayerHandler(a);
        p2 = new PlayerHandler(b);
    }

    /**
     * Main loop: notify match, alternate turns, forward actions, and send results.
     */
    @Override
    public void run() {
        try (p1; p2) {
            // Send initial turn notifications
            JsonElement payloadP1 = JsonParser.parseString("{\"yourTurn\":true}");
            JsonElement payloadP2 = JsonParser.parseString("{\"yourTurn\":false}");
            p1.send(new Message(MessageType.MATCHED, payloadP1));
            p2.send(new Message(MessageType.MATCHED, payloadP2));

            PlayerHandler current = p1;
            PlayerHandler other = p2;

            // Game loop
            while (true) {
                // Skip turn if player has finished
                if ((current == p1 && p1Finished) || (current == p2 && p2Finished)) {
                    PlayerHandler temp = current;
                    current = other;
                    other = temp;
                    // Break if both finished
                    if ((current == p1 && p1Finished) || (current == p2 && p2Finished)) {
                        break;
                    }
                    continue;
                }

                Message m = current.read(); // Read player message

                // Forward roll/select actions
                if (m.type() == MessageType.SELECT || m.type() == MessageType.ROLL) {
                    other.send(m);
                }

                // Handle SELECT: check for game over and record score
                if (m.type() == MessageType.SELECT) {
                    JsonObject data = gson.fromJson(m.payload().toString(), JsonObject.class);
                    boolean gameOver = data.get("gameOver").getAsBoolean();
                    if (gameOver) {
                        int finalScore = data.get("grand").getAsInt();
                        if (current == p1) {
                            p1FinalScore = finalScore;
                            p1Finished = true;
                        } else {
                            p2FinalScore = finalScore;
                            p2Finished = true;
                        }
                    }
                    // Swap turns
                    PlayerHandler temp = current;
                    current = other;
                    other = temp;
                }

                // Handle END: concession logic
                if (m.type() == MessageType.END) {
                    JsonObject data = gson.fromJson(m.payload().toString(), JsonObject.class);
                    if (data.has("concede") && data.get("concede").getAsBoolean()) {
                        // Prepare conceding result
                        JsonObject concedeResult = new JsonObject();
                        concedeResult.addProperty("yourScore", current == p1 ?
                                (p1FinalScore != null ? p1FinalScore : 0) :
                                (p2FinalScore != null ? p2FinalScore : 0));
                        concedeResult.addProperty("opponentScore", other == p1 ?
                                (p1FinalScore != null ? p1FinalScore : 0) :
                                (p2FinalScore != null ? p2FinalScore : 0));
                        concedeResult.addProperty("winner", "Opponent");
                        concedeResult.addProperty("reason", "You conceded");

                        // Prepare winning result for opponent
                        JsonObject winResult = new JsonObject();
                        winResult.addProperty("yourScore", other == p1 ?
                                (p1FinalScore != null ? p1FinalScore : 0) :
                                (p2FinalScore != null ? p2FinalScore : 0));
                        winResult.addProperty("opponentScore", current == p1 ?
                                (p1FinalScore != null ? p1FinalScore : 0) :
                                (p2FinalScore != null ? p2FinalScore : 0));
                        winResult.addProperty("winner", "You");
                        winResult.addProperty("reason", "Opponent conceded");

                        // Send end messages
                        current.send(new Message(MessageType.END, concedeResult));
                        other.send(new Message(MessageType.END, winResult));
                        break;
                    }
                }

                // If both players have final scores, send results
                if (p1FinalScore != null && p2FinalScore != null) {
                    JsonObject res1 = new JsonObject();
                    res1.addProperty("yourScore", p1FinalScore);
                    res1.addProperty("opponentScore", p2FinalScore);
                    res1.addProperty("winner", p1FinalScore > p2FinalScore ? "You" :
                            (p1FinalScore < p2FinalScore ? "Opponent" : "Tie"));

                    JsonObject res2 = new JsonObject();
                    res2.addProperty("yourScore", p2FinalScore);
                    res2.addProperty("opponentScore", p1FinalScore);
                    res2.addProperty("winner", p2FinalScore > p1FinalScore ? "You" :
                            (p2FinalScore < p1FinalScore ? "Opponent" : "Tie"));

                    p1.send(new Message(MessageType.END, res1));
                    p2.send(new Message(MessageType.END, res2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log errors
        }
    }
}
