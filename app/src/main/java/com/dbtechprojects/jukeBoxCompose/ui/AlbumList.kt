package com.dbtechprojects.jukeBoxCompose.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dbtechprojects.jukeBoxCompose.Album

@Composable
fun AlbumList(
    isPlaying: MutableState<Boolean>,
    listState: LazyListState,
    playingSongIndex: MutableState<Int>,
    overlayIcon: Int,
    albums: List<Album>,
) {
    // create AlbumRow
    LazyRow(contentPadding = PaddingValues(16.dp), state = listState) {
        items(
            items = albums,
            itemContent = {
                AlbumListItem(album = it, playingSongIndex, isPlaying, overlayIcon)
            })
    }
}

@Composable
fun AlbumListItem(
    album: Album,
    playingSongIndex: MutableState<Int>,
    isPlaying: MutableState<Boolean>,
    overlayIcon: Int
) {
    Row {
        Column {
            Box(Modifier.padding(6.dp)) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(album.img)
                        .crossfade(true)
                        .build(),
                    "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(RoundedCornerShape(32.dp)).size(120.dp)
                )
                Log.d("TAG", "AlbumListItem:${playingSongIndex.value}  $album")

                if (playingSongIndex.value == album.index && isPlaying.value) {
                    AlbumOverlayRoundedBox(
                        shape = RoundedCornerShape(32.dp),
                        color = Color.Gray.copy(alpha = 0.6f),
                        size = 120.dp,
                        overlayIcon = overlayIcon
                    )
                }

            }
        }
    }
}

@Composable
fun AlbumOverlayRoundedBox(shape: Shape, color: Color, size: Dp, overlayIcon: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color)
        ) {
            Image(
                painter = painterResource(id = overlayIcon),
                contentDescription = "Album Play indicator",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}