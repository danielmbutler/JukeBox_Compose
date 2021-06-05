package com.dbtechprojects.JukeBoxCompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbtechprojects.JukeBoxCompose.ui.AlbumList
import com.dbtechprojects.JukeBoxCompose.ui.TurnTableDrawable
import com.dbtechprojects.JukeBoxCompose.ui.theme.MyApplicationTheme
import com.dbtechprojects.JukeBoxCompose.ui.theme.appBackground
import com.dbtechprojects.JukeBoxCompose.ui.theme.titleFont
import com.dbtechprojects.JukeBoxCompose.ui.theme.turntableBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity(), OnAlbumClick, onMusicPlayerClick {

    private val isPlaying = mutableStateOf(false)
    private val currentSong = mutableStateOf(AlbumRepository.getAlbums()[0])
    private val currentSongIndex = mutableStateOf(-1)
    lateinit var listState: LazyListState
    lateinit var coroutineScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = appBackground, modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    // should retrieve from viewmodel to keep on rotation
                    listState = rememberLazyListState()
                    coroutineScope = rememberCoroutineScope()
                    MainContent(
                        onAlbumClick = this,
                        isPlaying = isPlaying,
                        currentSong,
                        listState,
                        onMusicPlayerClick = this,
                        currentSongIndex
                    )

                }
            }
        }
    }

    override fun albumclick(album: Album) {
        Log.d(TAG, "Album Clicked : ${album.songTitle} ")
        isPlaying.value = false
        currentSong.value = album
        isPlaying.value = true
        currentSongIndex.value = album.index
    }

    override fun onMusicButtonClick(command: String) {

        when(command){
            "skip" -> {
                currentSong.value = AlbumRepository.getAlbums()[currentSong.value.index + 1]
                currentSongIndex.value ++
                coroutineScope.launch {
                    currentSong.value.isPlaying = true
                    listState.animateScrollToItem(currentSong.value.index)
                }
            }
            "previous" -> {
                currentSong.value = AlbumRepository.getAlbums()[currentSong.value.index - 1]
                currentSongIndex.value --
                coroutineScope.launch {
                    currentSong.value.isPlaying = true
                    listState.animateScrollToItem(currentSong.value.index)
                }
            }

            "play" -> {
                isPlaying.value = !isPlaying.value
                currentSong.value.isPlaying = !isPlaying.value
                currentSongIndex.value = currentSong.value.index
            }
        }

    }
}


@Composable
fun MainContent(
    onAlbumClick: OnAlbumClick,
    isPlaying: MutableState<Boolean>,
    album: MutableState<Album>,
    listState: LazyListState,
    onMusicPlayerClick: onMusicPlayerClick,
    currentSongIndex: MutableState<Int>
) {
    Column {
        Title()
        AlbumList(onAlbumClick, listState,currentSongIndex, R.drawable.ic_baseline_pause_24)
        TurnTable(isPlaying)
        Player(album, isPlaying, listState, onMusicPlayerClick )
    }
}

@Composable
fun Title() {
    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()

    ) {

        Text(
            "JukeBox Compose",
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = titleFont,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Player(
    album: MutableState<Album>,
    isPlaying: MutableState<Boolean>,
    listState: LazyListState,
    onMusicPlayerClick: onMusicPlayerClick
) {
    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()

    ) {
        Log.d(TAG, album.value.toString())
        Text(
            album.value.songTitle,
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = titleFont,
            textAlign = TextAlign.Center
        )
        Row(Modifier.align(Alignment.CenterHorizontally)) {

            Image(
                painter = painterResource(
                    id =
                    R.drawable.ic_baseline_skip_previous_24
                ),
                contentDescription = "Previous",
                modifier = Modifier
                    .clickable(true, onClick = {
                        onMusicPlayerClick.onMusicButtonClick("previous")
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
                    .clickable(true, onClick = {onMusicPlayerClick.onMusicButtonClick("play")})
                    .padding(16.dp)
                    .size(35.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.ic_baseline_skip_next_24),
                contentDescription = "Next Song",
                modifier = Modifier
                    .clickable(true, onClick = {
                        onMusicPlayerClick.onMusicButtonClick("skip")
                    })
                    .padding(16.dp)
                    .size(35.dp)
            )
        }


    }
}


@Composable
fun TurnTable(isPlaying: MutableState<Boolean>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(6.dp)

    ) {

        TurnTableDrawable(
            shape = RoundedCornerShape(20.dp),
            color = turntableBackground,
            size = 240.dp,
            turntableDrawable = R.drawable.record,
            armDrawable = R.drawable.tone_arm_only,
            armLockDrawable = R.drawable.arm_lock,
            isPlaying = isPlaying
        )
    }
}
// onAlbumClickInterFace

interface OnAlbumClick {
    fun albumclick(album: Album)
}

// onMusicPlayerClick
interface onMusicPlayerClick {
    fun onMusicButtonClick(command: String)
}