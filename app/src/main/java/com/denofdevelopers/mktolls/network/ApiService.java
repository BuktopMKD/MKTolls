package com.denofdevelopers.mktolls.network;

import com.denofdevelopers.mktolls.model.RouteResponse;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiService {
    @GET("directions/json")
    Observable<Result<RouteResponse>> getPointsBetweenTwoLocations(@Query("origin") String origin, @Query("destination") String destination,
                                                                   @Query("waypoints") String waypoints,
                                                                   @Query("sensor") boolean sensor, @Query("key") String key);

    @GET("geocode/json")
    Observable<Result<Void>> getPointsFromAddress(@Query("address") String address, @Query("key") String key);
}
