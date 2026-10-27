package app.callnotes.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phrase de passe de la base protégée par Android Keystore.
 *
 * Fonctionnement :
 * 1. Au premier lancement, on tire 32 octets aléatoires (la phrase de passe).
 * 2. On les chiffre en AES-256-GCM avec une clé générée DANS le Keystore
 *    (matériel sécurisé du téléphone : la clé n'est jamais extractible).
 * 3. On stocke le résultat chiffré dans `noBackupFilesDir` (exclu des
 *    sauvegardes : restaurée sur un autre téléphone, elle serait inutilisable
 *    puisque la clé Keystore, elle, ne voyage pas).
 *
 * La clé ne demande pas d'authentification de l'utilisateur : le traitement
 * des appels en arrière-plan doit pouvoir ouvrir la base téléphone verrouillé.
 * Le verrouillage biométrique (étape 7) protège l'accès à l'interface.
 */
@Singleton
class KeystoreDatabaseKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : DatabaseKeyProvider {

    private val keyFile: File get() = File(context.noBackupFilesDir, KEY_FILE_NAME)

    @Synchronized
    override fun getOrCreatePassphrase(): ByteArray {
        val file = keyFile
        if (file.exists()) {
            return decrypt(file.readBytes())
        }
        // Encodée en hexadécimal : 64 caractères ASCII, sans octet nul.
        val random = ByteArray(PASSPHRASE_BYTES).also { SecureRandom().nextBytes(it) }
        val passphrase = random.toHex().toByteArray(Charsets.US_ASCII)
        val tmp = File(file.parentFile, "$KEY_FILE_NAME.tmp")
        tmp.writeBytes(encrypt(passphrase))
        // Écriture atomique : jamais de fichier à moitié écrit.
        check(tmp.renameTo(file)) { "Impossible d'enregistrer la clé de la base" }
        return passphrase
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }

        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE_BITS)
            .build()
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            .apply { init(spec) }
            .generateKey()
    }

    /** Format stocké : [IV de 12 octets][texte chiffré + étiquette GCM]. */
    private fun encrypt(plain: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, secretKey()) }
        return cipher.iv + cipher.doFinal(plain)
    }

    private fun decrypt(stored: ByteArray): ByteArray {
        val iv = stored.copyOfRange(0, GCM_IV_BYTES)
        val cipherText = stored.copyOfRange(GCM_IV_BYTES, stored.size)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
        }
        return cipher.doFinal(cipherText)
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "callnotes_db_key"
        const val KEY_FILE_NAME = "db_passphrase.bin"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_SIZE_BITS = 256
        const val PASSPHRASE_BYTES = 32
        const val GCM_IV_BYTES = 12
        const val GCM_TAG_BITS = 128
    }
}
