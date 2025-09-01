package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 1234;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        Server server = new Server(); // Create instance for non-static access
        server.startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this); // Pass 'this' reference
                clientHandlers.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void updateUserLists() {
        StringBuilder userList = new StringBuilder("USERLIST:");
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                userList.append(client.getUsername()).append(",");
            }
        }
        String listMessage = userList.toString();
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                client.sendMessage(listMessage);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        synchronized (clientHandlers) {
            clientHandlers.remove(client);
        }
        updateUserLists();
    }

}