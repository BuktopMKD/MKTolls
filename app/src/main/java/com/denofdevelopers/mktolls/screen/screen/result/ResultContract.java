package com.denofdevelopers.mktolls.screen.screen.result;

import com.denofdevelopers.mktolls.common.presenter.BasePresenter;
import com.google.android.gms.maps.model.LatLng;

public interface ResultContract {

    interface Presenter extends BasePresenter<ResultActivity> {
        void getRoutePoints(LatLng startPoint, LatLng endPoint);
    }

    interface View {

        void showError(String message);

        void showProgress();

        void showMessage(String message);

        void hideProgress();
    }
}
