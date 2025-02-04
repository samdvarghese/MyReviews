package com.joeracosta.myreviews.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.joeracosta.myreviews.R
import com.joeracosta.myreviews.data.Constants
import com.joeracosta.myreviews.logic.MapViewModel
import com.joeracosta.myreviews.ui.theme.MyReviewsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMapScreen(
    mapViewModel: MapViewModel,
    locateMeClicked: () -> Unit
) {

    val mapState = mapViewModel.state.collectAsState()
    var expanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val padding = 0.dp //else 16.dp
            SearchBar(
                modifier = Modifier.padding(start = padding, end = padding),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = mapState.value.searchQuery.orEmpty(),
                        onQueryChange = {
                            mapViewModel.updateSearchQuery(it)
                        },
                        onSearch = {
                            mapViewModel.doPlaceSearch()
                        },
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = it
                            mapViewModel.clearSearch()
                        },
                        placeholder = { Text("Search") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (!expanded) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = null
                                )
                            }
                        },
                    )
                },
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                    mapViewModel.clearSearch()
                },
            ) {
                LazyColumn {
                    val placeSearchResults = mapState.value.placeSearchResults
                    placeSearchResults?.let {
                        itemsIndexed(it) { index, place ->
                            SearchResult(
                                place = place,
                                firstItem = index == 0,
                                lastItem = index == placeSearchResults.size - 1
                            ) {
                                mapViewModel.placeClicked(place)
                                expanded = false
                                mapViewModel.clearSearch()
                            }
                        }
                    }
                }
            }
        }) { innerPadding ->

        val layoutDirection = LocalLayoutDirection.current
        Box(
            Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {


            val positionToJumpTo = mapState.value.positionToJumpTo
            val defaultMap = mapState.value.currentMapCenter
            val defaultPosition = positionToJumpTo ?: defaultMap

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(
                        defaultPosition.latitude,
                        defaultPosition.longitude
                    ), Constants.DEFAULT_ZOOM_LEVEL
                )
            }

            LaunchedEffect(positionToJumpTo) {

                if (positionToJumpTo != null) {
                    //update position specifically for when location changes
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                positionToJumpTo.latitude,
                                positionToJumpTo.longitude
                            ),
                            Constants.DEFAULT_ZOOM_LEVEL
                        ),
                        durationMs = Constants.POSITION_JUMP_SPEED
                    )
                    /*cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            positionToJumpTo.latLng.latitude,
                            positionToJumpTo.lng
                        ), 14f
                    )*/
                    mapViewModel.clearPositionToJumpTo()
                }
            }


            LaunchedEffect(cameraPositionState.isMoving) {
                if (!cameraPositionState.isMoving) {
                    cameraPositionState.projection?.visibleRegion?.latLngBounds?.center?.let(
                        mapViewModel::updateCurrentMapCenter
                    )
                }
            }

            GoogleMap(
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                ),
                onMapLoaded = {
                    cameraPositionState.projection?.visibleRegion?.latLngBounds?.center?.let(
                        mapViewModel::updateCurrentMapCenter
                    )
                },
                properties = MapProperties(
                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                        LocalContext.current,
                        R.raw.empty_map_style
                    ),
                    isMyLocationEnabled = false
                )
            ) {
                mapState.value.reviewedPlaces.forEach {
                    MapMarker(it) {
                        mapViewModel.placeClicked(it)
                    }
                }

                //add temp map marker if a searched place
                val currentlyOpenedPlace = mapState.value.openedPlace
                if (currentlyOpenedPlace != null && currentlyOpenedPlace.review == null) {
                    MapMarker(currentlyOpenedPlace) {}
                }
            }

            // Locate Me Button
            FloatingActionButton(
                onClick = locateMeClicked,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color.DarkGray
            ) {
                Icon(
                    painter = painterResource(R.drawable.gps_icon),
                    contentDescription = "Locate Me",
                    tint = Color.White
                )
            }

            val currentlyOpenedPlace = mapState.value.openedPlace
            if (currentlyOpenedPlace != null) {


                PlaceSheet(
                    place = currentlyOpenedPlace,
                    onJumpToGoogleMapsClicked = {
                        //todo
                    },
                    onEditClicked = {
                        //todo
                    }
                ) {
                    mapViewModel.placeClosed()
                }
            }
        }
    }
}