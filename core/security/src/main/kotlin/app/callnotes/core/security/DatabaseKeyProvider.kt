package app.callnotes.core.security

/** Fournit la phrase de passe de la base de données chiffrée. */
interface DatabaseKeyProvider {

    /**
     * Renvoie la phrase de passe SQLCipher, en la créant au premier appel.
     * Toujours la même valeur ensuite, tant que l'app n'est pas désinstallée.
     */
    fun getOrCreatePassphrase(): ByteArray
}
