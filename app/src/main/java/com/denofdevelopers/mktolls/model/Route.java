package com.denofdevelopers.mktolls.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Route {
    @SerializedName("legs")
    public final List<RouteLeg> routeLegs;

    public Route(List<RouteLeg> routeLegs) {
        this.routeLegs = routeLegs;
    }
}
