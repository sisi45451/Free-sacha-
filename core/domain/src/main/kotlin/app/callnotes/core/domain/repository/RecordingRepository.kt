package app.callnotes.core.domain.repository

import app.callnotes.core.model.Recording
import app.callnotes.core.model.RecordingListItem
import app.callnotes.core.model.RecordingType
import kotlinx.coroutines.flow.Flow

/**
 * Filtre du fil chronologique. Les filtres par contact, dossier, tag,
 * période et to-dos non faites arrivent avec l'historique complet (étape 2).
 */
data class FeedFilter(
    /** `null` = tous les types. */
    val type: RecordingType? = null,
    val limit: Int = DEFAULT_LIMIT,
) {
    companion object {
        const val DEFAULT_LIMIT = 200
    }
}

/**
 * Accès aux enregistrements (appels, réunions, mémos).
 *
 * La base locale est la source de vérité : les écrans observent des [Flow]
 * qui se mettent à jour tout seuls quand le pipeline (ou la synchronisation)
 * écrit en base.
 */
interface RecordingRepository {

    /** Fil chronologique, du plus récent au plus ancien. */
    fun observeFeed(filter: FeedFilter = FeedFilter()): Flow<List<RecordingListItem>>

    /** Un enregistrement, ou `null` s'il n'existe pas (ou a été supprimé). */
    fun observeRecording(id: String): Flow<Recording?>

    /** Crée ou remplace un enregistrement. */
    suspend fun upsert(recording: Recording)

    /**
     * Suppression « douce » : la ligne est marquée supprimée pour que la
     * suppression se propage à la synchronisation, puis purgée plus tard.
     */
    suspend fun delete(id: String)
}
