package com.dbtechprojects.jukeBoxCompose.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbtechprojects.jukeBoxCompose.OnMusicButtonClick
import com.dbtechprojects.jukeBoxCompose.R
import com.dbtechprojects.jukeBoxCompose.model.MusicPlayerOption
import com.dbtechprojects.jukeBoxCompose.model.Track
import com.dbtechprojects.jukeBoxCompose.ui.theme.titleFont


@Composable
fun Title() {
    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()

    ) {

        Text(
            stringResource(R.string.app_title),
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = titleFont,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Player(
    album: MutableState<Track>,
    isPlaying: MutableState<Boolean>,
    onMusicPlayerClick: OnMusicButtonClick,
    isTurntableArmFinished: MutableState<Boolean>,
    isBuffering: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()

    ) {
        Text(
            album.value.songTitle,
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = titleFont,
            textAlign = TextAlign.Center
        )

        Box(Modifier.align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center) {
            val canvasHeight = remember {
                mutableStateOf(0f)
            }

            val musicBarAnim = rememberInfiniteTransition()

            val musicBarHeight by musicBarAnim.animateFloat(
                initialValue = 0f,
                targetValue = if (isTurntableArmFinished.value && isPlaying.value) {
                    canvasHeight.value
                } else 0f,
                animationSpec = infiniteRepeatable(
                    tween(500),
                    repeatMode = RepeatMode.Reverse
                )
            )


            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(180F) // rotate so rectangles get drawn from the bottom
                    .background(Color.DarkGray.copy(0.4f))
            ) {
                val canvasWidth = this.size.width
                canvasHeight.value = this.size.height

                /* draw 8 rectangles along the canvas with a transparent color and a random height
                 an Offset, is configured to provide equal spacing between the bars
                 animation begins once turntable arm rotation is complete and will continue if music is playing
                 */
                for (i in 0..7) {
                    drawRect(
                        color = Color.DarkGray.copy(0.8f),
                        size = Size(canvasWidth / 9, musicBarHeight),
                        topLeft = Offset(x = canvasWidth / 8 * i.toFloat(), y = 0f)
                    )
                }


            }

            Row {
                Image(
                    painter = painterResource(
                        id =
                        R.drawable.ic_baseline_skip_previous_24
                    ),
                    contentDescription = stringResource(R.string.previous),
                    modifier = Modifier
                        .clickable(!isBuffering.value, onClick = {
                            onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.Previous)
                        })
                        .padding(16.dp)
                        .size(35.dp)
                )
                Image(
                    painter = painterResource(
                        id =

                        if (isPlaying.value) {
                            R.drawable.ic_baseline_pause_24
                        } else {
                            R.drawable.ic_baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "Play Button",
                    modifier = Modifier
                        .clickable(
                            !isBuffering.value,
                            onClick = { onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.Play) })
                        .padding(16.dp)
                        .size(35.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_skip_next_24),
                    contentDescription = stringResource(R.string.next_song),
                    modifier = Modifier
                        .clickable(!isBuffering.value, onClick = {
                            onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.Skip)
                        })
                        .padding(16.dp)
                        .size(35.dp)
                )
            }
            if (isBuffering.value) {
                ProgressOverlay()
            }

        }


    }
}

@Composable
fun TurnTable(
    isPlaying: MutableState<Boolean>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(6.dp)

    ) {

        TurnTableDrawable(
            shape = RoundedCornerShape(20.dp),
            size = 240.dp,
            turntableDrawable = R.drawable.record,
            isPlaying = isPlaying,
            turntableArmState,
            isTurntableArmFinished = isTurntableArmFinished

        )
    }
}


@Composable
fun TurnTableDrawable(
    shape: Shape,
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
                .background(MaterialTheme.colors.primary)
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
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
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

@Composable
fun ProgressOverlay(
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(Color.Gray.copy(alpha = 0.6f))
                .fillMaxSize()
        ) {

            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

        }
    }
}




