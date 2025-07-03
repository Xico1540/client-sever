package pt.estg.sd.client_sd.Views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.estg.sd.client_sd.Role;
import pt.estg.sd.client_sd.client.Client;

/**
 * Represents the main view of the application.
 */
public class MainView {

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;

    private Role role;
    private Client client;
    private int userId;

    /**
     * Constructs a MainView with the specified role, client, and user ID.
     *
     * @param role the role of the user
     * @param client the client instance
     * @param userId the user ID
     */
    public MainView(Role role, Client client, int userId) {
        this.role = role;
        this.client = client;
        this.userId = userId;
    }

    /**
     * Starts the main view on the specified primary stage.
     *
     * @param primaryStage the primary stage of the application
     */
    public void start(Stage primaryStage) {
        VBox navBar = new VBox(10);
        navBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/pt/estg/sd/client_sd/logo.png")));
        logo.setFitWidth(100);
        logo.setFitHeight(100);

        BorderPane layout = new BorderPane();

        HBox topLayout = new HBox(10);
        topLayout.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        topLayout.getChildren().addAll(logo, titleLabel);
        layout.setTop(topLayout);

        layout.setLeft(navBar);

        Button communicationChannelsButton = createStyledButton("Canais de Comunicação");
        communicationChannelsButton.setOnAction(e -> client.sendMessage("/GET_CHANNELS;" + userId));

        Button userListButton = createStyledButton("Lista de Utilizadores");
        userListButton.setOnAction(e -> client.sendMessage("/GET_USERS;" + userId));


        Button emergencySendButton = createStyledButton("Envio de Emergência");
        emergencySendButton.setOnAction(e -> client.sendMessage("/OPEN_EMERGENCY_SEND_VIEW;" + userId));





        if (role != Role.CANDIDATO) {
            Button emergencyListButton = createStyledButton("Central de Emergências");
            emergencyListButton.setOnAction(e -> client.sendMessage("/GET_REQUESTS;" + userId));
            navBar.getChildren().add(emergencyListButton);
        }

        Button logoutButton = createStyledButton("Logout");
        logoutButton.setOnAction(e -> {
            client.sendMessage("/LOGOUT;" + userId);
        });

        navBar.getChildren().addAll(
                communicationChannelsButton,
                userListButton,
                emergencySendButton,
                logoutButton
        );

        Scene scene = new Scene(layout, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard - alertOps");
        primaryStage.show();
    }

    /**
     * Creates a styled button with the specified text.
     *
     * @param text the text of the button
     * @return the styled button
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-pref-width: 200px;");
        return button;
    }
}