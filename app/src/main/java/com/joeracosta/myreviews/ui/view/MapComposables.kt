package com.joeracosta.myreviews.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.joeracosta.myreviews.data.MapData
import com.joeracosta.myreviews.data.MyPlace
import com.joeracosta.myreviews.data.Review
import com.joeracosta.myreviews.ui.theme.DeepRed
import com.joeracosta.myreviews.ui.theme.ForestGreen

@Composable
fun SearchResult(
    place: MyPlace,
    firstItem: Boolean,
    lastItem: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = if (lastItem) 16.dp else 8.dp, top = if (firstItem) 16.dp else 8.dp)
        ) {

            Text(
                text = place.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start
            )
            Text(
                text = place.mapData.address,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start
            )

        }
    }

}

@Composable
fun MapMarker(
    place: MyPlace,
    onClick: () -> Unit
) {
    val markerState =
        remember {
            MarkerState(
                position = LatLng(
                    place.mapData.latLng.latitude,
                    place.mapData.latLng.longitude
                )
            )
        }
    val shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 0.dp)

    MarkerComposable(
        state = markerState,
        title = place.name,
        anchor = Offset(0.5f, 1f),
        onClick = {
            onClick()
            true
        }
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = shape
                )
                .clip(shape)
                .background(if (place.review == null) Color.Gray else if (place.isFavorite) DeepRed else ForestGreen)
                .padding(5.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (place.isFavorite) Icons.Filled.Favorite else Icons.Filled.Place,
                contentDescription = "marker icon",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
fun SearchPreview() {
    val testPlace = MyPlace(
        id = "1",
        name = "Park West Tavern",
        review = Review(
            "This is review text for park west tavern. Their Guinness is not consistent",
            8.4F
        ),
        isFavorite = false,
        mapData = MapData(
            LatLng(
                40.980407,
                -74.118161
            ),
            "pwt address"
        )
    )
    SearchResult(
        testPlace,
        true,
        true
    ) { }
}

@Preview
@Composable
fun MapMarkerPreview() {
    val testPlace = MyPlace(
        id = "1",
        name = "Park West Tavern",
        review = Review(
            "This is review text for park west tavern. Their Guinness is not consistent",
            8.4F
        ),
        isFavorite = false,
        mapData = MapData(
            LatLng(
                40.980407,
                -74.118161
            ),
            "pwt address"
        )
    )
    MapMarker(testPlace) { }
}