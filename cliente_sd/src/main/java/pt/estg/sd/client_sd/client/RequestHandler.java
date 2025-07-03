package pt.estg.sd.client_sd.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pt.estg.sd.client_sd.Views.CentralEmergencyView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles request-related actions such as retrieving, accepting, and declining requests.
 */
public class RequestHandler {
    private Client client;
    private Stage primaryStage;

    /**
     * Constructs a RequestHandler with the specified client and primary stage.
     *
     * @param client the client instance
     * @param primaryStage the primary stage of the application
     */
    public RequestHandler(Client client, Stage primaryStage) {
        this.client = client;
        this.primaryStage = primaryStage;
    }

    /**
     * Handles the creation of a new request based on the received message.
     *
     * @param message the new request message
     */
    public void handleNewRequest(String message) {
        if (message.startsWith("NEW_REQUEST_SUCCESS")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New Request");
                alert.setHeaderText(null);
                alert.setContentText("Novo pedido na central");
                alert.showAndWait();
                client.notifyNewRequestListeners();
            });
        }
    }

    /**
     * Handles the retrieval of requests based on the received message.
     *
     * @param message the requests message
     */
    public void handleRequests(String message) {
        if (message.startsWith("GET_REQUESTS_SUCCESS")) {
            String[] parts = message.split(";", 3);
            if (parts.length == 3) {
                String requestsJson = parts[1];
                int id = Integer.parseInt(parts[2]);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<Map<String, Object>> requests = objectMapper.readValue(requestsJson, new TypeReference<List<Map<String, Object>>>() {});
                    Platform.runLater(() -> {
                        CentralEmergencyView central = new CentralEmergencyView(requests, client, id);
                        BorderPane layout = (BorderPane) primaryStage.getScene().getRoot();
                        layout.setCenter(central.getView());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to parse requests JSON: " + requestsJson);
                }
            } else {
                System.out.println("Invalid requests message format: " + message);
            }
        } else {
            System.out.println("Failed to get requests: " + message);
        }
    }

    /**
     * Handles the acceptance of a request based on the received message.
     *
     * @param message the accept request message
     */
    public void handleRequestsAccept(String message) {
        if (message.startsWith("ACCEPT_REQUEST_SUCCESS")) {
            String[] parts = message.split(";", 2);
            if (parts.length == 2) {
                String requestType = parts[1];
                Platform.runLater(() -> {
                    client.sendMessage("/GET_REQUESTS");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Request Accepted");
                    alert.setHeaderText(null);
                    alert.setContentText(requestType + " ACEITE");
                    alert.showAndWait();
                });
            } else {
                System.out.println("Invalid ACCEPT_REQUEST_SUCCESS message format: " + message);
            }
        } else {
            System.out.println("Failed to accept request: " + message);
        }
    }

    /**
     * Handles the decline of a request based on the received message.
     *
     * @param message the decline request message
     */
    public void handleRequestsDecline(String message) {
        if (message.startsWith("DECLINE_REQUEST_SUCCESS")) {
            Platform.runLater(() -> {
                client.sendMessage("/GET_REQUESTS");
            });
        } else {
            System.out.println("Failed to decline request: " + message);
        }
    }
}