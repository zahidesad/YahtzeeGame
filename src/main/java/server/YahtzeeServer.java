package server;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * Entry point for the Yahtzee server: accepts connections and pairs players into sessions.
 */
public class YahtzeeServer {
    private static final int PORT = 12345;                           // Server port
    private static final ConcurrentLinkedQueue<Socket> lobby =       // Queue for waiting players
            new ConcurrentLinkedQueue<>();

    /**
     * Listen for incoming player connections and start game sessions.
     */
    public static void main(String[] args) throws IOException {
        var pool = Executors.newCachedThreadPool();                // Thread pool for game sessions
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();      // Wait for player
                lobby.add(clientSocket);                          // Add to lobby queue
                System.out.println("Lobby size = " + lobby.size());
                if (lobby.size() >= 2) {
                    // Pair two players and start session
                    Socket p1 = lobby.poll();
                    Socket p2 = lobby.poll();
                    pool.execute(new GameSession(p1, p2));
                }
            }
        }
    }
}
