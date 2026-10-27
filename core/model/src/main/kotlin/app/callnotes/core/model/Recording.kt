package app.callnotes.core.model

import java.time.Duration
import java.time.Instant

/**
 * Les trois types d'« Enregistrement ». Transcription, analyse et indexation
 * sont communes aux trois.
 */
enum class RecordingType { CALL, MEETING, MEMO }

/** Sens d'un appel, lu dans le journal d'appels (ou inconnu). */
enum class CallDirection { INCOMING, OUTGOING, UNKNOWN }

/**
 * Origine de l'identification de l'interlocuteur, dans l'ordre de priorité
 * du cahier des charges : journal d'appels → contacts du téléphone →
 * nom du fichier. MANUAL = corrigé à la main.
 */
enum class IdentificationSource { CALL_LOG, DEVICE_CONTACTS, FILE_NAME, MANUAL, NONE }

/** Où se trouve le fichier audio. */
enum class AudioStorage {
    /** Fichier de l'enregistreur natif, dans le dossier surveillé (accès SAF). */
    WATCHED_FOLDER,

    /** Fichier enregistré par l'app (réunion, mémo), dans son stockage privé. */
    APP_PRIVATE,

    /** Fichier importé à la main, copié dans le stockage privé. */
    IMPORTED,
}

/**
 * Étapes du pipeline de traitement. Chaque étape est idempotente et
 * reprenable : si le téléphone tue l'app, le travail repart de l'étape
 * enregistrée ici.
 */
enum class ProcessingStage {
    /** Réunion ou mémo en cours d'enregistrement. */
    RECORDING,
    QUEUED,
    IDENTIFYING,
    TRANSCRIBING,
    ANALYZING,
    INDEXING,
    DONE,
    FAILED,
}

/**
 * Numéro de téléphone.
 *
 * @property normalized format international E.164 (« +33612345678 ») quand le
 * numéro est analysable par libphonenumber, sinon ses seuls chiffres. C'est la
 * clé qui regroupe « 06 12… » et « +33 6 12… » sur une même fiche.
 * @property raw le numéro tel qu'il a été lu (journal d'appels, fichier…).
 */
data class PhoneNumber(
    val normalized: String,
    val raw: String,
)

/** Fichier audio d'un enregistrement. */
data class AudioFile(
    /** URI `content://` (SAF) ou `file://` (stockage privé). */
    val uri: String,
    val storage: AudioStorage,
    val mimeType: String?,
    val sizeBytes: Long?,
    /** Nom du fichier d'origine : sert au dernier recours d'identification. */
    val sourceFileName: String?,
)

/** État de traitement d'un enregistrement. */
data class ProcessingState(
    val stage: ProcessingStage,
    val error: String? = null,
    /** Avancement de la transcription : permet de reprendre une longue réunion. */
    val transcribedUntil: Duration = Duration.ZERO,
)

/**
 * Un enregistrement : appel, réunion ou mémo.
 *
 * Les participants (un ou plusieurs contacts) sont dans une table de liaison ;
 * [primaryContactId] est l'interlocuteur principal (l'autre personne d'un
 * appel, le contact d'un mémo), utilisé pour l'affichage du fil.
 */
data class Recording(
    val id: String,
    val type: RecordingType,
    /** Titre saisi (réunion, mémo). Pour un appel, on affiche le contact. */
    val title: String?,
    val startedAt: Instant,
    val duration: Duration,
    /** Uniquement pour les appels. */
    val direction: CallDirection?,
    /** Uniquement pour les appels. */
    val counterpartNumber: PhoneNumber?,
    val identificationSource: IdentificationSource,
    val primaryContactId: String?,
    /** Lieu d'une réunion. */
    val location: String?,
    val folderId: String?,
    /** Mémo rattaché à un appel (« Ajouter un mémo » après l'appel). */
    val parentRecordingId: String?,
    val audio: AudioFile,
    val processing: ProcessingState,
    /** Langue détectée ou imposée (« fr »). */
    val language: String?,
)

/**
 * Une ligne du fil chronologique : données dénormalisées pour un affichage
 * rapide (nom du contact, résumé en une ligne, nombre de to-dos ouvertes).
 */
data class RecordingListItem(
    val id: String,
    val type: RecordingType,
    val title: String?,
    val startedAt: Instant,
    val duration: Duration,
    val direction: CallDirection?,
    val counterpartNumber: PhoneNumber?,
    val contactId: String?,
    val contactName: String?,
    val contactPhotoUri: String?,
    val oneLineSummary: String?,
    val stage: ProcessingStage,
    val openTodoCount: Int,
) {
    /**
     * Libellé principal de la ligne : pour un appel, le nom du contact sinon le
     * numéro ; pour une réunion ou un mémo, le titre sinon le contact.
     * `null` si rien n'est connu (l'écran affiche alors « Numéro masqué » ou
     * « Sans titre »).
     */
    val headline: String?
        get() = when (type) {
            RecordingType.CALL -> contactName ?: counterpartNumber?.raw ?: title
            RecordingType.MEETING, RecordingType.MEMO -> title ?: contactName
        }
}
