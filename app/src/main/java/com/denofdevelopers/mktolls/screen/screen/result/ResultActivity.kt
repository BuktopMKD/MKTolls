package com.denofdevelopers.mktolls.screen.screen.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.denofdevelopers.mktolls.BuildConfig
import com.denofdevelopers.mktolls.R
import com.denofdevelopers.mktolls.application.App
import com.denofdevelopers.mktolls.common.Constants
import com.denofdevelopers.mktolls.di.NamedValues
import com.denofdevelopers.mktolls.model.RouteResponse
import com.denofdevelopers.mktolls.model.Toll
import com.denofdevelopers.mktolls.network.ApiService
import com.denofdevelopers.mktolls.screen.screen.map.MapActivity
import com.denofdevelopers.mktolls.ui.ResultScreen
import com.denofdevelopers.mktolls.util.MapUtil
import com.denofdevelopers.mktolls.util.NetworkUtil
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named
import retrofit2.adapter.rxjava.Result

class ResultActivity : AppCompatActivity(), ResultContract.View {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    @field:Named(NamedValues.tollList)
    lateinit var tolls: List<Toll>

    private var fromLocation: String = ""
    private var toLocation: String = ""
    private var midLocation: String = ""
    private var category: String = ""
    private var routePoints = mutableListOf<LatLng>()
    private var routeTolls = mutableStateListOf<Toll>()
    
    private var isLoading by mutableStateOf(false)
    private var totalDenars by mutableStateOf(0.0)
    private var totalEuros by mutableStateOf(0.0)

    private var request: Subscription? = null

    companion object {
        private const val FROM_LOCATION_EXTRA = "from.location.extra"
        private const val TO_LOCATION_EXTRA = "to.location.extra"
        private const val MID_LOCATION_EXTRA = "mid.location.extra"
        private const val CATEGORY = "category"

        @JvmStatic
        fun start(context: Context, fromLocation: String, toLocation: String, midLocation: String, category: String) {
            val intent = Intent(context, ResultActivity::class.java).apply {
                putExtra(FROM_LOCATION_EXTRA, fromLocation)
                putExtra(TO_LOCATION_EXTRA, toLocation)
                putExtra(MID_LOCATION_EXTRA, midLocation)
                putExtra(CATEGORY, category)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setupActivityComponent()
        
        fromLocation = intent.getStringExtra(FROM_LOCATION_EXTRA) ?: ""
        toLocation = intent.getStringExtra(TO_LOCATION_EXTRA) ?: ""
        midLocation = intent.getStringExtra(MID_LOCATION_EXTRA) ?: ""
        category = intent.getStringExtra(CATEGORY) ?: ""

        setContent {
            ResultScreen(
                tolls = routeTolls,
                category = category,
                onBackClick = { finish() },
                onShowMapClick = { onShowMapClick() },
                totalDenars = totalDenars,
                totalEuros = totalEuros,
                isLoading = isLoading
            )
        }

        displayTolls()
    }

    private fun setupActivityComponent() {
        App.get(this).appComponent.plus(this)
    }

    private fun displayTolls() {
        lifecycleScope.launch(Dispatchers.IO) {
            val startPoint = MapUtil.getLocationPoints(this@ResultActivity, fromLocation)
            val endPoint = MapUtil.getLocationPoints(this@ResultActivity, toLocation)
            val midPoint = if (midLocation.isNotEmpty()) MapUtil.getLocationPoints(this@ResultActivity, midLocation) else null

            withContext(Dispatchers.Main) {
                if (startPoint == null || endPoint == null) {
                    if (!NetworkUtil.isConnected(this@ResultActivity)) {
                        showError(getString(R.string.no_internet_connection))
                    } else {
                        showMessage(getString(R.string.address_not_found))
                    }
                } else {
                    showProgress()
                    getRoutePoints(startPoint, endPoint, midPoint)
                }
            }
        }
    }

    private fun getRoutePoints(startPoint: LatLng, endPoint: LatLng, midPoint: LatLng?) {
        val waypoints = midPoint?.let { "via:${MapUtil.formatMapServiceQueryParameters(it)}" }
        request = apiService.getPointsBetweenTwoLocations(
            MapUtil.formatMapServiceQueryParameters(startPoint),
            MapUtil.formatMapServiceQueryParameters(endPoint),
            waypoints,
            false,
            BuildConfig.API_KEY_GOOGLE_MAPS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Result<RouteResponse>> {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    hideProgress()
                    showError(if (!NetworkUtil.isConnected(this@ResultActivity)) getString(R.string.no_internet_connection) else getString(R.string.generic_error))
                }

                override fun onNext(result: Result<RouteResponse>) {
                    hideProgress()
                    if (result.response()?.code() == 200) {
                        val routeResponse = result.response()?.body()
                        if (routeResponse != null && routeResponse.routes.isNotEmpty()) {
                            routePoints.clear()
                            for (leg in routeResponse.routes[0].routeLegs) {
                                routePoints.addAll(MapUtil.getRoutePoints(leg))
                            }
                            redrawRecyclerView()
                        } else {
                            showError(getString(R.string.generic_error))
                        }
                    }
                }
            })
    }

    private fun redrawRecyclerView() {
        routeTolls.clear()
        for (toll in tolls) {
            if (PolyUtil.isLocationOnPath(LatLng(toll.lat, toll.lng), routePoints, false, Constants.RADIUS_TOLERANCE.toDouble())) {
                routeTolls.add(toll)
            }
        }
        if (routeTolls.isNotEmpty()) {
            calculateResultByCategory()
        } else {
            showMessage(getString(R.string.tolls_not_found))
        }
    }

    private fun calculateResultByCategory() {
        var resultDen = 0.0
        var resultEur = 0.0
        for (toll in routeTolls) {
            when (category) {
                getString(R.string.category_1A) -> {
                    resultDen += toll.categoryOneADen
                    resultEur += toll.categoryOneAEuro
                }
                getString(R.string.category_1B) -> {
                    resultDen += toll.categoryOneDen
                    resultEur += toll.categoryOneEuro
                }
                getString(R.string.category_2) -> {
                    resultDen += toll.categoryTwoDen
                    resultEur += toll.categoryTwoEuro
                }
                getString(R.string.category_3) -> {
                    resultDen += toll.categoryThreeDen
                    resultEur += toll.categoryThreeEuro
                }
                getString(R.string.category_4) -> {
                    resultDen += toll.categoryFourDen
                    resultEur += toll.categoryFourEuro
                }
            }
        }
        totalDenars = resultDen
        totalEuros = resultEur
    }

    override fun showError(message: String) {
        showMessage(message)
    }

    override fun showProgress() {
        isLoading = true
    }

    override fun showMessage(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun hideProgress() {
        isLoading = false
    }

    private fun onShowMapClick() {
        MapActivity.start(this, fromLocation, toLocation, midLocation, ArrayList(routeTolls), category)
    }

    override fun onStop() {
        request?.unsubscribe()
        super.onStop()
    }
}
