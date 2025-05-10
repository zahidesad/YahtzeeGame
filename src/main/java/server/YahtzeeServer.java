package server;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class YahtzeeServer {
    private static final int PORT = 55555;
    private static final ConcurrentLinkedQueue<Socket> lobby = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws IOException {
        var pool = Executors.newCachedThreadPool();
        try (ServerSocket ss = new ServerSocket(PORT)) {
            System.out.println("Server listening on " + PORT);
            while (true) {
                Socket s = ss.accept();
                lobby.add(s);
                System.out.println("Lobby size = " + lobby.size());
                if (lobby.size() >= 2) {
                    pool.execute(new GameSession(lobby.poll(), lobby.poll()));
                }
            }
        }
    }
}
