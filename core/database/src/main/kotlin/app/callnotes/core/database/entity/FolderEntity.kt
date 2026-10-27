package app.callnotes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import app.callnotes.core.model.FolderStatus

/** Table `folders` : dossiers (projets, clients, chantiers). */
@Entity(
    tableName = "folders",
    indices = [Index("status"), Index("name")],
)
data class FolderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val status: FolderStatus,
    /** Liste JSON de mots-clés / alias. */
    @ColumnInfo(name = "keywords_json") val keywordsJson: String,
    @ColumnInfo(name = "status_summary") val statusSummary: String?,
    @ColumnInfo(name = "status_summary_updated_at") val statusSummaryUpdatedAt: Long?,
    val color: Int?,
    /** Après une fusion : identifiant du dossier qui a absorbé celui-ci. */
    @ColumnInfo(name = "merged_into_id") val mergedIntoId: String?,
    @Embedded val sync: SyncMetadata,
)
