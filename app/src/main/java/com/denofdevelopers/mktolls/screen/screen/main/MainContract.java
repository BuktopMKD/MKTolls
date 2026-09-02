package com.denofdevelopers.mktolls.screen.screen.main;

import com.denofdevelopers.mktolls.common.presenter.BasePresenter;
import com.denofdevelopers.mktolls.model.CurrentLocation;

public interface MainContract {

    interface Presenter extends BasePresenter<MainActivity> {
        void shouldStartNextActivity(String start, String end);

        boolean hasPlayServices(boolean shouldDisplayNoServicesPopup);

        void checkPermissions();

        void connectApiClient();

        CurrentLocation getLocation();
    }

    interface View {
        void showAlertMessage(String message);

        void startNextActivity(String fromLocation, String toLocation);
    }
}
