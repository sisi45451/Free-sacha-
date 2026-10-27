package app.callnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.callnotes.core.database.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    /**
     * To-dos non faites, triées par échéance (celles sans échéance à la fin),
     * filtrables par dossier ou par contact (`null` = pas de filtre).
     */
    @Query(
        """
        SELECT * FROM todos
        WHERE is_done = 0 AND deleted_at IS NULL
          AND (:folderId IS NULL OR folder_id = :folderId)
          AND (:contactId IS NULL OR contact_id = :contactId)
        ORDER BY due_date IS NULL, due_date, due_time IS NULL, due_time, created_at
        """,
    )
    fun observeOpen(folderId: String?, contactId: String?): Flow<List<TodoEntity>>

    @Query(
        """
        SELECT * FROM todos
        WHERE recording_id = :recordingId AND deleted_at IS NULL
        ORDER BY created_at
        """,
    )
    fun observeForRecording(recordingId: String): Flow<List<TodoEntity>>

    @Upsert
    suspend fun upsert(todos: List<TodoEntity>)

    /** Coche / décoche une to-do (aussi utilisé par le widget et Gemini). */
    @Query(
        """
        UPDATE todos
        SET is_done = :done, done_at = :doneAt, updated_at = :nowMs, is_dirty = 1
        WHERE id = :id
        """,
    )
    suspend fun setDone(id: String, done: Boolean, doneAt: Long?, nowMs: Long)
}
