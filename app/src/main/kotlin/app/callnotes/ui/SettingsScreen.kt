package app.callnotes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.callnotes.BuildConfig
import app.callnotes.R

/**
 * Réglages (version minimale de l'étape 1) : affiche la variante installée.
 * Dossier surveillé, moteur IA, clé API, synchronisation, biométrie et RGPD
 * s'ajouteront ici au fil des étapes.
 */
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ListItem(
            leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) },
            headlineContent = { Text(stringResource(R.string.settings_version)) },
            supportingContent = { Text("${BuildConfig.VERSION_NAME} (${BuildConfig.FLAVOR})") },
        )
        ListItem(
            leadingContent = { Icon(Icons.Outlined.PhoneInTalk, contentDescription = null) },
            headlineContent = { Text(stringResource(R.string.settings_call_log)) },
            supportingContent = {
                Text(
                    stringResource(
                        if (BuildConfig.CALL_LOG_ENABLED) R.string.settings_call_log_on else R.string.settings_call_log_off,
                    ),
                )
            },
        )
        ListItem(
            leadingContent = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            headlineContent = { Text(stringResource(R.string.settings_privacy)) },
            supportingContent = { Text(stringResource(R.string.settings_privacy_summary)) },
        )
    }
}
