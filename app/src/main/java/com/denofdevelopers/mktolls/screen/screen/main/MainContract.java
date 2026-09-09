package com.denofdevelopers.mktolls.screen.screen.main;

import com.denofdevelopers.mktolls.common.presenter.BasePresenter;
import com.denofdevelopers.mktolls.model.CurrentLocation;

import android.location.Location;
import rx.functions.Action1;

public interface MainContract {

    interface Presenter extends BasePresenter<MainActivity> {
        void shouldStartNextActivity(String start, String end, String middle);

        boolean hasPlayServices(boolean shouldDisplayNoServicesPopup);

        void checkPermissions();

        void connectApiClient();

        CurrentLocation getLocation();

        void getAddressForLocation(Location location, Action1<String> callback);
    }

    interface View {
        void showAlertMessage(String message);

        void startNextActivity(String fromLocation, String toLocation, String midLocation);
    }
}
