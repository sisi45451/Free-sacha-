package app.callnotes.core.database.entity

import androidx.room.ColumnInfo

/**
 * Colonnes communes à toutes les tables synchronisées avec Supabase.
 *
 * - Identifiants : UUID texte générés sur l'appareil (pas d'auto-incrément),
 *   pour que téléphone et web créent des lignes sans collision.
 * - [updatedAt] : horodatage de la dernière modification locale ; sert à la
 *   règle « la dernière écriture gagne » en cas de conflit.
 * - [deletedAt] : suppression douce (« pierre tombale ») propagée par la
 *   synchronisation, puis purgée.
 * - [isDirty] : la ligne a changé localement et doit être envoyée au serveur.
 *
 * Toutes les dates sont stockées en millisecondes depuis l'epoch (UTC).
 */
data class SyncMetadata(
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "deleted_at") val deletedAt: Long? = null,
    @ColumnInfo(name = "is_dirty") val isDirty: Boolean = true,
) {
    companion object {
        /** Métadonnées d'une ligne créée à l'instant [nowMs]. */
        fun created(nowMs: Long) = SyncMetadata(createdAt = nowMs, updatedAt = nowMs)
    }

    /** Copie marquée modifiée à l'instant [nowMs]. */
    fun touched(nowMs: Long) = copy(updatedAt = nowMs, isDirty = true)

    /** Copie marquée supprimée à l'instant [nowMs]. */
    fun deleted(nowMs: Long) = copy(updatedAt = nowMs, deletedAt = nowMs, isDirty = true)
}
