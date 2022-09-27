package com.dbtechprojects.jukeBoxCompose

import android.util.Log
import com.dbtechprojects.jukeBoxCompose.model.Track
import com.dbtechprojects.jukeBoxCompose.model.toTrack
import com.dbtechprojects.jukeBoxCompose.util.Constants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AlbumRepository() {
    private val storage = Firebase.storage(Firebase.app)
    private val albumArtRef = storage.reference.child(Constants.ALBUM_ART_CAPS)
    private val trackReference = storage.reference


    suspend fun getAlbums() = suspendCoroutine<List<Track>> {

        val albumList = mutableListOf<Track>()
        try {
            Firebase.firestore.collection(Constants.TRACKS)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var index = 0
                        task.result.forEach { document ->
                            val imageUrl =
                                albumArtRef.child(document.getString(Constants.ALBUM_ART)!!)
                            val trackUrl =
                                trackReference.child(document.getString(Constants.FILENAME)!!)
                            imageUrl.downloadUrl.addOnSuccessListener { imgDownloadUrl ->
                                trackUrl.downloadUrl.addOnSuccessListener { trackDownloadUrl ->
                                    albumList.add(
                                        document.toTrack(
                                            index,
                                            imgDownloadUrl.toString(),
                                            trackDownloadUrl.toString()
                                        )
                                    )
                                    if (index == task.result.size() - 1) {
                                        it.resume(albumList)
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



