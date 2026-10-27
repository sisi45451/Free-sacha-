package app.callnotes.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import app.callnotes.core.designsystem.component.EmptyState

/** Écran d'attente d'une fonctionnalité qui arrive à une étape ultérieure. */
@Composable
fun PlaceholderScreen(
    icon: ImageVector,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    detail: String? = null,
) {
    val message = stringResource(messageRes)
    EmptyState(
        icon = icon,
        title = stringResource(titleRes),
        message = if (detail != null) "$message\n$detail" else message,
    )
}
