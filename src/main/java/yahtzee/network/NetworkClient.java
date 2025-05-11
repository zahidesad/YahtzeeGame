package yahtzee.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.Socket;
import java.io.*;
import java.util.function.Consumer;

public class NetworkClient implements AutoCloseable, Runnable {
    private final BufferedReader in;
    private final BufferedWriter out;
    private final Gson gson = new Gson();
    private final Consumer<Message> onMsg;

    public NetworkClient(String host, int port, String nickname,
                         Consumer<Message> handler) throws IOException {
        Socket socket = new Socket(host, port);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.onMsg = handler;

        JsonElement helloPayload = JsonParser.parseString("{\"name\":\"" + nickname + "\"}");
        send(new Message(MessageType.HELLO, helloPayload));
        new Thread(this, "NetReader").start();
    }

    public void send(Message m) {
        try { out.write(gson.toJson(m)); out.newLine(); out.flush(); }
        catch (IOException e) { e.printStackTrace(); }
    }
    @Override public void run() {
        try { String line;
            while ((line = in.readLine()) != null)
                onMsg.accept(gson.fromJson(line, Message.class));
        } catch (IOException e) { e.printStackTrace(); }
    }
    @Override public void close() throws IOException { in.close(); out.close(); }
}
