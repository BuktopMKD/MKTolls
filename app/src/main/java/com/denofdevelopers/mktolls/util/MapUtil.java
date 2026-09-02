package com.denofdevelopers.mktolls.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.denofdevelopers.mktolls.model.RouteLeg;
import com.denofdevelopers.mktolls.model.RouteStep;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MapUtil {

    private static final String TAG = MapUtil.class.getSimpleName();

    public static String formatMapServiceQueryParameters(LatLng location) {
        return location.latitude + "," + location.longitude;
    }

    public static LatLng getLocationPoints(Context context, String location) {
        LatLng latLng = null;
        Double longitude;
        Double latitude;
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());

        location = selectedVeles(location);

        try {
            List<Address> addresses = geoCoder.getFromLocationName(location, 1);
            if (addresses.size() > 0) {
                longitude = addresses.get(0).getLongitude();
                latitude = addresses.get(0).getLatitude();
                latLng = new LatLng(latitude, longitude);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return latLng;
    }

    public static List<LatLng> getRoutePoints(RouteLeg routeLeg) {
        List<LatLng> route = new ArrayList<>();
        route.add(new LatLng(routeLeg.startLocation.lat, routeLeg.startLocation.lng));
        for (RouteStep routeStep : routeLeg.steps) {
            List<LatLng> decodedPolyline = PolyUtil.decode(routeStep.polyline.points);
            for (LatLng point : decodedPolyline) {
                route.add(point);
            }
        }
        route.add(new LatLng(routeLeg.endLocation.lat, routeLeg.endLocation.lng));
        return route;
    }

    //TODO quick fix for selected city Veles
    private static String selectedVeles(String enteredLocation) {
        if (enteredLocation.equals("veles") || enteredLocation.equals(" veles") || enteredLocation.equals("veles ") || enteredLocation.equals(" veles ")
                || enteredLocation.equals("Veles") || enteredLocation.equals(" Veles") || enteredLocation.equals("Veles ") || enteredLocation.equals(" Veles ")
                || enteredLocation.equals("велес") || enteredLocation.equals("Велес")) {
            return "veles city";
        } else {
            return enteredLocation;
        }
    }
}
