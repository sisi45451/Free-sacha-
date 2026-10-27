package app.callnotes.core.model

/**
 * Suivi d'un fichier repéré dans le dossier surveillé de l'enregistreur natif.
 * Évite de traiter deux fois le même fichier et garde la trace des échecs.
 */
enum class SourceFileState {
    /** Repéré, en attente que l'enregistreur ait fini d'écrire. */
    DISCOVERED,

    /** Taille stable : un enregistrement a été créé et mis en file. */
    QUEUED,

    /** Traitement terminé. */
    PROCESSED,

    /** Pas un fichier audio, ou écarté par l'utilisateur. */
    IGNORED,

    /** Échec après plusieurs tentatives (voir l'erreur associée). */
    FAILED,
}
