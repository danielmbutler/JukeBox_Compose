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
    private val storage = Firebase.storage(Firebase.app)
    private val albumArtRef = storage.reference.child(ALBUM_ART_CAPS)
    private val trackReference = storage.reference


    suspend fun getAlbums() = suspendCoroutine<List<Album>> {

        val albumlist = mutableListOf<Album>()
        try {
            Firebase.firestore.collection(TRACKS)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var index = 0
                        task.result.forEach { document ->
                            val imageUrl = albumArtRef.child(document.getString(ALBUM_ART)!!)
                            val trackUrl = trackReference.child(document.getString(FILENAME)!!)
                            imageUrl.downloadUrl.addOnSuccessListener { imgDownloadUrl ->
                                trackUrl.downloadUrl.addOnSuccessListener { trackDownloadUrl ->
                                    albumlist.add(
                                        document.toAlbum(
                                            index,
                                            imgDownloadUrl.toString(),
                                            trackDownloadUrl.toString()
                                        )
                                    )
                                    if (index == task.result.size() - 1) {
                                        it.resume(albumlist)
                                    }
                                    index++
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
    val img: String,
    val songTitle: String,
    val index: Int,
    val artist: String,
    val trackUrl: String,
    var isPlaying: Boolean,
    var fileName: String
)

const val NAME = "name"
const val ARTIST = "artist"
const val FILENAME = "fileName"
const val ALBUM_ART = "albumArt"
const val TRACKS = "tracks"
const val ALBUM_ART_CAPS = "AlbumArt"


fun QueryDocumentSnapshot.toAlbum(index: Int, imgUrl: String, trackUrl: String): Album {
    return Album(
        img = imgUrl,
        songTitle = this.getString(NAME) ?: "",
        artist = this.getString(ARTIST) ?: "",
        fileName = this.getString(FILENAME) ?: "",
        isPlaying = false,
        index = index,
        trackUrl = trackUrl
    )
}
