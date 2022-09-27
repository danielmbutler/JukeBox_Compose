package com.dbtechprojects.jukeBoxCompose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dbtechprojects.jukeBoxCompose.R
import com.dbtechprojects.jukeBoxCompose.model.Track

@Composable
fun AlbumList(
    isPlaying: MutableState<Boolean>,
    listState: LazyListState,
    playingSongIndex: MutableState<Int>,
    overlayIcon: Int,
    tracks: List<Track>,
    onTrackItemClick: (Track) -> Unit,
) {
    LazyRow(contentPadding = PaddingValues(16.dp), state = listState) {
        items(
            items = tracks,
            itemContent = {
                TrackListItem(
                    track = it,
                    playingSongIndex,
                    isPlaying,
                    overlayIcon,
                    onTrackItemClick
                )
            })
    }
}

@Composable
fun TrackListItem(
    track: Track,
    playingSongIndex: MutableState<Int>,
    isPlaying: MutableState<Boolean>,
    overlayIcon: Int,
    onTrackItemClick: (Track) -> Unit
) {
    Row(modifier = Modifier.clickable(true, onClick = { onTrackItemClick(track) })) {
        Column {
            Box(
                Modifier.padding(6.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(track.img)
                        .crossfade(true)
                        .build(),
                    "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .size(120.dp)
                )
                if (playingSongIndex.value == track.index && isPlaying.value) {
                    OverlayRoundedBox(
                        shape = RoundedCornerShape(32.dp),
                        color = Color.Gray.copy(alpha = 0.6f),
                        size = 120.dp,
                        overlayIcon = overlayIcon,
                        contentDescription = stringResource(R.string.play_indicator)
                    )
                }

            }
        }
    }
}

@Composable
fun OverlayRoundedBox(
    shape: Shape,
    color: Color,
    size: Dp? = null,
    overlayIcon: Int? = null,
    showProgressBar: Boolean = false,
    contentDescription: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = if (size == null) Modifier
                .clip(shape)
                .background(color) else Modifier
                .clip(shape)
                .background(color)
                .size(size)
        ) {
            if (showProgressBar) {
                CircularProgressIndicator()
            } else {
                Image(
                    painter = painterResource(id = overlayIcon!!),
                    contentDescription = contentDescription,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

        }
    }
}