package com.dbtechprojects.JukeBoxCompose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dbtechprojects.JukeBoxCompose.Album
import com.dbtechprojects.JukeBoxCompose.AlbumRepository
import com.dbtechprojects.JukeBoxCompose.OnAlbumClick

@Composable
fun AlbumList(onClick: OnAlbumClick, listState: LazyListState, playingSongIndex: MutableState<Int>, overlayIcon: Int) {

    val albums = remember { AlbumRepository.getAlbums() }
    // create AlbumRow

    LazyRow(contentPadding = PaddingValues(16.dp), state = listState) {
        items(
            items = albums,
            itemContent = {
                AlbumListItem(album = it, onClick, playingSongIndex, overlayIcon)
            })
    }
}

@Composable
fun AlbumListItem(album: Album, onClick: OnAlbumClick, playingSongIndex: MutableState<Int>, overlayIcon: Int) {
    Row {
        Column {
            Box(Modifier.padding(6.dp)) {

                Image(
                    painter = painterResource(id = album.img),
                    contentDescription = "album image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(32.dp)))
                        .clickable(true, onClick = { onClick.albumclick(album) }),
                )

                if (playingSongIndex.value == album.index){
                    AlbumOverlayRoundedBox(
                        shape = RoundedCornerShape(32.dp) ,
                        color = Color.Gray.copy(alpha = 0.6f),
                        size = 120.dp,
                    overlayIcon = overlayIcon)
                }

            }
        }
    }
}

@Composable
fun AlbumOverlayRoundedBox(shape: Shape, color: Color, size: Dp, overlayIcon: Int){
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color)
        ){
            Image(
                painter = painterResource(id = overlayIcon),
                contentDescription = "Album Play indicator",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}