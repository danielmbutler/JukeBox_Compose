package com.dbtechprojects.jukeBoxCompose

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.dbtechprojects.jukeBoxCompose.model.Track
import com.dbtechprojects.jukeBoxCompose.ui.AlbumList
import com.dbtechprojects.jukeBoxCompose.ui.Player
import com.dbtechprojects.jukeBoxCompose.ui.Title
import com.dbtechprojects.jukeBoxCompose.ui.TurnTable
import com.dbtechprojects.jukeBoxCompose.ui.theme.MyApplicationTheme
import com.dbtechprojects.jukeBoxCompose.ui.theme.appBackground
import kotlinx.coroutines.CoroutineScope

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity(), OnMusicButtonClick {

    private val isPlaying = mutableStateOf(false) // is music current being played
    private lateinit var trackList: List<Track> // retrieve song list
    private lateinit var currentSong: MutableState<Track>// currently playing song
    private val currentSongIndex = mutableStateOf(-1) // used for recyclerview playing overlay
    private val turntableArmState = mutableStateOf(false)// turns turntable arm
    private val isBuffering = mutableStateOf(false)
    private val isTurntableArmFinished =
        mutableStateOf(false) // lets us know the turntable Arm rotation is finished
    private lateinit var listState: LazyListState // current state of album list
    private lateinit var coroutineScope: CoroutineScope // scope to be used in composables
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var viewModel: TracksViewModel

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
                        isPlaying = isPlaying,
                        currentSong,
                        listState,
                        onMusicPlayerClick = this@MainActivity,
                        currentSongIndex,
                        turntableArmState,
                        isTurntableArmFinished,
                        isBuffering = isBuffering,
                        trackList
                    )
                    Log.d(TAG, "onCreate: TrackList : $trackList")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun play() {
        try {
            if (this::mediaPlayer.isInitialized && isPlaying.value) {
                mediaPlayer.stop()
                mediaPlayer.release()
                isPlaying.value = false
                turntableArmState.value = false
                isTurntableArmFinished.value = false
            }
            isBuffering.value = true
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(currentSong.value.trackUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isBuffering.value = false
                isPlaying.value = true
                if (!turntableArmState.value) {
                    turntableArmState.value = true
                }
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            Log.d("exc", "2")
            e.printStackTrace()
        }
    }

    override fun onMusicButtonClick(command: String) {
        viewModel.playerBtnClick(command)
    }
}


//UI
@Composable
fun MainContent(
    isPlaying: MutableState<Boolean>,
    album: MutableState<Track>,
    listState: LazyListState,
    onMusicPlayerClick: OnMusicButtonClick,
    currentSongIndex: MutableState<Int>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>,
    isBuffering: MutableState<Boolean>,
    albums: List<Track>,
) {
    Column {
        Title()
        AlbumList(
            isPlaying,
            listState,
            currentSongIndex,
            R.drawable.ic_baseline_pause_24,
            albums
        )
        TurnTable(isPlaying, turntableArmState, isTurntableArmFinished)
        Player(
            album, isPlaying,
            onMusicPlayerClick = onMusicPlayerClick,
            isTurntableArmFinished = isTurntableArmFinished,
            isBuffering = isBuffering
        )
    }
}

// onMusicPlayerClick
interface OnMusicButtonClick {
    fun onMusicButtonClick(command: String)
}