package server;

import yahtzee.network.*;
import com.google.gson.Gson;

import java.net.Socket;
import java.io.*;

/**
 * Handles communication with a single player over a socket.
 */
class PlayerHandler implements AutoCloseable {
    private final BufferedReader in;      // Input stream from client
    private final BufferedWriter out;     // Output stream to client
    private final Socket socket;          // Underlying socket connection
    private final Gson gson = new Gson(); // JSON serializer/deserializer

    /**
     * Wraps socket with buffered reader/writer.
     */
    PlayerHandler(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }

    /**
     * Read a Message object from the client.
     */
    Message read() throws IOException {
        return gson.fromJson(in.readLine(), Message.class);
    }

    /**
     * Send a Message object to the client and flush.
     */
    void send(Message m) throws IOException {
        String json = gson.toJson(m);
        out.write(json + "\n");
        out.flush();
        System.out.println("Sent: " + json);  // Log outgoing message
    }

    /**
     * Close the socket and associated streams.
     */
    @Override
    public void close() throws IOException {
        socket.close();
    }

    boolean hasIncoming() throws IOException {
        return in.ready();
    }
}
