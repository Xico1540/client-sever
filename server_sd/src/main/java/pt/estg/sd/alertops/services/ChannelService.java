package pt.estg.sd.alertops.services;

import pt.estg.sd.alertops.components.Channel;
import pt.estg.sd.alertops.components.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ChannelService class that handles various channel-related operations such as creating channels, checking if a channel exists, and retrieving channels from the database.
 */
public class ChannelService {

    private static final String URL = "jdbc:postgresql://localhost:5432/alertops_db";
    private static final String USER = "alertops_user";
    private static final String PASSWORD = "password123";

    /**
     * Creates a new channel with the specified name, description, and user IDs.
     * @param name The name of the channel.
     * @param description The description of the channel.
     * @param userIdsString The user IDs associated with the channel.
     * @throws SQLException If a database access error occurs.
     */
    public static void createChannel(String name, String description, String userIdsString) throws SQLException {
        String[] userIds = userIdsString.replace("[", "").replace("]", "").split(",");
        String insertChannelQuery = "INSERT INTO channels (name, description) VALUES (?, ?)";
        String insertChannelUserQuery = "INSERT INTO channel_users (channel_id, user_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement channelStatement = connection.prepareStatement(insertChannelQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Insert the new channel
            channelStatement.setString(1, name);
            channelStatement.setString(2, description);
            channelStatement.executeUpdate();

            // Get the generated channel ID
            ResultSet generatedKeys = channelStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int channelId = generatedKeys.getInt(1);

                // Insert the user associations
                try (PreparedStatement channelUserStatement = connection.prepareStatement(insertChannelUserQuery)) {
                    for (String userId : userIds) {
                        channelUserStatement.setInt(1, channelId);
                        channelUserStatement.setInt(2, Integer.parseInt(userId.trim()));
                        channelUserStatement.addBatch();
                    }
                    channelUserStatement.executeBatch();
                }
            }
        }
    }

    /**
     * Checks if a channel exists between two users.
     * @param userId1 The ID of the first user.
     * @param userId2 The ID of the second user.
     * @return True if the channel exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public static boolean channelExists(int userId1, int userId2) throws SQLException {
        String user1Name = UserService.getNameById(userId1);
        String user2Name = UserService.getNameById(userId2);
        String query = "SELECT id FROM channels WHERE name = ? OR name = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "Chat " + user1Name + " e " + user2Name);
            statement.setString(2, "Chat " + user2Name + " e " + user1Name);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    /**
     * Creates a new chat between two users.
     * @param userId1 The ID of the first user.
     * @param userId2 The ID of the second user.
     * @throws SQLException If a database access error occurs.
     */
    public static void createChat(int userId1, int userId2) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Fetch usernames
            String user1Name = getUsernameById(connection, userId1);
            String user2Name = getUsernameById(connection, userId2);

            // Create a new channel
            String channelSql = "INSERT INTO channels (name, description) VALUES (?, ?)";
            try (PreparedStatement channelStatement = connection.prepareStatement(channelSql, Statement.RETURN_GENERATED_KEYS)) {
                channelStatement.setString(1, "Chat " + user1Name + " e " + user2Name);
                channelStatement.setString(2, "Private chat");
                channelStatement.executeUpdate();

                // Get the generated channel ID
                ResultSet generatedKeys = channelStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int channelId = generatedKeys.getInt(1);

                    // Insert into channel_users table
                    String userChannelSql = "INSERT INTO channel_users (user_id, channel_id) VALUES (?, ?)";
                    try (PreparedStatement userChannelStatement = connection.prepareStatement(userChannelSql)) {
                        userChannelStatement.setInt(1, userId1);
                        userChannelStatement.setInt(2, channelId);
                        userChannelStatement.executeUpdate();

                        userChannelStatement.setInt(1, userId2);
                        userChannelStatement.setInt(2, channelId);
                        userChannelStatement.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * Retrieves the username by user ID.
     * @param connection The database connection.
     * @param userId The user ID.
     * @return The username.
     * @throws SQLException If a database access error occurs.
     */
    private static String getUsernameById(Connection connection, int userId) throws SQLException {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("username");
            } else {
                throw new SQLException("User not found with ID: " + userId);
            }
        }
    }

    /**
     * Retrieves the channels associated with a user from the database.
     * @param userId The user ID.
     * @return A list of channels.
     * @throws SQLException If a database access error occurs.
     */
    public static List<Channel> getChannelsFromDatabase(int userId) throws SQLException {
        List<Channel> channels = new ArrayList<>();
        String query = "SELECT c.id, c.name, c.description FROM channels c " +
                "JOIN channel_users cu ON c.id = cu.channel_id " +
                "WHERE cu.user_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    List<User> users = getUsersByChannelId(id);
                    channels.add(new Channel(id, name, description, users));
                }
            }
        }
        return channels;
    }

    /**
     * Retrieves the channel ID by channel name.
     * @param channelName The channel name.
     * @return The channel ID.
     * @throws SQLException If a database access error occurs.
     */
    public static int getChannelIdByName(String channelName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM channels WHERE name = ?")) {
            statement.setString(1, channelName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        return -1;
    }

    /**
     * Deletes a channel by name.
     * @param name The name of the channel.
     * @throws SQLException If a database access error occurs.
     */
    public static void deleteChannel(String name) throws SQLException {
        String deleteMessagesQuery = "DELETE FROM messages WHERE channel_id = (SELECT id FROM channels WHERE name = ?)";
        String deleteChannelQuery = "DELETE FROM channels WHERE name = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement deleteMessagesStatement = connection.prepareStatement(deleteMessagesQuery);
             PreparedStatement deleteChannelStatement = connection.prepareStatement(deleteChannelQuery)) {

            // Delete messages associated with the channel
            deleteMessagesStatement.setString(1, name);
            deleteMessagesStatement.executeUpdate();

            // Delete the channel
            deleteChannelStatement.setString(1, name);
            deleteChannelStatement.executeUpdate();
        }
    }

    /**
     * Retrieves all users from the database.
     * @return A list of users.
     * @throws SQLException If a database access error occurs.
     */
