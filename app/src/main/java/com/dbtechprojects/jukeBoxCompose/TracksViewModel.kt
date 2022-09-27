package com.dbtechprojects.jukeBoxCompose

import android.util.Log
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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            AlbumRepository.getAlbums().let {
                _trackList.postValue( it.sortedBy { it.index })
                Log.d("tracks", "${it}")
            }
        }
    }

}