package com.dbtechprojects.JukeBoxCompose

import android.graphics.drawable.Drawable

object AlbumRepository {

    fun getAlbums() : List<Album>{

        val albumlist = mutableListOf<Album>()
        val drawableList = listOf<Int>(
            R.drawable.album_1,
            R.drawable.album_2,
            R.drawable.album_3,
            R.drawable.album_4,
            R.drawable.album_5,
            R.drawable.album_6,
        )

        for(i in drawableList){
            albumlist.add(Album(i, (drawableList.indexOf(i)).toString() + "Song Title", drawableList.indexOf(i), false))
        }

        return albumlist
    }
}


data class Album(
    val img: Int,
    val songTitle: String,
    val index: Int,
    var isPlaying: Boolean
)
