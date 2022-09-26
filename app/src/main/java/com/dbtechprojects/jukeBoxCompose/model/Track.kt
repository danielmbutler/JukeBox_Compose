package com.dbtechprojects.jukeBoxCompose.model

import com.dbtechprojects.jukeBoxCompose.ARTIST
import com.dbtechprojects.jukeBoxCompose.FILENAME
import com.dbtechprojects.jukeBoxCompose.NAME
import com.google.firebase.firestore.QueryDocumentSnapshot

data class Track(
    val img: String,
    val songTitle: String,
    val index: Int,
    val artist: String,
    val trackUrl: String,
    var isPlaying: Boolean,
    var fileName: String
)

fun QueryDocumentSnapshot.toTrack(index: Int, imgUrl: String, trackUrl: String): Track {
    return Track(
        img = imgUrl,
        songTitle = this.getString(NAME) ?: "",
        artist = this.getString(ARTIST) ?: "",
        fileName = this.getString(FILENAME) ?: "",
        isPlaying = false,
        index = index,
        trackUrl = trackUrl
    )
}