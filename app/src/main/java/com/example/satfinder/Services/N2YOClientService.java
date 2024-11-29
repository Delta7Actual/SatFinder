package com.example.satfinder.Services;

import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class N2YOClientService {
    private final IN2YOApiService apiService;

    public N2YOClientService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.n2yo.com/rest/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IN2YOApiService.class);
    }

    public void getSatellitePositions(String apiKey,
                                     int id,
                                     float observer_lat,
                                     float observer_lng,
                                     float observer_alt,
                                     int seconds,
                                     Callback<SatellitePositionsResponse> callback) {

        Call<SatellitePositionsResponse> call = apiService.getSatellitePositions(apiKey,
                id,
                observer_lat,
                observer_lng,
                observer_alt,
                seconds);

        call.enqueue(callback);
    }

    public void getSatellitePasses(String apiKey,
                                   int satelliteId,
                                   float observer_lat,
                                   float observer_lng,
                                   float observer_alt,
                                   int days,
                                   int min_visibility,
                                   Callback<SatelliteVisualPassesResponse> callback) {
        // Call the Retrofit method for satellite passes
        Call<SatelliteVisualPassesResponse> call = apiService.getSatelliteVisualPasses(apiKey,
                satelliteId,
                observer_lat,
                observer_lng,
                observer_alt,
                days,
                min_visibility);

        call.enqueue(callback);
    }

    // TODO: add more methods for other API endpoints
}

