package pt.estg.sd.alertops.server;

import pt.estg.sd.alertops.utils.RequestType;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Protocol class that processes incoming messages and routes them to the appropriate handlers.
 */
public class Protocol {

    private ClientHandler clientHandler;
    protected MultithreadedServer server;

    /**
     * Constructor for Protocol.
     * @param clientHandler The client handler.
     * @param server The server instance.
     * @throws IOException If an I/O error occurs.
     */
    public Protocol(ClientHandler clientHandler, MultithreadedServer server) throws IOException {
        this.clientHandler = clientHandler;
        this.server = server;
    }

    /**
     * Processes the incoming message and routes it to the appropriate handler based on the request type.
     * @param requestMessage The incoming request message.
     * @return The response message.
     * @throws SQLException If a database access error occurs.
     */
    protected synchronized String processMessage(String requestMessage) throws SQLException {
        RequestType requestType;

        if (requestMessage.startsWith("/")) {
            requestMessage = requestMessage.substring(1);
        }

        try {
            requestType = RequestType.valueOf(requestMessage.split(";")[0]);
        } catch (IllegalArgumentException e) {
            return e.getMessage().toString();
        }

        switch (requestType) {
            case LOGIN:
                return AuthHandler.loginHandler(requestMessage, clientHandler);
            case REGISTER:
                return AuthHandler.registerHandler(requestMessage, clientHandler);
            case GET_USERS:
                return UserHandler.getUsersHandler(requestMessage, clientHandler);
            case UPDATE_ROLE:
                return UserHandler.updateRoleHandler(requestMessage, clientHandler);
            case DELETE_USER:
                return UserHandler.deleteUserHandler(requestMessage, clientHandler);
            case OPEN_EMERGENCY_SEND_VIEW:
                return RequestHandler.openSendViewHandler(requestMessage, clientHandler);
            case NEW_REQUEST:
                return RequestHandler.newRequestHandler(requestMessage, clientHandler);
            case GET_REQUESTS:
                return RequestHandler.getRequestsHandler(requestMessage, clientHandler);
            case ACCEPT_REQUEST:
                return RequestHandler.acceptRequestHandler(requestMessage, clientHandler);
            case DECLINE_REQUEST:
                return RequestHandler.declineRequestHandler(requestMessage, clientHandler);
            case GET_CHANNELS:
                return ChannelsHandler.getChannelsHandler(requestMessage, clientHandler);
            case DELETE_CHANNEL:
                return ChannelsHandler.deleteChannelHandler(requestMessage, clientHandler);
            case ENTER_CHAT:
                return ChannelsHandler.enterChatHandler(requestMessage, clientHandler);
            case CREATE_GROUP:
                return ChannelsHandler.createGroupHandler(requestMessage, clientHandler);
            case CREATE_CHAT:
                return ChannelsHandler.createChatHandler(requestMessage, clientHandler);
            case SEND_MESSAGE:
                return ChannelsHandler.sendMessageHandler(requestMessage, clientHandler);
            default:
                return "ERROR: Invalid request type";
        }
    }
}