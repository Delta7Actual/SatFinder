package com.example.satfinder.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.Objects.ObserverLocation;
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

    public void spSaveUserLocation(Context context, ObserverLocation location) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_loc_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("longitude", String.valueOf(location.getLongitude()));
        editor.putString("latitude", String.valueOf(location.getLatitude()));
        editor.putString("altitude", String.valueOf(location.getAltitude()));

        editor.apply();
    }

    public ObserverLocation spGetUserLocation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_loc_prefs", Context.MODE_PRIVATE);

        String latitude = sharedPreferences.getString("latitude", null);
        String longitude = sharedPreferences.getString("longitude", null);
        String altitude = sharedPreferences.getString("altitude", null);

        if (longitude == null || latitude == null || altitude == null) {
            return new ObserverLocation();
        } else {
            float savedLatitude = Float.parseFloat(latitude);
            float savedLongitude = (Float.parseFloat(latitude));
            float savedAltitude = (Float.parseFloat(altitude));

            return new ObserverLocation(savedLatitude, savedLongitude, savedAltitude);
        }
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
