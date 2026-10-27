package app.callnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.callnotes.core.database.entity.SourceFileEntity
import app.callnotes.core.model.SourceFileState

/** Suivi des fichiers du dossier surveillé (enregistreur natif). */
@Dao
interface SourceFileDao {

    /** Insère un fichier nouvellement repéré ; ignoré s'il est déjà connu. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(file: SourceFileEntity): Long

    @Query("SELECT * FROM source_files WHERE document_uri = :documentUri")
    suspend fun findByDocumentUri(documentUri: String): SourceFileEntity?

    @Query("SELECT * FROM source_files WHERE state = :state ORDER BY last_modified")
    suspend fun getByState(state: SourceFileState): List<SourceFileEntity>

    @Query(
        """
        UPDATE source_files
        SET state = :state, recording_id = :recordingId, last_error = :error,
            attempts = attempts + :attemptIncrement
        WHERE id = :id
        """,
    )
    suspend fun updateState(
        id: Long,
        state: SourceFileState,
        recordingId: String?,
        error: String?,
        attemptIncrement: Int,
    )
}
