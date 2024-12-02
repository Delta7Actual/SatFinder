package com.example.satfinder.Services;

import com.example.satfinder.BuildConfig;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class N2YOClientService {

    private static N2YOClientService instance;
    private static final IN2YOApiService apiService;
    private final String apiKey = BuildConfig.API_KEY;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.n2yo.com/rest/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IN2YOApiService.class);
    }

    public static synchronized N2YOClientService getInstance() {
        if (instance == null) {
            instance = new N2YOClientService();
        }
        return instance;
    }

    public void getSatellitePositions(
            int id,
            float observer_lat,
            float observer_lng,
            float observer_alt,
            int seconds,
            Callback<SatellitePositionsResponse> callback) {

        Call<SatellitePositionsResponse> call = apiService.getSatellitePositions(
                id,
                observer_lat,
                observer_lng,
                observer_alt,
                seconds,
                apiKey
        );

        call.enqueue(callback);
    }

    public void getSatellitePasses(
            int satelliteId,
            float observer_lat,
            float observer_lng,
            float observer_alt,
            int days,
            int min_visibility,
            Callback<SatelliteVisualPassesResponse> callback) {

        Call<SatelliteVisualPassesResponse> call = apiService.getSatelliteVisualPasses(
                satelliteId,
                observer_lat,
                observer_lng,
                observer_alt,
                days,
                min_visibility,
                apiKey
        );

        call.enqueue(callback);
    }

    public void getSatelliteTLE(
            int id,
            Callback<SatelliteTLEResponse> callback) {

        Call<SatelliteTLEResponse> call = apiService.getSatelliteTLE(id, apiKey);
        call.enqueue(callback);
    }

    // TODO: add more methods for other API endpoints
}
