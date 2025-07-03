package pt.estg.sd.alertops.services;

import pt.estg.sd.alertops.components.Requests;
import pt.estg.sd.alertops.components.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RequestService {

    private static final String URL = "jdbc:postgresql://localhost:5432/alertops_db";
    private static final String USER = "alertops_user";
    private static final String PASSWORD = "password123";

    public void createRequest(int userId, Requests.Status status, Requests.Type type, LocalDateTime timestamp) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO Requests (senderId, status, type, timestamp) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setString(2, status.name());
                statement.setString(3, type.name());
                statement.setTimestamp(4, Timestamp.valueOf(timestamp));
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Requests> getPendingRequests() throws SQLException {
        List<Requests> requestsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT id, senderId, status, type, timestamp FROM Requests WHERE status = 'PENDING'";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    Requests request = new Requests();
                    request.setId(resultSet.getInt("id"));
                    request.setSenderId(resultSet.getString("senderId"));
                    request.setStatus(Requests.Status.valueOf(resultSet.getString("status")));
                    request.setType(Requests.Type.valueOf(resultSet.getString("type")));
                    request.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                    requestsList.add(request);
                }
            }
        }
        return requestsList;
    }

    public static Requests acceptRequest(int requestId, int userId) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Requests request = getRequestById(requestId);
            if (request == null || !canHandleRequest(request, userId)) {
                return null;
            }

            String sql = "UPDATE Requests SET status = 'ACCEPTED' WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, requestId);
                statement.executeUpdate();
                return request;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean declineRequest(int requestId, int userId) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Requests request = getRequestById(requestId);
            if (request == null || !canHandleRequest(request, userId)) {
                return false;
            }

            String sql = "UPDATE Requests SET status = 'DECLINED' WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, requestId);
                statement.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static Requests getRequestById(int requestId) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT id, senderId, status, type, timestamp FROM Requests WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, requestId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Requests request = new Requests();
                        request.setId(resultSet.getInt("id"));
                        request.setSenderId(resultSet.getString("senderId"));
                        request.setStatus(Requests.Status.valueOf(resultSet.getString("status")));
                        request.setType(Requests.Type.valueOf(resultSet.getString("type")));
                        request.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                        return request;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static boolean canHandleRequest(Requests request, int userId) {
        if (request.getSenderId().equals(String.valueOf(userId))) {
            return false;
        }

        User.Role userRole = UserService.getRoleById(userId);
        switch (userRole) {
            case COORDENADOR_DE_EMERGENCIA:
                return true;
            case SUPERVISOR_DE_EMERGENCIA:
                return request.getType() == Requests.Type.ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA ||
                        request.getType() == Requests.Type.DISTRIBUICAO_DE_RECURSOS_DE_EMERGENCIA;
            case AGENTE_DE_EMERGENCIA:
                return request.getType() == Requests.Type.DISTRIBUICAO_DE_RECURSOS_DE_EMERGENCIA;
            default:
                return false;
        }
    }

    public static List<Requests> fetchRecentRequests(long period, TimeUnit timeUnit) {
        List<Requests> requestsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Convert period and timeUnit to a format suitable for SQL INTERVAL
            String interval = period + " " + timeUnit.toString().toLowerCase();
            String query = "SELECT * FROM requests WHERE timestamp >= NOW() - INTERVAL '" + interval + "'";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    Requests request = new Requests();
                    request.setId(resultSet.getInt("id"));
                    request.setSenderId(resultSet.getString("senderId"));
                    request.setStatus(Requests.Status.valueOf(resultSet.getString("status")));
                    request.setType(Requests.Type.valueOf(resultSet.getString("type")));
                    request.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                    requestsList.add(request);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return requestsList;
    }
}