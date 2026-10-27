package app.callnotes.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.callnotes.R
import app.callnotes.feature.feed.FeedRoute
import app.callnotes.ui.PlaceholderScreen
import app.callnotes.ui.SettingsScreen

/**
 * Graphe de navigation. Les onglets encore vides affichent un écran
 * d'attente qui indique l'étape du projet où ils arrivent.
 */
@Composable
fun CallNotesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Feed,
        modifier = modifier,
    ) {
        composable<Route.Feed> {
            FeedRoute(onOpenRecording = { id -> navController.navigate(Route.Recording(id)) })
        }
        composable<Route.Folders> {
            PlaceholderScreen(Icons.Outlined.Folder, R.string.tab_folders, R.string.placeholder_step_4)
        }
        composable<Route.Contacts> {
            PlaceholderScreen(Icons.Outlined.Contacts, R.string.tab_contacts, R.string.placeholder_step_4)
        }
        composable<Route.Ask> {
            PlaceholderScreen(Icons.Outlined.AutoAwesome, R.string.tab_ask, R.string.placeholder_step_3)
        }
        composable<Route.Todos> {
            PlaceholderScreen(Icons.Outlined.TaskAlt, R.string.tab_todos, R.string.placeholder_step_3)
        }
        composable<Route.Recording> { entry ->
            val route = entry.toRoute<Route.Recording>()
            PlaceholderScreen(
                Icons.Outlined.GraphicEq,
                R.string.recording_title,
                R.string.placeholder_step_2,
                detail = route.id,
            )
        }
        composable<Route.Settings> {
            SettingsScreen()
        }
    }
}

/**
 * Navigation vers un onglet : on revient à la racine de l'onglet en gardant
 * l'état des autres (position de défilement, filtres…).
 */
fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
