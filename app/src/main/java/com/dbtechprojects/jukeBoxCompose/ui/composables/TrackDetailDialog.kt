package com.dbtechprojects.jukeBoxCompose.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dbtechprojects.jukeBoxCompose.model.Track
import com.dbtechprojects.jukeBoxCompose.ui.theme.dialogColor

@Composable
fun TrackDetailDialog(track: MutableState<Track?>, openDialog: MutableState<Boolean>) {
    if (openDialog.value && track.value != null) {
        val track = track.value!!
        AlertDialog(
            backgroundColor = dialogColor,
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = track.songTitle)
            },
            text = {
                Column {
                    Text("Artist " + track.artist)
                    Text("Track No. " + (track.index + 1))
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        onClick = { openDialog.value = false }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        )
    }
}