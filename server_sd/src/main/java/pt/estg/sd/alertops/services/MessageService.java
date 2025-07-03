package pt.estg.sd.alertops.services;

import pt.estg.sd.alertops.components.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageService class that handles various message-related operations such as saving messages to the database and retrieving messages for a specific channel.
 */
public class MessageService {

    private static final String URL = "jdbc:postgresql://localhost:5432/alertops_db";
    private static final String USER = "alertops_user";
    private static final String PASSWORD = "password123";

    /**
     * Saves a message to the database.
     * @param message The message to be saved.
     * @throws SQLException If a database access error occurs.
     */
    public static void saveMessageToDatabase(Message message) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (sender_id, sender_name, recipient_id, channel_id, content, timestamp) VALUES (?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, message.getSenderId());
            statement.setString(2, message.getSenderName());
            statement.setInt(3, message.getRecipientId());
            statement.setInt(4, message.getChannelId());
            statement.setString(5, message.getContent());
            statement.setTimestamp(6, message.getTimestamp());
            statement.executeUpdate();
        }
    }

    /**
     * Retrieves messages for a specific channel from the database.
     * @param channelId The ID of the channel.
     * @return A list of messages for the specified channel.
     * @throws SQLException If a database access error occurs.
     */
    public static List<Message> getMessagesForChannel(int channelId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE channel_id = ? ORDER BY id")) {
            statement.setInt(1, channelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Message message = new Message();
                    message.setId(resultSet.getInt("id"));
                    message.setSenderId(resultSet.getInt("sender_id"));
                    message.setSenderName(resultSet.getString("sender_name"));
                    message.setRecipientId(resultSet.getInt("recipient_id"));
                    message.setChannelId(resultSet.getInt("channel_id"));
                    message.setContent(resultSet.getString("content"));
                    message.setTimestamp(resultSet.getTimestamp("timestamp"));
                    messages.add(message);
                }
            }
        }
        return messages;
    }
}