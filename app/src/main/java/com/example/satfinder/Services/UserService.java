package com.example.satfinder.Services;

import com.example.satfinder.Objects.Interfaces.IUserAuthCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

/**
 * A service class for interacting with Firebase Authentication.
 * This service is responsible for user authentication and management using Firebase.
 */
public class UserService {

    private static final String TAG = "USERSERVICE";

    private static UserService instance;
    private final FirebaseAuth mAuth;

    private UserService() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Singleton access method to get the instance of UserService.
     *
     * @return the single instance of UserService.
     */
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Checks if the current user is logged in.
     *
     * @return true if the user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Retrieves the UID of the currently logged-in user.
     *
     * @return the UID of the current user, or null if not logged in.
     */
    public String getCurrentUserUid() {
        if (!isLoggedIn()) {
            return null;
        }
        return Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    }

    /**
     * Retrieves the display name of the currently logged-in user.
     *
     * @return the display name of the current user, or null if not logged in.
     */
    public String getCurrentUserDisplayName() {
        if (!isLoggedIn()) {
            return null;
        }
        return Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
    }

    /**
     * Retrieves the email of the currently logged-in user.
     *
     * @return the email of the current user, or null if not logged in.
     */
    public String getCurrentUserEmail() {
        if (!isLoggedIn()) {
            return null;
        }
        return Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
    }

    /**
     * Updates the display name of the currently logged-in user.
     *
     * @param newName the new display name to set.
     * @param callback the callback to notify of success or failure.
     */
    public void setDisplayName(String newName, IUserAuthCallback callback) {
        // Check if user is not logged in
        if (!isLoggedIn()) {
            callback.onFailure("User not logged in!");
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not logged in!");
            return;
        }
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        currentUser.updateProfile(profileUpdates).addOnCompleteListener(profileUpdateTask -> {
            if (profileUpdateTask.isSuccessful()) {
                callback.onSuccess(currentUser);
            } else {
                callback.onFailure(Objects.requireNonNull(profileUpdateTask.getException()).getMessage());
            }
        });
    }

    /**
     * Attempts to log in a user with the provided email and password.
     *
     * @param email the email of the user.
     * @param password the password of the user.
     * @param callback the callback to notify of success or failure.
     */
    public void login(String email, String password, IUserAuthCallback callback) {
        // Check if already logged in
        if (isLoggedIn()) {
            callback.onSuccess(mAuth.getCurrentUser());
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(mAuth.getCurrentUser());
            } else {
                callback.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    /**
     * Signs up a new user with the provided name, email, and password.
     *
     * @param name the name of the user.
     * @param email the email of the user.
     * @param password the password for the user.
     * @param callback the callback to notify of success or failure.
     */
    public void signUp(String name, String email, String password, IUserAuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!isLoggedIn()) {
                    callback.onFailure("User creation failed");
                    return;
                }
                setDisplayName(name, callback);
            } else {
                callback.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    /**
     * Sends a password reset email to the provided email address.
     *
     * @param email the email address to send the reset email to.
     * @param callback the callback to notify of success or failure.
     */
    public void recoverPassword(String email, IUserAuthCallback callback) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                callback.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    /**
     * Deletes the currently logged-in user.
     *
     * @param callback the callback to notify of success or failure.
     */
    public void deleteCurrentUser(IUserAuthCallback callback) {
        if (!isLoggedIn()) {
            callback.onFailure("User not logged in!");
            return;
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not logged in!");
            return;
        }
        currentUser.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(currentUser);
            } else {
                callback.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }
}