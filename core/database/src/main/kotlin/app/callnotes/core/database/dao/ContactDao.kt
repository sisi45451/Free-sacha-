package app.callnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.callnotes.core.database.entity.ContactEntity
import app.callnotes.core.database.entity.ContactFactEntity
import app.callnotes.core.database.entity.ContactPhoneEntity
import app.callnotes.core.database.entity.KeyFactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query(
        """
        SELECT * FROM contacts
        WHERE deleted_at IS NULL
        ORDER BY display_name IS NULL, display_name COLLATE NOCASE
        """,
    )
    fun observeAll(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id AND deleted_at IS NULL")
    fun observeById(id: String): Flow<ContactEntity?>

    /** Fiche à laquelle appartient un numéro normalisé (E.164), s'il est connu. */
    @Query(
        """
        SELECT c.* FROM contacts c
        JOIN contact_phones p ON p.contact_id = c.id
        WHERE p.normalized_number = :normalizedNumber
          AND p.deleted_at IS NULL AND c.deleted_at IS NULL
        LIMIT 1
        """,
    )
    suspend fun findByNumber(normalizedNumber: String): ContactEntity?

    @Query("SELECT * FROM contacts WHERE device_lookup_key = :lookupKey AND deleted_at IS NULL LIMIT 1")
    suspend fun findByDeviceLookupKey(lookupKey: String): ContactEntity?

    @Upsert
    suspend fun upsert(contact: ContactEntity)

    @Query("SELECT * FROM contact_phones WHERE contact_id = :contactId AND deleted_at IS NULL")
    fun observePhones(contactId: String): Flow<List<ContactPhoneEntity>>

    @Upsert
    suspend fun upsertPhones(phones: List<ContactPhoneEntity>)

    @Query(
        """
        SELECT * FROM contact_facts
        WHERE contact_id = :contactId AND deleted_at IS NULL
        ORDER BY created_at DESC
        """,
    )
    fun observeFacts(contactId: String): Flow<List<ContactFactEntity>>

    @Upsert
    suspend fun upsertFacts(facts: List<ContactFactEntity>)

    /**
     * Historique des prix, délais et engagements cités avec ce contact, du plus
     * récent au plus ancien (tableau date / valeur / enregistrement source).
     */
    @Query(
        """
        SELECT k.* FROM key_facts k
        JOIN recordings r ON r.id = k.recording_id
        WHERE k.contact_id = :contactId
          AND k.category IN ('PRICE', 'DEADLINE', 'COMMITMENT', 'QUANTITY')
          AND k.deleted_at IS NULL AND r.deleted_at IS NULL
        ORDER BY r.started_at DESC
        """,
    )
    fun observeKeyFactHistory(contactId: String): Flow<List<KeyFactEntity>>
}
