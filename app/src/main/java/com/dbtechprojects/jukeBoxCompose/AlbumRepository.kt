package com.dbtechprojects.jukeBoxCompose

import android.util.Log
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AlbumRepository {

    suspend fun getAlbums()  = suspendCoroutine<List<Album>> {

        val albumlist = mutableListOf<Album>()
            try {
                Firebase.firestore.collection("tracks")
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result.forEachIndexed { index, document ->
                                albumlist.add(document.toAlbum(index))
                            }
                            it.resume(albumlist)
                        }
                    }
            }catch (e: Exception){
                Log.d("album", "failed : ${e.message}")
            }



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

fun QueryDocumentSnapshot.toAlbum(index: Int): Album {
    return Album(
        img = this.getString("albumArt") ?: "",
        songTitle = this.getString("name") ?: "",
        artist = this.getString("artist") ?: "",
        fileName = this.getString("fileName") ?: "",
        isPlaying = false,
        index = index,
    )
}
