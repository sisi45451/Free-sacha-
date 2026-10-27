package app.callnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.callnotes.core.database.entity.FolderEntity
import app.callnotes.core.database.entity.KeyFactEntity
import app.callnotes.core.database.entity.RecordingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    /** Dossiers non fusionnés, les plus récemment modifiés d'abord. */
    @Query(
        """
        SELECT * FROM folders
        WHERE deleted_at IS NULL AND merged_into_id IS NULL
        ORDER BY updated_at DESC
        """,
    )
    fun observeAll(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id = :id AND deleted_at IS NULL")
    fun observeById(id: String): Flow<FolderEntity?>

    /** Liste compacte envoyée à l'IA pour qu'elle propose un dossier existant. */
    @Query(
        """
        SELECT * FROM folders
        WHERE deleted_at IS NULL AND merged_into_id IS NULL AND status != 'DONE'
        """,
    )
    suspend fun getOpenFolders(): List<FolderEntity>

    @Upsert
    suspend fun upsert(folder: FolderEntity)

    /** Chronologie du dossier : appels, réunions et mémos. */
    @Query(
        """
        SELECT * FROM recordings
        WHERE folder_id = :folderId AND deleted_at IS NULL
        ORDER BY started_at DESC
        """,
    )
    fun observeTimeline(folderId: String): Flow<List<RecordingEntity>>

    @Query(
        """
        SELECT k.* FROM key_facts k
        JOIN recordings r ON r.id = k.recording_id
        WHERE (k.folder_id = :folderId OR r.folder_id = :folderId)
          AND k.deleted_at IS NULL AND r.deleted_at IS NULL
        ORDER BY r.started_at DESC
        """,
    )
    fun observeKeyFacts(folderId: String): Flow<List<KeyFactEntity>>

    /** Fusion : tout ce qui pointait vers [sourceId] pointe désormais vers [targetId]. */
    @Query(
        """
        UPDATE recordings SET folder_id = :targetId, updated_at = :nowMs, is_dirty = 1
        WHERE folder_id = :sourceId
        """,
    )
    suspend fun moveRecordings(sourceId: String, targetId: String, nowMs: Long)

    @Query(
        """
        UPDATE todos SET folder_id = :targetId, updated_at = :nowMs, is_dirty = 1
        WHERE folder_id = :sourceId
        """,
    )
    suspend fun moveTodos(sourceId: String, targetId: String, nowMs: Long)

    @Query(
        """
        UPDATE key_facts SET folder_id = :targetId, updated_at = :nowMs, is_dirty = 1
        WHERE folder_id = :sourceId
        """,
    )
    suspend fun moveKeyFacts(sourceId: String, targetId: String, nowMs: Long)
}
