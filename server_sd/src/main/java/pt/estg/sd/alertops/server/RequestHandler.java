package pt.estg.sd.alertops.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pt.estg.sd.alertops.components.Requests;
import pt.estg.sd.alertops.services.RequestService;
import pt.estg.sd.alertops.utils.ResponseType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static pt.estg.sd.alertops.server.MultithreadedServer.authenticatedClients;
import static pt.estg.sd.alertops.server.MultithreadedServer.sendMessageBROADCAST;

/**
 * RequestHandler class that handles various request-related operations such as creating, accepting, and declining requests.
 */
public class RequestHandler {

    /**
     * Handles the request to open the send view.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String openSendViewHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        int id = Integer.parseInt(requestParts[1]);
        clientHandler.sendMessage(ResponseType.OPEN_SUCCESS.name() + ";" + id);
        return ResponseType.OPEN_SUCCESS.name();
    }

    /**
     * Handles the request to create a new request.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String newRequestHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        if (requestParts.length != 4) {
            return ResponseType.INVALID_REQUEST.name();
        }

        try {
            String typeString = requestParts[1];
            int userId = Integer.parseInt(requestParts[2]);
            long timestampMillis = Long.parseLong(requestParts[3]);
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault());

            Requests.Type type = Requests.Type.valueOf(typeString);
            RequestService requestService = new RequestService();
            Requests.Status status = Requests.Status.PENDING;

            requestService.createRequest(userId, status, type, timestamp);

          sendMessageToAllAuthenticatedClients(ResponseType.NEW_REQUEST_SUCCESS.name());
            return ResponseType.NEW_REQUEST_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseType.NEW_REQUEST_FAILURE.name();
        }
    }

    public static void sendMessageToAllAuthenticatedClients(String message) {
        for (ClientHandler client : authenticatedClients) {
            client.sendMessage(message);
        }
    }

    /**
     * Handles the request to get pending requests.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String getRequestsHandler(String requestMessage, ClientHandler clientHandler) {
        try {
            String[] requestParts = requestMessage.split(";");
            if (requestParts.length < 2) {
                return ResponseType.INVALID_REQUEST.name();
            }
            int id = Integer.parseInt(requestParts[1]);

            List<Requests> requests = RequestService.getPendingRequests();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String requestsJson = objectMapper.writeValueAsString(requests);

            clientHandler.sendMessage(ResponseType.GET_REQUESTS_SUCCESS.name() + ";" + requestsJson + ";" + id);
            return ResponseType.GET_REQUESTS_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.GET_REQUESTS_FAILED.name();
    }

    /**
     * Handles the request to accept a request.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String acceptRequestHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        int id = Integer.parseInt(requestParts[1]);
        int userId = Integer.parseInt(requestParts[2]);

        try {
            Requests request = RequestService.acceptRequest(id, userId);
            if (request == null) {
                clientHandler.sendMessage(ResponseType.ACCEPT_REQUEST_FAILED.name());
                return ResponseType.ACCEPT_REQUEST_FAILED.name();
            }
            sendMessageBROADCAST(ResponseType.ACCEPT_REQUEST_SUCCESS.name() + ";" + request.getType());
            return ResponseType.ACCEPT_REQUEST_SUCCESS.name() + ";" + request.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseType.ACCEPT_REQUEST_FAILED.name();
    }

    /**
     * Handles the request to decline a request.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String declineRequestHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        int id = Integer.parseInt(requestParts[1]);
        int userId = Integer.parseInt(requestParts[2]);

        try {
            if (!RequestService.declineRequest(id, userId)) {
                clientHandler.sendMessage(ResponseType.DECLINE_REQUEST_FAILED.name());
                return ResponseType.DECLINE_REQUEST_FAILED.name();
            }
            clientHandler.sendMessage(ResponseType.DECLINE_REQUEST_SUCCESS.name());
            return ResponseType.DECLINE_REQUEST_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.DECLINE_REQUEST_FAILED.name();
    }
}