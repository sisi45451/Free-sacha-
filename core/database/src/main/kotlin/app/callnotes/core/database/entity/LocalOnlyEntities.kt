package app.callnotes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.callnotes.core.model.SourceFileState

// -----------------------------------------------------------------------------
// Tables locales, jamais synchronisées : ce sont des données dérivées
// (recalculables) ou propres au téléphone.
// -----------------------------------------------------------------------------

/**
 * Table `passages` : découpage des transcriptions pour la recherche et
 * l'assistant (quelques phrases, avec horodatage).
 *
 * La clé est un entier auto-incrémenté (alias du `rowid` SQLite) : la table
 * plein texte et la table des vecteurs s'y réfèrent, et un `rowid` explicite
 * reste stable après un VACUUM.
 */
@Entity(
    tableName = "passages",
    foreignKeys = [
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recording_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("recording_id")],
)
data class PassageEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "row_id") val rowId: Long = 0,
    @ColumnInfo(name = "recording_id") val recordingId: String,
    val ordinal: Int,
    val text: String,
    @ColumnInfo(name = "start_ms") val startMs: Long,
    @ColumnInfo(name = "end_ms") val endMs: Long,
)

/**
 * Index plein texte `passages_fts` (FTS4) adossé à `passages`.
 * `remove_diacritics=2` : « café » et « cafe » se trouvent mutuellement.
 * Room crée les déclencheurs qui le tiennent à jour.
 */
@Fts4(
    contentEntity = PassageEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=2"],
)
@Entity(tableName = "passages_fts")
data class PassageFtsEntity(
    val text: String,
)

/**
 * Table `passage_embeddings` : vecteur sémantique de chaque passage
 * (float32 petit-boutiste). Séparée de `passages` pour pouvoir changer de
 * modèle d'embedding et tout recalculer sans toucher au texte.
 */
@Entity(
    tableName = "passage_embeddings",
    foreignKeys = [
        ForeignKey(
            entity = PassageEntity::class,
            parentColumns = ["row_id"],
            childColumns = ["passage_row_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
class PassageEmbeddingEntity(
    @PrimaryKey @ColumnInfo(name = "passage_row_id") val passageRowId: Long,
    val model: String,
    val dimensions: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val vector: ByteArray,
)

/**
 * Table `source_files` : fichiers repérés dans le dossier surveillé de
 * l'enregistreur natif. L'URI du document est unique : un fichier n'est
 * jamais traité deux fois.
 */
@Entity(
    tableName = "source_files",
    foreignKeys = [
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recording_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["document_uri"], unique = true),
        Index("state"),
        Index("recording_id"),
    ],
)
data class SourceFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "tree_uri") val treeUri: String,
    @ColumnInfo(name = "document_uri") val documentUri: String,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "mime_type") val mimeType: String?,
    @ColumnInfo(name = "size_bytes") val sizeBytes: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "first_seen_at") val firstSeenAt: Long,
    val state: SourceFileState,
    @ColumnInfo(name = "recording_id") val recordingId: String?,
    val attempts: Int,
    @ColumnInfo(name = "last_error") val lastError: String?,
)
