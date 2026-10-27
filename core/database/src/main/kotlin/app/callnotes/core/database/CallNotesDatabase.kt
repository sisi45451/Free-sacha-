package app.callnotes.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.callnotes.core.database.dao.AnalysisOutputDao
import app.callnotes.core.database.dao.ChatDao
import app.callnotes.core.database.dao.ContactDao
import app.callnotes.core.database.dao.FolderDao
import app.callnotes.core.database.dao.RecordingDao
import app.callnotes.core.database.dao.SearchDao
import app.callnotes.core.database.dao.SourceFileDao
import app.callnotes.core.database.dao.TodoDao
import app.callnotes.core.database.dao.TranscriptDao
import app.callnotes.core.database.entity.AnalysisEntity
import app.callnotes.core.database.entity.ChatMessageEntity
import app.callnotes.core.database.entity.ChatThreadEntity
import app.callnotes.core.database.entity.ContactEntity
import app.callnotes.core.database.entity.ContactFactEntity
import app.callnotes.core.database.entity.ContactPhoneEntity
import app.callnotes.core.database.entity.FolderEntity
import app.callnotes.core.database.entity.KeyFactEntity
import app.callnotes.core.database.entity.PassageEmbeddingEntity
import app.callnotes.core.database.entity.PassageEntity
import app.callnotes.core.database.entity.PassageFtsEntity
import app.callnotes.core.database.entity.RecordingEntity
import app.callnotes.core.database.entity.RecordingParticipantEntity
import app.callnotes.core.database.entity.RecordingTagEntity
import app.callnotes.core.database.entity.SourceFileEntity
import app.callnotes.core.database.entity.SpeakerEntity
import app.callnotes.core.database.entity.SuggestionEntity
import app.callnotes.core.database.entity.TagEntity
import app.callnotes.core.database.entity.TodoEntity
import app.callnotes.core.database.entity.TranscriptSegmentEntity

/**
 * Base de données CallNotes (chiffrée par SQLCipher, voir DatabaseModule).
 *
 * Règle d'évolution : toute modification du schéma incrémente [version] et
 * s'accompagne d'une migration (automatique via `autoMigrations` quand c'est
 * possible). Les schémas exportés sont dans `core/database/schemas/`.
 */
@Database(
    version = 1,
    exportSchema = true,
    entities = [
        // Synchronisées (étape 6)
        RecordingEntity::class,
        RecordingParticipantEntity::class,
        SpeakerEntity::class,
        TranscriptSegmentEntity::class,
        AnalysisEntity::class,
        TodoEntity::class,
        KeyFactEntity::class,
        TagEntity::class,
        RecordingTagEntity::class,
        SuggestionEntity::class,
        FolderEntity::class,
        ContactEntity::class,
        ContactPhoneEntity::class,
        ContactFactEntity::class,
        ChatThreadEntity::class,
        ChatMessageEntity::class,
        // Locales uniquement
        PassageEntity::class,
        PassageFtsEntity::class,
        PassageEmbeddingEntity::class,
        SourceFileEntity::class,
    ],
)
abstract class CallNotesDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
    abstract fun transcriptDao(): TranscriptDao
    abstract fun todoDao(): TodoDao
    abstract fun contactDao(): ContactDao
    abstract fun folderDao(): FolderDao
    abstract fun analysisOutputDao(): AnalysisOutputDao
    abstract fun chatDao(): ChatDao
    abstract fun searchDao(): SearchDao
    abstract fun sourceFileDao(): SourceFileDao

    companion object {
        const val NAME = "callnotes.db"
    }
}
