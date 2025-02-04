package com.joeracosta.myreviews.data

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchByTextResponse
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface MapRepository {
    suspend fun searchPlace(searchQuery: String, currentMapViewCenter: LatLng): MyResult<List<MyPlace>>
}

class MapRepositoryImpl(private val placesClient: PlacesClient) : MapRepository {
    override suspend fun searchPlace(
        searchQuery: String,
        currentMapViewCenter: LatLng
    ): MyResult<List<MyPlace>> {

        val placeFields = listOf(
            com.google.android.libraries.places.api.model.Place.Field.ID,
            com.google.android.libraries.places.api.model.Place.Field.DISPLAY_NAME,
            com.google.android.libraries.places.api.model.Place.Field.FORMATTED_ADDRESS,
            com.google.android.libraries.places.api.model.Place.Field.LOCATION
        )
        val circle = CircularBounds.newInstance(currentMapViewCenter, 5000.0)

        val searchByTextRequest = SearchByTextRequest.builder(searchQuery, placeFields)
            .setMaxResultCount(10)
            .setLocationBias(circle).build();


        return suspendCoroutine { cont ->

            placesClient.searchByText(searchByTextRequest)
                .addOnSuccessListener { response: SearchByTextResponse ->
                    val places = response.places
                    val myPlaces = places.mapNotNull {
                        MyPlace(
                            id = it.id.orEmpty(),
                            name = it.displayName.orEmpty(),
                            review = null,
                            isFavorite = false,
                            mapData = MapData(
                                latitude = it.location?.latitude ?: 0.0,
                                longitude = it.location?.longitude ?: 0.0,
                                address = it.formattedAddress.orEmpty()
                            )
                        )
                    }
                    cont.resume(MyResult.Success(myPlaces))
                }
                .addOnFailureListener { e ->
                    cont.resume(MyResult.Error(e.message.orEmpty()))
                }

        }

    }

}