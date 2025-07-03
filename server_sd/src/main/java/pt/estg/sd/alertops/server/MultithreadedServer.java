package pt.estg.sd.alertops.server;

import lombok.Getter;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static pt.estg.sd.alertops.services.RequestService.fetchRecentRequests;

/**
 * MultithreadedServer class that handles multiple client connections using a thread pool.
 */
public class MultithreadedServer {
    private static final int PORT = 12345;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    static final List<ClientHandler> authenticatedClients = new CopyOnWriteArrayList<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long initialDelay = 300;
    private static final long period = 300;
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;
    static final Map<String, String> groupMulticastMap = new HashMap<>();
    static int broadcastPort = 5000;

    /**
     * Main method to start the server.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName("127.0.0.1"))) {
            System.out.println("Server is listening on localhost, port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, new MultithreadedServer());
                clients.add(clientHandler);
                threadPool.execute(clientHandler);

                scheduler.scheduleAtFixedRate(() -> sendMessageBROADCAST( generateMessageReport()), initialDelay, period, timeUnit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to an authenticated client.
     * @param username The username of the client.
     * @param message The message to send.
     */
    public static void sendMessageToAuthenticatedClient(String username, String message) {
        for (ClientHandler client : authenticatedClients) {
            if (client.getUsername().equals(username)) {
                client.sendMessage(message);
                break;
            }
        }
    }

    /**
     * Generates a report of messages.
     * @return The generated message report.
     */
    public static String generateMessageReport() {
        StringBuilder report = new StringBuilder();
        report.append("REPORT;");
        report.append(getAuthenticatedClients());
        report.append(fetchRecentRequests(period, timeUnit));

        return report.toString();
    }

    /**
     * Sends a multicast message.
     * @param message The message to send.
     */
    public static void sendMessageMULTICAST(String message, String multicastIP, int multicastPort) {
        try (DatagramSocket multicastSocket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName(multicastIP);
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), group, multicastPort);
            multicastSocket.send(packet);
            System.out.println("Multicast address: " + multicastIP + " Multicast port: " + multicastPort);
            System.out.println("Multicast message sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageBROADCAST(String message) {
        try (DatagramSocket broadcastSocket = new DatagramSocket()) {
            broadcastSocket.setBroadcast(true);
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), broadcastAddress, broadcastPort);
            broadcastSocket.send(packet);
            System.out.println("Broadcast port: " + broadcastPort);
            System.out.println("Broadcast message sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a client from the list of clients.
     * @param clientHandler The client handler to remove.
     */
    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        authenticatedClients.remove(clientHandler);
    }

    /**
     * Adds a client to the list of authenticated clients.
     * @param clientHandler The client handler to add.
     */
    public static void addAuthenticatedClient(ClientHandler clientHandler) {
        authenticatedClients.add(clientHandler);
    }

    /**
     * Gets a list of authenticated clients.
     * @return A string representation of authenticated clients.
     */
    public static String getAuthenticatedClients() {
        StringBuilder listLoggedInClients = new StringBuilder();
        for (ClientHandler client : authenticatedClients) {
            listLoggedInClients.append(client.toString()).append(";");
        }
        return listLoggedInClients.toString();
    }
}



/**
 * ClientHandler class that handles communication with a single client.
 */
class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Protocol protocol;
    private MultithreadedServer server;
    private boolean authenticated = false;
    /**
     * -- GETTER --
     *  Gets the username of the client.
     *
     * @return The username.
     */
    @Getter
    private String username;

    /**
     * Constructor for ClientHandler.
     * @param socket The client socket.
     * @param server The server instance.
     */
    public ClientHandler(Socket socket, MultithreadedServer server) {
        this.socket = socket;
        this.server = server;
        try {
            this.protocol = new Protocol(this, server);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The run method to handle client communication.
     */
    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received from client: " + message);
                if (message.startsWith("/LOGIN") && !authenticated) {
                    handleLogin(message);
                }else if (message.startsWith("/REGISTER") && !authenticated) {
                    protocol.processMessage(message);
                } else if (authenticated && !message.startsWith("/LOGOUT")) {
                    protocol.processMessage(message);
                } else if(message.startsWith("/LOGOUT") && authenticated ){
                    authenticated = false;
                    MultithreadedServer.removeClient(this);
                        sendMessage("LOGOUT_SUCCESS");
                }else {
                    sendMessage("ERROR: Not authenticated");
                }
            }
            System.out.println("Client connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MultithreadedServer.removeClient(this);
        }
    }

    /**
     * Handles client login.
     * @param message The login message.
     * @throws SQLException If a database access error occurs.
     */
    private void handleLogin(String message) throws SQLException {
        String[] parts = message.split(";");
        this.username = parts[1];
        authenticated = true;
        protocol.processMessage(message);
        MultithreadedServer.addAuthenticatedClient(this);
    }

    /**
     * Sends a message to the client.
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }

    /**
     * Returns a string representation of the client handler.
     * @return A string representation of the client handler.
     */
    @Override
    public String toString() {
        return "ClientHandler{" +
                "IP=" + socket.getInetAddress().getHostAddress() +
                ", username=" + username +
                ", authenticated=" + authenticated +
                '}';
    }
}