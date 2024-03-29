package com.dbtechprojects.jukeBoxCompose

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.dbtechprojects.jukeBoxCompose.model.MusicPlayerOption
import com.dbtechprojects.jukeBoxCompose.model.Track
import com.dbtechprojects.jukeBoxCompose.ui.AlbumList
import com.dbtechprojects.jukeBoxCompose.ui.Player
import com.dbtechprojects.jukeBoxCompose.ui.Title
import com.dbtechprojects.jukeBoxCompose.ui.TurnTable
import com.dbtechprojects.jukeBoxCompose.ui.composables.LoadingScreen
import com.dbtechprojects.jukeBoxCompose.ui.composables.TrackDetailDialog
import com.dbtechprojects.jukeBoxCompose.ui.theme.JukeBoxComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnMusicButtonClick {

    private val isPlaying = mutableStateOf(false) // is music current being played
    private var trackList = listOf<Track>() // retrieve song list
    private lateinit var currentSong: MutableState<Track>// currently playing song
    private val clickedSong: MutableState<Track?> = mutableStateOf(null)// currently playing song
    private val currentSongIndex = mutableStateOf(-1) // used for recyclerview playing overlay
    private val turntableArmState = mutableStateOf(false)// turns turntable arm
    private val isBuffering = mutableStateOf(false)
    private val isTurntableArmFinished =
        mutableStateOf(false) // lets us know the turntable Arm rotation is finished
    private lateinit var listState: LazyListState // current state of album list
    private lateinit var coroutineScope: CoroutineScope // scope to be used in composables
    private lateinit var mediaPlayer: MediaPlayer
    private val tracksViewModel: TracksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        setContent {
            JukeBoxComposeTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    listState = rememberLazyListState()
                    coroutineScope = rememberCoroutineScope()
                    val openDialog = remember { mutableStateOf(false) }
                    val trackList by tracksViewModel.trackList.observeAsState()
                    if (trackList?.isNotEmpty() == true) {
                        MainContent(
                            isPlaying = isPlaying,
                            currentSong,
                            listState,
                            onMusicPlayerClick = this@MainActivity,
                            currentSongIndex,
                            turntableArmState,
                            isTurntableArmFinished,
                            isBuffering = isBuffering,
                            trackList!!
                        ) { song ->
                            clickedSong.value = song
                            openDialog.value = true
                        }
                        TrackDetailDialog(track = clickedSong, openDialog = openDialog)
                    } else {
                        LoadingScreen()
                    }
                }
            }
        }
    }


    private fun observeViewModel() {
        tracksViewModel.trackList.observe(this) { list ->
            if (list.isNotEmpty()) {
                trackList = list
                currentSong = mutableStateOf(list.first())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> {
                window.statusBarColor = resources.getColor(R.color.white, null)
            }
            else -> {
                window.statusBarColor = resources.getColor(R.color.black, null)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized && isPlaying.value) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
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
            e.printStackTrace()
        }
    }

    private fun updateList() {
        coroutineScope.launch {
            if (isPlaying.value) {
                currentSong.value.isPlaying = true
            }
            listState.animateScrollToItem(
                currentSong.value.index
            )
        }
    }

    override fun onMusicButtonClick(command: MusicPlayerOption) {
        when (command) {
            MusicPlayerOption.Skip -> {
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
                currentSong.value = trackList[nextSongIndex]

                if (isPlaying.value) {
                    play()
                }
                updateList()
            }
            MusicPlayerOption.Previous -> {

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

                if (isPlaying.value) {
                    play()
                }

                updateList()
            }

            MusicPlayerOption.Play -> {
                currentSong.value.isPlaying =
                    !isPlaying.value // confirms whether current song is played or paused
                currentSongIndex.value = currentSong.value.index //confirms current song Index
                try {
                    if (this::mediaPlayer.isInitialized && isPlaying.value) {
                        mediaPlayer.stop()
                        mediaPlayer.release()
                        isPlaying.value = false
                    } else play()
                } catch (e: Exception) {
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
    album: MutableState<Track>,
    listState: LazyListState,
    onMusicPlayerClick: OnMusicButtonClick,
    currentSongIndex: MutableState<Int>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>,
    isBuffering: MutableState<Boolean>,
    albums: List<Track>,
    onTrackItemClick: (Track) -> Unit,
) {
    Column {
        Title()
        AlbumList(
            isPlaying,
            listState,
            currentSongIndex,
            R.drawable.ic_baseline_pause_24,
            albums,
            onTrackItemClick
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
    fun onMusicButtonClick(command: MusicPlayerOption)
}