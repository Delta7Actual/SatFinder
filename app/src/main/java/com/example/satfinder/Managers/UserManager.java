package com.example.satfinder.Managers;

import com.example.satfinder.Objects.Interfaces.IUserAuthCallback;
import com.example.satfinder.Services.UserService;

/**
 * Manages user authentication and profile actions.
 */
public class UserManager {
    private static UserManager instance;
    private final UserService userService;

    // Private constructor for singleton
    private UserManager() {
        userService = UserService.getInstance();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return {@code true} if the user is logged in, {@code false} otherwise.
     */
    public boolean isUserLoggedIn() {
        return userService.isLoggedIn();
    }

    /**
     * Gets the unique identifier (UID) of the current user.
     *
     * @return The UID of the current user, or {@code null} if no user is logged in.
     */
    public String getCurrentUserUid() {
        return userService.getCurrentUserUid();
    }

    /**
     * Sets the display name of the currently logged-in user.
     *
     * @param newName The new display name to set for the user.
     * @param callback The callback to notify on success or failure.
     */
    public void setUserDisplayName(String newName, IUserAuthCallback callback) {
        userService.setDisplayName(newName, callback);
    }

    /**
     * Logs in a user with the provided email and password.
     *
     * @param email The email address of the user.
     * @param password The password for the user.
     * @param callback The callback to notify on success or failure.
     */
    public void loginUser(String email, String password, IUserAuthCallback callback) {
        userService.login(email, password, callback);
    }

    /**
     * Signs up a new user with the provided name, email, and password.
     *
     * @param name The name of the user.
     * @param email The email address for the user.
     * @param password The password for the user.
     * @param callback The callback to notify on success or failure.
     */
    public void signUpUser(String name, String email, String password, IUserAuthCallback callback) {
        userService.signUp(name, email, password, callback);
    }
}
