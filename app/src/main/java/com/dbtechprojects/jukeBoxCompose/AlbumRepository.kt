package com.dbtechprojects.jukeBoxCompose

import android.util.Log
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AlbumRepository {
    val storage = Firebase.storage(Firebase.app)
    val albumArtRef = storage.reference.child("AlbumArt")


    suspend fun getAlbums() = suspendCoroutine<List<Album>> {

        val albumlist = mutableListOf<Album>()
        try {
            Firebase.firestore.collection("tracks")
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var index = 0
                        task.result.forEach { document ->
                            document.getString("albumArt")
                                ?.let { imageFileName ->
                                    val imageUrl = albumArtRef.child(imageFileName)
                                    imageUrl.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        Log.d("firebase", "download : ${downloadUrl}")
                                        albumlist.add(
                                            document.toAlbum(
                                                index,
                                                downloadUrl.toString()
                                            )
                                        )

                                        if (index == task.result.size() -1) {
                                            it.resume(albumlist)
                                        }
                                        index ++
                                    }
                                }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.d("album", "failed : ${e.message}")
        }

    }
}


data class Album(
    val img: String?,
    val songTitle: String,
    val index: Int,
    val artist: String,
    var isPlaying: Boolean,
    var fileName: String
)

fun QueryDocumentSnapshot.toAlbum(index: Int, downloadUrl: String?): Album {
    return Album(
        img = downloadUrl,
        songTitle = this.getString("name") ?: "",
        artist = this.getString("artist") ?: "",
        fileName = this.getString("fileName") ?: "",
        isPlaying = false,
        index = index,
    )
}
