package app.callnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.callnotes.core.database.entity.AnalysisEntity
import app.callnotes.core.database.entity.SpeakerEntity
import app.callnotes.core.database.entity.TranscriptSegmentEntity
import kotlinx.coroutines.flow.Flow

/** Transcription (segments, voix) et analyse d'un enregistrement. */
@Dao
interface TranscriptDao {

    @Query(
        """
        SELECT * FROM transcript_segments
        WHERE recording_id = :recordingId AND deleted_at IS NULL
        ORDER BY start_ms
        """,
    )
    fun observeSegments(recordingId: String): Flow<List<TranscriptSegmentEntity>>

    @Upsert
    suspend fun upsertSegments(segments: List<TranscriptSegmentEntity>)

    @Query("SELECT * FROM speakers WHERE recording_id = :recordingId AND deleted_at IS NULL")
    fun observeSpeakers(recordingId: String): Flow<List<SpeakerEntity>>

    @Upsert
    suspend fun upsertSpeakers(speakers: List<SpeakerEntity>)

    /** Renommage manuel d'une voix (« Intervenant 2 » → « Sophie »). */
    @Query(
        """
        UPDATE speakers
        SET display_name = :displayName, contact_id = :contactId,
            updated_at = :nowMs, is_dirty = 1
        WHERE id = :speakerId
        """,
    )
    suspend fun renameSpeaker(speakerId: String, displayName: String?, contactId: String?, nowMs: Long)

    @Query("SELECT * FROM analyses WHERE recording_id = :recordingId AND deleted_at IS NULL")
    fun observeAnalysis(recordingId: String): Flow<AnalysisEntity?>

    @Upsert
    suspend fun upsertAnalysis(analysis: AnalysisEntity)
}
