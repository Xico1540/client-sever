package pt.estg.sd.alertops.server;

import pt.estg.sd.alertops.components.User;
import pt.estg.sd.alertops.services.UserService;
import pt.estg.sd.alertops.utils.ResponseType;

import static pt.estg.sd.alertops.services.ChannelService.addChannelUser;

/**
 * AuthHandler class that handles authentication requests such as login and registration.
 */
public class AuthHandler {

    /**
     * Handles the login request.
     * @param requestMessage The login request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    public static String loginHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        String username = requestParts[1];
        String password = requestParts[2];
        System.out.println("Username: " + username + " Password: " + password);
        boolean isAuthenticated = UserService.authenticate(username, password);

        if (isAuthenticated) {
            int id = UserService.getIdByUsername(username);
            User.Role role = UserService.getRoleByUsername(username);

            clientHandler.sendMessage(ResponseType.LOGIN_SUCCESS.name() + ";" + id + ";" + role);
            return ResponseType.LOGIN_SUCCESS.name();
        }
        return ResponseType.LOGIN_FAILED.name();
    }

    /**
     * Handles the registration request.
     * @param requestMessage The registration request message.
     * @param clientHandler The client handler.
     * @return The response type indicating success or failure.
     */
    public static String registerHandler(String requestMessage, ClientHandler clientHandler) {
        String[] requestParts = requestMessage.split(";");
        String username = requestParts[1];
        String password = requestParts[2];
        String email = requestParts[3];
        User.Role role = User.Role.valueOf(requestParts[4]);

        try {
            User user = new User(null, username, password, email, role);
            UserService.saveUserToDatabase(user);
            int userId = UserService.getIdByUsername(username);

            boolean isAdded = false;
            switch (role) {
                case CANDIDATO:
                    isAdded = addChannelUser("Candidatos", userId);
                    break;
                case AGENTE_DE_EMERGENCIA:
                    isAdded = addChannelUser("Agentes de Emergencia", userId) && addChannelUser("Candidatos", userId);
                    break;
                case SUPERVISOR_DE_EMERGENCIA:
                    isAdded = addChannelUser("Supervisores de Emergencia", userId) && addChannelUser("Agentes de Emergencia", userId) && addChannelUser("Candidatos", userId);
                    break;
                case COORDENADOR_DE_EMERGENCIA:
                    isAdded = addChannelUser("Coordenadores de Emergencia", userId) && addChannelUser("Supervisores de Emergencia", userId) && addChannelUser("Agentes de Emergencia", userId) && addChannelUser("Candidatos", userId);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown role: " + role);
            }

            if (isAdded) {
                clientHandler.sendMessage(ResponseType.REGISTER_SUCCESS.name());
                return ResponseType.REGISTER_SUCCESS.name();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseType.REGISTER_FAILED.name();
    }
}