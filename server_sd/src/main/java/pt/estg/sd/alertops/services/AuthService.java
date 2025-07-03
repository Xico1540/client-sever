package pt.estg.sd.alertops.services;

import pt.estg.sd.alertops.components.User;

import javax.servlet.http.HttpSession;

import static pt.estg.sd.alertops.services.UserService.getUserByUsername;

/**
 * AuthService class that handles authentication-related operations such as managing the session and retrieving the logged-in user.
 */
public class AuthService {

    private static HttpSession session;
    public static String loggedInUserName;

    /**
     * Sets the current session.
     * @param session The HTTP session.
     */
    public static void setSession(HttpSession session) {
        AuthService.session = session;
    }

    /**
     * Retrieves the currently logged-in user.
     * @return The logged-in user.
     */
    public static User getLoggedInUser() {
        return getUserByUsername(loggedInUserName);
    }

    /**
     * Sets the username of the logged-in user.
     * @param username The username of the logged-in user.
     */
    public static void setLoggedInUserName(String username) {
        loggedInUserName = username;
    }

    /**
     * Retrieves the username of the logged-in user.
     * @return The username of the logged-in user.
     */
    public static String getLoggedInUserName() {
        return loggedInUserName;
    }

    /**
     * Retrieves the ID of the logged-in user.
     * @return The ID of the logged-in user.
     */
    public static Integer getLoggedInUserId() {
        User userInfo = getUserByUsername(loggedInUserName);
        return userInfo.getId();
    }

    /**
     * Invalidates the current session and logs out the user.
     */
    public static void invalidateSession() {
        if (session != null) {
            session.invalidate();
            session = null;
        }
        loggedInUserName = null;
    }
}