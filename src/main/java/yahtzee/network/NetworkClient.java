package yahtzee.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.Socket;
import java.io.*;
import java.util.function.Consumer;

/**
 * Manages client-side network I/O: sending messages and reading incoming messages on a background thread.
 */
public class NetworkClient implements AutoCloseable, Runnable {
    private final BufferedReader in;      // Reader for incoming messages
    private final BufferedWriter out;     // Writer for outgoing messages
    private final Gson gson = new Gson(); // JSON serializer/deserializer
    private final Consumer<Message> onMsg; // Handler for received messages
    private volatile boolean closed = false;
    private final Thread readerThread;

    /**
     * Connect to server, send HELLO, and start listening thread.
     */
    public NetworkClient(String host, int port, String nickname,
                         Consumer<Message> handler) throws IOException {
        Socket socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.onMsg = handler;

        // Send initial handshake with player name
        JsonElement helloPayload = JsonParser.parseString("{\"name\":\"" + nickname + "\"}");
        send(new Message(MessageType.HELLO, helloPayload));

        // Launch background thread to read messages
        readerThread = new Thread(this, "NetReader");
        readerThread.start();
    }

    /**
     * Send a message to the server in JSON format.
     */
    public void send(Message m) {
        try {
            out.write(gson.toJson(m));
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Continuously read lines and dispatch parsed Message objects.
     */
    @Override
    public void run() {
        try {
            String line;
            while (!closed && (line = in.readLine()) != null) {
                Message msg = gson.fromJson(line, Message.class);
                onMsg.accept(msg);
            }
        } catch (IOException e) {
            if (!closed)
                e.printStackTrace();
        }
    }

    /**
     * Close I/O streams and socket connection.
     */
    @Override
    public void close() throws IOException {
        closed = true;
        readerThread.interrupt();
        in.close();
        out.close();
    }
}