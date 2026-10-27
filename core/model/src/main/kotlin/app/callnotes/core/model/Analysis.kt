package app.callnotes.core.model

import java.time.Instant

/**
 * Résultat de l'analyse IA d'un enregistrement (partie « texte »).
 *
 * Les to-dos, points clés, tags et propositions sont stockés dans leurs propres
 * tables pour pouvoir être cochés, filtrés et agrégés par contact ou dossier.
 */
data class Analysis(
    val recordingId: String,
    /** Résumé de 3 à 5 lignes. */
    val summary: String,
    /** Résumé en une ligne, affiché dans le fil. */
    val oneLineSummary: String,
    /** Questions restées en suspens. */
    val openQuestions: List<String>,
    /** Moteur utilisé (« claude », « gemini-nano »). */
    val engine: String,
    /** Modèle exact, pour la traçabilité. */
    val model: String,
    /** Version du schéma JSON de sortie (voir docs/architecture.md § 5.6). */
    val schemaVersion: Int,
    val createdAt: Instant,
)

/** Tag automatique ou manuel. */
data class Tag(
    val id: String,
    val name: String,
)
