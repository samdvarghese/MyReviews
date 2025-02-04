package com.joeracosta.myreviews.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import com.joeracosta.myreviews.data.MyPlace

@Composable
fun ReviewEditScreen(
    place: MyPlace
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

        val layoutDirection = LocalLayoutDirection.current

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Red)
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {

        }

    }
}