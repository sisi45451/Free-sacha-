package app.callnotes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.callnotes.core.model.AudioStorage
import app.callnotes.core.model.CallDirection
import app.callnotes.core.model.IdentificationSource
import app.callnotes.core.model.ParticipantRole
import app.callnotes.core.model.ProcessingStage
import app.callnotes.core.model.RecordingType

/**
 * Table `recordings` : un appel, une réunion ou un mémo.
 * Les énumérations sont stockées sous forme de texte (nom de la constante).
 */
@Entity(
    tableName = "recordings",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["primary_contact_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_recording_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("started_at"),
        Index("type"),
        Index("folder_id"),
        Index("primary_contact_id"),
        Index("parent_recording_id"),
        Index("counterpart_number"),
    ],
)
data class RecordingEntity(
    @PrimaryKey val id: String,
    val type: RecordingType,
    val title: String?,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "duration_ms") val durationMs: Long,
    val direction: CallDirection?,
    /** Numéro normalisé (E.164) de l'interlocuteur d'un appel. */
    @ColumnInfo(name = "counterpart_number") val counterpartNumber: String?,
    @ColumnInfo(name = "counterpart_raw") val counterpartRaw: String?,
    @ColumnInfo(name = "identification_source") val identificationSource: IdentificationSource,
    @ColumnInfo(name = "primary_contact_id") val primaryContactId: String?,
    val location: String?,
    @ColumnInfo(name = "folder_id") val folderId: String?,
    @ColumnInfo(name = "parent_recording_id") val parentRecordingId: String?,
    @ColumnInfo(name = "audio_uri") val audioUri: String,
    @ColumnInfo(name = "audio_storage") val audioStorage: AudioStorage,
    @ColumnInfo(name = "audio_mime_type") val audioMimeType: String?,
    @ColumnInfo(name = "audio_size_bytes") val audioSizeBytes: Long?,
    @ColumnInfo(name = "source_file_name") val sourceFileName: String?,
    /** Chemin du fichier dans le stockage Supabase, une fois envoyé (étape 6). */
    @ColumnInfo(name = "remote_audio_path") val remoteAudioPath: String?,
    @ColumnInfo(name = "processing_stage") val processingStage: ProcessingStage,
    @ColumnInfo(name = "processing_error") val processingError: String?,
    /** Avancement de la transcription par morceaux (reprise après interruption). */
    @ColumnInfo(name = "transcribed_until_ms") val transcribedUntilMs: Long,
    val language: String?,
    @ColumnInfo(name = "transcription_engine") val transcriptionEngine: String?,
    @Embedded val sync: SyncMetadata,
)

/**
 * Table de liaison `recording_participants` : un enregistrement peut être lié
 * à un ou plusieurs contacts.
 */
@Entity(
    tableName = "recording_participants",
    primaryKeys = ["recording_id", "contact_id"],
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
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("contact_id")],
)
data class RecordingParticipantEntity(
    @ColumnInfo(name = "recording_id") val recordingId: String,
    @ColumnInfo(name = "contact_id") val contactId: String,
    val role: ParticipantRole,
    @Embedded val sync: SyncMetadata,
)

/**
 * Table `speakers` : les voix distinguées dans un enregistrement
 * (« Moi », « Interlocuteur », « Intervenant 1 »…), renommables.
 */
@Entity(
    tableName = "speakers",
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
    ],
    indices = [Index("recording_id"), Index("contact_id")],
)
data class SpeakerEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "recording_id") val recordingId: String,
    val label: String,
    @ColumnInfo(name = "display_name") val displayName: String?,
    @ColumnInfo(name = "contact_id") val contactId: String?,
    @ColumnInfo(name = "is_me") val isMe: Boolean,
    @Embedded val sync: SyncMetadata,
)

/** Table `transcript_segments` : la transcription horodatée, segment par segment. */
@Entity(
    tableName = "transcript_segments",
    foreignKeys = [
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recording_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SpeakerEntity::class,
            parentColumns = ["id"],
            childColumns = ["speaker_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("recording_id", "start_ms"), Index("speaker_id")],
)
data class TranscriptSegmentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "recording_id") val recordingId: String,
    val ordinal: Int,
    @ColumnInfo(name = "start_ms") val startMs: Long,
    @ColumnInfo(name = "end_ms") val endMs: Long,
    @ColumnInfo(name = "speaker_id") val speakerId: String?,
    val text: String,
    val confidence: Float?,
    @Embedded val sync: SyncMetadata,
)

/**
 * Table `analyses` (1 ligne par enregistrement) : la partie « texte » de
 * l'analyse IA. Le JSON brut validé est conservé pour la traçabilité.
 */
@Entity(
    tableName = "analyses",
    foreignKeys = [
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recording_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AnalysisEntity(
    @PrimaryKey @ColumnInfo(name = "recording_id") val recordingId: String,
    val summary: String,
    @ColumnInfo(name = "one_line_summary") val oneLineSummary: String,
    /** Liste JSON de chaînes. */
    @ColumnInfo(name = "open_questions_json") val openQuestionsJson: String,
    val engine: String,
    val model: String,
    @ColumnInfo(name = "schema_version") val schemaVersion: Int,
    @ColumnInfo(name = "raw_json") val rawJson: String,
    @Embedded val sync: SyncMetadata,
)
