package pt.estg.sd.alertops.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.estg.sd.alertops.components.Channel;
import pt.estg.sd.alertops.components.Message;
import pt.estg.sd.alertops.components.User;
import pt.estg.sd.alertops.services.ChannelService;
import pt.estg.sd.alertops.services.MessageService;
import pt.estg.sd.alertops.services.UserService;
import pt.estg.sd.alertops.utils.ResponseType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pt.estg.sd.alertops.server.MultithreadedServer.groupMulticastMap;
import static pt.estg.sd.alertops.services.UserService.getNameById;

/**
 * ChannelsHandler class that handles various channel-related requests.
 */
public class ChannelsHandler {

    /**
     * Handles the request to get channels for a user.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String getChannelsHandler(String requestMessage, ClientHandler clientHandler) {
        try {
            String[] requestParts = requestMessage.split(";");
            if (requestParts.length < 2) {
                return ResponseType.INVALID_REQUEST.name();
            }
            int userId = Integer.parseInt(requestParts[1]);
            List<Channel> channels = ChannelService.getChannelsFromDatabase(userId);
            List<User> users = ChannelService.getUsersFromDatabase();

            ObjectMapper objectMapper = new ObjectMapper();
            String channelsJson = objectMapper.writeValueAsString(channels);
            String usersJson = objectMapper.writeValueAsString(users);

            clientHandler.sendMessage(ResponseType.GET_CHANNELS_SUCCESS.name() + ";" + userId + ";" + channelsJson + ";" + usersJson);
            return ResponseType.GET_CHANNELS_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.GET_CHANNELS_FAILED.name();
    }

    /**
     * Handles the request to delete a channel.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String deleteChannelHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        String channelName = requestParts[1];
        int userId = Integer.parseInt(requestParts[2]);

        try {
            ChannelService.deleteChannel(channelName);
            clientHandler.sendMessage(ResponseType.DELETE_CHANNEL_SUCCESS.name() + ";" + userId);
            return ResponseType.DELETE_CHANNEL_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.DELETE_CHANNEL_FAILED.name();
    }

    /**
     * Handles the request to enter a chat.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String enterChatHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        String channelName = requestParts[1];
        int userId = Integer.parseInt(requestParts[2]);

        try {
            int id = ChannelService.getchannelIdbyName(channelName);
            String username = getNameById(userId);
            List<Message> messages = MessageService.getMessagesForChannel(id);

            String multicastIPAndPort = "";

            ObjectMapper objectMapper = new ObjectMapper();
            String messagesJson = objectMapper.writeValueAsString(messages);

            if (!channelName.toLowerCase().startsWith("chat")) {
                // Check if the group already has a multicast IP and port
                if (groupMulticastMap.containsKey(channelName)) {
                    multicastIPAndPort = groupMulticastMap.get(channelName);
                } else {
                    // Generate a new multicast IP and port for the group
                    multicastIPAndPort = generateMulticastIPAndPort(channelName);
                    groupMulticastMap.put(channelName, multicastIPAndPort);
                }
                clientHandler.sendMessage(ResponseType.ENTER_GROUP_CHAT_SUCCESS.name() + ";" + userId + ";" + messagesJson + ";" + channelName + ";" + username + ";" + multicastIPAndPort);
            } else {
                clientHandler.sendMessage(ResponseType.ENTER_CHAT_SUCCESS.name() + ";" + userId + ";" + messagesJson + ";" + channelName + ";" + username);
            }

            return ResponseType.ENTER_CHAT_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.ENTER_CHAT_FAILED.name();
    }

    private static String generateMulticastIPAndPort(String channelName) {
        int hash = channelName.hashCode();
        int group1 = 224 + (Math.abs(hash) % 16); // Ensure the first octet is between 224 and 239
        int group2 = (Math.abs(hash) >> 8) & 0xFF;
        int group3 = (Math.abs(hash) >> 16) & 0xFF;
        int group4 = (Math.abs(hash) >> 24) & 0xFF;

        int port = 1024 + (Math.abs(hash) % (65535 - 1024));

        return String.format("%d.%d.%d.%d:%d", group1, group2, group3, group4, port);
    }

    /**
     * Handles the request to create a group.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String createGroupHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        String channelName = requestParts[1];
        String channelDescription = requestParts[2];
        String userIdsString = requestParts[3];
        int userId = Integer.parseInt(requestParts[4]);

        try {
            ChannelService.createChannel(channelName, channelDescription, userIdsString);
            clientHandler.sendMessage(ResponseType.CREATE_GROUP_SUCCESS.name() + ";" + userId);
            return ResponseType.CREATE_GROUP_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.CREATE_GROUP_FAILED.name();
    }

    /**
     * Handles the request to create a chat.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String createChatHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        int userId1 = Integer.parseInt(requestParts[1]);
        int userId2 = Integer.parseInt(requestParts[2]);

        try {
            if (ChannelService.channelExists(userId1, userId2)) {
                clientHandler.sendMessage(ResponseType.CHAT_EXISTS.name());
                return ResponseType.CHAT_EXISTS.name();
            } else {
                ChannelService.createChat(userId1, userId2);
                clientHandler.sendMessage(ResponseType.CREATE_CHAT_SUCCESS.name());
                return ResponseType.CREATE_CHAT_SUCCESS.name();
            }
        } catch (Exception e) {
            e.printStackTrace();
            clientHandler.sendMessage(ResponseType.CREATE_CHAT_FAILED.name());
            return ResponseType.CREATE_CHAT_FAILED.name();
        }
    }

    /**
     * Handles the request to send a message.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     * @throws SQLException If a database access error occurs.
     */
    static String sendMessageHandler(String requestMessage, ClientHandler clientHandler) throws SQLException {
        String[] requestParts = requestMessage.split(";");
        String username = requestParts[1];
        String channelName = requestParts[2];
        String messageContent = requestParts[3];
        long timestamp = Long.parseLong(requestParts[4]);

        try {
            if (channelName.startsWith("Chat")) {
                int userId = UserService.getIdByUsername(username);
                int channelId = ChannelService.getChannelIdByName(channelName);
                List<User> users = ChannelService.getUsersByChannelId(channelId);

                Message message = new Message(userId, username, 0, channelId, messageContent, new Timestamp(timestamp));
                MessageService.saveMessageToDatabase(message);

                for (User user : users) {
                    if (user.getId() != userId) {
                        MultithreadedServer.sendMessageToAuthenticatedClient(
                                user.getUsername(),
                                ResponseType.MESSAGE_RECEIVED_SUCCESS.name() + ";" + username + ";" + channelName + ";" + messageContent + ";" + timestamp
                        );
                    }
                }

                clientHandler.sendMessage(ResponseType.SEND_MESSAGE_SUCCESS.name());
                return ResponseType.SEND_MESSAGE_SUCCESS.name();
            } else {
                int userId = UserService.getIdByUsername(username);
                int channelId = ChannelService.getChannelIdByName(channelName);

                String multicastIPAndPort = groupMulticastMap.get(channelName);
                String[] parts = multicastIPAndPort.split(":");
                String multicastIP = parts[0];

                int multicastPort = Integer.parseInt(parts[1]);

                Message message = new Message(userId, username, 0, channelId, messageContent, new Timestamp(timestamp));
                MessageService.saveMessageToDatabase(message);

                MultithreadedServer.sendMessageMULTICAST(
                        ResponseType.MESSAGE_RECEIVED_SUCCESS.name() + ";" + username + ";" + channelName + ";" + messageContent + ";" + timestamp, multicastIP, multicastPort
                );

                clientHandler.sendMessage(ResponseType.SEND_MESSAGE_SUCCESS.name());
                return ResponseType.SEND_MESSAGE_SUCCESS.name();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        clientHandler.sendMessage(ResponseType.SEND_MESSAGE_FAILED.name());
        return ResponseType.SEND_MESSAGE_FAILED.name();
    }
}