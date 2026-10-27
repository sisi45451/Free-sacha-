package app.callnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import app.callnotes.core.database.entity.PassageEmbeddingEntity
import app.callnotes.core.database.entity.PassageEntity

/**
 * Index de recherche : passages, plein texte (FTS4) et vecteurs.
 * La fusion des deux classements (recherche hybride) est faite en Kotlin,
 * voir docs/architecture.md § 5.7.
 */
@Dao
interface SearchDao {

    @Insert
    suspend fun insertPassages(passages: List<PassageEntity>): List<Long>

    @Query("DELETE FROM passages WHERE recording_id = :recordingId")
    suspend fun deletePassagesFor(recordingId: String)

    /**
     * Recherche plein texte. [ftsQuery] suit la syntaxe FTS4
     * (`devis carrelage`, `"mot exact"`, `prix*`).
     */
    @Query(
        """
        SELECT passages.* FROM passages
        JOIN passages_fts ON passages_fts.rowid = passages.row_id
        WHERE passages_fts MATCH :ftsQuery
        LIMIT :limit
        """,
    )
    suspend fun searchFullText(ftsQuery: String, limit: Int): List<PassageEntity>

    @Query("SELECT * FROM passages WHERE row_id IN (:rowIds)")
    suspend fun getPassages(rowIds: List<Long>): List<PassageEntity>

    @Upsert
    suspend fun upsertEmbeddings(embeddings: List<PassageEmbeddingEntity>)

    /**
     * Vecteurs candidats pour la recherche sémantique, déjà filtrés par les
     * critères de la question (enregistrements autorisés).
     */
    @Query(
        """
        SELECT e.* FROM passage_embeddings e
        JOIN passages p ON p.row_id = e.passage_row_id
        WHERE e.model = :model AND p.recording_id IN (:recordingIds)
        """,
    )
    suspend fun getEmbeddings(model: String, recordingIds: List<String>): List<PassageEmbeddingEntity>

    /** Passages sans vecteur pour le modèle courant (à calculer). */
    @Query(
        """
        SELECT p.* FROM passages p
        LEFT JOIN passage_embeddings e
          ON e.passage_row_id = p.row_id AND e.model = :model
        WHERE e.passage_row_id IS NULL
        LIMIT :limit
        """,
    )
    suspend fun getPassagesWithoutEmbedding(model: String, limit: Int): List<PassageEntity>
}
