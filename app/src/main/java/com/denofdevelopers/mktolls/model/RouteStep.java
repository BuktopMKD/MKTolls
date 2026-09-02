package com.denofdevelopers.mktolls.model;

import com.google.gson.annotations.SerializedName;

public final class RouteStep {

    @SerializedName("end_location")
    public final RoutePoint endLocation;
    @SerializedName("start_location")
    public final RoutePoint startLocation;
    public final MyPolyline polyline;

    public RouteStep(RoutePoint endLocation, RoutePoint startLocation, MyPolyline polyline) {
        this.endLocation = endLocation;
        this.startLocation = startLocation;
        this.polyline = polyline;
    }
}
