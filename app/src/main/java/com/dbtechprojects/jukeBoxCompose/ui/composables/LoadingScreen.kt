package com.dbtechprojects.jukeBoxCompose.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dbtechprojects.jukeBoxCompose.ui.theme.turntableBackground

@Composable
fun LoadingScreen(){
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = turntableBackground)
    }
}