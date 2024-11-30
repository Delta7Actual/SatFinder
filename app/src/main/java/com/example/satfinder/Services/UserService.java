package com.example.satfinder.Services;

import com.example.satfinder.Objects.Interfaces.UserAuthCallback;
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

    public void login(String email, String password, UserAuthCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            callback.onFailure("User is already logged in!");
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(mAuth.getCurrentUser());
            } else {
                callback.onFailure(task.getException().getMessage());
            }
        });
    }

    public void signUp(String name, String email, String password, UserAuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser userCreated = mAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                if (userCreated == null) callback.onFailure("User is null!");
                assert userCreated != null;
                userCreated.updateProfile(profileUpdates).addOnCompleteListener(profileUpdateTask -> {
                    if (profileUpdateTask.isSuccessful()) {
                        callback.onSuccess(userCreated);
                    } else {
                        callback.onFailure(profileUpdateTask.getException().getMessage());
                    }
                });

                callback.onSuccess(mAuth.getCurrentUser());
            } else {
                callback.onFailure(task.getException().getMessage());
            }
        });
    }
}
