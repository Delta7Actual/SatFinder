package com.example.satfinder.Services;

import com.example.satfinder.Objects.Interfaces.IUserAuthCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserService {
    private static UserService instance;
    private final FirebaseAuth mAuth;

    // Private constructor for singleton
    private UserService() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Singleton access method
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /*
     *   Authentication related methods
     */

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }
    public String getCurrentUserUid() {
        if (!isLoggedIn()) {
            return null;
        }
        return mAuth.getCurrentUser().getUid();
    }

    public void setDisplayName(String newName, IUserAuthCallback callback) {
        // Check if user is not logged in
        if (!isLoggedIn()) {
            callback.onFailure("User not logged in!");
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        currentUser.updateProfile(profileUpdates).addOnCompleteListener(profileUpdateTask -> {
            if (profileUpdateTask.isSuccessful()) {
                callback.onSuccess(currentUser);
            } else {
                callback.onFailure(profileUpdateTask.getException().getMessage());
            }
        });
    }

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
                callback.onFailure(task.getException().getMessage());
            }
        });
    }


    public void signUp(String name, String email, String password, IUserAuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!isLoggedIn()) {
                    callback.onFailure("User creation failed");
                    return;
                }
                setDisplayName(name, callback);
            } else {
                callback.onFailure(task.getException().getMessage());
            }
        });
    }
}
