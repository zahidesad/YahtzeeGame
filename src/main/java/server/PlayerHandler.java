package server;

import yahtzee.network.*;
import com.google.gson.Gson;
import java.net.Socket;
import java.io.*;

class PlayerHandler implements AutoCloseable {
    private final BufferedReader in;
    private final BufferedWriter out;
    private final Socket socket;
    private final Gson gson = new Gson();

    PlayerHandler(Socket s) throws IOException {
        socket = s;
        in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }
    Message read() throws IOException {
        return gson.fromJson(in.readLine(), Message.class);
    }
    void send(Message m) throws IOException {
        out.write(gson.toJson(m));
        out.newLine(); out.flush();
    }
    @Override public void close() throws IOException { socket.close(); }
}
