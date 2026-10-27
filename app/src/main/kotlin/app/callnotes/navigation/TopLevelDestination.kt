package app.callnotes.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import app.callnotes.R

/**
 * Les cinq onglets de la barre de navigation, dans l'ordre :
 * Fil / Dossiers / Contacts / Demander / To-dos.
 */
enum class TopLevelDestination(
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val labelRes: Int,
) {
    FEED(Route.Feed, Icons.Filled.History, Icons.Outlined.History, R.string.tab_feed),
    FOLDERS(Route.Folders, Icons.Filled.Folder, Icons.Outlined.Folder, R.string.tab_folders),
    CONTACTS(Route.Contacts, Icons.Filled.Contacts, Icons.Outlined.Contacts, R.string.tab_contacts),
    ASK(Route.Ask, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, R.string.tab_ask),
    TODOS(Route.Todos, Icons.Filled.TaskAlt, Icons.Outlined.TaskAlt, R.string.tab_todos),
}
