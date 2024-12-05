package com.example.satfinder.Services;

import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface defining the N2YO API endpoints for satellite data.
 * Uses Retrofit annotations to map the HTTP requests to the respective API methods.
 */
public interface IN2YOApiService {

    /**
     * Fetches the visual passes of a satellite over a specified number of days.
     *
     * @param id the unique identifier of the satellite.
     * @param observer_lat the latitude of the observer (in degrees).
     * @param observer_lng the longitude of the observer (in degrees).
     * @param observer_alt the altitude of the observer (in meters).
     * @param days the number of days for which to retrieve the visual passes.
     * @param min_visibility the minimum visibility threshold (in degrees).
     * @param apiKey the API key used for authentication.
     * @return a {@link Call} object for the {@link SatelliteVisualPassesResponse}.
     */
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

    /**
     * Fetches the current positions of a satellite for a given number of seconds.
     *
     * @param id the unique identifier of the satellite.
     * @param observer_lat the latitude of the observer (in degrees).
     * @param observer_lng the longitude of the observer (in degrees).
     * @param observer_alt the altitude of the observer (in meters).
     * @param seconds the time interval (in seconds) to fetch satellite positions.
     * @param apiKey the API key used for authentication.
     * @return a {@link Call} object for the {@link SatellitePositionsResponse}.
     */
    @GET("satellite/positions/{id}/{observer_lat}/{observer_lng}/{observer_alt}/{seconds}")
    Call<SatellitePositionsResponse> getSatellitePositions(
            @Path("id") int id,
            @Path("observer_lat") float observer_lat,
            @Path("observer_lng") float observer_lng,
            @Path("observer_alt") float observer_alt,
            @Path("seconds") int seconds,
            @Query("apiKey") String apiKey
    );

    /**
     * Fetches the Two-Line Element (TLE) data for a specific satellite.
     *
     * @param id the unique identifier of the satellite.
     * @param apiKey the API key used for authentication.
     * @return a {@link Call} object for the {@link SatelliteTLEResponse}.
     */
    @GET("satellite/tle/{id}")
    Call<SatelliteTLEResponse> getSatelliteTLE(
            @Path("id") int id,
            @Query("apiKey") String apiKey
    );

    // TODO: add more methods for other API endpoints
}
