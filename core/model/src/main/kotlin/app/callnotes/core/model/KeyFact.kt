package app.callnotes.core.model

import java.time.Duration
import java.time.LocalDate

/** Nature d'un point clé extrait d'une conversation. */
enum class KeyFactCategory {
    PRICE,
    QUANTITY,
    DATE,
    DEADLINE,
    ADDRESS,
    COMMITMENT,
    FIGURE,
    CONTACT_INFO,
    OTHER,
}

/**
 * Point clé : chiffre, prix, quantité, date, adresse, engagement…
 *
 * Rattaché au contact et au dossier concernés, il alimente l'historique des
 * prix, délais et engagements de la fiche contact (tableau date / valeur /
 * enregistrement source).
 */
data class KeyFact(
    val id: String,
    val recordingId: String,
    val contactId: String?,
    val folderId: String?,
    val category: KeyFactCategory,
    /** De quoi il s'agit : « Prix du carrelage posé ». */
    val label: String,
    /** La valeur telle qu'énoncée : « 45 € HT le m² ». */
    val value: String,
    /** Valeur numérique extraite, pour trier et comparer (45.0). */
    val amount: Double?,
    /** Unité (« m² », « jours »…). */
    val unit: String?,
    /** Devise ISO 4217 (« EUR »). */
    val currency: String?,
    /** Date mentionnée dans la conversation (échéance, rendez-vous…). */
    val mentionedDate: LocalDate?,
    val sourceOffset: Duration?,
    val origin: Origin,
)
