package app.callnotes.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.callnotes.R
import app.callnotes.navigation.CallNotesNavHost
import app.callnotes.navigation.Route
import app.callnotes.navigation.TopLevelDestination
import app.callnotes.navigation.navigateToTopLevel
import kotlinx.coroutines.launch

/**
 * Coquille de l'app :
 * - barre de navigation à 5 onglets (en bas sur téléphone, sur le côté sur
 *   tablette ou téléphone pliable déplié, grâce à NavigationSuiteScaffold) ;
 * - barre du haut avec les Réglages à droite ;
 * - bouton « Enregistrer » (réunion ou mémo) toujours accessible : bouton
 *   flottant, ou icône micro dans la barre du haut sur l'onglet « Demander »
 *   où le bas de l'écran est occupé par la saisie.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallNotesApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentTopLevel = TopLevelDestination.entries.firstOrNull { destination ->
        currentDestination.isInHierarchy(destination.route)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showRecordSheet by rememberSaveable { mutableStateOf(false) }
    val comingSoon = stringResource(R.string.record_coming_soon)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                val selected = destination == currentTopLevel
                item(
                    selected = selected,
                    onClick = { navController.navigateToTopLevel(destination) },
                    icon = {
                        Icon(
                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(destination.labelRes)) },
                )
            }
        },
    ) {
        Scaffold(
            // Les marges système du bas sont déjà gérées par la barre de navigation.
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(titleFor(currentDestination, currentTopLevel))) },
                    navigationIcon = {
                        if (currentTopLevel == null && navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.action_back),
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentTopLevel == TopLevelDestination.ASK) {
                            IconButton(onClick = { showRecordSheet = true }) {
                                Icon(Icons.Filled.Mic, contentDescription = stringResource(R.string.action_record))
                            }
                        }
                        IconButton(onClick = { navController.navigate(Route.Settings) }) {
                            Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings_title))
                        }
                    },
                )
            },
            floatingActionButton = {
                if (currentTopLevel != null && currentTopLevel != TopLevelDestination.ASK) {
                    ExtendedFloatingActionButton(
                        onClick = { showRecordSheet = true },
                        icon = { Icon(Icons.Filled.Mic, contentDescription = null) },
                        text = { Text(stringResource(R.string.action_record)) },
                    )
                }
            },
        ) { innerPadding ->
            CallNotesNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (showRecordSheet) {
        RecordChooserSheet(
            onDismiss = { showRecordSheet = false },
            // Réunions et mémos arrivent à l'étape 5.
            onMeeting = {
                showRecordSheet = false
                scope.launch { snackbarHostState.showSnackbar(comingSoon) }
            },
            onMemo = {
                showRecordSheet = false
                scope.launch { snackbarHostState.showSnackbar(comingSoon) }
            },
        )
    }
}

private fun NavDestination?.isInHierarchy(route: Route): Boolean =
    this?.hierarchy?.any { it.hasRoute(route::class) } == true

/** Titre de la barre du haut selon l'écran affiché. */
private fun titleFor(destination: NavDestination?, topLevel: TopLevelDestination?): Int = when {
    topLevel != null -> topLevel.labelRes
    destination?.hasRoute(Route.Settings::class) == true -> R.string.settings_title
    destination?.hasRoute(Route.Recording::class) == true -> R.string.recording_title
    else -> R.string.app_name
}
