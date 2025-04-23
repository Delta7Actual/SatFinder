package com.example.satfinder.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.satfinder.Misc.Utility.MathUtils;
import com.example.satfinder.Objects.Interfaces.ICacheUpdateCallback;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
 * ============================
 *  STORAGE STRUCTURE OVERVIEW
 * ============================
 * 1. SharedPreferences ("user_data")
 * └── user_loc : String
 *     └── Format: "timestamp,Lat,Long,Alt"
 *
 * └── fav_sat : String
 *     └── Comma-separated list of favorite satellite IDs
 *     └── Format: "25544,12345,..."
 *
 * └── sat_pass_<satId> : String
 *     └── Format: "timestamp,startUTC"
 *     └── Error Format: "err:<message>"
 *
 * └── sat_pos_<satId> : String
 *     └── Format: "timestamp,Lat,Long,Alt"
 *     └── Error Format: "err:<message>"
 *
 * └── sat_tle_<satId> : String
 *     └── Format: "TLE,satid,satname"
 *     └── Error Format: "err:<message>"
 *
 * ============================
 *
 * 2. Firestore ("users" collection)
 * └── users
 *     └── {userId} : Document
 *         └── favouriteSatelliteIds : Array<String>
 *             └── Format: ["25544", "12345", ...]
 *
 */

/**
 * Manages storage and retrieval with SharedStorage and FireStore.
 */
public class StorageManager {


    private static final String TAG = "STORAGE";
    
    private static StorageManager instance;
    private final SharedPreferences sharedPreferences;
    private final FirebaseFirestore firestore;
    private final UserManager userManager;

    private static final String PREFS_NAME = "user_data";

