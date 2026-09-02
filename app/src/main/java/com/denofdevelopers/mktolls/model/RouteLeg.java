package com.denofdevelopers.mktolls.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class RouteLeg {

    public final List<RouteStep> steps;
    @SerializedName("start_location")
    public final RoutePoint startLocation;
    @SerializedName("end_location")
    public final RoutePoint endLocation;

    public RouteLeg(List<RouteStep> steps, RoutePoint startLocation, RoutePoint endLocation) {
        this.steps = steps;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }
}
