package com.dbtechprojects.JukeBoxCompose.ui


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun RoundedBox(shape: Shape, color: Color, size: Dp){
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color)
        )
    }
}


@Composable
fun TurnTableDrawable(
    shape: Shape,
    color: Color,
    size: Dp,
    turntableDrawable: Int,
    armDrawable: Int,
    armLockDrawable: Int,
    isPlaying : MutableState<Boolean>){

    var playingState = remember { isPlaying }

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color)
                .padding(0.dp)
        ) {

            val infiniteTransition = rememberInfiniteTransition()
            val turntableRotation by infiniteTransition.animateFloat(
                initialValue = 0F,
                targetValue = 360F,
                animationSpec = infiniteRepeatable(
                    animation = tween(5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )


            Image(
                painter = painterResource(id = turntableDrawable),
                contentDescription = "TurnTable",
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
                    .rotate(
                        degrees = if (playingState.value) {
                            turntableRotation
                        } else {
                            0F
                        }
                    )
                    .clickable(enabled = true, onClick = { playingState.value = false })
            )


            // arm lock stays fixed

//            Image(
//                painter = painterResource(id = armLockDrawable),
//                contentDescription = "TurnTable Arm Image",
//                modifier = Modifier
//                    .requiredSize(60.dp)
//                    .align(Alignment.BottomStart)
//            )

        }
    }
}

//animations
//            val infiniteTransition = rememberInfiniteTransition()
//            val color by infiniteTransition.animateColor(
//                initialValue = Color.Red,
//                targetValue = Color.Green,
//                animationSpec = infiniteRepeatable(
//                    animation = tween(1000, easing = LinearEasing),
//                    repeatMode = RepeatMode.Reverse
//                )
//            )
//
//            Box(Modifier.fillMaxSize().background(color))




