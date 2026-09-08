package com.denofdevelopers.mktolls.screen.screen.main;

import android.location.Address;
import android.location.Location;
import android.util.Log;

import com.denofdevelopers.mktolls.BuildConfig;
import com.denofdevelopers.mktolls.R;
import com.denofdevelopers.mktolls.location.LocationHelper;
import com.denofdevelopers.mktolls.model.CurrentLocation;
import com.denofdevelopers.mktolls.network.ApiService;
import com.denofdevelopers.mktolls.util.MapUtil;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private LocationHelper locationHelper;
    private Subscription request;
    private ApiService apiService;
    private MainActivity activity;

    public MainPresenter(ApiService apiService, MainActivity activity) {
        this.apiService = apiService;
        this.activity = activity;
    }


    @Override
    public void shouldStartNextActivity(String start, String end) {
        Observable.fromCallable(() -> areLocationsValid(start, end))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isValid -> {
                    if (isValid) {
                        activity.startNextActivity(start, end);
                    } else {
                        activity.showAlertMessage(activity.getString(R.string.address_not_found));
                        activity.hideProgress();
                    }
                }, throwable -> {
                    Log.e("MainPresenter", "Error validating locations", throwable);
                    activity.showAlertMessage(activity.getString(R.string.address_not_found));
                    activity.hideProgress();
                });
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

    public void takeView() {
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
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (locationHelper != null) {
                locationHelper.connectApiClient();
            }
        }, 500);
    }

    @Override
    public CurrentLocation getLocation() {
        Location mLastLocation = locationHelper.getLocation();
        if (mLastLocation != null) {
            CurrentLocation reportProblemLocation = new CurrentLocation();
            reportProblemLocation.setLatitude(mLastLocation.getLatitude());
            reportProblemLocation.setLongitude(mLastLocation.getLongitude());
            return reportProblemLocation;
        }

        return null;
    }

    @Override
    public void getAddressForLocation(Location location, Action1<String> callback) {
        Observable.fromCallable(() -> getAddress(location))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> {
                    if (address != null) {
                        callback.call(address.getAddressLine(0));
                    } else {
                        callback.call("");
                    }
                }, throwable -> {
                    Log.e("MainPresenter", "Error getting address", throwable);
                    callback.call("");
                });
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
        if (locationHelper != null) {
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
