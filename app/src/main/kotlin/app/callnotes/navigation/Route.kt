package app.callnotes.navigation

import kotlinx.serialization.Serializable

/**
 * Destinations de navigation, typées (Navigation Compose « type-safe ») :
 * les arguments sont des propriétés vérifiées à la compilation.
 */
sealed interface Route {
    // Onglets principaux
    @Serializable data object Feed : Route
    @Serializable data object Folders : Route
    @Serializable data object Contacts : Route
    @Serializable data object Ask : Route
    @Serializable data object Todos : Route

    // Écrans secondaires
    @Serializable data object Settings : Route

    /**
     * Fiche d'un enregistrement, ouverte éventuellement à un instant précis
     * (tap sur une source citée par l'assistant).
     */
    @Serializable data class Recording(val id: String, val startMs: Long = 0) : Route
}
