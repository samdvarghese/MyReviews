package com.joeracosta.myreviews.logic

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.joeracosta.myreviews.data.MapData
import com.joeracosta.myreviews.data.MapRepository
import com.joeracosta.myreviews.data.MapState
import com.joeracosta.myreviews.data.MyPlace
import com.joeracosta.myreviews.data.MyResult
import com.joeracosta.myreviews.data.Review
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MapViewModel(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        MapState(
            currentLocation = null,
            positionToJumpTo = null, //todo better defaults
            reviewedPlaces = emptyList(),
            openedPlace = null,
            searchQuery = null,
            currentMapCenter = LatLng(1.35, 103.87), //todo better defaults
            placeSearchResults = null
        )
    )

    private var placeSearchJob: Job? = null

    init {
        //todo testing
        val testPlace = MyPlace(
            id = "1",
            name = "Park West Tavern",
            review = Review(
                "This is review text for park west tavern. Their Guinness is not consistent",
                8.4F
            ),
            isFavorite = false,
            mapData = MapData(
                40.980407,
                -74.118161,
                "pwt address"
            )
        )

        val testPlace2 = MyPlace(
            id = "2",
            name = "Daily Treat",
            review = Review(
                "This is review text for Daily Treat aka Tasty Treat",
                9F
            ),
            isFavorite = true,
            mapData = MapData(
                40.9792684,
                -74.1158964,
                "dt address"
            )
        )

        val listOfPlacesTest = listOf(testPlace, testPlace2)
        updateMapState(
            _state.value.copy(
                reviewedPlaces = listOfPlacesTest
            )
        )

    }

    val state: StateFlow<MapState> = _state

    private fun startPlaceSearchJob() {
        placeSearchJob?.cancel()
        placeSearchJob = viewModelScope.launch {
            delay(500)
            doPlaceSearch()
        }
    }

    fun doPlaceSearch() {
        viewModelScope.launch {
            val result = mapRepository.searchPlace(
                state.value.searchQuery.orEmpty(),
                state.value.currentMapCenter
            )
            if (result is MyResult.Success) {
                updateMapState(
                    _state.value.copy(
                        placeSearchResults = result.value
                    )
                )
            } else {
                //todo error
            }
            placeSearchJob = null
        }
    }

    fun updateCurrentLocation(currentLocation: LatLng, moveMap: Boolean) {
        updateMapState(
            _state.value.copy(
                currentLocation = currentLocation,
                positionToJumpTo = if (moveMap) currentLocation else _state.value.positionToJumpTo
            )
        )
    }

    fun updateCurrentMapCenter(currentMapCenter: LatLng) {
        updateMapState(
            _state.value.copy(
                currentMapCenter = currentMapCenter
            )
        )
    }

    fun placeClicked(place: MyPlace) {
        updateMapState(
            _state.value.copy(
                openedPlace = place,
                positionToJumpTo = LatLng(
                    place.mapData.latitude,
                    place.mapData.longitude
                )
            )
        )
    }

    fun placeClosed() {
        updateMapState(
            _state.value.copy(
                openedPlace = null
            )
        )
    }

    fun clearPositionToJumpTo() {
        updateMapState(
            _state.value.copy(
                positionToJumpTo = null
            )
        )
    }


    fun updateSearchQuery(searchQuery: String) {
        updateMapState(
            _state.value.copy(
                searchQuery = searchQuery
            )
        )
        startPlaceSearchJob()
    }

    fun clearSearch() {
        updateMapState(
            _state.value.copy(
                searchQuery = null,
                placeSearchResults = null
            )
        )
    }


    private fun updateMapState(newState: MapState) {
        _state.value = newState
    }

    // Define ViewModel factory in a companion object
    companion object {
        class Factory(private val mapRepository: MapRepository) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(mapRepository) as T
            }
        }
    }

}
