package com.example.satfinder.Objects.Interfaces;

import com.google.firebase.auth.FirebaseUser;

/**
 * Callback interface for handling user authentication events (e.g., logging in or signing up).
 */
public interface IUserAuthCallback {
    void onSuccess(FirebaseUser user);
    void onFailure(String error);
}
