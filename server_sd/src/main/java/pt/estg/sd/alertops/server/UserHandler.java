package pt.estg.sd.alertops.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.estg.sd.alertops.components.User;
import pt.estg.sd.alertops.services.ChannelService;
import pt.estg.sd.alertops.services.UserService;
import pt.estg.sd.alertops.utils.ResponseType;

import java.sql.SQLException;
import java.util.List;

/**
 * UserHandler class that handles various user-related requests such as retrieving users, updating roles, and deleting users.
 */
public class UserHandler {

    /**
     * Handles the request to get users from the database.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String getUsersHandler(String requestMessage, ClientHandler clientHandler) {
        try {
            String[] requestParts = requestMessage.split(";");
            int userId = Integer.parseInt(requestParts[1]);

            List<User> users = UserService.getUsersFromDatabase();
            ObjectMapper objectMapper = new ObjectMapper();
            String usersJson = objectMapper.writeValueAsString(users);
            clientHandler.sendMessage(ResponseType.GET_USERS_SUCCESS.name() + ";" + usersJson + ";" + userId);
            return ResponseType.GET_USERS_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.GET_USERS_FAILED.name();
    }

    /**
     * Handles the request to update a user's role.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String updateRoleHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        int id = Integer.parseInt(requestParts[1]);
        User.Role newRole = User.Role.valueOf(requestParts[2]);

        try {
            User.Role oldRole = UserService.getRoleById(id);

            removeUserFromAllChannels(oldRole, id);

            UserService.updateRole(id, String.valueOf(newRole));

            addUserToChannels(newRole, id);

            clientHandler.sendMessage(ResponseType.UPDATE_ROLE_SUCCESS.name() + ";" + id);
            return ResponseType.UPDATE_ROLE_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.UPDATE_ROLE_FAILED.name();
    }

    /**
     * Removes a user from all channels based on their role.
     * @param role The role of the user.
     * @param userId The ID of the user.
     * @throws SQLException If a database access error occurs.
     */
    private static void removeUserFromAllChannels(User.Role role, int userId) throws SQLException {
        switch (role) {
            case CANDIDATO:
                ChannelService.removeChannelUser("Candidatos", userId);
                break;
            case AGENTE_DE_EMERGENCIA:
                ChannelService.removeChannelUser("Agentes de Emergencia", userId);
                ChannelService.removeChannelUser("Candidatos", userId);
                break;
            case SUPERVISOR_DE_EMERGENCIA:
                ChannelService.removeChannelUser("Supervisores de Emergencia", userId);
                ChannelService.removeChannelUser("Agentes de Emergencia", userId);
                ChannelService.removeChannelUser("Candidatos", userId);
                break;
            case COORDENADOR_DE_EMERGENCIA:
                ChannelService.removeChannelUser("Coordenadores de Emergencia", userId);
                ChannelService.removeChannelUser("Supervisores de Emergencia", userId);
                ChannelService.removeChannelUser("Agentes de Emergencia", userId);
                ChannelService.removeChannelUser("Candidatos", userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    /**
     * Adds a user to the appropriate channels based on their role.
     * @param role The role of the user.
     * @param userId The ID of the user.
     * @throws SQLException If a database access error occurs.
     */
    private static void addUserToChannels(User.Role role, int userId) throws SQLException {
        switch (role) {
            case CANDIDATO:
                ChannelService.addChannelUser("Candidatos", userId);
                break;
            case AGENTE_DE_EMERGENCIA:
                ChannelService.addChannelUser("Agentes de Emergencia", userId);
                ChannelService.addChannelUser("Candidatos", userId);
                break;
            case SUPERVISOR_DE_EMERGENCIA:
                ChannelService.addChannelUser("Supervisores de Emergencia", userId);
                ChannelService.addChannelUser("Agentes de Emergencia", userId);
                ChannelService.addChannelUser("Candidatos", userId);
                break;
            case COORDENADOR_DE_EMERGENCIA:
                ChannelService.addChannelUser("Coordenadores de Emergencia", userId);
                ChannelService.addChannelUser("Supervisores de Emergencia", userId);
                ChannelService.addChannelUser("Agentes de Emergencia", userId);
                ChannelService.addChannelUser("Candidatos", userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }


    /**
     * Handles the request to delete a user.
     * @param requestMessage The request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    static String deleteUserHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        int id = Integer.parseInt(requestParts[1]);

        try {
            UserService.deleteUser(id);
            clientHandler.sendMessage(ResponseType.DELETE_USER_SUCCESS.name()+";"+id);
            return ResponseType.DELETE_USER_SUCCESS.name();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.DELETE_USER_FAILED.name();
    }
}