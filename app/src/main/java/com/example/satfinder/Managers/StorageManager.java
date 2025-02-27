package com.example.satfinder.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageManager {

    private static StorageManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    private static final String PREFS_NAME = "user_data";

    private StorageManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
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

    /** Saves user location */
    public void saveUserLocation(ObserverLocation location) {
        sharedPreferences.edit()
                .putString("userLocation", gson.toJson(location))
                .apply();
    }

    /** Retrieves user location */
    public ObserverLocation getUserLocation() {
        String json = sharedPreferences.getString("userLocation", null);
        return (json != null) ? gson.fromJson(json, ObserverLocation.class) : new ObserverLocation();
    }

    /** Saves favorite satellites */
    public void saveUserFavoriteSatellites(List<String> satelliteIds) {
        sharedPreferences.edit()
                .putString("fav_satelliteIds", gson.toJson(satelliteIds))
                .apply();
    }

    /** Retrieves favorite satellites */
    public List<String> getUserFavoriteSatellites() {
        String json = sharedPreferences.getString("fav_satelliteIds", null);
        Type type = new TypeToken<List<String>>() {}.getType();
        return (json != null) ? gson.fromJson(json, type) : new ArrayList<>();
    }

    /** Saves satellite data (passes, positions, and TLE) */
    public void saveSatelliteData(SatelliteManager satelliteManager) {
        List<String> satelliteIds = getUserFavoriteSatellites();
        if (satelliteIds.isEmpty()) return;

        ObserverLocation curr = getUserLocation();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (String satelliteId : satelliteIds) {
            int id = Integer.parseInt(satelliteId);
            saveSatellitePasses(editor, satelliteManager, id, curr);
            saveSatellitePositions(editor, satelliteManager, id, curr);
            saveSatelliteTLE(editor, satelliteManager, id);
        }

        editor.apply();
    }

    private void saveSatellitePasses(SharedPreferences.Editor editor, SatelliteManager manager, int id, ObserverLocation curr) {
        manager.fetchSatelliteVisualPasses(id
                , curr.getLatitude()
                , curr.getLongitude()
                , curr.getAltitude()
                , 7
                , 60
                , new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
                if (svpResponse == null || svpResponse.getPasses().isEmpty()) return;

                Map<String, Object> passData = new HashMap<>();
                passData.put("timestamp", System.currentTimeMillis());
                passData.put("passInfo", svpResponse.getPasses().get(0));

                editor.putString("sat_pass_" + id, gson.toJson(passData));
                editor.apply();
            }

            @Override
            public void onError(String errorMessage) {
                editor.putString("sat_pass_" + id, null);
                editor.apply();
            }
        });
    }

    private void saveSatellitePositions(SharedPreferences.Editor editor, SatelliteManager manager, int id, ObserverLocation curr) {
        manager.fetchSatellitePositions(id, curr.getLatitude(), curr.getLongitude(), curr.getAltitude(), 1, new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatellitePositionsResponse spResponse = (SatellitePositionsResponse) response;
                if (spResponse == null || spResponse.getPositions().isEmpty()) return;

                Map<String, Object> posData = new HashMap<>();
                posData.put("timestamp", System.currentTimeMillis());
                posData.put("latitude", spResponse.getPositions().get(0).getSatlatitude());
                posData.put("longitude", spResponse.getPositions().get(0).getSatlongitude());
                posData.put("altitude", spResponse.getPositions().get(0).getSataltitude());

                editor.putString("sat_pos_" + id, gson.toJson(posData));
                editor.apply();
            }

            @Override
            public void onError(String errorMessage) {
                editor.putString("sat_pos_" + id, null);
                editor.apply();
            }
        });
    }

    private void saveSatelliteTLE(SharedPreferences.Editor editor, SatelliteManager manager, int id) {
        manager.fetchSatelliteTLE(id, new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatelliteTLEResponse stleResponse = (SatelliteTLEResponse) response;
                if (stleResponse == null || stleResponse.getTle() == null) return;

                Map<String, Object> tleData = new HashMap<>();
                tleData.put("timestamp", System.currentTimeMillis());
                tleData.put("tle", stleResponse.getTle());

                editor.putString("sat_tle_" + id, gson.toJson(tleData));
                editor.apply();
            }

            @Override
            public void onError(String errorMessage) {
                editor.putString("sat_tle_" + id, null);
                editor.apply();
            }
        });
    }

    /** Retrieves satellite data */
    public Map<String, Object> getSatelliteData(String key) {
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : null;
    }
}