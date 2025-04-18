package com.example.satfinder.Services;

import com.example.satfinder.BuildConfig;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A service class for interacting with the N2YO API using Retrofit.
 * This service is responsible for retrieving satellite data such as positions, passes, and TLE data.
 */
public class N2YOClientService {

    private static N2YOClientService instance;
    private static final IN2YOApiService apiService;
    private final String apiKey = BuildConfig.API_KEY;

    /*
      Static initializer to set up Retrofit instance and create the API service.
     */
    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.n2yo.com/rest/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IN2YOApiService.class);
    }

    /**
     * Singleton access method to get the instance of N2YOClientService.
     *
     * @return the single instance of N2YOClientService.
     */
    public static synchronized N2YOClientService getInstance() {
        if (instance == null) {
            instance = new N2YOClientService();
        }
        return instance;
    }

    /**
     * Fetches the current [seconds] positions of a satellite.
     *
     * @param id the satellite's unique identifier.
     * @param observer_lat the latitude of the observer.
     * @param observer_lng the longitude of the observer.
     * @param observer_alt the altitude of the observer (in meters).
     * @param seconds number of future positions to return.
     * @param callback the callback to handle the response.
     */
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

    /**
     * Fetches visual passes of a satellite over a specified number of days.
     *
     * @param satelliteId the satellite's unique identifier.
     * @param observer_lat the latitude of the observer.
     * @param observer_lng the longitude of the observer.
     * @param observer_alt the altitude of the observer (in meters).
     * @param days the number of days for which to retrieve visual passes.
     * @param min_visibility the minimum visibility threshold (in degrees).
     * @param callback the callback to handle the response.
     */
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

    /**
     * Fetches the Two-Line Element (TLE) data for a specific satellite.
     *
     * @param id the satellite's unique identifier.
     * @param callback the callback to handle the response.
     */
    public void getSatelliteTLE(
            int id,
            Callback<SatelliteTLEResponse> callback) {

        Call<SatelliteTLEResponse> call = apiService.getSatelliteTLE(id, apiKey);
        call.enqueue(callback);
    }
}