package app.callnotes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.callnotes.core.model.KeyFactCategory
import app.callnotes.core.model.Origin
import app.callnotes.core.model.SuggestionKind
import app.callnotes.core.model.SuggestionStatus

/**
 * Table `todos`. Une to-do issue d'un enregistrement est supprimée avec lui ;
 * une to-do créée à la main n'a pas d'enregistrement.
 */
@Entity(
    tableName = "todos",
    foreignKeys = [
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recording_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("recording_id"),
        Index("folder_id"),
        Index("contact_id"),
        Index("is_done", "due_date"),
    ],
)
data class TodoEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "recording_id") val recordingId: String?,
    @ColumnInfo(name = "folder_id") val folderId: String?,
    @ColumnInfo(name = "contact_id") val contactId: String?,
    val title: String,
    val assignee: String?,
    @ColumnInfo(name = "assignee_is_me") val assigneeIsMe: Boolean,
    /** Date ISO-8601 (« 2026-11-03 ») : triable telle quelle. */
    @ColumnInfo(name = "due_date") val dueDate: String?,
    /** Heure ISO-8601 (« 14:30 »). */
    @ColumnInfo(name = "due_time") val dueTime: String?,
    @ColumnInfo(name = "due_text") val dueText: String?,
    @ColumnInfo(name = "is_done") val isDone: Boolean,
    @ColumnInfo(name = "done_at") val doneAt: Long?,
    @ColumnInfo(name = "source_offset_ms") val sourceOffsetMs: Long?,
    val origin: Origin,
    @ColumnInfo(name = "calendar_event_uri") val calendarEventUri: String?,
    @Embedded val sync: SyncMetadata,
)

/**
 * Table `key_facts` : chiffres, prix, quantités, dates, adresses, engagements.
 * Rattachés au contact et au dossier pour l'historique des prix / délais.
 */
@Entity(
    tableName = "key_facts",
    foreignKeys = [
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recording_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("recording_id"),
        Index("contact_id", "category"),
        Index("folder_id"),
    ],
)
data class KeyFactEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "recording_id") val recordingId: String,
    @ColumnInfo(name = "contact_id") val contactId: String?,
    @ColumnInfo(name = "folder_id") val folderId: String?,
    val category: KeyFactCategory,
    val label: String,
    val value: String,
    val amount: Double?,
    val unit: String?,
    val currency: String?,
    @ColumnInfo(name = "mentioned_date") val mentionedDate: String?,
    @ColumnInfo(name = "source_offset_ms") val sourceOffsetMs: Long?,
    val origin: Origin,
    @Embedded val sync: SyncMetadata,
)

/** Table `tags`. Le nom normalisé (minuscules, sans accents) est unique. */
@Entity(
    tableName = "tags",
    indices = [Index(value = ["normalized_name"], unique = true)],
)
data class TagEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "normalized_name") val normalizedName: String,
    @Embedded val sync: SyncMetadata,
)

/** Table de liaison `recording_tags`. */
@Entity(
    tableName = "recording_tags",
    primaryKeys = ["recording_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recording_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("tag_id")],
)
data class RecordingTagEntity(
    @ColumnInfo(name = "recording_id") val recordingId: String,
    @ColumnInfo(name = "tag_id") val tagId: String,
    val origin: Origin,
    @Embedded val sync: SyncMetadata,
)

/**
 * Table `suggestions` : propositions de l'IA en attente de validation
 * (rattachement à un dossier, mise à jour de fiche contact…).
 */
@Entity(
    tableName = "suggestions",
    foreignKeys = [
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recording_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("recording_id"), Index("status")],
)
data class SuggestionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "recording_id") val recordingId: String,
    val kind: SuggestionKind,
    /** [app.callnotes.core.model.SuggestionPayload] sérialisé en JSON. */
    @ColumnInfo(name = "payload_json") val payloadJson: String,
    val status: SuggestionStatus,
    @ColumnInfo(name = "resolved_at") val resolvedAt: Long?,
    @Embedded val sync: SyncMetadata,
)
