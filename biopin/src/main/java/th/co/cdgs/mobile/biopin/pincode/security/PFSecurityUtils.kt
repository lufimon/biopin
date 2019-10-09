package th.co.cdgs.mobile.biopin.pincode.security

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.security.spec.InvalidKeySpecException
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

class PFSecurityUtils private constructor() :
    IPFSecurityUtils {

    private val cipherInstance: Cipher
        @Throws(PFSecurityException::class)
        get() {
            try {
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                throw PFSecurityException(
                    "Can not get instance of Cipher object" + e.message,
                    PFSecurityUtilsErrorCodes.ERROR_GET_CIPHER_INSTANCE
                )
            } catch (e: NoSuchPaddingException) {
                e.printStackTrace()
                throw PFSecurityException(
                    "Can not get instance of Cipher object" + e.message,
                    PFSecurityUtilsErrorCodes.ERROR_GET_CIPHER_INSTANCE
                )
            }

        }

    @Throws(PFSecurityException::class)
    private fun loadKeyStore(): KeyStore {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            return keyStore
        } catch (e: KeyStoreException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Can not load keystore:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_LOAD_KEY_STORE
            )
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Can not load keystore:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_LOAD_KEY_STORE
            )
        } catch (e: CertificateException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Can not load keystore:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_LOAD_KEY_STORE
            )
        } catch (e: IOException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Can not load keystore:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_LOAD_KEY_STORE
            )
        }

    }

    @Throws(PFSecurityException::class)
    override fun encode(
        context: Context,
        alias: String,
        input: String,
        isAuthorizationRequared: Boolean
    ): String {
        try {
            val cipher = getEncodeCipher(context, alias, isAuthorizationRequared)
            val bytes = cipher.doFinal(input.toByteArray())
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error while encoding : " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_ENCODING
            )
        } catch (e: BadPaddingException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error while encoding : " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_ENCODING
            )
        }

    }

    @Throws(PFSecurityException::class)
    private fun getEncodeCipher(
        context: Context,
        alias: String,
        isAuthenticationRequired: Boolean
    ): Cipher {
        val cipher = cipherInstance
        val keyStore = loadKeyStore()
        generateKeyIfNecessary(context, keyStore, alias, isAuthenticationRequired)
        initEncodeCipher(cipher, alias, keyStore)
        return cipher

    }

    private fun generateKeyIfNecessary(
        context: Context, keyStore: KeyStore, alias: String,
        isAuthenticationRequired: Boolean
    ): Boolean {
        try {
            return keyStore.containsAlias(alias) || generateKey(
                context,
                alias,
                isAuthenticationRequired
            )
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }

        return false
    }

    private fun generateKey(
        context: Context,
        keystoreAlias: String,
        isAuthenticationRequired: Boolean
    ): Boolean {
        return generateKey(keystoreAlias, isAuthenticationRequired)
    }

    @Throws(PFSecurityException::class)
    private fun decode(encodedString: String, cipher: Cipher): String {
        try {
            val bytes = Base64.decode(encodedString, Base64.NO_WRAP)
            return String(cipher.doFinal(bytes))
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error while decoding: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_DEENCODING
            )
        } catch (e: BadPaddingException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error while decoding: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_DEENCODING
            )
        }

    }

    @Throws(PFSecurityException::class)
    override fun decode(alias: String, encodedString: String): String {
        try {
            val cipher = cipherInstance
            initDecodeCipher(cipher, alias)
            val bytes = Base64.decode(encodedString, Base64.NO_WRAP)
            return String(cipher.doFinal(bytes))
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error while decoding: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_DEENCODING
            )
        } catch (e: BadPaddingException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error while decoding: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_DEENCODING
            )
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKey(keystoreAlias: String, isAuthenticationRequired: Boolean): Boolean {
        try {
            val keyGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
            )
            keyGenerator.initialize(
                KeyGenParameterSpec.Builder(
                    keystoreAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .setUserAuthenticationRequired(isAuthenticationRequired)
                    .build()
            )
            keyGenerator.generateKeyPair()
            return true

        } catch (exc: NoSuchAlgorithmException) {
            exc.printStackTrace()
            return false
        } catch (exc: NoSuchProviderException) {
            exc.printStackTrace()
            return false
        } catch (exc: InvalidAlgorithmParameterException) {
            exc.printStackTrace()
            return false
        }

    }


    @Throws(PFSecurityException::class)
    private fun initDecodeCipher(cipher: Cipher, alias: String) {
        try {
            val keyStore = loadKeyStore()
            val key = keyStore.getKey(alias, null) as PrivateKey
            cipher.init(Cipher.DECRYPT_MODE, key)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error init decode Cipher: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_DECODE_CIPHER
            )
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error init decode Cipher: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_DECODE_CIPHER
            )
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error init decode Cipher: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_DECODE_CIPHER
            )
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error init decode Cipher: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_DECODE_CIPHER
            )
        }

    }

    @Throws(PFSecurityException::class)
    private fun initEncodeCipher(cipher: Cipher, alias: String, keyStore: KeyStore) {
        try {
            val key = keyStore.getCertificate(alias).publicKey
            val unrestricted = KeyFactory.getInstance(key.algorithm).generatePublic(
                X509EncodedKeySpec(key.encoded)
            )
            val spec = OAEPParameterSpec(
                "SHA-256", "MGF1",
                MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT
            )
            cipher.init(Cipher.ENCRYPT_MODE, unrestricted, spec)
        } catch (e: KeyStoreException) {
            throw PFSecurityException(
                "Can not initialize Encode Cipher:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_ENDECODE_CIPHER
            )
        } catch (e: InvalidKeySpecException) {
            throw PFSecurityException(
                "Can not initialize Encode Cipher:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_ENDECODE_CIPHER
            )
        } catch (e: NoSuchAlgorithmException) {
            throw PFSecurityException(
                "Can not initialize Encode Cipher:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_ENDECODE_CIPHER
            )
        } catch (e: InvalidKeyException) {
            throw PFSecurityException(
                "Can not initialize Encode Cipher:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_ENDECODE_CIPHER
            )
        } catch (e: InvalidAlgorithmParameterException) {
            throw PFSecurityException(
                "Can not initialize Encode Cipher:" + e.message,
                PFSecurityUtilsErrorCodes.ERROR_INIT_ENDECODE_CIPHER
            )
        }

    }

    @Throws(PFSecurityException::class)
    override fun isKeystoreContainAlias(alias: String): Boolean {
        val keyStore = loadKeyStore()
        try {
            return keyStore.containsAlias(alias)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
            throw PFSecurityException(
                e.message!!,
                PFSecurityUtilsErrorCodes.ERROR_KEY_STORE
            )
        }

    }

    @Throws(PFSecurityException::class)
    override fun deleteKey(alias: String) {
        val keyStore = loadKeyStore()
        try {
            keyStore.deleteEntry(alias)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
            throw PFSecurityException(
                "Can not delete key: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_DELETE_KEY
            )
        }

    }

    companion object {
        val instance = PFSecurityUtils()
    }

}