package app.callnotes.core.model

import kotlinx.serialization.Serializable
import java.time.Instant

enum class ChatRole { USER, ASSISTANT }

/** Conversation avec l'assistant « Demande à tes appels ». */
data class ChatThread(
    val id: String,
    val title: String,
    val updatedAt: Instant,
)

/**
 * Message de l'assistant ou de l'utilisateur. Chaque affirmation de
 * l'assistant s'appuie sur des [citations] : un tap ouvre l'enregistrement au
 * bon passage.
 */
data class ChatMessage(
    val id: String,
    val threadId: String,
    val role: ChatRole,
    val text: String,
    val citations: List<Citation>,
    val createdAt: Instant,
)

/** Source citée par l'assistant : un moment précis d'un enregistrement. */
@Serializable
data class Citation(
    val recordingId: String,
    val startMs: Long,
    val endMs: Long? = null,
    /** Libellé lisible : « Martin — 12 mars, 04:32 ». */
    val label: String,
)
