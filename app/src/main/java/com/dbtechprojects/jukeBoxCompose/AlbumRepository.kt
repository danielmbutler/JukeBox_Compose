package com.dbtechprojects.jukeBoxCompose

import android.util.Log
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object AlbumRepository {

    fun getAlbums() : List<Album>{

        val albumlist = mutableListOf<Album>()

        GlobalScope.launch {
                try {
                    Firebase.firestore.collection("tracks")
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                albumlist.add(document.toAlbum())
                            }
                        }
                    }
                }catch (e: Exception){
                    Log.d("album", "failed : ${e.message}")
                }

            }
        return albumlist
    }
}


data class Album(
    val img: String,
    val songTitle: String,
    val index: Int,
    val artist: String,
    var isPlaying: Boolean,
    var fileName :String
)

fun QueryDocumentSnapshot.toAlbum(): Album {
    return Album(
        img = this.getString("albumArt") ?: "",
        songTitle = this.getString("name") ?: "",
        artist = this.getString("artist") ?: "",
        fileName = this.getString("fileName") ?: "",
        isPlaying = false,
        index = 0,
    )
}