    private StorageManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firestore = FirebaseFirestore.getInstance();
        userManager = UserManager.getInstance();
    }

    /**
     * Gets the singleton instance of StorageManager.
     * @param context The current application context.
     * @return The singleton instance of StorageManager.
     */
    public static synchronized StorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new StorageManager(context);
        }
        return instance;
    }

    /////////////////////////////////////
    /* SHAREDPREFERENCES BASED METHODS */
    /////////////////////////////////////

    /**
     * Saves the user's current location to SharedPreferences.
     * @param location The current location to save.
     */
    public void spSaveUserLocation(ObserverLocation location) {
        sharedPreferences.edit()
                .putString("user_loc",  System.currentTimeMillis()
                        + "," + location.getLatitude()
                        + "," + location.getLongitude()
                        + "," + location.getAltitude())
                .apply();
    }

    /**
     * Retrieves the user's current location from SharedPreferences.
     * @return The user's current location.
     */
    public ObserverLocation spGetUserLocation() {
        String location = sharedPreferences.getString("user_loc", null);
        if (location != null) {
            String[] parts = location.split(",");
            if (parts.length == 4) {
                return new ObserverLocation(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
            }
        }
        return new ObserverLocation();
    }

    /**
     * Retrieves the time of the user's last location update from SharedPreferences.
     * @return The UTC timestamp of the last location update.
     */
    public long spGetUserLocationTime() {
        String location = sharedPreferences.getString("user_loc", null);
        if (location != null) {
            String[] parts = location.split(",");
            if (parts.length == 4) {
                return Long.parseLong(parts[0]);
            }
        }
        return -1;
    }

    /**
     * Saves a list of favorite satellite IDs to SharedPreferences.
     * @param satelliteIds The list of favorite satellite IDs.
     */
    public void spSaveUserFavoriteSatellites(List<String> satelliteIds) {
        String favoriteSatellites = String.join(",", satelliteIds);
        sharedPreferences.edit()
                .putString("fav_sat", favoriteSatellites)
                .apply();
    }

    /**
     * Retrieves the list of favorite satellite IDs from SharedPreferences.
     * @return The list of favorite satellite IDs.
     */
    public List<String> spGetUserFavoriteSatellites() {
        String favoriteSatellites = sharedPreferences.getString("fav_sat", "");
        return favoriteSatellites.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(favoriteSatellites.split(",")));
    }

    /**
     * Retrieves the closest pass data for a satellite from SharedPreferences.
     * @param satelliteId The NORAD ID of the satellite.
     * @return The closest pass data as a comma-separated string.
     */
    public String spGetSatelliteClosestPass(int satelliteId) {
        return sharedPreferences.getString("sat_pass_" + satelliteId, "NONE,,");
    }

    /**
     * Retrieves the satellite position data for a satellite from SharedPreferences.
     * @param satelliteId The NORAD ID of the satellite.
     * @return The satellite position data as a comma-separated string.
     */
    public String spGetSatellitePos(int satelliteId) {
        return sharedPreferences.getString("sat_pos_" + satelliteId, "NONE,,");
    }

    /**
     * Retrieves the TLE data for a satellite from SharedPreferences.
     * @param satelliteId The NORAD ID of the satellite.
     * @return The TLE data as a comma-separated string.
     */
    public String spGetSatelliteTLE(int satelliteId) {
        return sharedPreferences.getString("sat_tle_" + satelliteId, "NONE,,");
    }

    /**
     * Checks if the cached satellite data for a satellite is stale.
     * @param satelliteId The NORAD ID of the satellite.
     * @return An array of boolean values indicating if the pass, position, and TLE data are stale.
     */
    private boolean[] spIsSatelliteDataStale(int satelliteId) {
        String passData = spGetSatelliteClosestPass(satelliteId);
        String posData = spGetSatellitePos(satelliteId);
        String tleData = spGetSatelliteTLE(satelliteId);

        long threshold = 60 * 60; // One hour
        return new boolean[]{
                passData.equals("NONE,,") || MathUtils.isStale(Long.parseLong(passData.split(",")[0]), threshold),
                posData.equals("NONE,,") || MathUtils.isStale(Long.parseLong(posData.split(",")[0]), threshold),
                tleData.equals("NONE,,") // TLE is never outdated
        };
    }

    /**
     * Clears the cached satellite data for all favorites.
     * @param callback The callback to notify on completion.
     */
    public void spClearSatelliteData(ICacheUpdateCallback callback) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<String> favIds = spGetUserFavoriteSatellites();
        if (favIds.isEmpty()) {
            Log.w(TAG, "Empty favorite list. Nothing to clear.");
            callback.onComplete();
            return;
        }

        Log.d(TAG, "Clearing cached satellite data (Items to clear:" + favIds.size() + ")...");
        for (String id : favIds) {
            editor.remove("sat_pass_" + id);
            editor.remove("sat_pos_" + id);
            editor.remove("sat_tle_" + id);
        }

        editor.apply();
        Log.d(TAG, "Satellite data cache cleared. Favorites and location preserved.");
        callback.onComplete();
    }

    /**
     * Updates the cached satellite data for all favorites.
     * @param satelliteManager The satellite manager for fetching data.
     * @param callback The callback to notify on completion.
     */
    public void spSaveAndUpdateSatelliteData(SatelliteManager satelliteManager, ICacheUpdateCallback callback) {
        List<String> satelliteIds = spGetUserFavoriteSatellites();
        Log.i(TAG, "IDS to update: " + satelliteIds.size() + " items");
        if (satelliteIds.isEmpty()) {
            callback.onComplete();
            return;
        }

        ObserverLocation curr = spGetUserLocation();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (String satelliteId : satelliteIds) {
            int id = Integer.parseInt(satelliteId);

            Log.d(TAG, "pass/" + satelliteId + ": " + spGetSatelliteClosestPass(id));
            Log.d(TAG, "pos/" + satelliteId + ": " + spGetSatellitePos(id));
            Log.d(TAG, "tle/" + satelliteId + ": " + spGetSatelliteTLE(id));

            boolean[] stale = spIsSatelliteDataStale(id);
            if (stale[0]) spSaveSatellitePasses(editor, satelliteManager, id, curr);
            if (stale[1]) spSaveSatellitePositions(editor, satelliteManager, id, curr);
            if (stale[2]) spSaveSatelliteTLE(editor, satelliteManager, id);
        }

        editor.apply();
        callback.onComplete();
    }

    /**
     * Updates the cached satellite data for a single satellite.
     * @param editor The SharedPreferences editor.
     * @param manager The satellite manager for fetching data.
     * @param id The NORAD ID of the satellite.
     * @param curr The current observer location.
     */
    private void spSaveSatellitePasses(SharedPreferences.Editor editor, SatelliteManager manager, int id, ObserverLocation curr) {
        Log.d(TAG, "Updating data for ID: " + id);
        manager.fetchSatelliteVisualPasses(id, curr, 7, 60, new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
                if (svpResponse == null) return;
                if (svpResponse.getPasses() == null) return;
                if (svpResponse.getPasses().isEmpty()) return;

                String passData = System.currentTimeMillis() + "," + svpResponse.getPasses().get(0).getStartUTC();
                Log.d("HELP", "onSuccess: " + passData);
                editor.putString("sat_pass_" + id, passData);
                editor.apply();
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("HELP", "onError: " + errorMessage);
                editor.putString("sat_pass_" + id, "err:" + errorMessage);
                editor.apply();
            }
        });
    }

    /**
     * Updates the cached satellite position data for a single satellite.
     * @param editor The SharedPreferences editor.
     * @param manager The satellite manager for fetching data.
     * @param id The NORAD ID of the satellite.
     * @param curr The current observer location.
     */
    private void spSaveSatellitePositions(SharedPreferences.Editor editor, SatelliteManager manager, int id, ObserverLocation curr) {
        Log.d(TAG, "Updating data for ID: " + id);
        manager.fetchSatellitePositions(id, curr, 1, new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatellitePositionsResponse spResponse = (SatellitePositionsResponse) response;
                if (spResponse == null) return;
                if (spResponse.getPositions() == null) return;
                if (spResponse.getPositions().isEmpty()) return;

                String posData = System.currentTimeMillis() + "," + spResponse.getPositions().get(0).getSatlatitude() + "," + spResponse.getPositions().get(0).getSatlongitude() + "," + spResponse.getPositions().get(0).getSataltitude();
                Log.d("HELP", "onSuccess: " + posData);
                editor.putString("sat_pos_" + id, posData);
                editor.apply();
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("HELP", "onError: " + errorMessage);
                editor.putString("sat_pos_" + id, "err:" + errorMessage);
                editor.apply();
            }
        });
    }

    /**
     * Updates the cached satellite TLE data for a single satellite.
     * @param editor The SharedPreferences editor.
     * @param manager The satellite manager for fetching data.
     * @param id The NORAD ID of the satellite.
     */
    private void spSaveSatelliteTLE(SharedPreferences.Editor editor, SatelliteManager manager, int id) {
        Log.d(TAG, "Updating data for ID: " + id);
        manager.fetchSatelliteTLE(id, new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatelliteTLEResponse stleResponse = (SatelliteTLEResponse) response;
                if (stleResponse == null || stleResponse.getTle() == null || stleResponse.getTle().isEmpty()) return;

                String tleData = stleResponse.getTle();
                String dataToSave = tleData + "," + stleResponse.getInfo().getSatid() + "," + stleResponse.getInfo().getSatname();
                Log.d("HELP", "onSuccess: " + tleData);
                editor.putString("sat_tle_" + id, dataToSave);
                editor.apply();
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("HELP", "onError: " + errorMessage);
                editor.putString("sat_tle_" + id, "err:" + errorMessage);
                editor.apply();
            }
        });
    }

    /////////////////////////////////////
    /*    FIRESTORE BASED METHODS      */
    /////////////////////////////////////

    /**
     * Adds a satellite to the user's favorite list.
     * @param satelliteId The NORAD ID of the satellite to add.
     * @param callback The callback to notify on completion.
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
                        SetOptions.merge()
                ).addOnSuccessListener(aVoid -> {
                    // Update the SharedPreferences after a successful Firebase update
                    List<String> favSatellites = spGetUserFavoriteSatellites();
                    if (!favSatellites.contains(String.valueOf(satelliteId))) {
                        favSatellites.add(String.valueOf(satelliteId));
                        spSaveUserFavoriteSatellites(favSatellites);  // Save to SharedPreferences
                    }
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Removes a satellite from the user's favorite list.
     * @param satelliteId The NORAD ID of the satellite to remove.
     * @param callback The callback to notify on completion.
     */
    public void removeFavouriteSatelliteId(int satelliteId, IStorageCallback<Void> callback) {
        String userId = userManager.getCurrentUserUid();
        if (userId == null) {
            callback.onFailure("User not logged in");
            return;
        }

        firestore.collection("users").document(userId)
                .update("favouriteSatelliteIds", FieldValue.arrayRemove(String.valueOf(satelliteId)))
                .addOnSuccessListener(aVoid -> {
                    // Update the SharedPreferences after a successful Firebase update
                    List<String> favSatellites = spGetUserFavoriteSatellites();
                    favSatellites.remove(String.valueOf(satelliteId));
                    spSaveUserFavoriteSatellites(favSatellites);  // Save to SharedPreferences
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Retrieves the user's favorite satellite IDs.
     * @param callback The callback to notify with the result.
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
                    List<String> satelliteIds = (List<String>) documentSnapshot.get("favouriteSatelliteIds");
                    callback.onSuccess(satelliteIds == null ? new ArrayList<>() : satelliteIds);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Deletes the user's document from Firestore.
     * @param userId The UID of the user.
     * @param callback The callback to notify on completion.
     */
    public void deleteUserDocument(String userId, IStorageCallback<Void> callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onFailure("Invalid user ID");
            return;
        }
        firestore.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}