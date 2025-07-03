package pt.estg.sd.client_sd.client;

import javafx.application.Platform;
import pt.estg.sd.client_sd.Role;
import pt.estg.sd.client_sd.Views.LoginView;
import pt.estg.sd.client_sd.Views.MainView;
import javafx.stage.Stage;

/**
 * Handles authentication-related actions such as login and registration.
 */
public class AuthHandler {
    private Client client;
    private Stage primaryStage;

    /**
     * Constructs an AuthHandler with the specified client and primary stage.
     *
     * @param client the client instance
     * @param primaryStage the primary stage of the application
     */
    public AuthHandler(Client client, Stage primaryStage) {
        this.client = client;
        this.primaryStage = primaryStage;
    }

    /**
     * Handles the login process based on the received message.
     *
     * @param message the login message
     */
    public void handleLogin(String message) {
        if (message.startsWith("LOGIN_SUCCESS")) {
            String[] parts = message.split(";");
            if (parts.length == 3) {
                int id = Integer.parseInt(parts[1]);
                Role role = Role.valueOf(parts[2]);

                Platform.runLater(() -> {
                    MainView mainView = new MainView(role, client, id);
                    mainView.start(primaryStage);
                });
                new Thread(client::listenForBroadcastMessages).start();
            } else {
                System.out.println("Invalid login message format: " + message);
            }
        } else {
            System.out.println("Login failed: " + message);
        }
    }

    /**
     * Handles the registration process based on the received message.
     *
     * @param message the registration message
     */
    public void handleRegister(String message) {
        if ("REGISTER_SUCCESS".equals(message)) {
            Platform.runLater(() -> {
                LoginView loginView = new LoginView(client);
                loginView.start(primaryStage);
            });
        } else {
            System.out.println("Registo falhou: " + message);
        }
    }

    public void handleLogout(String message) {
        if ("LOGOUT_SUCCESS".equals(message)) {
            Platform.runLater(() -> {
                LoginView loginView = new LoginView(client);
                loginView.start(primaryStage);
            });
        } else {
            System.out.println("Logout falhou: " + message);
        }
    }
}