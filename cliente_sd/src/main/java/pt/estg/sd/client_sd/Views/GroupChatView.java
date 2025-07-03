package pt.estg.sd.client_sd.Views;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import pt.estg.sd.client_sd.client.Client;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Represents the view for displaying and handling group chat messages.
 */
public class GroupChatView {
    private VBox chatBox;
    private static ListView<String> messageList;
    private TextField messageField;
    private Button sendButton;
    private Client client;
    private String channelName;
    private int userId;
    private String username;

    /**
     * Constructs a GroupChatView with the specified user ID, messages, client, channel name, and username.
     *
     * @param userId the user ID
     * @param messages the list of messages
     * @param client the client instance
     * @param channelName the name of the channel
     * @param username the username of the user
     */


    public GroupChatView(int userId, List<Map<String, Object>> messages, Client client, String channelName, String username) {
        this.client = client;
        this.channelName = channelName;
        this.userId = userId;
        this.username = username;
        initializeChatBox();
        loadMessagesForChannel(messages);
    }

    public void leaveGroup() {
        client.leaveGroup();
        // Redirect to the CommunicationChannelsView
        client.sendMessage("/GET_CHANNELS;" + userId);
    }

    /**
     * Initializes the chat box layout.
     */
    private void initializeChatBox() {
        messageList = new ListView<>();
        messageField = new TextField();
        messageField.setPromptText("Type a message...");

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            handleSendMessage();
            messageField.clear();
        });

        Button leaveButton = new Button("Leave");
        leaveButton.setOnAction(e -> leaveGroup());

        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.getChildren().addAll(messageList, messageField, sendButton, leaveButton);

        chatBox = new VBox(10);
        chatBox.setAlignment(Pos.CENTER);
        chatBox.getChildren().add(messageBox);

        Label chatTitle = new Label("Chat: " + channelName);
        BorderPane titleBox = new BorderPane();
        titleBox.setCenter(chatTitle);
        chatBox.getChildren().add(0, titleBox);
    }

    /**
     * Loads messages for the specified channel.
     *
     * @param messages the list of messages
     */
    private void loadMessagesForChannel(List<Map<String, Object>> messages) {
        messageList.getItems().clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> message : messages) {
            String formattedMessage = String.format("%s - %s: %s",
                    message.get("senderName"),
                    dateFormat.format(new Date((Long) message.get("timestamp"))),
                    message.get("content"));
            messageList.getItems().add(formattedMessage);
        }

        if (!messageList.getItems().isEmpty()) {
            messageList.scrollTo(messageList.getItems().size() - 1);
        }
    }

    /**
     * Handles sending a message.
     */
    private void handleSendMessage() {
        String messageContent = messageField.getText();
        if (!messageContent.isEmpty()) {
            client.sendMessage("/SEND_MESSAGE;" + username + ";" + channelName + ";" + messageContent + ";" + System.currentTimeMillis());
            if (channelName.startsWith("Chat")) {
                GroupChatView.addMessageToChat(username, messageContent, System.currentTimeMillis());
            }
            messageField.clear();
        }
    }

    /**
     * Adds a message to the chat.
     *
     * @param senderName the name of the sender
     * @param messageContent the content of the message
     * @param timestamp the timestamp of the message
     */
    public static void addMessageToChat(String senderName, String messageContent, long timestamp) {
        Platform.runLater(() -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedMessage = String.format("%s - %s: %s", senderName, dateFormat.format(new Date(timestamp)), messageContent);
            messageList.getItems().add(formattedMessage);
            messageList.scrollTo(messageList.getItems().size() - 1);
        });
    }

    /**
     * Returns the view node for displaying the group chat.
     *
     * @return the view node
     */
    public VBox getView() {
        return chatBox;
    }
}