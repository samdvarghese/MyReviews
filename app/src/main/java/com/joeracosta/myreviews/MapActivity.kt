package com.joeracosta.myreviews

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.joeracosta.myreviews.data.MapRepositoryImpl
import com.joeracosta.myreviews.logic.LastLocationGetter
import com.joeracosta.myreviews.logic.LastLocationProviderActivityImpl
import com.joeracosta.myreviews.logic.MapViewModel
import com.joeracosta.myreviews.ui.theme.MyReviewsTheme
import com.joeracosta.myreviews.ui.view.EditScreen
import com.joeracosta.myreviews.ui.view.MainMapScreen
import com.joeracosta.myreviews.ui.view.MapScreen
import kotlinx.coroutines.launch


class MapActivity : ComponentActivity() {

    private var lastLocationGetter: LastLocationGetter? = null

    private val foregroundLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }

        if (granted) {
            updateCurrentLocation(mapViewModel.state.value.currentLocation == null)
        } else {
            //todo error saying need permissions
        }
    }

    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapViewModel =
            ViewModelProvider(
                this,
                MapViewModel.Companion.Factory(
                    MapRepositoryImpl(
                        Places.createClient(applicationContext)
                    )
                )
            )[MapViewModel::class.java]

        handleLocation(mapViewModel.state.value.currentLocation == null)

        enableEdgeToEdge()

        setContent {
            MyReviewsTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = MapScreen) {
                    composable<MapScreen> {
                        MainMapScreen(
                            mapViewModel
                        ) {
                            handleLocation(true)
                        }
                    }
                    composable<EditScreen> { backStackEntry ->
                        val editScreen: EditScreen = backStackEntry.toRoute()
                        //todo
                    }
                }

            }
        }
    }

    private fun handleLocation(jumpToPosition: Boolean) {

        val permissionFine = android.Manifest.permission.ACCESS_FINE_LOCATION
        val permissionCoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION

        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            permissionFine
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            permissionCoarse
        ) == PackageManager.PERMISSION_GRANTED

        // Permission has been granted
        if (fineGranted || coarseGranted) {
            updateCurrentLocation(jumpToPosition)
            return
        }

        //todo rationale dialog?

        foregroundLocationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }


    private fun updateCurrentLocation(jumpToPosition: Boolean) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        lastLocationGetter = LastLocationProviderActivityImpl(fusedLocationProviderClient)

        lifecycleScope.launch {
            val latestLocation = lastLocationGetter?.getLastLocation()
            if (latestLocation != null) {
                mapViewModel.updateCurrentLocation(
                    latestLocation,
                    jumpToPosition
                )
            }
        }
    }


}

