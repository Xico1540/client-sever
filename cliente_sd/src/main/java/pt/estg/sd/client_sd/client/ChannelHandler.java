package pt.estg.sd.client_sd.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pt.estg.sd.client_sd.Views.CommunicationChannelsView;
import pt.estg.sd.client_sd.Views.GroupChatView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles channel-related actions such as retrieving, deleting, and entering channels.
 */
public class ChannelHandler {
    private Client client;
    private Stage primaryStage;

    /**
     * Constructs a ChannelHandler with the specified client and primary stage.
     *
     * @param client the client instance
     * @param primaryStage the primary stage of the application
     */
    public ChannelHandler(Client client, Stage primaryStage) {
        this.client = client;
        this.primaryStage = primaryStage;
    }

    /**
     * Handles the retrieval of channels based on the received message.
     *
     * @param message the channels message
     */
    public void handleChannels(String message) {
        if (message.startsWith("GET_CHANNELS_SUCCESS")) {
            String[] parts = message.split(";", 4);
            if (parts.length == 4) {
                int id = Integer.parseInt(parts[1]);
                String channelsJson = parts[2];
                String usersJson = parts[3];
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<Map<String, Object>> channels = objectMapper.readValue(channelsJson, new TypeReference<List<Map<String, Object>>>() {});
                    List<Map<String, Object>> users = objectMapper.readValue(usersJson, new TypeReference<List<Map<String, Object>>>() {});
                    Platform.runLater(() -> {
                        CommunicationChannelsView communicationChannelsView = new CommunicationChannelsView(id, channels, client, users);
                        BorderPane layout = (BorderPane) primaryStage.getScene().getRoot();
                        layout.setCenter(communicationChannelsView.getView());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to parse channels or users JSON: " + channelsJson + " or " + usersJson);
                }
            } else {
                System.out.println("Invalid channels message format: " + message);
            }
        } else {
            System.out.println("Failed to get channels: " + message);
        }
    }

    /**
     * Handles the deletion of channels based on the received message.
     *
     * @param message the delete channels message
     */
    public void handleDeleteChannels(String message) {
        if (message.startsWith("DELETE_CHANNEL_SUCCESS")) {
            String[] parts = message.split(";", 2);
            if (parts.length == 2) {
                String userId = parts[1];
                Platform.runLater(() -> {
                    client.sendMessage("/GET_CHANNELS;" + userId);
                });
            } else {
                System.out.println("Invalid DELETE_CHANNEL_SUCCESS message format: " + message);
            }
        } else {
            System.out.println("Failed to delete channel: " + message);
        }
    }

    /**
     * Handles entering a chat based on the received message.
     *
     * @param message the enter chat message
     */
    public void handleEnterChat(String message) {
        if (message.startsWith("ENTER_CHAT_SUCCESS")) {
            String[] parts = message.split(";", 5);
            if (parts.length == 5) {
                int id = Integer.parseInt(parts[1]);
                String messagesJson = parts[2];
                String channelName = parts[3];
                String username = parts[4];
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<Map<String, Object>> messages = objectMapper.readValue(messagesJson, new TypeReference<List<Map<String, Object>>>() {});
                    Platform.runLater(() -> {
                        GroupChatView groupChatView = new GroupChatView(id, messages, client, channelName, username);
                        BorderPane layout = (BorderPane) primaryStage.getScene().getRoot();
                        layout.setCenter(groupChatView.getView());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to parse messages JSON: " + messagesJson);
                }
            } else {
                System.out.println("Invalid messages message format: " + message);
            }
        } else if (message.startsWith("ENTER_GROUP_CHAT_SUCCESS")) {
            String[] parts = message.split(";", 6);
            if (parts.length == 6) {
                int id = Integer.parseInt(parts[1]);
                String messagesJson = parts[2];
                String channelName = parts[3];
                String username = parts[4];
                String multicastIPAndPort = parts[5];
                String[] multicastParts = multicastIPAndPort.split(":");
                if (multicastParts.length == 2) {
                    String multicastIP = multicastParts[0];
                    int multicastPort = Integer.parseInt(multicastParts[1]);
                    client.setMulticastConfig(multicastIP, multicastPort);
                    client.startMulticastListener();
                }
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<Map<String, Object>> messages = objectMapper.readValue(messagesJson, new TypeReference<List<Map<String, Object>>>() {});
                    Platform.runLater(() -> {
                        GroupChatView groupChatView = new GroupChatView(id, messages, client, channelName, username);
                        BorderPane layout = (BorderPane) primaryStage.getScene().getRoot();
                        layout.setCenter(groupChatView.getView());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to parse messages JSON: " + messagesJson);
                }
            } else {
                System.out.println("Invalid messages message format: " + message);
            }
        }
        }


    /**
     * Handles the creation of a group based on the received message.
     *
     * @param message the create group message
     */
    public void handleCreateGroup(String message) {
        if (message.startsWith("CREATE_GROUP_SUCCESS")) {
            String[] parts = message.split(";", 2);
            if (parts.length == 2) {
                String userId = parts[1];
                Platform.runLater(() -> {
                    client.sendMessage("/GET_CHANNELS;" + userId);
                });
            } else {
                System.out.println("Invalid CREATE_GROUP_SUCCESS message format: " + message);
            }
        } else {
            System.out.println("Failed to create group: " + message);
        }
    }

    /**
     * Handles the creation of a chat based on the received message.
     *
     * @param message the create chat message
     */
    public void handleCreateChat(String message) {
        if (message.equals("CREATE_CHAT_SUCCESS")) {
            Platform.runLater(() -> {
                showAlert("Chat criado com sucesso. Você pode encontrá-lo nos canais de comunicação.");
            });
        } else if (message.equals("CHAT_EXISTS")) {
            Platform.runLater(() -> {
                showAlert("O chat já existe. Você pode encontrá-lo nos canais de comunicação.");
            });
        } else if (message.equals("CREATE_CHAT_FAILED")) {
            Platform.runLater(() -> {
                showAlert("Falha ao criar o chat. Tente novamente mais tarde.");
            });
        } else {
            System.out.println("Failed to create chat: " + message);
        }
    }

    /**
     * Handles sending a message based on the received message.
     *
     * @param message the send message
     */
    public void handleSendMessage(String message) {
        if (message.startsWith("SEND_MESSAGE_SUCCESS")) {
            System.out.println("Message sent successfully.");
        } else if (message.startsWith("SEND_MESSAGE_FAILED")) {
            Platform.runLater(() -> {
                Label errorLabel = new Label("Failed to send message: " + message);
                errorLabel.setStyle("-fx-text-fill: red;");
                BorderPane layout = (BorderPane) primaryStage.getScene().getRoot();
                layout.setBottom(errorLabel);
            });
        } else {
            System.out.println("Unknown message format: " + message);
        }
    }

    /**
     * Handles receiving a message based on the received message.
     *
     * @param message the received message
     */
    public void handleMessageReceived(String message) {
        if (message.startsWith("MESSAGE_RECEIVED_SUCCESS")) {
            String[] parts = message.split(";", 5);
            if (parts.length == 5) {
                String senderName = parts[1];
                String channelName = parts[2];
                String messageContent = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                Platform.runLater(() -> {
                    GroupChatView.addMessageToChat(senderName, messageContent, timestamp);
                });
            } else {
                System.out.println("Invalid MESSAGE_RECEIVED_SUCCESS message format: " + message);
            }
        } else {
            System.out.println("Failed to receive message: " + message);
        }
    }

    /**
     * Shows an alert with the specified message.
     *
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação do Chat");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}