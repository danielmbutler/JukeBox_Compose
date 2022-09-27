package com.dbtechprojects.jukeBoxCompose.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbtechprojects.jukeBoxCompose.ui.theme.turntableBackground

@Composable
fun LoadingScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                "Loading TrackList..",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.size(12.dp))
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterHorizontally),
                color = turntableBackground
            )
        }

    }
}