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
import java.util.HashSet;
import java.util.List;

/**
 * Manages interactions with local storage and remote Firebase Firestore.
 */
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

    /**
     * Saves the user's location in SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @param location The location to save.
     */
    public void spSaveUserLocation(Context context, ObserverLocation location) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_loc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("longitude", String.valueOf(location.getLongitude()));
        editor.putString("latitude", String.valueOf(location.getLatitude()));
        editor.putString("altitude", String.valueOf(location.getAltitude()));

        editor.apply();
    }

    public void spSaveUserFavouriteSatellites(Context context, List<String> satelliteIds) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_fav_sat", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putStringSet("satelliteIds", new HashSet<>(satelliteIds));
    }

    public List<String> spGetUserFavouriteSatellites(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_fav_sat", Context.MODE_PRIVATE);
        List<String> satelliteIds = (List<String>) sharedPreferences.getStringSet("satelliteIds", null);
        return satelliteIds;
    }

    /**
     * Retrieves the user's saved location from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @return The saved location, or a default {@link ObserverLocation} if none is found.
     */
    public ObserverLocation spGetUserLocation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_loc", Context.MODE_PRIVATE);

        String latitude = sharedPreferences.getString("latitude", null);
        String longitude = sharedPreferences.getString("longitude", null);
        String altitude = sharedPreferences.getString("altitude", null);

        if (longitude == null || latitude == null || altitude == null) {
            return new ObserverLocation();
        } else {
            float savedLatitude = Float.parseFloat(latitude);
            float savedLongitude = Float.parseFloat(longitude);
            float savedAltitude = Float.parseFloat(altitude);

            return new ObserverLocation(savedLatitude, savedLongitude, savedAltitude);
        }
    }

    /**
     * Adds a satellite ID to the user's list of favorite satellites in Firestore.
     *
     * @param satelliteId The ID of the satellite to add.
     * @param callback The callback to notify upon success or failure.
     */
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

    /**
     * Removes a satellite ID from the user's list of favorite satellites in Firestore.
     *
     * @param satelliteId The ID of the satellite to remove.
     * @param callback The callback to notify upon success or failure.
     */
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

    /**
     * Retrieves the list of favorite satellite IDs for the current user from Firestore.
     *
     * @param callback The callback to notify with the list of satellite IDs or an error message.
     */
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