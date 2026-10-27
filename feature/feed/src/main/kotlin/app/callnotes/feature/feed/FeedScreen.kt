package app.callnotes.feature.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.callnotes.core.designsystem.component.EmptyState
import app.callnotes.core.model.CallDirection
import app.callnotes.core.model.ProcessingStage
import app.callnotes.core.model.RecordingListItem
import app.callnotes.core.model.RecordingType
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Point d'entrée de l'onglet, branché sur son ViewModel. */
@Composable
fun FeedRoute(
    onOpenRecording: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FeedScreen(
        uiState = uiState,
        onTypeFilterChange = viewModel::onTypeFilterChange,
        onOpenRecording = onOpenRecording,
        modifier = modifier,
    )
}

/** Écran « Fil » sans état : facile à prévisualiser et à tester. */
@Composable
fun FeedScreen(
    uiState: FeedUiState,
    onTypeFilterChange: (RecordingType?) -> Unit,
    onOpenRecording: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        val selected = (uiState as? FeedUiState.Ready)?.typeFilter
        TypeFilterRow(selected = selected, onSelect = onTypeFilterChange)

        when (uiState) {
            FeedUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            is FeedUiState.Error -> EmptyState(
                icon = Icons.Outlined.History,
                title = stringResource(R.string.feed_error_title),
                message = uiState.message ?: stringResource(R.string.feed_error_message),
            )

            is FeedUiState.Ready -> if (uiState.items.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.History,
                    title = stringResource(R.string.feed_empty_title),
                    message = stringResource(R.string.feed_empty_message),
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
                    items(uiState.items, key = { it.id }) { item ->
                        RecordingRow(item = item, onOpen = { onOpenRecording(item.id) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

/** Puces de filtre par type : Tous / Appels / Réunions / Mémos. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeFilterRow(
    selected: RecordingType?,
    onSelect: (RecordingType?) -> Unit,
) {
    val options = listOf(
        null to R.string.feed_filter_all,
        RecordingType.CALL to R.string.feed_filter_calls,
        RecordingType.MEETING to R.string.feed_filter_meetings,
        RecordingType.MEMO to R.string.feed_filter_memos,
    )
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (type, label) ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { Text(stringResource(label)) },
            )
        }
    }
}

/**
 * Une ligne du fil : nom ou numéro, sens de l'appel, date, durée et résumé en
 * une ligne. Pour TalkBack, la ligne est lue d'un bloc avec une description
 * complète.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordingRow(
    item: RecordingListItem,
    onOpen: () -> Unit,
) {
    val headline = item.headline ?: stringResource(
        if (item.type == RecordingType.CALL) R.string.feed_unknown_number else R.string.feed_untitled,
    )
    val date = DATE_FORMATTER.format(item.startedAt.atZone(ZoneId.systemDefault()))
    val duration = formatDuration(item.duration)
    val kind = stringResource(kindLabel(item))
    val summary = when (item.stage) {
        ProcessingStage.DONE -> item.oneLineSummary
        ProcessingStage.FAILED -> stringResource(R.string.feed_processing_failed)
        ProcessingStage.RECORDING -> stringResource(R.string.feed_recording_in_progress)
        else -> stringResource(R.string.feed_processing)
    }
    val todos = if (item.openTodoCount > 0) {
        pluralStringResource(R.plurals.feed_open_todos, item.openTodoCount, item.openTodoCount)
    } else {
        null
    }
    val description = listOfNotNull(kind, headline, date, duration, summary, todos).joinToString(", ")

    ListItem(
        modifier = Modifier
            .clickable(onClick = onOpen)
            .clearAndSetSemantics {
                contentDescription = description
                role = Role.Button
                onClick { onOpen(); true }
            },
        leadingContent = { Icon(kindIcon(item), contentDescription = null) },
        headlineContent = {
            Text(headline, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        overlineContent = { Text("$kind · $date · $duration") },
        supportingContent = if (summary != null) {
            { Text(summary, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        } else {
            null
        },
        trailingContent = if (item.openTodoCount > 0) {
            {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.TaskAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Badge { Text(item.openTodoCount.toString()) }
                }
            }
        } else {
            null
        },
    )
}

private fun kindIcon(item: RecordingListItem): ImageVector = when (item.type) {
    RecordingType.CALL -> when (item.direction) {
        CallDirection.INCOMING -> Icons.Filled.CallReceived
        CallDirection.OUTGOING -> Icons.Filled.CallMade
        else -> Icons.Filled.Call
    }
    RecordingType.MEETING -> Icons.Filled.Groups
    RecordingType.MEMO -> Icons.Filled.Mic
}

private fun kindLabel(item: RecordingListItem): Int = when (item.type) {
    RecordingType.CALL -> when (item.direction) {
        CallDirection.INCOMING -> R.string.feed_kind_incoming_call
        CallDirection.OUTGOING -> R.string.feed_kind_outgoing_call
        else -> R.string.feed_kind_call
    }
    RecordingType.MEETING -> R.string.feed_kind_meeting
    RecordingType.MEMO -> R.string.feed_kind_memo
}

/** Durée au format « 4:05 » ou « 1:04:05 ». */
internal fun formatDuration(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutesPart()
    val seconds = duration.toSecondsPart()
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}

/** Date et heure courtes, dans la langue du téléphone. */
private val DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
