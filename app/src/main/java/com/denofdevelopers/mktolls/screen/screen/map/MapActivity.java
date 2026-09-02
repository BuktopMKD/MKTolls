package com.denofdevelopers.mktolls.screen.screen.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.denofdevelopers.mktolls.BuildConfig;
import com.denofdevelopers.mktolls.R;
import com.denofdevelopers.mktolls.application.App;
import com.denofdevelopers.mktolls.common.Constants;
import com.denofdevelopers.mktolls.model.RouteResponse;
import com.denofdevelopers.mktolls.model.Toll;
import com.denofdevelopers.mktolls.network.ApiService;
import com.denofdevelopers.mktolls.util.MapUtil;
import com.denofdevelopers.mktolls.util.NetworkUtil;
import com.denofdevelopers.mktolls.util.ToastUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.adapter.rxjava.Result;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @Inject
    ApiService apiService;
    private Subscription request;
    private GoogleMap googleMap;
    private static final String START_POINT_EXTRA = "start.point.extra";
    private static final String END_POINT_EXTRA = "end.point.extra";
    private static final String CATEGORY_EXTRA = "category.extra";
    private static final String TOLL_POINTS_EXTRA = "toll.points.extra";
    private String category;

    public static void start(Context context, String startPoint, String endPoint, ArrayList<Toll> tollPoints, String category) {
        Intent intent = new Intent(context, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(TOLL_POINTS_EXTRA, tollPoints);
        bundle.putString(START_POINT_EXTRA, startPoint);
        bundle.putString(END_POINT_EXTRA, endPoint);
        bundle.putString(CATEGORY_EXTRA, category);
        intent.putExtras(bundle);
        ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_APPOINTMENT_DETAILS);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        setupActivityComponent();
        initToolBar();
        progressBar.setVisibility(View.VISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon();
        toolbar.setNavigationOnClickListener(v -> {
            setResult(Constants.REQUEST_CODE_APPOINTMENT_DETAILS);
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        drawStartAndEndPoint();
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getRoutePoints(LatLng startPoint, LatLng endPoint) {
        request = apiService.getPointsBetweenTwoLocations(MapUtil.formatMapServiceQueryParameters(startPoint),
                MapUtil.formatMapServiceQueryParameters(endPoint), false, BuildConfig.API_KEY_GOOGLE_MAPS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Result<RouteResponse>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetworkUtil.isConnected(MapActivity.this)) {
                            ToastUtil.showToast(MapActivity.this, getString(R.string.no_internet_connection));
                        } else {
                            ToastUtil.showToast(MapActivity.this, getString(R.string.generic_error));
                        }
                    }

                    @Override
                    public void onNext(Result<RouteResponse> result) {
                        if (result.response().code() == 200) {
                            RouteResponse routeResponse = result.response().body();
                            if (routeResponse != null && routeResponse.routes.size() > 0) {
                                addPolyline(MapUtil.getRoutePoints(routeResponse.routes.get(0).routeLegs.get(0)));
                                Bundle bundle = getIntent().getExtras();
                                ArrayList<Toll> tollPoints = bundle.getParcelableArrayList(TOLL_POINTS_EXTRA);
                                drawTolls(tollPoints);
                                addZoom(MapUtil.getRoutePoints(routeResponse.routes.get(0).routeLegs.get(0)));
                            }
                        }
                    }
                });
    }

    private void drawStartAndEndPoint() {
        Bundle bundle = getIntent().getExtras();
        String startPoint = bundle.getString(START_POINT_EXTRA);
        String endPoint = bundle.getString(END_POINT_EXTRA);
        category = bundle.getString(CATEGORY_EXTRA);
        LatLng startLocation = MapUtil.getLocationPoints(this, startPoint);
        LatLng endLocation = MapUtil.getLocationPoints(this, endPoint);
        addMarkerStartEnd(startLocation, startPoint, true);
        addMarkerStartEnd(endLocation, endPoint, false);
        getRoutePoints(startLocation, endLocation);
    }

    private void addMarkerToll(LatLng latLng, String title, String price) {
        googleMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(price)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.toll)));
    }

    private void addMarkerStartEnd(LatLng latLng, String title, boolean isStartLocation) {
        googleMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(isStartLocation ? "Start location" : "End location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    private void addPolyline(List<LatLng> route) {
        if (route.isEmpty())
            return;
        PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);
        polylineOptions.color(Color.BLUE);
        for (LatLng point : route) {
            polylineOptions.add(point);
        }
        googleMap.addPolyline(polylineOptions);
    }

    private void drawTolls(ArrayList<Toll> tolls) {
        for (Toll toll : tolls) {
            addMarkerToll(new LatLng(toll.lat, toll.lng), toll.tollName, getCategoryPrice(toll, category));
        }
    }

    private String getCategoryPrice(Toll toll, String category) {
        double resultDen = 0;
        double resultEur = 0.0;
        if (category.equals(getResources().getString(R.string.category_1A))) {
            resultDen = resultDen + toll.categoryOneADen;
            resultEur = resultEur + toll.categoryOneAEuro;
        } else if (category.equals(getResources().getString(R.string.category_1B))) {
            resultDen = resultDen + toll.categoryOneDen;
            resultEur = resultEur + toll.categoryOneEuro;
        } else if (category.equals(getResources().getString(R.string.category_2))) {
            resultDen = resultDen + toll.categoryTwoDen;
            resultEur = resultEur + toll.categoryTwoEuro;
        } else if (category.equals(getResources().getString(R.string.category_3))) {
            resultDen = resultDen + toll.categoryThreeDen;
            resultEur = resultEur + toll.categoryThreeEuro;
        } else if (category.equals(getResources().getString(R.string.category_4))) {
            resultDen = resultDen + toll.categoryFourDen;
            resultEur = resultEur + toll.categoryFourEuro;
        }
        return (int) resultDen + " den  |  " + resultEur + " eur";
    }

    private void addZoom(List<LatLng> route) {
        if (!route.isEmpty() && route.size() > 2) {
            LatLng centerPoint = route.get(route.size() / 2);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(centerPoint, 8);
            googleMap.animateCamera(update);
        }
    }

    private void cancelRequest() {
        if (request != null) {
            request.unsubscribe();
            request = null;
        }
    }

    @Override
    protected void onStop() {
        cancelRequest();
        super.onStop();
    }

    protected void setupActivityComponent() {
        App.get(this).getAppComponent().plus(this);
    }

}
