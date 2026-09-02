package com.denofdevelopers.mktolls.model;

import android.text.TextUtils;

public final class CurrentLocation {
    private Double longitude;
    private Double latitude;

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    private String name;

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNameEmpty() {
        return TextUtils.isEmpty(name);
    }
}
