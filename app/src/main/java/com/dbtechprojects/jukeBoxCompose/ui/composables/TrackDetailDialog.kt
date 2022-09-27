package com.dbtechprojects.jukeBoxCompose.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dbtechprojects.jukeBoxCompose.R
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
                    Text(stringResource(R.string.artist) + track.artist)
                    Text(stringResource(R.string.trackNo) + (track.index + 1))
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
                        Text(stringResource(R.string.dismiss))
                    }
                }
            }
        )
    }
}