package app.callnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.callnotes.core.designsystem.theme.CallNotesTheme
import app.callnotes.ui.CallNotesApp
import dagger.hilt.android.AndroidEntryPoint

/** Activité unique : toute l'interface est en Jetpack Compose. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Affichage bord à bord (obligatoire pour les apps ciblant Android 15+).
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CallNotesTheme {
                CallNotesApp()
            }
        }
    }
}
