package com.denofdevelopers.mktolls.screen.screen.main

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.denofdevelopers.mktolls.R
import com.denofdevelopers.mktolls.application.App
import com.denofdevelopers.mktolls.common.BaseActivity
import com.denofdevelopers.mktolls.screen.screen.result.ResultActivity
import com.denofdevelopers.mktolls.ui.MainActivityContent
import com.denofdevelopers.mktolls.util.NetworkUtil
import javax.inject.Inject

class MainActivity : BaseActivity(), MainContract.View {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(android.content.Intent(context, MainActivity::class.java))
        }
    }

    @Inject
    lateinit var mainPresenter: MainPresenter

    private var fromLocation by mutableStateOf("")
    private var toLocation by mutableStateOf("")
    private var isFromMyLocation by mutableStateOf(false)
    private var isToMyLocation by mutableStateOf(false)
    private var selectedCategoryIndex by mutableIntStateOf(1) // Default to Category 1B
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
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
    }

    override fun setupActivityComponent() {
        App.get(this).appComponent.plus(MainModule(this)).inject(this)
    }

    private fun getLocation(isFrom: Boolean) {
        if (!mainPresenter.hasPlayServices(true)) return
        
        val currentLocation = mainPresenter.location
        if (currentLocation != null) {
            if (isFrom) {
                fromLocation = currentLocation.name
            } else {
                toLocation = currentLocation.name
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
        hideProgress()
    }

    override fun onStop() {
        mainPresenter.dropView()
        super.onStop()
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
