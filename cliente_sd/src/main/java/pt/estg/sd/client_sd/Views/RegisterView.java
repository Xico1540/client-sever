package pt.estg.sd.client_sd.Views;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.estg.sd.client_sd.Role;
import pt.estg.sd.client_sd.client.Client;

/**
 * Represents the view for user registration.
 */
public class RegisterView {

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField emailField;
    private ComboBox<Role> roleComboBox;
    private Label errorMessage;
    private Button registerButton;
    private Client client;

    /**
     * Constructs a RegisterView with the specified client.
     *
     * @param client the client instance
     */
    public RegisterView(Client client) {
        this.client = client;
    }

    /**
     * Starts the registration view on the specified primary stage.
     *
     * @param primaryStage the primary stage of the application
     */
    public void start(Stage primaryStage) {
        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        emailField = new TextField();
        emailField.setPromptText("Email");

        roleComboBox = new ComboBox<>(FXCollections.observableArrayList(Role.values()));
        roleComboBox.setPromptText("Role");

        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red;");

        registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            String role = roleComboBox.getValue() != null ? roleComboBox.getValue().name() : "";
            client.sendMessage("/REGISTER;" + usernameField.getText() + ";" + passwordField.getText() + ";" + emailField.getText() + ";" + role);
        });

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(new Label("alertOps - Register"), usernameField, passwordField, emailField, roleComboBox, registerButton, errorMessage);

        Scene scene = new Scene(layout, 300, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Register - alertOps");
        primaryStage.show();
    }
}