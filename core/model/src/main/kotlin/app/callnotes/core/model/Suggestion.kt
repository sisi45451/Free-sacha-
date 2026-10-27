package app.callnotes.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/** Une proposition de l'IA reste en attente jusqu'à ce que l'utilisateur tranche. */
enum class SuggestionStatus { PENDING, ACCEPTED, REJECTED }

/** Type de proposition, pour filtrer sans décoder le contenu. */
enum class SuggestionKind {
    ATTACH_TO_FOLDER,
    CREATE_FOLDER,
    UPDATE_CONTACT_FIELD,
    ADD_CONTACT_FACT,
}

/** Champ d'une fiche contact que l'IA peut proposer de compléter. */
@Serializable
enum class ContactField { DISPLAY_NAME, COMPANY, JOB_TITLE, EMAIL, POSTAL_ADDRESS }

/**
 * Contenu d'une proposition de l'IA. Rien n'est appliqué sans un tap de
 * confirmation : rattachement à un dossier, création de dossier, mise à jour
 * de fiche contact.
 *
 * Sérialisé en JSON dans la base (colonne `payload_json`).
 */
@Serializable
sealed interface SuggestionPayload {

    @Serializable
    @SerialName("attach_to_folder")
    data class AttachToFolder(
        val folderId: String,
        /** Pourquoi l'IA pense à ce dossier (« Il parle du chantier Dupont »). */
        val reason: String,
    ) : SuggestionPayload

    @Serializable
    @SerialName("create_folder")
    data class CreateFolder(
        val name: String,
        val reason: String,
    ) : SuggestionPayload

    @Serializable
    @SerialName("update_contact_field")
    data class UpdateContactField(
        val contactId: String,
        val field: ContactField,
        val value: String,
        /** Extrait de la conversation qui justifie la proposition. */
        val evidence: String,
    ) : SuggestionPayload

    @Serializable
    @SerialName("add_contact_fact")
    data class AddContactFact(
        val contactId: String,
        val text: String,
    ) : SuggestionPayload
}

/** Type correspondant à un contenu de proposition. */
val SuggestionPayload.kind: SuggestionKind
    get() = when (this) {
        is SuggestionPayload.AttachToFolder -> SuggestionKind.ATTACH_TO_FOLDER
        is SuggestionPayload.CreateFolder -> SuggestionKind.CREATE_FOLDER
        is SuggestionPayload.UpdateContactField -> SuggestionKind.UPDATE_CONTACT_FIELD
        is SuggestionPayload.AddContactFact -> SuggestionKind.ADD_CONTACT_FACT
    }

data class Suggestion(
    val id: String,
    val recordingId: String,
    val payload: SuggestionPayload,
    val status: SuggestionStatus,
    val createdAt: Instant,
)
