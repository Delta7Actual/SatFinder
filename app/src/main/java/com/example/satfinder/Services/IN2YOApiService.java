package com.example.satfinder.Services;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IN2YOApiService {
    @GET("satellite/visualpasses")
    Call<SatelliteVisualPassesResponse> getSatelliteVisualPasses(
            @Query("apiKey") String apiKey,
            @Query("id") int id,
            @Query("observer_lat") float observer_lat,
            @Query("observer_lng") float observer_lng,
            @Query("observer_alt") float observer_alt,
            @Query("days") int days,
            @Query("min_visibility") int min_visibility
    );

    // Example of another endpoint for satellite position
    @GET("satellite/positions")
    Call<SatellitePositionsResponse> getSatellitePositions(
            @Query("apiKey") String apiKey,
            @Query("id") int id,
            @Query("observer_lat") float observer_lat,
            @Query("observer_lng") float observer_lng,
            @Query("observer_alt") float observer_alt,
            @Query("seconds") int seconds
    );

    // TODO: add more methods like this for other endpoints
}
