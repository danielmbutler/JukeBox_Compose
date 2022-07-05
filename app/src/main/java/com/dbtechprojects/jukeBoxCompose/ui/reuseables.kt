package com.dbtechprojects.jukeBoxCompose.ui


import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
fun TurnTableDrawable(
    shape: Shape,
    color: Color,
    size: Dp,
    turntableDrawable: Int,
    isPlaying: MutableState<Boolean>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>
) {

    val playingState = remember { isPlaying }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {

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

            val turntableArmRotation: Float by animateFloatAsState(
                targetValue = if (turntableArmState.value) 35f else 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing,),
                finishedListener = {
                    isTurntableArmFinished.value = true
                }
            )

            Image(
                painter = painterResource(id = turntableDrawable),
                contentDescription = "TurnTable",
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
                    .rotate(
                        degrees = if (playingState.value && isTurntableArmFinished.value) {
                            turntableRotation
                        } else {
                            0F
                        }
                    )
            )

            // Turntable Arm and arm holder
            Box(
                modifier =
                Modifier
                    .size(40.dp)
                    .background(Color.DarkGray)
                    .clip(CircleShape) // not working
                    .padding(12.dp)
                    .align(Alignment.BottomStart)
            )

            Column(
                Modifier
                    .rotate(turntableArmRotation) // value changes when turntablearm state is set to true
                    .align(Alignment.BottomStart)
                    .padding(12.dp, 0.dp, 0.dp, 0.dp)

            ) {
                Box(
                    modifier =
                    Modifier
                        .size(16.dp, height = 120.dp)
                        .background(Color.LightGray)
                        .border(BorderStroke(2.dp, Color.DarkGray))
                )
            }

        }
    }
}




