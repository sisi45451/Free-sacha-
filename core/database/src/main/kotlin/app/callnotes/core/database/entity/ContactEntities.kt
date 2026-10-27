package app.callnotes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.callnotes.core.model.Origin
import app.callnotes.core.model.PhoneNumberSource

/**
 * Table `contacts` : fiche contact enrichie de l'app, éventuellement liée à un
 * contact du téléphone par sa clé de recherche (lookup key).
 */
@Entity(
    tableName = "contacts",
    indices = [Index("device_lookup_key"), Index("display_name")],
)
data class ContactEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "display_name") val displayName: String?,
    @ColumnInfo(name = "device_lookup_key") val deviceLookupKey: String?,
    /** URI de la photo du contact du téléphone (non synchronisée). */
    @ColumnInfo(name = "photo_uri") val photoUri: String?,
    val company: String?,
    @ColumnInfo(name = "job_title") val jobTitle: String?,
    val email: String?,
    @ColumnInfo(name = "postal_address") val postalAddress: String?,
    val notes: String?,
    @Embedded val sync: SyncMetadata,
)

/**
 * Table `contact_phones` : les numéros d'une fiche. L'index unique sur le
 * numéro normalisé garantit qu'un numéro n'appartient qu'à une seule fiche :
 * c'est ce qui regroupe « 06 12… » et « +33 6 12… ».
 */
@Entity(
    tableName = "contact_phones",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["normalized_number"], unique = true),
        Index("contact_id"),
    ],
)
data class ContactPhoneEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "contact_id") val contactId: String,
    @ColumnInfo(name = "normalized_number") val normalizedNumber: String,
    @ColumnInfo(name = "raw_number") val rawNumber: String,
    val label: String?,
    val source: PhoneNumberSource,
    @Embedded val sync: SyncMetadata,
)

/** Table `contact_facts` : infos utiles retenues sur une personne. */
@Entity(
    tableName = "contact_facts",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_recording_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("contact_id"), Index("source_recording_id")],
)
data class ContactFactEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "contact_id") val contactId: String,
    val text: String,
    @ColumnInfo(name = "source_recording_id") val sourceRecordingId: String?,
    @ColumnInfo(name = "source_offset_ms") val sourceOffsetMs: Long?,
    val origin: Origin,
    @Embedded val sync: SyncMetadata,
)
