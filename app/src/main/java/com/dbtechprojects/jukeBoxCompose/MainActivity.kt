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
import androidx.lifecycle.lifecycleScope
import com.dbtechprojects.jukeBoxCompose.ui.AlbumList
import com.dbtechprojects.jukeBoxCompose.ui.Player
import com.dbtechprojects.jukeBoxCompose.ui.Title
import com.dbtechprojects.jukeBoxCompose.ui.TurnTable
import com.dbtechprojects.jukeBoxCompose.ui.theme.MyApplicationTheme
import com.dbtechprojects.jukeBoxCompose.ui.theme.appBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity(), onMusicPlayerClick {

    private val isPlaying = mutableStateOf(false) // is music current being played
    private lateinit var trackList: List<Album> // retrieve song list
    private lateinit var currentSong: MutableState<Album>// currently playing song
    private val currentSongIndex = mutableStateOf(-1) // used for recyclerview playing overlay
    private val turntableArmState = mutableStateOf(false)// turns turntable arm
    private val isBuffering = mutableStateOf(false)
    private val isTurntableArmFinished = mutableStateOf(false) // lets us know the turntable Arm rotation is finished
    private lateinit var listState: LazyListState // current state of album list
    private lateinit var coroutineScope: CoroutineScope // scope to be used in composables
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            AlbumRepository.getAlbums().let {
                trackList = it.sortedBy { it.index }
                Log.d("tracks", "${it}")
                currentSong = mutableStateOf(trackList.first())
            }

            withContext(Dispatchers.Main) {
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
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun play() {
        try {
            if (this::mediaPlayer.isInitialized && isPlaying.value){
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
                if (!turntableArmState.value){
                    turntableArmState.value = true
                }
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            Log.d("exc" , "2")
            e.printStackTrace()
        }
    }

    override fun onMusicButtonClick(command: String) {
        when (command) {
            "skip" -> {
                // check list
                var nextSongIndex = currentSong.value.index + 1 // increment next
                // if current song is last song in the tracklist (track list starts at 0)
                if (currentSong.value.index == trackList.size - 1) {
                    nextSongIndex = 0 // go back to first song
                    if (isPlaying.value) {
                        currentSongIndex.value = 0 // playing song is first song in list
                    }
                } else {
                    currentSongIndex.value++ // increment song index
                }


                Log.d(TAG, "onMusicButtonClick: next song index $nextSongIndex")
                Log.d(TAG, "onMusicButtonClick: tracklist size ${trackList.size}")
                Log.d(TAG, "onMusicButtonClick: currentsong index ${currentSong.value.index}")
                currentSong.value = trackList[nextSongIndex]

                if (isPlaying.value){
                    play()
                }
                coroutineScope.launch {
                    if (isPlaying.value) {
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
                    if (isPlaying.value) {
                        currentSongIndex.value =
                            trackList.lastIndex // last song is now playing song
                    }
                } else {
                    currentSongIndex.value-- // decrement current song
                }
                currentSong.value = trackList[previousSongIndex]

                if (isPlaying.value){
                    play()
                }

                coroutineScope.launch {
                    currentSong.value.isPlaying = true
                    listState.animateScrollToItem(currentSong.value.index)
                }
            }

            "play" -> {
                currentSong.value.isPlaying =
                    !isPlaying.value // confirms whether current song is played or paused
                currentSongIndex.value = currentSong.value.index //confirms current song Index
                try {
                    if (this::mediaPlayer.isInitialized && isPlaying.value){
                        mediaPlayer.stop()
                        mediaPlayer.release()
                        isPlaying.value = false
                    } else play()
                }catch (e: Exception){
                    mediaPlayer.release()
                    isPlaying.value = false
                }
            }
        }

    }
}


//UI

@Composable
fun MainContent(
    isPlaying: MutableState<Boolean>,
    album: MutableState<Album>,
    listState: LazyListState,
    onMusicPlayerClick: onMusicPlayerClick,
    currentSongIndex: MutableState<Int>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>,
    isBuffering: MutableState<Boolean>,
    albums: List<Album>,
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
interface onMusicPlayerClick {
    fun onMusicButtonClick(command: String)
}