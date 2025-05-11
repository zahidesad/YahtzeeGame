package server;

import com.google.gson.JsonObject;
import yahtzee.network.*;
import com.google.gson.Gson;

import java.net.Socket;
import java.io.*;

final class GameSession implements Runnable {
    private final PlayerHandler p1, p2;
    private final Gson gson = new Gson();
    private boolean p1Finished = false;
    private boolean p2Finished = false;
    private Integer p1FinalScore = null;
    private Integer p2FinalScore = null;

    GameSession(Socket a, Socket b) throws IOException {
        p1 = new PlayerHandler(a);
        p2 = new PlayerHandler(b);
    }

    @Override
    public void run() {
        try (p1; p2) {
            p1.send(new Message(MessageType.MATCHED, "{\"yourTurn\":true}"));
            p2.send(new Message(MessageType.MATCHED, "{\"yourTurn\":false}"));
            PlayerHandler current = p1;
            PlayerHandler other = p2;
            while (true) {
                if ((current == p1 && p1Finished) || (current == p2 && p2Finished)) {
                    PlayerHandler temp = current;
                    current = other;
                    other = temp;
                    if ((current == p1 && p1Finished) || (current == p2 && p2Finished)) {
                        break;
                    }
                    continue;
                }
                Message m = current.read();
                if (m.type() == MessageType.SELECT || m.type() == MessageType.ROLL) {
                    other.send(m);
                }
                if (m.type() == MessageType.SELECT) {
                    JsonObject payload = gson.fromJson(m.payload().toString(), JsonObject.class);
                    boolean gameOver = payload.get("gameOver").getAsBoolean();
                    if (gameOver) {
                        int finalScore = payload.get("grand").getAsInt();
                        if (current == p1) {
                            p1FinalScore = finalScore;
                            p1Finished = true;
                        } else {
                            p2FinalScore = finalScore;
                            p2Finished = true;
                        }
                    }
                    PlayerHandler temp = current;
                    current = other;
                    other = temp;
                }
                if (m.type() == MessageType.END) {
                    JsonObject payload = gson.fromJson(m.payload().toString(), JsonObject.class);
                    if (payload.has("concede") && payload.get("concede").getAsBoolean()) {
                        JsonObject resultConceding = new JsonObject();
                        resultConceding.addProperty("yourScore", current == p1 ? p1FinalScore != null ? p1FinalScore : 0 : p2FinalScore != null ? p2FinalScore : 0);
                        resultConceding.addProperty("opponentScore", other == p1 ? p1FinalScore != null ? p1FinalScore : 0 : p2FinalScore != null ? p2FinalScore : 0);
                        resultConceding.addProperty("winner", "Opponent");
                        resultConceding.addProperty("reason", "You conceded");

                        JsonObject resultWinning = new JsonObject();
                        resultWinning.addProperty("yourScore", other == p1 ? p1FinalScore != null ? p1FinalScore : 0 : p2FinalScore != null ? p2FinalScore : 0);
                        resultWinning.addProperty("opponentScore", current == p1 ? p1FinalScore != null ? p1FinalScore : 0 : p2FinalScore != null ? p2FinalScore : 0);
                        resultWinning.addProperty("winner", "You");
                        resultWinning.addProperty("reason", "Opponent conceded");

                        current.send(new Message(MessageType.END, resultConceding));
                        other.send(new Message(MessageType.END, resultWinning));
                        break;
                    }
                }
                if (p1FinalScore != null && p2FinalScore != null) {
                    JsonObject resultP1 = new JsonObject();
                    resultP1.addProperty("yourScore", p1FinalScore);
                    resultP1.addProperty("opponentScore", p2FinalScore);
                    resultP1.addProperty("winner", p1FinalScore > p2FinalScore ? "You" : (p1FinalScore < p2FinalScore ? "Opponent" : "Tie"));

                    JsonObject resultP2 = new JsonObject();
                    resultP2.addProperty("yourScore", p2FinalScore);
                    resultP2.addProperty("opponentScore", p1FinalScore);
                    resultP2.addProperty("winner", p2FinalScore > p1FinalScore ? "You" : (p2FinalScore < p1FinalScore ? "Opponent" : "Tie"));
                    p1.send(new Message(MessageType.END, resultP1));
                    p2.send(new Message(MessageType.END, resultP2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
