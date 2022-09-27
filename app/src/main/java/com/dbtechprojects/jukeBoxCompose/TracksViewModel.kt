package com.dbtechprojects.jukeBoxCompose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbtechprojects.jukeBoxCompose.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TracksViewModel @Inject constructor(albumRepository: TrackRepository) : ViewModel() {

    private val _trackList = MutableLiveData<List<Track>>()
    val trackList : LiveData<List<Track>> get() = _trackList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            albumRepository.getTracks().let {
                _trackList.postValue( it.sortedBy { it.index })
            }
        }
    }

}