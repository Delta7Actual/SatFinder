package com.example.satfinder.Services;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IN2YOApiService {
    @GET("satellite/visualpasses/{id}/{observer_lat}/{observer_lng}/{observer_alt}/{days}/{min_visibility}")
    Call<SatelliteVisualPassesResponse> getSatelliteVisualPasses(
            @Path("id") int id,
            @Path("observer_lat") float observer_lat,
            @Path("observer_lng") float observer_lng,
            @Path("observer_alt") float observer_alt,
            @Path("days") int days,
            @Path("min_visibility") int min_visibility,
            @Query("apiKey") String apiKey
    );

    // Example of another endpoint for satellite position
    @GET("satellite/positions/{id}/{observer_lat}/{observer_lng}/{observer_alt}/{seconds}")
    Call<SatellitePositionsResponse> getSatellitePositions(
            @Path("id") int id,
            @Path("observer_lat") float observer_lat,
            @Path("observer_lng") float observer_lng,
            @Path("observer_alt") float observer_alt,
            @Path("seconds") int seconds,
            @Query("apiKey") String apiKey
    );

    @GET("satellite/tle/{id}")
    Call<SatelliteTLEResponse> getSatelliteTLE(
            @Path("id") int id,
            @Query("apiKey") String apiKey
    );

    // TODO: add more methods like this for other endpoints
}
