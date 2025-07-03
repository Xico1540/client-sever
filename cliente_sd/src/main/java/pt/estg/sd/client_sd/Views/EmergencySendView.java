package pt.estg.sd.client_sd.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pt.estg.sd.client_sd.Type;
import pt.estg.sd.client_sd.client.Client;

/**
 * Represents the view for sending emergency requests.
 */
public class EmergencySendView {

    private Client client;
    private int userId;

    /**
     * Constructs an EmergencySendView with the specified client and user ID.
     *
     * @param client the client instance
     * @param userId the user ID
     */
    public EmergencySendView(Client client, int userId) {
        this.client = client;
        this.userId = userId;
    }

    /**
     * Returns the view node for sending emergency requests.
     *
     * @return the view node
     */
    public Node getView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Enviar Pedido");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        titleLabel.setAlignment(Pos.CENTER);

        Label selectLabel = new Label("Selecione o tipo de pedido:");
        selectLabel.setAlignment(Pos.CENTER);

        ComboBox<Type> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().setAll(Type.values());

        Button sendButton = new Button("Enviar pedido");
        sendButton.setOnAction(e -> {
            try {
                long timestamp = System.currentTimeMillis();
                client.sendMessage("/NEW_REQUEST;" + typeComboBox.getValue() + ";" + userId + ";" + timestamp);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        layout.getChildren().addAll(titleLabel, selectLabel, typeComboBox, sendButton);

        return layout;
    }
}