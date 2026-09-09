package com.denofdevelopers.mktolls.screen.screen.main

import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import androidx.core.os.LocaleListCompat
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
import com.google.gson.reflect.TypeToken
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
    private var midLocation by mutableStateOf("")
    private var isFromMyLocation by mutableStateOf(false)
    private var isToMyLocation by mutableStateOf(false)
    private var isMidMyLocation by mutableStateOf(false)
    private var showMidLocation by mutableStateOf(false)
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
                midLocation = midLocation,
                showMidLocation = showMidLocation,
                onFromLocationChange = { fromLocation = it },
                onToLocationChange = { toLocation = it },
                onMidLocationChange = { midLocation = it },
                onShowMidLocationChange = { showMidLocation = it },
                isFromMyLocation = isFromMyLocation,
                isToMyLocation = isToMyLocation,
                isMidMyLocation = isMidMyLocation,
                onFromMyLocationClick = { checked ->
                    isFromMyLocation = checked
                    if (checked) {
                        isToMyLocation = false
                        isMidMyLocation = false
                        getLocation(true, false)
                    } else {
                        fromLocation = ""
                    }
                },
                onToMyLocationClick = { checked ->
                    isToMyLocation = checked
                    if (checked) {
                        isFromMyLocation = false
                        isMidMyLocation = false
                        getLocation(false, false)
                    } else {
                        toLocation = ""
                    }
                },
                onMidMyLocationClick = { checked ->
                    isMidMyLocation = checked
                    if (checked) {
                        isFromMyLocation = false
                        isToMyLocation = false
                        getLocation(false, true)
                    } else {
                        midLocation = ""
                    }
                },
                categories = categories,
                selectedCategoryIndex = selectedCategoryIndex,
                onCategorySelected = { selectedCategoryIndex = it },
                onLanguageChange = { langCode ->
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                },
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

    private fun getLocation(isFrom: Boolean, isMid: Boolean) {
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
                    when {
                        isFrom -> fromLocation = addressName
                        isMid -> midLocation = addressName
                        else -> toLocation = addressName
                    }
                } else {
                    showAlertMessage(getString(R.string.address_not_found))
                    when {
                        isFrom -> isFromMyLocation = false
                        isMid -> isMidMyLocation = false
                        else -> isToMyLocation = false
                    }
                }
            }
        } else {
            mainPresenter.checkPermissions()
            showAlertMessage(getString(R.string.check_if_location_is_enabled))
            when {
                isFrom -> isFromMyLocation = false
                isMid -> isMidMyLocation = false
                else -> isToMyLocation = false
            }
        }
    }

    override fun showAlertMessage(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun startNextActivity(fromLocation: String, toLocation: String, midLocation: String) {
        val categories = resources.getStringArray(R.array.category_array)
        ResultActivity.start(this, fromLocation, toLocation, midLocation, categories[selectedCategoryIndex])
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
        val jsonString = assets.open("tolls.json").bufferedReader().use { it.readText() }
        val tollList: List<Toll> = gson.fromJson(jsonString, object : TypeToken<List<Toll>>() {}.type)
        tolls.set(gson.toJson(tollList))
    }

    private fun calculateOnClick() {
        showProgress()
        if (!NetworkUtil.isConnected(this)) {
            showAlertMessage(getString(R.string.no_internet_connection))
            hideProgress()
        } else {
            if (fromLocation.isNotEmpty() && toLocation.isNotEmpty()) {
                mainPresenter.shouldStartNextActivity(fromLocation, toLocation, if (showMidLocation) midLocation else "")
            } else {
                showAlertMessage(getString(R.string.start_end_filled))
                hideProgress()
            }
        }
    }
}
