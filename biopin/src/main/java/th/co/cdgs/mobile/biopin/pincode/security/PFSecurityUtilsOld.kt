package th.co.cdgs.mobile.biopin.pincode.security

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

class PFSecurityUtilsOld private constructor() :
    IPFSecurityUtils {

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

    private fun generateKeyIfNecessary(
        context: Context,
        keyStore: KeyStore,
        alias: String,
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

    @Throws(PFSecurityException::class)
    override fun encode(
        context: Context,
        alias: String,
        input: String,
        isAuthorizationRequared: Boolean
    ): String {
        try {
            val bytes = rsaEncrypt(context, input.toByteArray(), alias)
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error while encoding : " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_ENCODING
            )
        }

    }

    @Throws(Exception::class)
    private fun rsaEncrypt(
        context: Context,
        secret: ByteArray,
        keystoreAlias: String
    ): ByteArray {
        val keyStore = loadKeyStore()
        generateKeyIfNecessary(context, keyStore, keystoreAlias, false)
        val privateKeyEntry = keyStore.getEntry(keystoreAlias, null) as KeyStore.PrivateKeyEntry
        val inputCipher = Cipher.getInstance(
            RSA_MODE,
            PROVIDER
        )
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)
        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()
        return outputStream.toByteArray()
    }

    @Throws(PFSecurityException::class)
    override fun decode(
        alias: String,
        encodedString: String
    ): String {
        try {
            val bytes = Base64.decode(encodedString, Base64.NO_WRAP)
            return String(rsaDecrypt(bytes, alias))
        } catch (e: Exception) {
            e.printStackTrace()
            throw PFSecurityException(
                "Error while decoding: " + e.message,
                PFSecurityUtilsErrorCodes.ERROR_DEENCODING
            )
        }

    }

    @Throws(Exception::class)
    private fun rsaDecrypt(
        encrypted: ByteArray,
        keystoreAlias: String
    ): ByteArray {
        val keyStore = loadKeyStore()
        val privateKeyEntry = keyStore.getEntry(keystoreAlias, null) as KeyStore.PrivateKeyEntry
        val output = Cipher.getInstance(
            RSA_MODE,
            PROVIDER
        )
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)
        val cipherInputStream = CipherInputStream(
            ByteArrayInputStream(encrypted), output
        )
        val values = ArrayList<Byte>()
        var nextByte: Int
        do {
            nextByte = cipherInputStream.read()
            if (nextByte == -1)
                break
            values.add(nextByte.toByte())
        } while (true)
        val bytes = ByteArray(values.size)
        for (i in bytes.indices) {
            bytes[i] = values[i]
        }
        return bytes
    }

    private fun generateKey(
        context: Context,
        keystoreAlias: String,
        isAuthenticationRequired: Boolean
    ): Boolean {
        return generateKeyOld(context, keystoreAlias, isAuthenticationRequired)
    }

    private fun generateKeyOld(
        context: Context,
        keystoreAlias: String,
        isAuthenticationRequired: Boolean
    ): Boolean {
        try {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 25)

            val keyGen = KeyPairGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")

            val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(keystoreAlias)
                .setSubject(X500Principal("CN=$keystoreAlias"))
                .setSerialNumber(BigInteger.valueOf(Math.abs(keystoreAlias.hashCode()).toLong()))
                .setEndDate(end.time)
                .setStartDate(start.time)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(
                    X500Principal(
                        "CN = Secured Preference Store, O = Devliving Online"
                    )
                )
                .build()

            keyGen.initialize(spec)
            keyGen.generateKeyPair()
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
        val instance = PFSecurityUtilsOld()
        private val RSA_MODE = "RSA/ECB/PKCS1Padding"
        private val PROVIDER =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                "AndroidKeyStoreBCWorkaround"
            else
                "AndroidOpenSSL"
    }

}