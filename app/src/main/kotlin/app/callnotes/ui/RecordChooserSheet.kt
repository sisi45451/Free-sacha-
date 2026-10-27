package app.callnotes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.callnotes.R

/**
 * Choix du type d'enregistrement, en bas de l'écran (atteignable au pouce).
 * Avant une réunion, un écran rappellera de prévenir les participants
 * (étape 5).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordChooserSheet(
    onDismiss: () -> Unit,
    onMeeting: () -> Unit,
    onMemo: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.navigationBarsPadding().padding(bottom = 16.dp)) {
            ListItem(
                modifier = Modifier.clickable(onClick = onMeeting),
                leadingContent = { Icon(Icons.Filled.Groups, contentDescription = null) },
                headlineContent = { Text(stringResource(R.string.record_meeting)) },
                supportingContent = { Text(stringResource(R.string.record_meeting_hint)) },
            )
            ListItem(
                modifier = Modifier.clickable(onClick = onMemo),
                leadingContent = { Icon(Icons.Filled.Mic, contentDescription = null) },
                headlineContent = { Text(stringResource(R.string.record_memo)) },
                supportingContent = { Text(stringResource(R.string.record_memo_hint)) },
            )
        }
    }
}
