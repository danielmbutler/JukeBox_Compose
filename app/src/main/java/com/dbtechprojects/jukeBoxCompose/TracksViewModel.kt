package com.dbtechprojects.jukeBoxCompose

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbtechprojects.jukeBoxCompose.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TracksViewModel : ViewModel() {

    private val _trackList = MutableLiveData<List<Track>>()
    val trackList : LiveData<List<Track>> get() = _trackList

    var currentSong :Track? = null

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying : LiveData<Boolean> get() = _isPlaying

    var currentSongIndex = 0

    init {
        viewModelScope.launch(Dispatchers.IO) {
            AlbumRepository.getAlbums().let {
                _trackList.postValue( it.sortedBy { it.index })
                Log.d("tracks", "${it}")
                currentSong = it.first()
            }
        }
    }

    private fun skipSong(){
        // check list
        var nextSongIndex = currentSong?.index?.plus(1)  ?: 0 // increment next
        // if current song is last song in the tracklist we need to go back to first song
        if (currentSong?.index == _trackList.value?.size?.minus(1)) {
            nextSongIndex = 0 // go back to first song
            if (_isPlaying.value == true) {
                currentSongIndex = 0 // playing song is first song in list
            }
        } else {
            currentSongIndex ++ // increment song index
        }

        currentSong = trackList.value?.get(nextSongIndex)

        if (_isPlaying.value == true){
            currentSong?.isPlaying = true
        }
//
//        if (isPlaying.value){
//            play()
//        }
//        coroutineScope.launch {
//            if (isPlaying.value) {
//                currentSong.value.isPlaying = true
//            }
//            listState.animateScrollToItem(
//                currentSong.value.index
//            )
//        }
    }

    fun playerBtnClick(command: String){
        when (command) {
            "skip" -> { skipSong() }
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