package pt.estg.sd.alertops.services;

import pt.estg.sd.alertops.components.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static pt.estg.sd.alertops.services.AuthService.setLoggedInUserName;

/**
 * UserService class that handles various user-related operations such as retrieving users, updating roles, and deleting users.
 */
public class UserService {

    private static final String URL = "jdbc:postgresql://localhost:5432/alertops_db";
    private static final String USER = "alertops_user";
    private static final String PASSWORD = "password123";

    /**
     * Retrieves the role of a user by username.
     * @param username The username of the user.
     * @return The role of the user.
     */
    public static User.Role getRoleByUsername(String username) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return User.Role.valueOf(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the role of a user by ID.
     * @param id The ID of the user.
     * @return The role of the user.
     */
    public static User.Role getRoleById(int id) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT role FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return User.Role.valueOf(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves a user to the database.
     * @param user The user to be saved.
     * @throws SQLException If a database access error occurs.
     */
    public static void saveUserToDatabase(User user) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String checkSql = "SELECT COUNT(*) FROM users";
            try (Statement checkStatement = connection.createStatement();
                 ResultSet resultSet = checkStatement.executeQuery(checkSql)) {
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count == 0) {
                    String resetSql = "ALTER SEQUENCE users_id_seq RESTART WITH 1";
                    try (Statement resetStatement = connection.createStatement()) {
                        resetStatement.executeUpdate(resetSql);
                    }
                }
            }

            String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getEmail());
                statement.setString(4, user.getRole().name());
                statement.executeUpdate();
            }
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
     * Authenticates a user by username and password.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return True if authentication is successful, false otherwise.
     */
    public static boolean authenticate(String username, String password) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    setLoggedInUserName(username);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves a user by username.
     * @param username The username of the user.
     * @return The user.
     */
    public static User getUserByUsername(String username) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        User.Role.valueOf(rs.getString("role"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the username by user ID.
     * @param id The user ID.
     * @return The username.
     */
    public static String getNameById(int id) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT username FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the user ID by username.
     * @param username The username of the user.
     * @return The user ID.
     */
    public static Integer getIdByUsername(String username) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the role of a user.
     * @param userId The ID of the user.
     * @param role The new role of the user.
     * @throws SQLException If a database access error occurs.
     */
    public static void updateRole(int userId, String role) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "UPDATE users SET role = ? WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, role);
                statement.setInt(2, userId);
                statement.executeUpdate();
            }
        }
    }

    /**
     * Deletes a user by ID.
     * @param userId The ID of the user.
     * @throws SQLException If a database access error occurs.
     */
    public static void deleteUser(int userId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "DELETE FROM users WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            }
        }
    }
}