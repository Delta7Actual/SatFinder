package com.example.satfinder.Managers;

import android.util.Log;

import com.example.satfinder.Objects.Interfaces.IUserAuthCallback;
import com.example.satfinder.Services.UserService;

/**
 * Manages user authentication and profile actions.
 */
public class UserManager {

    private static final String TAG = "USER";

    private static UserManager instance;
    private final UserService userService;

    // Private constructor for singleton
    private UserManager() {
        userService = UserService.getInstance();
    }

    /**
     * Gets the singleton instance of UserManager.
     *
     * @return The singleton instance of UserManager.
     */
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
        Log.d(TAG, "isUserLoggedIn: " + userService.isLoggedIn());
        return userService.isLoggedIn();
    }

    /**
     * Gets the unique identifier (UID) of the current user.
     *
     * @return The UID of the current user, or {@code null} if no user is logged in.
     */
    public String getCurrentUserUid() {
        Log.d(TAG, "getCurrentUserUid: " + userService.getCurrentUserUid());
        return userService.getCurrentUserUid();
    }

    /**
     * Gets the display name of the currently logged-in user.
     *
     * @return The display name of the current user, or {@code null} if no user is logged in.
     */
    public String getCurrentUserDisplayName() {
        Log.d(TAG, "getCurrentUserDisplayName: " + userService.getCurrentUserDisplayName());
        return userService.getCurrentUserDisplayName();
    }

    /**
     * Gets the email address of the currently logged-in user.
     *
     * @return The email address of the current user, or {@code null} if no user is logged in.
     */
    public String getCurrentUserEmail() {
        Log.d(TAG, "getCurrentUserEmail: " + userService.getCurrentUserEmail());
        return userService.getCurrentUserEmail();
    }

    /**
     * Sets the display name of the currently logged-in user.
     *
     * @param newName The new display name to set for the user.
     * @param callback The callback to notify on success or failure.
     */
    public void setUserDisplayName(String newName, IUserAuthCallback callback) {
        Log.d(TAG, "setUserDisplayName: " + newName);
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
        Log.d(TAG, "loginUser: " + email);
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
        Log.d(TAG, "signUpUser: " + email);
        userService.signUp(name, email, password, callback);
    }

    /**
     * Sends a password reset email to the provided email address.
     *
     * @param email The email address to send the reset email to.
     * @param callback The callback to notify on success or failure.
     */
    public void recoverPassword(String email, IUserAuthCallback callback) {
        Log.d(TAG, "recoverPassword: " + email);
        userService.recoverPassword(email, callback);
    }

    /**
     * Deletes the currently logged-in user.
     *
     * @param callback The callback to notify on success or failure.
     */
    public void deleteUser(IUserAuthCallback callback) {
        Log.d(TAG, "deleteUser");
        userService.deleteCurrentUser(callback);
    }
}