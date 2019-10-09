package th.co.cdgs.mobile.biopin.fingerprint.v28

import androidx.biometric.BiometricPrompt
import th.co.cdgs.mobile.biopin.fingerprint.listener.BiometricCallback

class BiometricCallbackV28() : BiometricPrompt.AuthenticationCallback() {

    private var biometricCallback: BiometricCallback? = null

    constructor(biometricCallback: BiometricCallback) : this() {
        this.biometricCallback = biometricCallback
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        biometricCallback?.onAuthenticationSuccessful()
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        biometricCallback?.onAuthenticationError(errorCode, errString)
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        biometricCallback?.onAuthenticationFailed()
    }
}