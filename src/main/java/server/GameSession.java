package server;

import yahtzee.network.*;
import com.google.gson.Gson;
import java.net.Socket;
import java.io.*;

final class GameSession implements Runnable {
    private final PlayerHandler p1, p2;
    private final Gson gson = new Gson();

    GameSession(Socket a, Socket b) throws IOException {
        p1 = new PlayerHandler(a);
        p2 = new PlayerHandler(b);
    }
    @Override public void run() {
        try (p1; p2) {
            p1.send(new Message(MessageType.MATCHED, "{\"yourTurn\":true}"));
            p2.send(new Message(MessageType.MATCHED, "{\"yourTurn\":false}"));

            while (true) {
                Message m = p1.read();
                p2.send(m);
                if (m.type() == MessageType.END) break;

                m = p2.read();
                p1.send(m);
                if (m.type() == MessageType.END) break;
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
