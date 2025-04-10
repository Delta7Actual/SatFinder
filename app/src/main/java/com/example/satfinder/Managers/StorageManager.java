package com.example.satfinder.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.satfinder.Misc.Utility.SatUtils;
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

public class StorageManager {

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

    public static synchronized StorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new StorageManager(context);
        }
        return instance;
    }

    /////////////////////////////////////
    /* SHAREDPREFERENCES BASED METHODS */
    /////////////////////////////////////

    public void spSaveUserLocation(ObserverLocation location) {
        sharedPreferences.edit()
                .putString("userLocation",  System.currentTimeMillis()
                        + "," + location.getLatitude()
                        + "," + location.getLongitude()
                        + "," + location.getAltitude())
                .apply();
    }

    public ObserverLocation spGetUserLocation() {
        String location = sharedPreferences.getString("userLocation", null);
        if (location != null) {
            String[] parts = location.split(",");
            if (parts.length == 4) {
                return new ObserverLocation(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
            }
        }
        return new ObserverLocation();
    }

    public long spGetUserLocationTime() {
        String location = sharedPreferences.getString("userLocation", null);
        if (location != null) {
            String[] parts = location.split(",");
            if (parts.length == 4) {
                return Long.parseLong(parts[0]);
            }
        }
        return -1;
    }

    public void spSaveUserFavoriteSatellites(List<String> satelliteIds) {
        String favoriteSatellites = String.join(",", satelliteIds);
        sharedPreferences.edit()
                .putString("fav_sat", favoriteSatellites)
                .apply();
    }

    public List<String> spGetUserFavoriteSatellites() {
        String favoriteSatellites = sharedPreferences.getString("fav_sat", "");
        return favoriteSatellites.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(favoriteSatellites.split(",")));
    }

    public String spGetSatelliteClosestPass(int satelliteId) {
        return sharedPreferences.getString("sat_pass_" + satelliteId, "NONE,,");
    }

    public String spGetSatellitePos(int satelliteId) {
        return sharedPreferences.getString("sat_pos_" + satelliteId, "NONE,,");
    }

    public String spGetSatelliteTLE(int satelliteId) {
        return sharedPreferences.getString("sat_tle_" + satelliteId, "NONE,,");
    }

    private boolean[] spIsSatelliteDataStale(int satelliteId) {
        String passData = spGetSatelliteClosestPass(satelliteId);
        String posData = spGetSatellitePos(satelliteId);
        String tleData = spGetSatelliteTLE(satelliteId);

        long threshold = 3600; // One hour
        return new boolean[]{
                passData.equals("NONE,,") || SatUtils.isStale(Long.parseLong(passData.split(",")[0]), threshold),
                posData.equals("NONE,,") || SatUtils.isStale(Long.parseLong(posData.split(",")[0]), threshold),
                tleData.equals("NONE,,") // TLE is never outdated
        };
    }

    public void spSaveAndUpdateSatelliteData(SatelliteManager satelliteManager, ICacheUpdateCallback callback) {
        List<String> satelliteIds = spGetUserFavoriteSatellites();
        if (satelliteIds.isEmpty()) {
            callback.onComplete();
            return;
        }

        ObserverLocation curr = spGetUserLocation();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (String satelliteId : satelliteIds) {
            int id = Integer.parseInt(satelliteId);

            Log.d("STORAGE", "pass/" + satelliteId + ": " + spGetSatelliteClosestPass(id));
            Log.d("STORAGE", "pos/" + satelliteId + ": " + spGetSatellitePos(id));
            Log.d("STORAGE", "tle/" + satelliteId + ": " + spGetSatelliteTLE(id));

            boolean[] stale = spIsSatelliteDataStale(id);
            if (stale[0]) saveSatellitePasses(editor, satelliteManager, id, curr);
            if (stale[1]) saveSatellitePositions(editor, satelliteManager, id, curr);
            if (stale[2]) saveSatelliteTLE(editor, satelliteManager, id);
        }

        editor.apply();
        callback.onComplete();
    }

    private void saveSatellitePasses(SharedPreferences.Editor editor, SatelliteManager manager, int id, ObserverLocation curr) {
        Log.d("STORAGE", "Updating data for ID: " + id);
        manager.fetchSatelliteVisualPasses(id, curr.getLatitude(), curr.getLongitude(), curr.getAltitude(), 7, 60, new IN2YOCallback() {
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

    private void saveSatellitePositions(SharedPreferences.Editor editor, SatelliteManager manager, int id, ObserverLocation curr) {
        Log.d("STORAGE", "Updating data for ID: " + id);
        manager.fetchSatellitePositions(id, curr.getLatitude(), curr.getLongitude(), curr.getAltitude(), 1, new IN2YOCallback() {
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

    private void saveSatelliteTLE(SharedPreferences.Editor editor, SatelliteManager manager, int id) {
        Log.d("STORAGE", "Updating data for ID: " + id);
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
}