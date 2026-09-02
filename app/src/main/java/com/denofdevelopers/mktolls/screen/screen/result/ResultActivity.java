package com.denofdevelopers.mktolls.screen.screen.result;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denofdevelopers.mktolls.BuildConfig;
import com.denofdevelopers.mktolls.R;
import com.denofdevelopers.mktolls.application.App;
import com.denofdevelopers.mktolls.common.Constants;
import com.denofdevelopers.mktolls.di.NamedValues;
import com.denofdevelopers.mktolls.model.RouteResponse;
import com.denofdevelopers.mktolls.model.Toll;
import com.denofdevelopers.mktolls.network.ApiService;
import com.denofdevelopers.mktolls.screen.screen.map.MapActivity;
import com.denofdevelopers.mktolls.util.MapUtil;
import com.denofdevelopers.mktolls.util.NetworkUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.adapter.rxjava.Result;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResultActivity extends AppCompatActivity implements ResultContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.calculateResult)
    TextView calculateResult;
    @BindView(R.id.errorLayout)
    ViewGroup errorLayout;
    @BindView(R.id.errorMessage)
    TextView errorMessage;
    @BindView(R.id.showMap)
    Button showMap;
    @Inject
    ApiService apiService;
    @Inject
    @Named(NamedValues.tollList)
    List<Toll> tolls;
    private ResultAdapter resultAdapter;
    private Subscription request;
    private String fromLocation = "";
    private String toLocation = "";
    private String category = "";
    private static final String FROM_LOCATION_EXTRA = "from.location.extra";
    private static final String TO_LOCATION_EXTRA = "to.location.extra";
    private static final String CATEGORY = "category";
    private ArrayList<LatLng> routePoints;
    private ArrayList<Toll> routeTolls;

    public static void start(Context context, String fromLocation, String toLocation, String category) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(FROM_LOCATION_EXTRA, fromLocation);
        intent.putExtra(TO_LOCATION_EXTRA, toLocation);
        intent.putExtra(CATEGORY, category);
        context.startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        setupActivityComponent();
        routePoints = new ArrayList<>();
        routeTolls = new ArrayList<>();

        getIntentExtras();
        initToolBar();
        setupUI();
        displayTolls();
    }

    private void displayTolls() {
        if (!areLocationsValid()) {
            if (!NetworkUtil.isConnected(this)) {
                showError(getString(R.string.no_internet_connection));
            } else {
                showMessage(getString(R.string.address_not_found));
            }
        } else {
            showProgress();
            getRoutePoints(convertLocations(fromLocation), convertLocations(toLocation));
        }
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon();
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupUI() {

        resultAdapter = new ResultAdapter(this, category);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(resultAdapter);

        DividerItemDecoration itemDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDivider);
    }

    @Override
    public void showProgress() {
        errorLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.showMap)
    public void onShowMapClick() {
        showProgress();
        MapActivity.start(this, fromLocation, toLocation, routeTolls, category);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_APPOINTMENT_DETAILS) {
            hideProgress();
        }
    }

    private void getIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        fromLocation = bundle.getString(FROM_LOCATION_EXTRA);
        toLocation = bundle.getString(TO_LOCATION_EXTRA);
        category = bundle.getString(CATEGORY);
    }

    private boolean areLocationsValid() {
        return convertLocations(fromLocation) != null && convertLocations(toLocation) != null;
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
                        hideProgress();
                        if (!NetworkUtil.isConnected(ResultActivity.this)) {
                            showError(getString(R.string.no_internet_connection));
                        } else {
                            showError(getString(R.string.generic_error));
                        }
                    }

                    @Override
                    public void onNext(Result<RouteResponse> result) {
                        hideProgress();
                        if (result.response().code() == 200) {
                            RouteResponse routeResponse = result.response().body();
                            if (routeResponse != null && routeResponse.routes.size() > 0) {
                                hideProgress();
                                routePoints.addAll(MapUtil.getRoutePoints(routeResponse.routes.get(0).routeLegs.get(0)));
                                redrawRecycleView();
                            } else {
                                showError(getString(R.string.generic_error));
                            }
                        }
                    }
                });
    }

    private LatLng convertLocations(String location) {
        return MapUtil.getLocationPoints(this, location);
    }

    @Override
    public void showError(String message) {
        progressBar.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
        errorMessage.setText(message);
    }

    @OnClick(R.id.retry)
    public void onRetry() {
        errorLayout.setVisibility(View.INVISIBLE);
        displayTolls();
    }

    private void redrawRecycleView() {
        for (Toll toll : tolls) {
            if (PolyUtil.isLocationOnPath(new LatLng(toll.lat, toll.lng), routePoints, false, Constants.RADIUS_TOLERANCE)) {
                routeTolls.add(toll);
            }
        }
        if (!routeTolls.isEmpty()) {
            resultAdapter.initializeList(routeTolls);
            invalidateOptionsMenu();
            calculateResultByCategory();
        } else {
            showMessage(getString(R.string.tolls_not_found));
        }

        showMap.setEnabled(true);
        showMap.setClickable(true);
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

    private void calculateResultByCategory() {
        double resultDen = 0;
        double resultEur = 0.0;
        if (routeTolls != null) {
            for (Toll toll : routeTolls) {
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
                calculateResult.setText(getString(R.string.total, String.valueOf((int) resultDen), String.valueOf(resultEur)));
                calculateResult.setVisibility(View.VISIBLE);
            }
        }
    }
}
