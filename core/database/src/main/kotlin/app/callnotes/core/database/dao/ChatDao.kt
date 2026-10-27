package app.callnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.callnotes.core.database.entity.ChatMessageEntity
import app.callnotes.core.database.entity.ChatThreadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Query("SELECT * FROM chat_threads WHERE deleted_at IS NULL ORDER BY updated_at DESC")
    fun observeThreads(): Flow<List<ChatThreadEntity>>

    @Query(
        """
        SELECT * FROM chat_messages
        WHERE thread_id = :threadId AND deleted_at IS NULL
        ORDER BY created_at
        """,
    )
    fun observeMessages(threadId: String): Flow<List<ChatMessageEntity>>

    @Upsert
    suspend fun upsertThread(thread: ChatThreadEntity)

    @Upsert
    suspend fun upsertMessages(messages: List<ChatMessageEntity>)
}
