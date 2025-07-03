package pt.estg.sd.client_sd;

import javafx.application.Application;
import javafx.stage.Stage;
import pt.estg.sd.client_sd.Views.LoginView;
import pt.estg.sd.client_sd.client.Client;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        Client client = new Client("localhost", 12345, primaryStage);
        LoginView loginView = new LoginView(client);
        loginView.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}