package pt.estg.sd.client_sd.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pt.estg.sd.client_sd.Views.EmergencySendView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a client that connects to a server and handles communication.
 */
public class Client {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Stage primaryStage;
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private List<Runnable> newRequestListeners = new ArrayList<>();

    private AuthHandler authHandler;
    private UserHandler userHandler;
    private ChannelHandler channelHandler;
    private RequestHandler requestHandler;

    private String MULTICAST_IP ;
    private int MULTICAST_PORT ;

    /**
     * Constructs a Client with the specified host, port, and primary stage.
     *
     * @param host the server host
     * @param port the server port
     * @param primaryStage the primary stage of the application
     */
    public Client(String host, int port, Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            socket = new Socket(host, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this::listenForMessages).start();
            new Thread(this::listenForMulticastMessages).start();

            authHandler = new AuthHandler(this, primaryStage);
            userHandler = new UserHandler(this, primaryStage);
            channelHandler = new ChannelHandler(this, primaryStage);
            requestHandler = new RequestHandler(this, primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }

    /**
     * Listens for messages from the server.
     */
    private void listenForMessages() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                System.out.println("Received from server: " + message);
                handleMessage(message);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading message", e);
        }
    }

    /**
     * Listens for multicast messages.
     */
    private void listenForMulticastMessages() {
        try (MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            multicastSocket.joinGroup(group);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received multicast message: " + message);
                handleMulticastMessage(message);
            }
        } catch (IOException e) {
            System.out.println("Waiting for multicast messages");
        }
    }

    /**
     * Handles a multicast message.
     *
     * @param message the multicast message
     */
    private void handleMulticastMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        String command = message.split(";")[0];

        if (command.startsWith("MESSAGE_RECEIVED_SUCCESS")) {
            channelHandler.handleMessageReceived(message);
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Multicast Message");
                alert.setHeaderText(null);
                alert.setContentText("Received multicast message: " + message);
                alert.showAndWait();
            });
            logger.warning("Unknown multicast command: " + command);
        }
    }

    private void handleBroadcastMessage(String message) {
        String[] parts = message.split(";", 2);
        if (parts.length == 2) {
            String reportContent = parts[1];
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Broadcast Message Report");
                alert.setHeaderText(null);
                alert.setContentText("Received report: " + reportContent);
                alert.showAndWait();
            });
        } else {
            logger.warning("Invalid report message format: " + message);
        }
    }

    void listenForBroadcastMessages() {
        try (DatagramSocket socket = new DatagramSocket(null)) {
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(5000));
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received broadcast message: " + message);
                handleBroadcastMessage(message);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error listening for broadcast messages", e);
        }
    }

    public void leaveGroup() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.leaveGroup(group);
            multicastSocket.close();
            System.out.println("Left multicast group: " + MULTICAST_IP + ":" + MULTICAST_PORT);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error leaving multicast group", e);
        }
    }

    /**
     * Handles a message from the server.
     *
     * @param message the message from the server
     */
    private void handleMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        String command = message.split(";")[0];
        switch (command) {
            case "LOGIN_SUCCESS", "LOGIN_FAILED":
                authHandler.handleLogin(message);
                break;
                case "LOGOUT_SUCCESS":
                authHandler.handleLogout(message);
                break;
            case "REGISTER_SUCCESS", "REGISTER_FAILED":
                authHandler.handleRegister(message);
                break;
            case "GET_USERS_SUCCESS", "GET_USERS_FAILED":
                userHandler.handleUsers(message);
                break;
            case "UPDATE_ROLE_SUCCESS", "UPDATE_ROLE_FAILED":
                userHandler.handleUpdateRole(message);
                break;
            case "DELETE_USER_SUCCESS", "DELETE_USER_FAILED":
                userHandler.handleDeleteUser(message);
                break;
            case "OPEN_SUCCESS", "OPEN_FAILED":
                handleOpenSendView(message);
                break;
            case "NEW_REQUEST_SUCCESS", "NEW_REQUEST_FAILURE", "INVALID_REQUEST":
                requestHandler.handleNewRequest(message);
                break;
            case "GET_REQUESTS_SUCCESS", "GET_REQUESTS_FAILED":
                requestHandler.handleRequests(message);
                break;
            case "ACCEPT_REQUEST_SUCCESS", "ACCEPT_REQUEST_FAILED":
                requestHandler.handleRequestsAccept(message);
                break;
            case "DECLINE_REQUEST_SUCCESS", "DECLINE_REQUEST_FAILED":
                requestHandler.handleRequestsDecline(message);
                break;
            case "GET_CHANNELS_SUCCESS", "GET_CHANNELS_FAILED":
                channelHandler.handleChannels(message);
                break;
            case "DELETE_CHANNEL_SUCCESS", "DELETE_CHANNEL_FAILED":
                channelHandler.handleDeleteChannels(message);
                break;
            case "ENTER_CHAT_SUCCESS", "ENTER_CHAT_FAILED", "ENTER_GROUP_CHAT_SUCCESS":
                channelHandler.handleEnterChat(message);
                break;
            case "CREATE_GROUP_SUCCESS", "CREATE_GROUP_FAILED":
                channelHandler.handleCreateGroup(message);
                break;
            case "CHAT_EXISTS", "CREATE_CHAT_SUCCESS", "CREATE_CHAT_FAILED":
                channelHandler.handleCreateChat(message);
                break;
            case "SEND_MESSAGE_SUCCESS", "SEND_MESSAGE_FAILED":
                channelHandler.handleSendMessage(message);
                break;
            case "MESSAGE_RECEIVED_SUCCESS":
                channelHandler.handleMessageReceived(message);
                break;
            case "REPORT":
                handleReport(message);
                break;
            default:
                logger.warning("Unknown command: " + command);
                break;
        }
    }

    /**
     * Adds a new request listener.
     *
     * @param listener the listener to add
     */
    public void addNewRequestListener(Runnable listener) {
        newRequestListeners.add(listener);
    }

    /**
     * Notifies all new request listeners.
     */
    public void notifyNewRequestListeners() {
        for (Runnable listener : newRequestListeners) {
            listener.run();
        }
    }

    /**
     * Handles a report message.
     *
     * @param message the report message
     */
    private void handleReport(String message) {
        String[] parts = message.split(";", 2);
        if (parts.length == 2) {
            String reportContent = parts[1];
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Server Report");
                alert.setHeaderText(null);
                alert.setContentText("Received report: " + reportContent);
                alert.showAndWait();
            });
        } else {
            logger.warning("Invalid report message format: " + message);
        }
    }

    /**
     * Handles opening the emergency send view.
     *
     * @param message the open send view message
     */
    public void handleOpenSendView(String message) {
        if (message.startsWith("OPEN_SUCCESS")) {
            String[] parts = message.split(";");
            if (parts.length == 2) {
                int id = Integer.parseInt(parts[1]);
                Platform.runLater(() -> {
                    EmergencySendView emergencySendView = new EmergencySendView(this, id);
                    BorderPane layout = (BorderPane) primaryStage.getScene().getRoot();
                    layout.setCenter(emergencySendView.getView());
                });
            } else {
                System.out.println("Invalid OPEN_SUCCESS message format: " + message);
            }
        } else {
            System.out.println("Failed to open emergency send view: " + message);
        }
    }
    public void setMulticastConfig(String ip, int port) {
        System.out.println("Setting multicast config: " + ip + ":" + port);
        this.MULTICAST_IP = ip;
        this.MULTICAST_PORT = port;
    }

    public void startMulticastListener() {
        new Thread(this::listenForMulticastMessages).start();
    }


}