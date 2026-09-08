package com.denofdevelopers.mktolls.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.denofdevelopers.mktolls.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationHelper implements LocationPermissionsUtil.PermissionResultCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private Context context;
    private Activity currentActivity;

    private boolean isPermissionGranted;

    private Location mLastLocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private ArrayList<String> permissions = new ArrayList<>();
    private LocationPermissionsUtil locationPermissionsUtil;

    private static final int PLAY_SERVICES_REQUEST = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;

    public LocationHelper(Context context) {
        this.context = context;
        this.currentActivity = (Activity) context;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);

        locationPermissionsUtil = new LocationPermissionsUtil(context, this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public void checkPermission() {
        locationPermissionsUtil.check_permission(permissions, context.getString(R.string.gps_permission), 1);
    }

    private boolean isPermissionGranted() {
        return isPermissionGranted;
    }

    public boolean checkPlayServices(boolean shouldDisplayNoServicesPopup) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (shouldDisplayNoServicesPopup) {
                if (googleApiAvailability.isUserResolvableError(resultCode)) {
                    googleApiAvailability.getErrorDialog(currentActivity, resultCode,
                            PLAY_SERVICES_REQUEST).show();
                } else {
                    showToast(context.getString(R.string.no_google_play_services));
                }
            }
            return false;
        }
        return true;
    }

    public Location getLocation() {
        if (isPermissionGranted()) {
            try {
                if (mLastLocation != null) {
                    return mLastLocation;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(currentActivity, location -> {
                    if (location != null) {
                        mLastLocation = location;
                    }
                });
                return mLastLocation;
            } catch (SecurityException e) {
                Log.e("--->", e.getMessage(), e.getCause());
            }
        }
        return null;
    }

    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0);
            }
        } catch (IOException e) {
            Log.e("--->", e.getMessage(), e.getCause());
        }
        return null;
    }

    public void buildGoogleApiClient() {
        createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                }
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        mSettingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(currentActivity, locationSettingsResponse -> startLocationUpdates())
                .addOnFailureListener(currentActivity, e -> {
                    if (e instanceof ResolvableApiException) {
                        try {
                            ((ResolvableApiException) e).startResolutionForResult(currentActivity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore
                        }
                    }
                });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }

    public void connectApiClient() {
        if (mLocationRequest == null) {
            buildGoogleApiClient();
        } else {
            startLocationUpdates();
        }
    }

    public Object getGoogleApiClient() {
        return mFusedLocationClient;
    }

    public void disconnectApiClient() {
        if (mFusedLocationClient != null && mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION", "GRANTED");
        isPermissionGranted = true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY", "GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION", "DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION", "NEVER ASK AGAIN");
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
