package com.denofdevelopers.mktolls.screen.screen.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.denofdevelopers.mktolls.BuildConfig
import com.denofdevelopers.mktolls.R
import com.denofdevelopers.mktolls.application.App
import com.denofdevelopers.mktolls.common.Constants
import com.denofdevelopers.mktolls.model.RouteResponse
import com.denofdevelopers.mktolls.model.Toll
import com.denofdevelopers.mktolls.network.ApiService
import com.denofdevelopers.mktolls.util.MapUtil
import com.denofdevelopers.mktolls.util.NetworkUtil
import com.denofdevelopers.mktolls.util.ToastUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import retrofit2.adapter.rxjava.Result

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar

    @Inject
    lateinit var apiService: ApiService
    
    private var request: Subscription? = null
    private var googleMap: GoogleMap? = null
    private var category: String? = null

    companion object {
        private const val START_POINT_EXTRA = "start.point.extra"
        private const val END_POINT_EXTRA = "end.point.extra"
        private const val MID_POINT_EXTRA = "mid.point.extra"
        private const val CATEGORY_EXTRA = "category.extra"
        private const val TOLL_POINTS_EXTRA = "toll.points.extra"

        @JvmStatic
        fun start(context: Context, startPoint: String, endPoint: String, midPoint: String, tollPoints: ArrayList<Toll>, category: String) {
            val intent = Intent(context, MapActivity::class.java).apply {
                val bundle = Bundle().apply {
                    putParcelableArrayList(TOLL_POINTS_EXTRA, tollPoints)
                    putString(START_POINT_EXTRA, startPoint)
                    putString(END_POINT_EXTRA, endPoint)
                    putString(MID_POINT_EXTRA, midPoint)
                    putString(CATEGORY_EXTRA, category)
                }
                putExtras(bundle)
            }
            (context as Activity).startActivityForResult(intent, Constants.REQUEST_CODE_APPOINTMENT_DETAILS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        
        setupActivityComponent()
        initToolBar()
        progressBar.visibility = View.VISIBLE

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initToolBar() {
        toolbar.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            setResult(Constants.REQUEST_CODE_APPOINTMENT_DETAILS)
            finish()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        drawStartAndEndPoint()
        progressBar.visibility = View.INVISIBLE
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
                    if (!NetworkUtil.isConnected(this@MapActivity)) {
                        ToastUtil.showToast(this@MapActivity, getString(R.string.no_internet_connection))
                    } else {
                        ToastUtil.showToast(this@MapActivity, getString(R.string.generic_error))
                    }
                }

                override fun onNext(result: Result<RouteResponse>) {
                    if (result.response()?.code() == 200) {
                        val routeResponse = result.response()?.body()
                        if (routeResponse != null && routeResponse.routes.isNotEmpty()) {
                            val route = mutableListOf<LatLng>()
                            for (leg in routeResponse.routes[0].routeLegs) {
                                route.addAll(MapUtil.getRoutePoints(leg))
                            }
                            addPolyline(route)
                            val bundle = intent.extras
                            val tollPoints = bundle?.getParcelableArrayList<Toll>(TOLL_POINTS_EXTRA)
                            drawTolls(tollPoints)
                            addZoom(route)
                        }
                    }
                }
            })
    }

    private fun drawStartAndEndPoint() {
        val bundle = intent.extras ?: return
        val startPoint = bundle.getString(START_POINT_EXTRA) ?: ""
        val endPoint = bundle.getString(END_POINT_EXTRA) ?: ""
        val midPoint = bundle.getString(MID_POINT_EXTRA) ?: ""
        category = bundle.getString(CATEGORY_EXTRA)

        lifecycleScope.launch(Dispatchers.IO) {
            val startLocation = MapUtil.getLocationPoints(this@MapActivity, startPoint)
            val endLocation = MapUtil.getLocationPoints(this@MapActivity, endPoint)
            val midLocation = if (midPoint.isNotEmpty()) MapUtil.getLocationPoints(this@MapActivity, midPoint) else null

            withContext(Dispatchers.Main) {
                if (startLocation != null) addMarkerStartEnd(startLocation, startPoint, "Start location")
                if (endLocation != null) addMarkerStartEnd(endLocation, endPoint, "End location")
                if (midLocation != null) addMarkerStartEnd(midLocation, midPoint, "Middle location")

                if (startLocation != null && endLocation != null) {
                    getRoutePoints(startLocation, endLocation, midLocation)
                }
            }
        }
    }

    private fun addMarkerToll(latLng: LatLng, title: String, price: String) {
        googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(price)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.toll))
        )
    }

    private fun addMarkerStartEnd(latLng: LatLng, title: String, snippet: String) {
        googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
    }

    private fun addPolyline(route: List<LatLng>) {
        if (route.isEmpty()) return
        val polylineOptions = PolylineOptions().geodesic(true).color(Color.BLUE)
        route.forEach { polylineOptions.add(it) }
        googleMap?.addPolyline(polylineOptions)
    }

    private fun drawTolls(tolls: ArrayList<Toll>?) {
        tolls?.forEach { toll ->
            addMarkerToll(LatLng(toll.lat, toll.lng), toll.tollName, getCategoryPrice(toll, category))
        }
    }

    private fun getCategoryPrice(toll: Toll, category: String?): String {
        var resultDen = 0.0
        var resultEur = 0.0
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
        return "${resultDen.toInt()} den  |  $resultEur eur"
    }

    private fun addZoom(route: List<LatLng>) {
        if (route.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            route.forEach { builder.include(it) }
            val bounds = builder.build()
            val padding = 150 // offset from edges of the map in pixels
            val update = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            googleMap?.animateCamera(update)
        }
    }

    private fun cancelRequest() {
        request?.unsubscribe()
        request = null
    }

    override fun onStop() {
        cancelRequest()
        super.onStop()
    }

    private fun setupActivityComponent() {
        App.get(this).appComponent.plus(this)
    }
}
