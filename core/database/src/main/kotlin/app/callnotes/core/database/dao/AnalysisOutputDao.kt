package app.callnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.callnotes.core.database.entity.KeyFactEntity
import app.callnotes.core.database.entity.RecordingTagEntity
import app.callnotes.core.database.entity.SuggestionEntity
import app.callnotes.core.database.entity.TagEntity
import app.callnotes.core.model.SuggestionStatus
import kotlinx.coroutines.flow.Flow

/** Sorties de l'analyse IA autres que les to-dos : points clés, tags, propositions. */
@Dao
interface AnalysisOutputDao {

    @Query(
        """
        SELECT * FROM key_facts
        WHERE recording_id = :recordingId AND deleted_at IS NULL
        ORDER BY source_offset_ms
        """,
    )
    fun observeKeyFacts(recordingId: String): Flow<List<KeyFactEntity>>

    @Upsert
    suspend fun upsertKeyFacts(facts: List<KeyFactEntity>)

    @Query("SELECT * FROM tags WHERE normalized_name = :normalizedName LIMIT 1")
    suspend fun findTag(normalizedName: String): TagEntity?

    @Upsert
    suspend fun upsertTag(tag: TagEntity)

    @Upsert
    suspend fun upsertRecordingTags(links: List<RecordingTagEntity>)

    @Query(
        """
        SELECT t.* FROM tags t
        JOIN recording_tags rt ON rt.tag_id = t.id
        WHERE rt.recording_id = :recordingId
          AND rt.deleted_at IS NULL AND t.deleted_at IS NULL
        ORDER BY t.name
        """,
    )
    fun observeTagsForRecording(recordingId: String): Flow<List<TagEntity>>

    /** Propositions en attente : affichées comme cartes « Confirmer / Ignorer ». */
    @Query(
        """
        SELECT * FROM suggestions
        WHERE status = 'PENDING' AND deleted_at IS NULL
        ORDER BY created_at DESC
        """,
    )
    fun observePendingSuggestions(): Flow<List<SuggestionEntity>>

    @Upsert
    suspend fun upsertSuggestions(suggestions: List<SuggestionEntity>)

    @Query(
        """
        UPDATE suggestions
        SET status = :status, resolved_at = :nowMs, updated_at = :nowMs, is_dirty = 1
        WHERE id = :id
        """,
    )
    suspend fun resolveSuggestion(id: String, status: SuggestionStatus, nowMs: Long)
}
