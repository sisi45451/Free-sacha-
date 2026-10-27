package app.callnotes.core.model

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

/**
 * Une to-do : action, qui la fait, échéance si elle est mentionnée.
 * Elle peut venir d'un enregistrement (analyse IA) ou être créée à la main
 * (ou depuis l'assistant, après confirmation).
 */
data class Todo(
    val id: String,
    val recordingId: String?,
    val folderId: String?,
    /** Contact concerné (celui qui doit agir, ou à qui l'action se rapporte). */
    val contactId: String?,
    /** L'action, formulée à l'infinitif : « Envoyer le devis à Martin ». */
    val title: String,
    /** Qui fait l'action, tel que dit dans la conversation (« Moi », « Martin »). */
    val assignee: String?,
    val assigneeIsMe: Boolean,
    val dueDate: LocalDate?,
    val dueTime: LocalTime?,
    /** Échéance telle que prononcée (« avant vendredi »), pour contrôle. */
    val dueText: String?,
    val isDone: Boolean,
    val doneAt: Instant?,
    /** Moment de l'enregistrement où l'action a été évoquée. */
    val sourceOffset: Duration?,
    val origin: Origin,
    /** URI de l'événement d'agenda créé à partir de cette to-do, le cas échéant. */
    val calendarEventUri: String?,
)
