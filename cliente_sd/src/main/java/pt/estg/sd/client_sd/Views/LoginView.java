package pt.estg.sd.client_sd.Views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.estg.sd.client_sd.client.Client;

/**
 * Represents the view for user login.
 */
public class LoginView {


    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 400;

    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorMessage;
    private Button loginButton;
    private Button registerButton;
    private Client client;

    /**
     * Constructs a LoginView with the specified client.
     *
     * @param client the client instance
     */
    public LoginView(Client client) {
        this.client = client;
    }

    /**
     * Starts the login view on the specified primary stage.
     *
     * @param primaryStage the primary stage of the application
     */
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("alertOps - Login");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        titleLabel.setAlignment(Pos.TOP_CENTER);

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(300);
        usernameField.setPrefHeight(40);

        HBox usernameBox = new HBox(usernameField);
        usernameBox.setAlignment(Pos.CENTER);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(300);
        passwordField.setPrefHeight(40);

        HBox passwordBox = new HBox(passwordField);
        passwordBox.setAlignment(Pos.CENTER);

        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red;");

        loginButton = new Button("Login");
        loginButton.setOnAction(e -> client.sendMessage("/LOGIN;" + usernameField.getText() + ";" + passwordField.getText()));

        registerButton = new Button("Register");
        registerButton.setOnAction(e -> handleRegister(primaryStage));

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, usernameBox, passwordBox, loginButton, registerButton, errorMessage);

        Scene scene = new Scene(layout, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/pt/estg/sd/client_sd/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login - alertOps");
        primaryStage.show();
    }

    /**
     * Handles the registration process by switching to the register view.
     *
     * @param primaryStage the primary stage of the application
     */
    private void handleRegister(Stage primaryStage) {
        RegisterView registerView = new RegisterView(client);
        registerView.start(primaryStage);
    }
}