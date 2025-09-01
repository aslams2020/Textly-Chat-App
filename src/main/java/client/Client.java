package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.*;
import util.ConfigLoader;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends Application {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private VBox messageFlow;
    private TextField messageField;
    private Button sendButton;
    private ListView<String> userList;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private ScrollPane messageScroll;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Get username
        username = showUsernameDialog();
        if (username == null)
            System.exit(0);

        // Set up connection
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(username);
            appendMessage("System", "Connected to server successfully!", Color.GREEN);
        } catch (IOException e) {
            appendMessage("Error", "Failed to connect to server: " + e.getMessage(), Color.RED);
            System.exit(1);
        }

        // Create UI components
        VBox messageContainer = new VBox();
        messageContainer.setSpacing(5);
        messageScroll = new ScrollPane(messageContainer);
        messageFlow = messageContainer;
        messageScroll.setFitToWidth(true);
        messageField = new TextField();
        messageField.setPromptText("Type a message...");

        sendButton = new Button("Send");
        sendButton.setOnAction(event -> sendMessage());

        userList = new ListView<>();
        userList.setPrefWidth(150);

        // Create main layout - ONLY ONCE!
        HBox mainLayout = new HBox(10,
                new VBox(10, messageScroll, new HBox(10, messageField, sendButton)),
                userList);
        mainLayout.setPadding(new javafx.geometry.Insets(10));

        // Apply CSS styling
        mainLayout.getStyleClass().add("root");
        messageScroll.getStyleClass().add("message-scroll");
        userList.getStyleClass().add("user-list");
        messageField.getStyleClass().add("message-field");
        sendButton.getStyleClass().add("send-button");

        // Create scene with CSS
        Scene scene = new Scene(mainLayout, 600, 400);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            System.out.println("CSS loaded successfully!");
        } catch (Exception e) {
            System.out.println("CSS file not found, using default styling");
        }

        primaryStage.setTitle("Chat - " + username);
        primaryStage.setScene(scene);
        primaryStage.show();

        listenForMessages();
    }

    private String showUsernameDialog() {
        TextInputDialog dialog = new TextInputDialog("User" + (int) (Math.random() * 1000));
        dialog.setTitle("Username");
        dialog.setHeaderText("Choose a username");
        dialog.setContentText("Username:");
        return dialog.showAndWait().orElse(null);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        int maxLength = ConfigLoader.getMaxMessageLength();

        if (message.isEmpty()) {
            return;
        }

        if (message.length() > maxLength) {
            appendMessage("System", "Message too long (max " + maxLength + " characters)", Color.RED);
            return;
        }

        out.println(message);
        messageField.clear();
        appendMessage("You", message, Color.BLUE);
    }

    private void listenForMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message); // Debug output

                    if (message.startsWith("USERLIST:")) {
                        updateUserList(message.substring(9));
                    } else if (message.startsWith("Welcome")) {
                        appendMessage("Server", message, Color.GREEN);
                    } else if (message.contains("has joined") || message.contains("has left")) {
                        appendMessage("System", message, Color.PURPLE);
                    } else if (message.contains(":")) {
                        String[] parts = message.split(":", 2);
                        appendMessage(parts[0].trim(), parts[1].trim(), Color.DARKGREEN);
                    } else {
                        appendMessage("Unknown", message, Color.GRAY); // Fallback
                    }
                }
            } catch (IOException e) {
                appendMessage("Error", "Connection lost: " + e.getMessage(), Color.RED);
            }
        }).start();
    }

    private void appendMessage(String sender, String message, Color color) {
        Platform.runLater(() -> {
            HBox messageRow = new HBox();
            messageRow.setPadding(new Insets(5, 10, 5, 10));

            // Align right for own messages, left for others
            if (sender.equals("You")) {
                messageRow.setAlignment(Pos.CENTER_RIGHT);
            } else {
                messageRow.setAlignment(Pos.CENTER_LEFT);
            }

            VBox messageBubble = new VBox();
            messageBubble.setPadding(new Insets(8, 12, 8, 12));
            messageBubble.setMaxWidth(250);

            // Style bubbles differently for sender vs others
            if (sender.equals("You")) {
                messageBubble.setStyle("-fx-background-color: #dcf8c6; -fx-background-radius: 15 15 0 15;");
            } else {
                messageBubble.setStyle(
                        "-fx-background-color: #ffffff; -fx-background-radius: 15 15 15 0; -fx-border-color: #e0e0e0; -fx-border-radius: 15 15 15 0;");
            }

            Text timestamp = new Text(timeFormat.format(new Date()));
            timestamp.setFill(Color.GRAY);
            timestamp.setStyle("-fx-font-size: 10px;");

            Text senderText = new Text(sender + ":");
            senderText.setFill(color);
            senderText.setStyle("-fx-font-weight: bold;");

            Text messageText = new Text(message);
            messageText.setWrappingWidth(230); // Allow text wrapping

            if (sender.equals("You")) {
                messageBubble.getChildren().addAll(senderText, messageText, timestamp);
            } else {
                messageBubble.getChildren().addAll(timestamp, senderText, messageText);
            }

            messageRow.getChildren().add(messageBubble);
            messageFlow.getChildren().add(messageRow); // Add to VBox

            // Auto-scroll to bottom
            if (messageScroll != null) {
                messageScroll.setVvalue(1.0);
            }
        });
    }

    private void updateUserList(String users) {
        Platform.runLater(() -> {
            userList.getItems().clear();
            for (String user : users.split(",")) {
                if (!user.trim().isEmpty()) {
                    String statusEmoji = "🟢 "; // Online
                    if (user.equals(username)) {
                        statusEmoji = "⭐ "; // Current user
                    }
                    userList.getItems().add(statusEmoji + user.trim());
                }
            }
        });
    }
}