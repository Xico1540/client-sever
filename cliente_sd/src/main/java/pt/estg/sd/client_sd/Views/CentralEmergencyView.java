package pt.estg.sd.client_sd.Views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.estg.sd.client_sd.client.Client;

import java.util.List;
import java.util.Map;

/**
 * Represents the view for displaying and handling emergency requests.
 */
public class CentralEmergencyView {

    private VBox layout;
    private List<Map<String, Object>> requests;
    private Client client;
    private int id;

    /**
     * Constructs a CentralEmergencyView with the specified requests, client, and user ID.
     *
     * @param requests the list of emergency requests
     * @param client the client instance
     * @param userId the user ID
     */
    public CentralEmergencyView(List<Map<String, Object>> requests, Client client, int userId) {
        this.requests = requests;
        this.client = client;
        this.id = userId;
        this.client.addNewRequestListener(this::updateView);
    }

    /**
     * Updates the view with the current list of requests.
     */
    private void updateView() {
        layout.getChildren().clear();

        for (Map<String, Object> request : requests) {
            HBox requestBox = new HBox(10);
            Label requestLabel = new Label(request.get("type").toString());
            Button acceptButton = new Button("Accept");
            Button declineButton = new Button("Decline");

            acceptButton.setOnAction(e -> client.sendMessage("/ACCEPT_REQUEST;" + request.get("id") + ";" + id));
            declineButton.setOnAction(e -> client.sendMessage("/DECLINE_REQUEST;" + request.get("id") + ";" + id));

            requestBox.getChildren().addAll(requestLabel, acceptButton, declineButton);
            layout.getChildren().add(requestBox);
        }
    }

    /**
     * Returns the view node for displaying the emergency requests.
     *
     * @return the view node
     */
    public Node getView() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().add(new Label("Pedidos de Emergência"));

        updateView();

        return layout;
    }
}