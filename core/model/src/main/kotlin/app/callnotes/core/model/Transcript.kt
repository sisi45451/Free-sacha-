package app.callnotes.core.model

import java.time.Duration

/**
 * Une voix distinguée dans un enregistrement.
 *
 * [label] est l'étiquette automatique (« Moi », « Interlocuteur »,
 * « Intervenant 2 ») ; [displayName] est le nom corrigé à la main.
 */
data class Speaker(
    val id: String,
    val recordingId: String,
    val label: String,
    val displayName: String?,
    val contactId: String?,
    val isMe: Boolean,
) {
    val name: String get() = displayName ?: label
}

/**
 * Un segment de transcription horodaté : un tap dessus fait sauter le lecteur
 * audio à [start].
 */
data class TranscriptSegment(
    val id: String,
    val recordingId: String,
    /** Position du segment dans la transcription (0, 1, 2…). */
    val ordinal: Int,
    val start: Duration,
    val end: Duration,
    val speakerId: String?,
    val text: String,
    /** Confiance moyenne du moteur (0..1), si disponible. */
    val confidence: Float?,
)

/**
 * Passage indexé pour la recherche et l'assistant : quelques phrases
 * consécutives d'une transcription, avec leur horodatage.
 */
data class Passage(
    val rowId: Long,
    val recordingId: String,
    val ordinal: Int,
    val text: String,
    val start: Duration,
    val end: Duration,
)
