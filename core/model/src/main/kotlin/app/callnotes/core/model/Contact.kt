package app.callnotes.core.model

import java.time.Duration

/** Qui a produit une donnée : l'analyse IA ou l'utilisateur. */
enum class Origin { AI, USER }

/** D'où vient un numéro rattaché à une fiche contact. */
enum class PhoneNumberSource { DEVICE_CONTACTS, CALL_LOG, FILE_NAME, MANUAL, AI }

/** Rôle d'un contact dans un enregistrement. */
enum class ParticipantRole {
    /** L'interlocuteur d'un appel. */
    COUNTERPART,

    /** Un participant d'une réunion. */
    ATTENDEE,

    /** Une personne dont parle un mémo. */
    MENTIONED,
}

/**
 * Fiche contact enrichie de l'app.
 *
 * Elle peut être liée à un contact du téléphone ([deviceLookupKey]) pour le nom
 * et la photo, ou exister seule (numéro inconnu « nommé dans l'app »).
 * Plusieurs numéros peuvent pointer vers la même fiche.
 */
data class Contact(
    val id: String,
    /** `null` tant qu'un numéro inconnu n'a pas été nommé. */
    val displayName: String?,
    /** Clé `ContactsContract.Contacts.LOOKUP_KEY` du contact du téléphone. */
    val deviceLookupKey: String?,
    val photoUri: String?,
    val company: String?,
    val jobTitle: String?,
    val email: String?,
    val postalAddress: String?,
    /** Notes libres saisies à la main. */
    val notes: String?,
    val phoneNumbers: List<ContactPhone>,
)

data class ContactPhone(
    val id: String,
    val number: PhoneNumber,
    /** « Mobile », « Bureau »… */
    val label: String?,
    val source: PhoneNumberSource,
)

/**
 * Info utile retenue sur une personne (« préfère être appelé le matin »),
 * avec l'enregistrement d'où elle provient.
 */
data class ContactFact(
    val id: String,
    val contactId: String,
    val text: String,
    val sourceRecordingId: String?,
    val sourceOffset: Duration?,
    val origin: Origin,
)