public static List<User> getUsersFromDatabase() throws SQLException {
    String sql = "SELECT id, username, password, email, role FROM users";

    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            String email = resultSet.getString("email");
            User.Role role = User.Role.valueOf(resultSet.getString("role"));
            users.add(new User(id, username, password, email, role));
        }

        return users;
    }
}

    /**
     * Retrieves the users associated with a channel by channel ID.
     * @param channelId The channel ID.
     * @return A list of users.
     * @throws SQLException If a database access error occurs.
     */
    public static List<User> getUsersByChannelId(int channelId) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.id, u.username FROM users u " +
                "JOIN channel_users cu ON u.id = cu.user_id WHERE cu.channel_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, channelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    users.add(new User(id, username, null, null, null));
                }
            }
        }
        return users;
    }

    /**
     * Retrieves the channel ID by channel name.
     * @param name The channel name.
     * @return The channel ID.
     * @throws SQLException If a database access error occurs.
     */
    public static int getchannelIdbyName(String name) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM channels WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("Channel not found with name: " + name);
            }
        }
    }


    /**
     * Adds a user to a channel.
     * @param channelName The name of the channel.
     * @param userId The ID of the user.
     * @return True if the user was successfully added to the channel, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public static boolean addChannelUser(String channelName, int userId) throws SQLException {
        int channelId = getchannelIdbyName(channelName);
        String query = "INSERT INTO channel_users (channel_id, user_id) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, channelId);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        }
    }


    /**
     * Removes a user from a channel.
     * @param ChannelName The name of the channel.
     * @param userId The ID of the user.
     * @return True if the user was successfully removed from the channel, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public static boolean removeChannelUser(String ChannelName, int userId) throws SQLException {
        int channelId = getchannelIdbyName(ChannelName);
        String query = "DELETE FROM channel_users WHERE channel_id = ? AND user_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, channelId);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        }
    }

}