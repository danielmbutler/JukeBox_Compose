package com.dbtechprojects.JukeBoxCompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    private val isPlaying = mutableStateOf(false) // is music current being played
    private val trackList = AlbumRepository.getAlbums() // retrieve song list
    private val currentSong = mutableStateOf(trackList[0]) // currently playing song
    private val currentSongIndex = mutableStateOf(-1) // used for recyclerview playing overlay
    private val turntableArmState  = mutableStateOf(false)// turns turntable arm
    private lateinit var listState: LazyListState // current state of album list
    private lateinit var coroutineScope: CoroutineScope // scope to be used in composables

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
                        currentSongIndex,
                        turntableArmState
                    )
                    Log.d(TAG, "onCreate: TrackList : $trackList")
                }
            }
        }
    }

    override fun albumclick(album: Album) {
        Log.d(TAG, "Album Clicked : ${album.songTitle} ")
        isPlaying.value = !isPlaying.value
        currentSong.value = album
        currentSongIndex.value = album.index
    }

    override fun onMusicButtonClick(command: String) {

        when(command){
            "skip" -> {
                // check list
                var nextSongIndex = currentSong.value.index + 1 // increment next
                // if current song is last song in the tracklist (track list starts at 0)
                if (currentSong.value.index == trackList.size -1) {
                    nextSongIndex = 0 // go back to first song
                    if (isPlaying.value){
                        currentSongIndex.value =0 // playing song is first song in list
                    }
                } else {
                    currentSongIndex.value ++ // increment song index
                }

                Log.d(TAG, "onMusicButtonClick: next song index $nextSongIndex")
                Log.d(TAG, "onMusicButtonClick: tracklist size ${trackList.size}")
                Log.d(TAG, "onMusicButtonClick: currentsong index ${currentSong.value.index}")
                currentSong.value = trackList[nextSongIndex]

                coroutineScope.launch {
                    if (isPlaying.value){
                        currentSong.value.isPlaying = true
                    }
                    listState.animateScrollToItem(
                        currentSong.value.index
                    )
                }
            }
            "previous" -> {

                var previousSongIndex = currentSong.value.index - 1 // increment previous
                // if current song is first song in the tracklist (track list starts at 0)
                if (currentSong.value.index == 0) {
                    previousSongIndex = trackList.lastIndex // go to last song in list
                    if (isPlaying.value){
                        currentSongIndex.value = trackList.lastIndex // last song is now playing song
                    }
                } else {
                    currentSongIndex.value -- // decrement current song
                }
                currentSong.value = trackList[previousSongIndex]

                coroutineScope.launch {
                    currentSong.value.isPlaying = true
                    listState.animateScrollToItem(currentSong.value.index)
                }
            }

            "play" -> {
                isPlaying.value = !isPlaying.value // confirms if we are in playing or paused mode
                currentSong.value.isPlaying = !isPlaying.value // confirms whether current song is played or paused
                currentSongIndex.value = currentSong.value.index //confirms current song Index
                turntableArmState.value = ! turntableArmState.value // moves turntable Arm Accordingly
            }
        }

    }
}


//UI

@Composable
fun MainContent(
    onAlbumClick: OnAlbumClick,
    isPlaying: MutableState<Boolean>,
    album: MutableState<Album>,
    listState: LazyListState,
    onMusicPlayerClick: onMusicPlayerClick,
    currentSongIndex: MutableState<Int>,
    turntableArmState: MutableState<Boolean>
) {
    Column {
        Title()
        AlbumList(isPlaying,onAlbumClick, listState,currentSongIndex, R.drawable.ic_baseline_pause_24)
        TurnTable(isPlaying, turntableArmState)
        Player(album, isPlaying, listState, onMusicPlayerClick )
    }
}

@Composable
fun Title() {
    Column(
        modifier = Modifier
            .padding(15.dp)
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
                    .clickable(true, onClick = { onMusicPlayerClick.onMusicButtonClick("play") })
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
fun TurnTable(isPlaying: MutableState<Boolean>, turntableArmState: MutableState<Boolean>) {
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
            isPlaying = isPlaying,
            turntableArmState

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