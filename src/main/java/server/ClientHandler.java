package server;

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
    private Server server;

    // Rate limiting variables
    private long lastMessageTime = 0;
    private static final long MESSAGE_COOLDOWN = 1000; // 1 second between messages
    private static final int MAX_MESSAGE_LENGTH = 500; // Max message characters

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // First message is username
            username = in.readLine().trim();
            if (!isValidUsername(username)) {
                out.println("ERROR: Invalid username. Use 3-20 alphanumeric characters.");
                socket.close();
                return;
            }
     
            System.out.println(username + " connected");

            out.println("Welcome " + username + "! Type 'exit' to leave.");
            Server.broadcast(username + " has joined the chat", this);
            Server.updateUserLists();

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit"))
                    break;

                // Message validation
                if (!isValidMessage(message)) {
                    out.println("ERROR: Message contains invalid characters or is too long");
                    continue;
                }

                // Rate limiting
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastMessageTime < MESSAGE_COOLDOWN) {
                    out.println("ERROR: Message rate too high. Wait 1 second between messages.");
                    continue;
                }
                lastMessageTime = currentTime;

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

    private boolean isValidUsername(String username) {
        return username != null &&
                username.length() >= 3 &&
                username.length() <= 20 &&
                username.matches("^[a-zA-Z0-9_]+$");
    }

    // Message content validation
    private boolean isValidMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        if (message.length() > MAX_MESSAGE_LENGTH) {
            return false;
        }

        // if (containsInappropriateContent(message)) {
        //     return false;
        // }

        if (message.matches(".*[<>].*")) {
            return false;
        }

        if (message.matches(".*(/|\\|&|;|`).*")) {
            return false;
        }

        return true;
    }

//     private boolean containsInappropriateContent(String message) {
//     String[] badWords = {"badword1", "badword2", "badword3"};
//     String lowerMessage = message.toLowerCase();
//     for (String word : badWords) {
//         if (lowerMessage.contains(word)) {
//             return true;
//         }
//     }
//     return false;
// }
}