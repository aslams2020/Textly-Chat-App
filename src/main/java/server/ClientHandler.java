import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // First message is username
            username = in.readLine();
            System.out.println(username + " connected");

            out.println("Welcome " + username + "! Type 'exit' to leave.");
            Server.broadcast(username + " has joined the chat", this);
            Server.updateUserLists();

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) break;
                System.out.println(username + ": " + message);
                Server.broadcast(username + ": " + message, this);
            }
        } catch (IOException e) {
            System.err.println("Error with " + username + ": " + e.getMessage());
        } finally {
            try {
                Server.broadcast(username + " has left the chat", this);
                socket.close();
                Server.removeClient(this);
            } catch (IOException e) {
                System.err.println("Error closing " + username + "'s connection");
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }
}