package com.joeracosta.myreviews.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.joeracosta.myreviews.R
import com.joeracosta.myreviews.data.MapData
import com.joeracosta.myreviews.data.MyPlace
import com.joeracosta.myreviews.data.Review
import com.joeracosta.myreviews.ui.theme.Amber
import com.joeracosta.myreviews.ui.theme.BrightRed
import com.joeracosta.myreviews.ui.theme.ForestGreen
import com.joeracosta.myreviews.ui.theme.Gold
import com.joeracosta.myreviews.ui.theme.GoogleMapsBlue
import com.joeracosta.myreviews.ui.theme.MyReviewsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSheet(
    place: MyPlace,
    onEditClicked: () -> Unit,
    onJumpToGoogleMapsClicked: () -> Unit,
    onDismissed: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissed
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        maxLines = 3,
                        text = place.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start
                    )
                    if (place.isFavorite) {
                        Text(
                            maxLines = 1,
                            text = stringResource(R.string.favorite_label),
                            color = BrightRed,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start
                        )
                    }
                }


                Button(
                    onClick = onEditClicked,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = ForestGreen,
                        containerColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    val editText = if (place.review != null) stringResource(R.string.edit_button) else stringResource(R.string.add_review_button)
                    Text(text = editText)
                }
            }

            Text(
                maxLines = 1,
                text = place.mapData.address,
                color = Color.LightGray,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start
            )


            Spacer(Modifier.height(12.dp))

            if (place.review != null) {
                Text(
                    maxLines = 1,
                    text = stringResource(R.string.rating_out_of_10, place.review.score),
                    color = Gold,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = place.review.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onJumpToGoogleMapsClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoogleMapsBlue
                )
            ) {
                val editText = stringResource(R.string.jump_to_google_maps)
                Text(text = editText)
            }


        }
    }
}

@Preview
@Composable
fun SheetPreview() {
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
    MyReviewsTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Amber)
        ) {

            PlaceSheet(testPlace, {}, {}) { }
        }
    }
}