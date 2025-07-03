package pt.estg.sd.client_sd.Views;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.estg.sd.client_sd.client.Client;

import java.util.List;
import java.util.Map;

/**
 * Represents the view for displaying and managing the list of users.
 */
public class UserListView {

    private List<Map<String, Object>> users;
    private Client client;
    private int userId;

    /**
     * Constructs a UserListView with the specified users, client, and user ID.
     *
     * @param users the list of users
     * @param client the client instance
     * @param userId the user ID
     */
    public UserListView(List<Map<String, Object>> users, Client client, int userId) {
        this.users = users;
        this.client = client;
        this.userId = userId;
    }

    /**
     * Returns the view node for displaying the list of users.
     *
     * @return the view node
     */
    public VBox getView() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(new Label("Lista de Utilizadores"));

        for (Map<String, Object> user : users) {
            HBox userBox = new HBox(10);
            userBox.setAlignment(Pos.CENTER);

            Label userInfoLabel = new Label("ID: " + user.get("id") + ", Username: " + user.get("username") + ", Email: " + user.get("email") + ", Role: ");

            ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("CANDIDATO", "COORDENADOR_DE_EMERGENCIA", "SUPERVISOR_DE_EMERGENCIA", "AGENTE_DE_EMERGENCIA"));
            roleComboBox.setValue((String) user.get("role"));

            Button updateButton = new Button("Update");
            updateButton.setOnAction(e -> {
                try {
                    user.put("role", roleComboBox.getValue());
                    client.sendMessage("/UPDATE_ROLE;" + user.get("id") + ";" + roleComboBox.getValue());
                    userInfoLabel.setText("ID: " + user.get("id") + ", Username: " + user.get("username") + ", Email: " + user.get("email") + ", Role: " + user.get("role"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            userBox.getChildren().addAll(userInfoLabel, roleComboBox, updateButton);

            if (!user.get("id").equals(userId)) {
                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(e -> {
                    try {
                        client.sendMessage("/DELETE_USER;" + user.get("id"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                userBox.getChildren().add(deleteButton);
            }


            if (!user.get("id").equals(userId)) {
                Button chatButton = new Button("Chat");
                chatButton.setOnAction(e -> {
                    try {
                        client.sendMessage("/CREATE_CHAT;" + userId + ";" + user.get("id"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                userBox.getChildren().add(chatButton);
            }

            layout.getChildren().add(userBox);
        }

        return layout;
    }
}