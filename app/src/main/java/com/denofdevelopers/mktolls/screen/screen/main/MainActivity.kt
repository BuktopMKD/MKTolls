package com.denofdevelopers.mktolls.screen.screen.main

import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.denofdevelopers.mktolls.R
import com.denofdevelopers.mktolls.application.App
import com.denofdevelopers.mktolls.common.BaseActivity
import com.denofdevelopers.mktolls.di.NamedValues
import com.denofdevelopers.mktolls.model.Toll
import com.denofdevelopers.mktolls.screen.screen.result.ResultActivity
import com.denofdevelopers.mktolls.ui.MainActivityContent
import com.denofdevelopers.mktolls.util.NetworkUtil
import com.f2prateek.rx.preferences2.Preference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class MainActivity : BaseActivity(), MainContract.View {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(android.content.Intent(context, MainActivity::class.java))
        }
    }

    @Inject
    lateinit var mainPresenter: MainPresenter

    @Inject
    @field:Named(NamedValues.tolls)
    lateinit var tolls: Preference<String>

    @Inject
    lateinit var gson: Gson

    private var fromLocation by mutableStateOf("")
    private var toLocation by mutableStateOf("")
    private var isFromMyLocation by mutableStateOf(false)
    private var isToMyLocation by mutableStateOf(false)
    private var selectedCategoryIndex by mutableIntStateOf(1) // Default to Category 1B
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch(Dispatchers.IO) {
            if (tolls.get().isEmpty()) {
                initTolls()
            }
        }

        val categories = resources.getStringArray(R.array.category_array)

        setContent {
            MainActivityContent(
                fromLocation = fromLocation,
                toLocation = toLocation,
                onFromLocationChange = { fromLocation = it },
                onToLocationChange = { toLocation = it },
                isFromMyLocation = isFromMyLocation,
                isToMyLocation = isToMyLocation,
                onFromMyLocationClick = { checked ->
                    isFromMyLocation = checked
                    if (checked) {
                        isToMyLocation = false
                        getLocation(true)
                    } else {
                        fromLocation = ""
                    }
                },
                onToMyLocationClick = { checked ->
                    isToMyLocation = checked
                    if (checked) {
                        isFromMyLocation = false
                        getLocation(false)
                    } else {
                        toLocation = ""
                    }
                },
                categories = categories,
                selectedCategoryIndex = selectedCategoryIndex,
                onCategorySelected = { selectedCategoryIndex = it },
                onCalculateClick = { calculateOnClick() },
                isLoading = isLoading
            )
        }
        lifecycleScope.launch {
            mainPresenter.takeView()
        }
    }

    override fun setupActivityComponent() {
        App.get(this).appComponent.plus(MainModule(this)).inject(this)
    }

    private fun getLocation(isFrom: Boolean) {
        if (!mainPresenter.hasPlayServices(true)) return

        val currentLocation = mainPresenter.location
        if (currentLocation != null) {
            val location = Location("fused").apply {
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude
            }
            showProgress()
            mainPresenter.getAddressForLocation(location) { addressName ->
                hideProgress()
                if (addressName.isNotEmpty()) {
                    if (isFrom) {
                        fromLocation = addressName
                    } else {
                        toLocation = addressName
                    }
                } else {
                    showAlertMessage(getString(R.string.address_not_found))
                    if (isFrom) isFromMyLocation = false else isToMyLocation = false
                }
            }
        } else {
            mainPresenter.checkPermissions()
            showAlertMessage(getString(R.string.check_if_location_is_enabled))
            if (isFrom) isFromMyLocation = false else isToMyLocation = false
        }
    }

    override fun showAlertMessage(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun startNextActivity(fromLocation: String, toLocation: String) {
        val categories = resources.getStringArray(R.array.category_array)
        ResultActivity.start(this, fromLocation, toLocation, categories[selectedCategoryIndex])
    }

    fun showProgress() {
        isLoading = true
    }

    fun hideProgress() {
        isLoading = false
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.connectApiClient()
        lifecycleScope.launch {
            hideProgress()
        }
    }

    override fun onStop() {
        mainPresenter.dropView()
        super.onStop()
    }

    private fun initTolls() {
        val tollList = mutableListOf<Toll>()

        // --- MACEDONIA (MK) ---
        tollList.add(Toll(41.988478, 21.292620, "Glumovo", "Глумово", 20.00, 40.00, 60.00, 110.00, 160.00, 0.50, 1.00, 1.50, 2.50, 4.00))
        tollList.add(Toll(41.819905, 20.917580, "Gostivar", "Гостивар", 20.00, 30.00, 40.00, 80.00, 110.00, 0.50, 0.50, 1.00, 1.50, 2.00))
        tollList.add(Toll(41.593404, 21.913106, "Stobi", "Стоби", 40.00, 60.00, 100.00, 180.00, 260.00, 1.00, 1.50, 2.00, 4.00, 6.00))
        tollList.add(Toll(41.983383, 21.641460, "Miladinovci", "Миладиновци", 20.00, 40.00, 60.00, 100.00, 150.00, 0.50, 1.00, 1.50, 2.50, 4.00))
        tollList.add(Toll(41.775878, 21.745353, "Sopot", "Сопот", 50.00, 80.00, 120.00, 220.00, 330.00, 1.00, 1.50, 2.00, 4.00, 5.50))
        tollList.add(Toll(41.778480, 21.767534, "Otovica", "Отовица", 50.00, 80.00, 120.00, 220.00, 330.00, 1.00, 1.50, 2.00, 4.00, 5.50))
        tollList.add(Toll(41.942027, 21.619643, "Petrovec", "Петровец", 20.00, 40.00, 50.00, 100.00, 150.00, 0.50, 1.00, 1.00, 2.00, 3.00))
        tollList.add(Toll(42.103704, 21.700195, "Romanovce", "Романовце", 40.00, 60.00, 80.00, 150.00, 220.00, 0.50, 1.00, 1.50, 2.50, 4.00))
        tollList.add(Toll(41.976341, 20.956737, "Tetovo", "Тетово", 20.00, 30.00, 40.00, 80.00, 110.00, 0.50, 0.50, 1.00, 1.50, 2.00))
        tollList.add(Toll(41.989927, 21.077206, "Zelino", "Желино", 20.00, 40.00, 60.00, 110.00, 160.00, 0.50, 1.00, 1.50, 2.50, 4.00))
        tollList.add(Toll(41.417236, 22.208121, "Demir Kapija", "Демир Капија", 50.00, 80.00, 130.00, 230.00, 340.00, 1.00, 1.50, 2.50, 4.00, 6.00))
        tollList.add(Toll(41.235711, 22.492559, "Gevgelija", "Гевгелија", 60.00, 100.00, 160.00, 290.00, 430.00, 1.50, 2.00, 3.00, 5.00, 7.50))
        tollList.add(Toll(41.905600, 21.782800, "Preod", "Преод", 40.00, 70.00, 100.00, 180.00, 270.00, 1.00, 1.50, 2.00, 3.00, 4.50))
        tollList.add(Toll(41.810100, 22.039800, "Kadrifakovo", "Кадрифаково", 30.00, 50.00, 80.00, 150.00, 220.00, 0.50, 1.00, 1.50, 2.50, 4.00))

        // --- GREECE - Direction Thessaloniki/Athens ---
        tollList.add(Toll(41.108700, 22.558600, "Evzoni", "Евзони", 101.50, 147.60, 147.60, 369.00, 516.60, 1.65, 2.40, 2.40, 6.00, 8.40))
        tollList.add(Toll(40.602400, 22.698200, "Malgara", "Малгара", 40.00, 55.30, 55.30, 144.50, 203.00, 0.65, 0.90, 0.90, 2.35, 3.30))
        tollList.add(Toll(40.519818, 22.572833, "Aeginio", "Аигинио (Клеиди)", 80.00, 110.70, 110.70, 282.90, 393.60, 1.30, 1.80, 1.80, 4.60, 6.40))
        tollList.add(Toll(40.035700, 22.569800, "Leptokarya", "Лептокарија", 116.80, 166.00, 166.00, 418.20, 584.20, 1.90, 2.70, 2.70, 6.80, 9.50))
        tollList.add(Toll(40.696140, 22.895804, "Thessaloniki Ring Road", "Солун (обиколница)", 24.60, 33.80, 33.80, 86.10, 116.80, 0.40, 0.55, 0.55, 1.40, 1.90))

        // --- GREECE - Direction Turkey (Kipoi) ---
        tollList.add(Toll(40.706900, 23.191500, "Analipsi", "Аналипси", 101.50, 144.50, 144.50, 362.80, 504.30, 1.65, 2.35, 2.35, 5.90, 8.20))
        tollList.add(Toll(40.709600, 23.680100, "Asprovalta", "Аспровалта", 49.20, 67.60, 67.60, 172.20, 239.80, 0.80, 1.10, 1.10, 2.80, 3.90))
        tollList.add(Toll(40.839983, 24.116135, "Moustheni", "Мустени", 92.20, 132.20, 132.20, 332.10, 461.20, 1.50, 2.15, 2.15, 5.40, 7.50))
        tollList.add(Toll(40.955555, 24.464166, "Kavala", "Кавала", 80.00, 113.80, 113.80, 282.90, 399.70, 1.30, 1.85, 1.85, 4.60, 6.50))
        tollList.add(Toll(41.116547, 25.184562, "Iasmos", "Иасмос", 76.80, 110.70, 110.70, 276.70, 387.40, 1.25, 1.80, 1.80, 4.50, 6.30))
        tollList.add(Toll(40.961974, 25.643425, "Mesti", "Мести", 95.30, 135.30, 135.30, 338.20, 473.50, 1.55, 2.20, 2.20, 5.50, 7.70))
        tollList.add(Toll(40.944691, 26.205027, "Ardanio", "Арданио", 67.60, 98.40, 98.40, 246.00, 344.40, 1.10, 1.60, 1.60, 4.00, 5.60))

        // --- GREECE - Direction Igoumenitsa/Lefkada ---
        tollList.add(Toll(40.367200, 22.060200, "Polymylos", "Полимилос", 83.00, 116.80, 116.80, 295.20, 412.00, 1.35, 1.90, 1.90, 4.80, 6.70))
        tollList.add(Toll(40.256544, 21.632969, "Siatista", "Сиатиста", 83.00, 116.80, 116.80, 295.20, 412.00, 1.35, 1.90, 1.90, 4.80, 6.70))
        tollList.add(Toll(39.789000, 21.266000, "Malakasi", "Малакаси (Мецово)", 83.00, 116.80, 116.80, 295.20, 412.00, 1.35, 1.90, 1.90, 4.80, 6.70))
        tollList.add(Toll(39.619000, 20.948000, "Pamvotida", "Памотида (Јанина)", 67.60, 95.30, 95.30, 246.00, 338.20, 1.10, 1.55, 1.55, 4.00, 5.50))
        tollList.add(Toll(39.540000, 20.674000, "Tyria", "Тириа (Игуменица)", 116.80, 166.00, 166.00, 418.20, 584.20, 1.90, 2.70, 2.70, 6.80, 9.50))
        tollList.add(Toll(39.425535, 20.905251, "Terovo", "Терово", 135.30, 193.70, 193.70, 482.70, 676.50, 2.20, 3.15, 3.15, 7.85, 11.00))
        tollList.add(Toll(38.940119, 20.766185, "Aktio-Preveza Tunnel", "Тунел Акцио-Превеза", 43.00, 184.50, 184.50, 307.50, 492.00, 0.70, 3.00, 3.00, 5.00, 8.00))

        tolls.set(gson.toJson(tollList))
    }

    private fun calculateOnClick() {
        showProgress()
        if (!NetworkUtil.isConnected(this)) {
            showAlertMessage(getString(R.string.no_internet_connection))
            hideProgress()
        } else {
            if (fromLocation.isNotEmpty() && toLocation.isNotEmpty()) {
                mainPresenter.shouldStartNextActivity(fromLocation, toLocation)
            } else {
                showAlertMessage(getString(R.string.start_end_filled))
                hideProgress()
            }
        }
    }
}
