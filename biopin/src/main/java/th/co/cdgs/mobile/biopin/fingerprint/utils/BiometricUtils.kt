package th.co.cdgs.mobile.biopin.fingerprint.utils

import android.Manifest
import android.Manifest.permission.USE_FINGERPRINT
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat


object BiometricUtils {

    fun isBiometricPromptEnabled(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    fun isSdkVersionSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun isHardwareSupported(context: Context): Boolean {
        return when {
            isSdkVersionSupported() -> {
                val fingerprintManager = FingerprintManagerCompat.from(context)
                fingerprintManager.isHardwareDetected
            }
            isBiometricPromptEnabled() -> {
                val packageManager: PackageManager = context.packageManager
                packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
            }
            else -> {
                false
            }
        }
    }

    fun isFingerprintAvailable(context: Context): Boolean {
        return when {
            isSdkVersionSupported() -> {
                val fingerprintManager = FingerprintManagerCompat.from(context)
                fingerprintManager.hasEnrolledFingerprints()
            }
            isBiometricPromptEnabled() -> {
                BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
            }
            else -> {
                false
            }
        }
    }

    fun isPermissionGranted(context: Context): Boolean {
        return when {
            isSdkVersionSupported() -> {
                ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.USE_FINGERPRINT
                ) == PackageManager.PERMISSION_GRANTED
            }
            isBiometricPromptEnabled() -> {
                ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.USE_BIOMETRIC
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                false
            }
        }
    }
}