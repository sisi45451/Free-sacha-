package app.callnotes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.callnotes.core.model.ChatRole

/** Table `chat_threads` : conversations avec l'assistant. */
@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val title: String,
    @Embedded val sync: SyncMetadata,
)

/** Table `chat_messages`. */
@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatThreadEntity::class,
            parentColumns = ["id"],
            childColumns = ["thread_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("thread_id", "created_at")],
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "thread_id") val threadId: String,
    val role: ChatRole,
    val text: String,
    /** Liste JSON de [app.callnotes.core.model.Citation]. */
    @ColumnInfo(name = "citations_json") val citationsJson: String,
    @Embedded val sync: SyncMetadata,
)
