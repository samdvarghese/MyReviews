package com.joeracosta.myreviews.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MyPlace (
    val id: String,
    val name: String,
    val review: Review?,
    val isFavorite: Boolean,
    val mapData: MapData
): Parcelable

@Serializable
@Parcelize
data class Review (
    val text: String,
    val score: Float
) : Parcelable

@Serializable
@Parcelize
data class MapData (
    val latitude: Double,
    val longitude: Double,
    val address: String
) : Parcelable

data class MapState(
    val currentLocation: LatLng?,
    val positionToJumpTo: LatLng?,
    val reviewedPlaces: List<MyPlace>,
    val openedPlace: MyPlace?,
    val searchQuery: String?,
    val currentMapCenter: LatLng,
    val placeSearchResults: List<MyPlace>?
)

sealed class MyResult<out T> {
    data class Success<out T>(val value: T): MyResult<T>()
    data class Error(val errorMessage: String): MyResult<Nothing>()
}