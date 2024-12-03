package com.example.satfinder.Objects.Interfaces;

import com.google.firebase.auth.FirebaseUser;

public interface IUserAuthCallback {
    void onSuccess(FirebaseUser user);
    void onFailure(String error);
}
