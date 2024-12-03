package com.example.satfinder.Managers;

import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StorageManager {

    private static StorageManager instance;
    private final FirebaseFirestore firestore;
    private final UserManager userManager;

    private StorageManager() {
        firestore = FirebaseFirestore.getInstance();
        userManager = UserManager.getInstance();
    }

    public static synchronized StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void addFavouriteSatelliteId(int satelliteId, IStorageCallback<Void> callback) {
        String userId = userManager.getCurrentUserUid();
        if (userId == null) {
            callback.onFailure("User not logged in");
            return;
        }

        DocumentReference userDocRef = firestore.collection("users").document(userId);

        userDocRef.set(
                        new HashMap<String, Object>() {{
                            put("favouriteSatelliteIds", FieldValue.arrayUnion(String.valueOf(satelliteId)));
                        }},
                        SetOptions.merge())  // Merge instead of overwriting
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void removeFavouriteSatelliteId(int satelliteId, IStorageCallback<Void> callback) {
        String userId = userManager.getCurrentUserUid();
        if (userId == null) {
            callback.onFailure("User not logged in");
            return;
        }

        firestore.collection("users").document(userId)
                .update("favouriteSatelliteIds", FieldValue.arrayRemove(String.valueOf(satelliteId)))
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getFavouriteSatelliteIds(IStorageCallback<List<String>> callback) {
        String userId = userManager.getCurrentUserUid();
        if (userId == null) {
            callback.onFailure("User not logged in");
            return;
        }

        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    List<String> satelliteIds = (List<String>) documentSnapshot.get("favouriteSatelliteIds");
                    if (satelliteIds == null) satelliteIds = new ArrayList<>();
                    callback.onSuccess(satelliteIds);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
