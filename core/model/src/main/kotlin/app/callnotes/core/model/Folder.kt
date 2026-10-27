package app.callnotes.core.model

import java.time.Instant

/** Statut d'un dossier : en cours, en attente, terminé. */
enum class FolderStatus { ACTIVE, ON_HOLD, DONE }

/**
 * Dossier (projet, client, chantier) qui regroupe appels, réunions et mémos.
 */
data class Folder(
    val id: String,
    val name: String,
    val description: String?,
    val status: FolderStatus,
    /**
     * Mots-clés et alias (« chantier Dupont », « rue des Lilas ») qui aident
     * l'IA à reconnaître le dossier dans une conversation.
     */
    val keywords: List<String>,
    /** Résumé IA « Où en est ce dossier ? », régénéré à chaque nouvel échange. */
    val statusSummary: String?,
    val statusSummaryUpdatedAt: Instant?,
    /** Couleur ARGB choisie par l'utilisateur (facultative). */
    val color: Int?,
    /** Après une fusion : dossier qui a absorbé celui-ci. */
    val mergedIntoId: String?,
)
