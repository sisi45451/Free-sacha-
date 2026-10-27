package app.callnotes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application : point de départ de l'injection de dépendances Hilt.
 * Aux étapes suivantes, elle initialisera aussi WorkManager (surveillance du
 * dossier des enregistrements) et les canaux de notification.
 */
@HiltAndroidApp
class CallNotesApplication : Application()
