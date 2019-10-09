package th.co.cdgs.mobile.biopin.pincode.security

import android.content.Context
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import th.co.cdgs.mobile.biopin.pincode.security.callback.PFPinCodeHelperCallback

class PFFingerprintPinCodeHelper private constructor() :
    IPFPinCodeHelper {

    private val pfSecurityUtils =
        PFSecurityUtilsFactory.getPFSecurityUtilsInstance()

    override fun encodePin(context: Context, pin: String, callback: PFPinCodeHelperCallback<String>?) {
        try {
            val encoded = pfSecurityUtils.encode(context,
                PIN_ALIAS, pin, false)
            callback?.onResult(PFResult(encoded))
        } catch (e: PFSecurityException) {
            callback?.onResult(PFResult(e.getError()))
        }

    }

    override fun checkPin(
        context: Context,
        encodedPin: String,
        pin: String,
        callback: PFPinCodeHelperCallback<Boolean>?
    ) {
        try {
            val pinCode = pfSecurityUtils.decode(PIN_ALIAS, encodedPin)
            callback?.onResult(PFResult(pinCode == pin))
        } catch (e: PFSecurityException) {
            callback?.onResult(PFResult(e.getError()))
        }

    }


    private fun isFingerPrintAvailable(context: Context): Boolean {
        return FingerprintManagerCompat.from(context).isHardwareDetected
    }

    private fun isFingerPrintReady(context: Context): Boolean {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
    }

    override fun delete(callback: PFPinCodeHelperCallback<Boolean>?) {
        try {
            pfSecurityUtils.deleteKey(PIN_ALIAS)
            callback?.onResult(PFResult(true))
        } catch (e: PFSecurityException) {
            callback?.onResult(PFResult(e.getError()))
        }

    }

    override fun isPinCodeEncryptionKeyExist(callback: PFPinCodeHelperCallback<Boolean>?) {
        try {
            val isExist = pfSecurityUtils.isKeystoreContainAlias(PIN_ALIAS)
            callback!!.onResult(PFResult(isExist))
        } catch (e: PFSecurityException) {
            callback!!.onResult(PFResult(e.getError()))
        }
    }

    companion object {
        private val FINGERPRINT_ALIAS = "fp_fingerprint_lock_screen_key_store"
        private val PIN_ALIAS = "fp_pin_lock_screen_key_store"
        val instance = PFFingerprintPinCodeHelper()
    }

}