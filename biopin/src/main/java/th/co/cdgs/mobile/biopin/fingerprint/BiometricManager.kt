package th.co.cdgs.mobile.biopin.fingerprint

import android.annotation.TargetApi
import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.core.os.CancellationSignal
import androidx.fragment.app.FragmentActivity
import th.co.cdgs.mobile.biopin.fingerprint.listener.BiometricCallback
import th.co.cdgs.mobile.biopin.fingerprint.utils.BiometricUtils
import th.co.cdgs.mobile.biopin.fingerprint.v23.BiometricManagerV23
import th.co.cdgs.mobile.biopin.fingerprint.v28.BiometricCallbackV28
import java.util.concurrent.Executors


class BiometricManager : BiometricManagerV23 {


    internal var cancellationSignal = CancellationSignal()

    constructor(biometricBuilder: BiometricBuilder) {
        this.activity = biometricBuilder.activity
        this.title = biometricBuilder.title
        this.subtitle = biometricBuilder.subtitle
        this.description = biometricBuilder.description
        this.negativeButtonText = biometricBuilder.negativeButtonText
    }

    fun authenticate(biometricCallback: BiometricCallback) {

        if (title == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog title cannot be null")
            return
        }


        if (subtitle == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog subtitle cannot be null")
            return
        }


        if (description == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog description cannot be null")
            return
        }

        if (negativeButtonText == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog negative button text cannot be null")
            return
        }


        if (!BiometricUtils.isSdkVersionSupported()) {
            biometricCallback.onSdkVersionNotSupported()
            return
        }

        if (!BiometricUtils.isPermissionGranted(activity!!.applicationContext)) {
            biometricCallback.onBiometricAuthenticationPermissionNotGranted()
            return
        }

        if (!BiometricUtils.isHardwareSupported(activity!!.applicationContext)) {
            biometricCallback.onBiometricAuthenticationNotSupported()
            return
        }

        if (!BiometricUtils.isFingerprintAvailable(activity!!.applicationContext)) {
            biometricCallback.onBiometricAuthenticationNotAvailable()
            return
        }

        displayBiometricDialog(biometricCallback)
    }

    fun cancelAuthentication() {
        if (BiometricUtils.isBiometricPromptEnabled()) {
            if (!cancellationSignal.isCanceled)
                cancellationSignal.cancel()
        } else {
            if (!cancellationSignalV23.isCanceled)
                cancellationSignalV23.cancel()
        }
    }


    private fun displayBiometricDialog(biometricCallback: BiometricCallback) {
        if (BiometricUtils.isBiometricPromptEnabled()) {
            displayBiometricPrompt(biometricCallback)
        } else {
            displayBiometricPromptV23(biometricCallback)
        }
    }


    @TargetApi(Build.VERSION_CODES.P)
    private fun displayBiometricPrompt(biometricCallback: BiometricCallback) {
        BiometricPrompt(
            activity!!,
            Executors.newSingleThreadExecutor(),
            BiometricCallbackV28(biometricCallback)
        ).authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(title!!)
                .setSubtitle(subtitle!!)
                .setDescription(description!!)
                .setNegativeButtonText(negativeButtonText!!)
                .build()
        )
    }


    class BiometricBuilder {
        internal var activity: FragmentActivity? = null
        internal var title: String? = null
        internal var subtitle: String? = null
        internal var description: String? = null
        internal var negativeButtonText: String? = null

        constructor(activity: FragmentActivity) {
            this.activity = activity
        }

        fun setTitle(title: String): BiometricBuilder {
            this.title = title
            return this
        }

        fun setSubtitle(subtitle: String): BiometricBuilder {
            this.subtitle = subtitle
            return this
        }

        fun setDescription(description: String): BiometricBuilder {
            this.description = description
            return this
        }


        fun setNegativeButtonText(negativeButtonText: String): BiometricBuilder {
            this.negativeButtonText = negativeButtonText
            return this
        }

        fun build(): BiometricManager {
            return BiometricManager(this)
        }
    }
}