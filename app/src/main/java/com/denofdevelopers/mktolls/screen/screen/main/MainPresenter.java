package com.denofdevelopers.mktolls.screen.screen.main;

import android.location.Address;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.denofdevelopers.mktolls.BuildConfig;
import com.denofdevelopers.mktolls.R;
import com.denofdevelopers.mktolls.location.LocationHelper;
import com.denofdevelopers.mktolls.model.CurrentLocation;
import com.denofdevelopers.mktolls.network.ApiService;
import com.denofdevelopers.mktolls.util.MapUtil;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.adapter.rxjava.Result;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private LocationHelper locationHelper;
    private Subscription request;
    private ApiService apiService;
    private MainActivity activity;

    public MainPresenter(ApiService apiService, MainActivity activity) {
        this.apiService = apiService;
        this.activity = activity;
        takeView();
    }


    @Override
    public void shouldStartNextActivity(String start, String end) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (areLocationsValid(start, end)) {
                activity.startNextActivity(start, end);
            } else {
                activity.showAlertMessage(activity.getString(R.string.address_not_found));
                activity.hideProgress();
            }
        }, 200);
    }

    private boolean areLocationsValid(String start, String end) {
        return convertLocations(start) != null && convertLocations(end) != null;
    }

    private LatLng convertLocations(String location) {
        return MapUtil.getLocationPoints(activity, location);
    }

    private void getLocationByAddress(String address) {
        request = apiService.getPointsFromAddress(address, BuildConfig.API_KEY_GOOGLE_MAPS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Result<Void>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("tag", e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Result<Void> result) {
                        Log.d("tag", result.response().code() + "");
                    }
                });
    }

    private void takeView() {
        locationHelper = new LocationHelper(activity);
        setupLocation();
    }

    private void setupLocation() {
        locationHelper.checkPermission();
        if (hasPlayServices(true)) {
            // Building the GoogleApi client
            locationHelper.buildGoogleApiClient();
        }
    }

    public void connectApiClient() {
        if (locationHelper.getGoogleApiClient() != null && !locationHelper.getGoogleApiClient().isConnected()) {
            locationHelper.connectApiClient();
        }
    }

    @Override
    public CurrentLocation getLocation() {
        Location mLastLocation = locationHelper.getLocation();
        if (mLastLocation != null) {
            CurrentLocation reportProblemLocation = new CurrentLocation();
            reportProblemLocation.setLatitude(mLastLocation.getLatitude());
            reportProblemLocation.setLongitude(mLastLocation.getLongitude());
            Address locationAddress = getAddress(mLastLocation);
            if (locationAddress != null) {
                reportProblemLocation.setName(locationAddress.getAddressLine(0));
            }
            return reportProblemLocation;
        }

        return null;
    }

    private Address getAddress(Location location) {
        return locationHelper.getAddress(location.getLatitude(),
                location.getLongitude());
    }

    @Override
    public boolean hasPlayServices(boolean shouldDisplayNoServicesPopup) {
        return locationHelper.checkPlayServices(shouldDisplayNoServicesPopup);
    }

    @Override
    public void checkPermissions() {
        locationHelper.checkPermission();
    }

    @Override
    public void dropView() {
        cancelRequest();
        if (hasPlayServices(false)) {
            locationHelper.disconnectApiClient();
        }
    }

    private void cancelRequest() {
        if (request != null) {
            request.unsubscribe();
            request = null;
        }
    }
}
