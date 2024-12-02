package com.example.satfinder.Managers;

import com.example.satfinder.Objects.Interfaces.UserAuthCallback;
import com.example.satfinder.Services.UserService;

public class UserManager {
    private static UserManager instance;
    private final UserService userService;

    // Private constructor for singleton
    private UserManager() {
        userService = UserService.getInstance();
    }

    // Singleton access method
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // Check if a user is logged in via UserService
    public boolean isUserLoggedIn() {
        return userService.isLoggedIn();
    }

    // Set a new display name via UserService
    public void setUserDisplayName(String newName, UserAuthCallback callback) {
        userService.setDisplayName(newName, callback);
    }

    // Login user via UserService
    public void loginUser(String email, String password, UserAuthCallback callback) {
        userService.login(email, password, callback);
    }

    // Sign up user via UserService
    public void signUpUser(String name, String email, String password, UserAuthCallback callback) {
        userService.signUp(name, email, password, callback);
    }
}
