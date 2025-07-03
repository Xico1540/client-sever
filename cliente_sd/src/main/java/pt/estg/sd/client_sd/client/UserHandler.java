package pt.estg.sd.client_sd.client;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import pt.estg.sd.client_sd.Views.UserListView;
import javafx.stage.Stage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles user-related actions such as retrieving, updating roles, and deleting users.
 */
public class UserHandler {
    private Client client;
    private Stage primaryStage;

    /**
     * Constructs a UserHandler with the specified client and primary stage.
     *
     * @param client the client instance
     * @param primaryStage the primary stage of the application
     */
    public UserHandler(Client client, Stage primaryStage) {
        this.client = client;
        this.primaryStage = primaryStage;
    }

    /**
     * Handles the retrieval of users based on the received message.
     *
     * @param message the users message
     */
    public void handleUsers(String message) {
        if (message.startsWith("GET_USERS_SUCCESS")) {
            String[] parts = message.split(";", 3);
            if (parts.length == 3) {
                String usersJson = parts[1];
                int userId = Integer.parseInt(parts[2]);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<Map<String, Object>> users = objectMapper.readValue(usersJson, new TypeReference<List<Map<String, Object>>>() {});
                    Platform.runLater(() -> {
                        UserListView userListView = new UserListView(users, client, userId);
                        BorderPane layout = (BorderPane) primaryStage.getScene().getRoot();
                        layout.setCenter(userListView.getView());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to parse users JSON: " + usersJson);
                }
            } else {
                System.out.println("Invalid users message format: " + message);
            }
        } else {
            System.out.println("Failed to get users: " + message);
        }
    }

    /**
     * Handles the update of a user's role based on the received message.
     *
     * @param message the update role message
     */
    public void handleUpdateRole(String message) {
        if (message.startsWith("UPDATE_ROLE_SUCCESS")) {
            String[] parts = message.split(";", 2);
            if (parts.length == 2) {
                int userId = Integer.parseInt(parts[1]);
                Platform.runLater(() -> {
                    client.sendMessage("/GET_USERS;" + userId);
                });
            } else {
                System.out.println("Invalid update role message format: " + message);
            }
        } else {
            System.out.println("Failed to update role: " + message);
        }
    }

    /**
     * Handles the deletion of a user based on the received message.
     *
     * @param message the delete user message
     */
    public void handleDeleteUser(String message) {
        if (message.startsWith("DELETE_USER_SUCCESS")) {
            String[] parts = message.split(";", 2);
            if (parts.length == 2) {
                int userId = Integer.parseInt(parts[1]);
                Platform.runLater(() -> {
                    client.sendMessage("/GET_USERS;" + userId);
                });
            } else {
                System.out.println("Invalid delete user message format: " + message);
            }
        } else {
            System.out.println("Failed to delete user: " + message);
        }
    }
}