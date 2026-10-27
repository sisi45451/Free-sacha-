package app.callnotes.core.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import app.callnotes.core.database.entity.RecordingEntity
import app.callnotes.core.database.entity.RecordingParticipantEntity
import app.callnotes.core.model.CallDirection
import app.callnotes.core.model.ProcessingStage
import app.callnotes.core.model.RecordingType
import kotlinx.coroutines.flow.Flow

/** Une ligne du fil : enregistrement + contact principal + résumé + to-dos ouvertes. */
data class FeedRow(
    val id: String,
    val type: RecordingType,
    val title: String?,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "duration_ms") val durationMs: Long,
    val direction: CallDirection?,
    @ColumnInfo(name = "counterpart_number") val counterpartNumber: String?,
    @ColumnInfo(name = "counterpart_raw") val counterpartRaw: String?,
    @ColumnInfo(name = "processing_stage") val processingStage: ProcessingStage,
    @ColumnInfo(name = "contact_id") val contactId: String?,
    @ColumnInfo(name = "contact_name") val contactName: String?,
    @ColumnInfo(name = "contact_photo_uri") val contactPhotoUri: String?,
    @ColumnInfo(name = "one_line_summary") val oneLineSummary: String?,
    @ColumnInfo(name = "open_todo_count") val openTodoCount: Int,
)

@Dao
abstract class RecordingDao {

    /**
     * Fil chronologique. Les lignes supprimées (suppression douce) sont
     * exclues partout.
     */
    @Query(
        """
        SELECT r.id, r.type, r.title, r.started_at, r.duration_ms, r.direction,
               r.counterpart_number, r.counterpart_raw, r.processing_stage,
               c.id AS contact_id, c.display_name AS contact_name,
               c.photo_uri AS contact_photo_uri,
               a.one_line_summary,
               (SELECT COUNT(*) FROM todos t
                 WHERE t.recording_id = r.id AND t.is_done = 0
                   AND t.deleted_at IS NULL) AS open_todo_count
        FROM recordings r
        LEFT JOIN contacts c ON c.id = r.primary_contact_id AND c.deleted_at IS NULL
        LEFT JOIN analyses a ON a.recording_id = r.id AND a.deleted_at IS NULL
        WHERE r.deleted_at IS NULL
          AND (:type IS NULL OR r.type = :type)
        ORDER BY r.started_at DESC
        LIMIT :limit
        """,
    )
    abstract fun observeFeed(type: RecordingType?, limit: Int): Flow<List<FeedRow>>

    @Query("SELECT * FROM recordings WHERE id = :id AND deleted_at IS NULL")
    abstract fun observeById(id: String): Flow<RecordingEntity?>

    @Query("SELECT * FROM recordings WHERE id = :id")
    abstract suspend fun getById(id: String): RecordingEntity?

    /** Enregistrements dont le traitement n'est pas terminé (reprise du pipeline). */
    @Query(
        """
        SELECT * FROM recordings
        WHERE deleted_at IS NULL
          AND processing_stage NOT IN ('RECORDING', 'DONE', 'FAILED')
        ORDER BY started_at
        """,
    )
    abstract suspend fun getPendingProcessing(): List<RecordingEntity>

    /** Mémos rattachés à un enregistrement (« Ajouter un mémo » après un appel). */
    @Query(
        """
        SELECT * FROM recordings
        WHERE parent_recording_id = :parentId AND deleted_at IS NULL
        ORDER BY started_at
        """,
    )
    abstract fun observeChildren(parentId: String): Flow<List<RecordingEntity>>

    @Upsert
    abstract suspend fun upsert(recording: RecordingEntity)

    @Upsert
    abstract suspend fun upsertParticipants(participants: List<RecordingParticipantEntity>)

    @Query(
        """
        SELECT * FROM recording_participants
        WHERE recording_id = :recordingId AND deleted_at IS NULL
        """,
    )
    abstract suspend fun getParticipants(recordingId: String): List<RecordingParticipantEntity>

    // --- Suppression douce d'un enregistrement et de tout ce qui en dépend ---

    @Query(SOFT_DELETE_RECORDING)
    abstract suspend fun softDeleteRecordingRow(id: String, nowMs: Long)

    @Query(SOFT_DELETE_PARTICIPANTS)
    abstract suspend fun softDeleteParticipants(id: String, nowMs: Long)

    @Query(SOFT_DELETE_SPEAKERS)
    abstract suspend fun softDeleteSpeakers(id: String, nowMs: Long)

    @Query(SOFT_DELETE_SEGMENTS)
    abstract suspend fun softDeleteSegments(id: String, nowMs: Long)

    @Query(SOFT_DELETE_ANALYSIS)
    abstract suspend fun softDeleteAnalysis(id: String, nowMs: Long)

    @Query(SOFT_DELETE_TODOS)
    abstract suspend fun softDeleteTodos(id: String, nowMs: Long)

    @Query(SOFT_DELETE_KEY_FACTS)
    abstract suspend fun softDeleteKeyFacts(id: String, nowMs: Long)

    @Query(SOFT_DELETE_TAGS)
    abstract suspend fun softDeleteTags(id: String, nowMs: Long)

    @Query(SOFT_DELETE_SUGGESTIONS)
    abstract suspend fun softDeleteSuggestions(id: String, nowMs: Long)

    /** Les passages sont des données dérivées locales : suppression réelle. */
    @Query("DELETE FROM passages WHERE recording_id = :id")
    abstract suspend fun deletePassages(id: String)

    /**
     * Marque supprimés l'enregistrement et toutes ses données, en une seule
     * transaction. La purge physique (et la suppression du fichier audio) a
     * lieu une fois la suppression synchronisée.
     */
    @Transaction
    open suspend fun softDeleteCascade(id: String, nowMs: Long) {
        softDeleteParticipants(id, nowMs)
        softDeleteSpeakers(id, nowMs)
        softDeleteSegments(id, nowMs)
        softDeleteAnalysis(id, nowMs)
        softDeleteTodos(id, nowMs)
        softDeleteKeyFacts(id, nowMs)
        softDeleteTags(id, nowMs)
        softDeleteSuggestions(id, nowMs)
        deletePassages(id)
        softDeleteRecordingRow(id, nowMs)
    }

    private companion object {
        const val SET_DELETED = "SET deleted_at = :nowMs, updated_at = :nowMs, is_dirty = 1"
        const val SOFT_DELETE_RECORDING = "UPDATE recordings $SET_DELETED WHERE id = :id"
        const val SOFT_DELETE_PARTICIPANTS =
            "UPDATE recording_participants $SET_DELETED WHERE recording_id = :id"
        const val SOFT_DELETE_SPEAKERS = "UPDATE speakers $SET_DELETED WHERE recording_id = :id"
        const val SOFT_DELETE_SEGMENTS =
            "UPDATE transcript_segments $SET_DELETED WHERE recording_id = :id"
        const val SOFT_DELETE_ANALYSIS = "UPDATE analyses $SET_DELETED WHERE recording_id = :id"
        const val SOFT_DELETE_TODOS = "UPDATE todos $SET_DELETED WHERE recording_id = :id"
        const val SOFT_DELETE_KEY_FACTS = "UPDATE key_facts $SET_DELETED WHERE recording_id = :id"
        const val SOFT_DELETE_TAGS = "UPDATE recording_tags $SET_DELETED WHERE recording_id = :id"
        const val SOFT_DELETE_SUGGESTIONS =
            "UPDATE suggestions $SET_DELETED WHERE recording_id = :id"
    }
}
