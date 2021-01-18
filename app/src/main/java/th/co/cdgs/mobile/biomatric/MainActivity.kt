package th.co.cdgs.mobile.biomatric

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import th.co.cdgs.mobile.biopin.SecurityAuthentication
import th.co.cdgs.mobile.biopin.fingerprint.BiometricManager
import th.co.cdgs.mobile.biopin.fingerprint.listener.BiometricCallback
import th.co.cdgs.mobile.biopin.pincode.security.PFLoginListener
import th.co.cdgs.mobile.biopin.settings.PreferencesSettings


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        SecurityAuthentication.Builder(this)
//                .setPinLength(6)
//                .setContainerView(R.id.container_view)
//                .setUseFingerprint(true)
//                .setAutoFingerprint(true)
//                .setLoginListenerr(object : PFLoginListener {
//                    override fun onCodeInputSuccessful() {
//                        toast("onCodeInputSuccessful")
//                    }
//
//                    override fun onFingerprintSuccessful() {
//                        Log.e("TAG","onFingerprintSuccessful")
//                    }
//
//                    override fun onPinLoginFailed() {
//                        toast("onPinLoginFailed")
//                    }
//
//                    override fun onFingerprintLoginFailed() {
//                        Log.e("TAG","onFingerprintLoginFailed")
//                    }
//
//                    override fun onFingerprintError(errorCode: Int, errorString: String) {
//                        Log.e("TAG","onFingerprintError$errorCode$errorString")
//                    }
//
//                    override fun onForgetPassCode() {
//                        toast("onForgetPassCode")
//                    }
//
//                    override fun onConfirmSuccessful() {
//                        toast("onConfirmSuccessful")
//                    }
//
//                    override fun onConfirmFailed() {
//                        toast("onConfirmFailed")
//                    }
//                })
//                .build()
//                .create()
//        BiometricManager.BiometricBuilder(this)
//            .setTitle("ทดสอบ")
//            .setSubtitle("test")
//            .setDescription("fasdfasdf")
//            .setNegativeButtonText("sadfasdf")
//            .build()
//            .authenticate(object : BiometricCallback{
//                override fun onSdkVersionNotSupported() {
//                    Log.e("TAG","onSdkVersionNotSupported")
//                }
//
//                override fun onBiometricAuthenticationNotSupported() {
//                    Log.e("TAG","onBiometricAuthenticationNotSupported")
//                }
//
//                override fun onBiometricAuthenticationNotAvailable() {
//                    Log.e("TAG","onBiometricAuthenticationNotAvailable")
//                }
//
//                override fun onBiometricAuthenticationPermissionNotGranted() {
//                    Log.e("TAG","onBiometricAuthenticationPermissionNotGranted")
//                }
//
//                override fun onBiometricAuthenticationInternalError(error: String) {
//                    Log.e("TAG","onBiometricAuthenticationInternalError$error")
//                }
//
//                override fun onAuthenticationFailed() {
//                    Log.e("TAG","onAuthenticationFailed")
//                }
//
//                override fun onAuthenticationCancelled() {
//                    Log.e("TAG","onAuthenticationCancelled")
//                }
//
//                override fun onAuthenticationSuccessful() {
//                    Log.e("TAG","onAuthenticationSuccessful")
//                }
//
//                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
//                    Log.e("TAG","onAuthenticationHelp$helpCode$helpString")
//                }
//
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    Log.e("TAG","onAuthenticationError$errorCode$errString")
//                }
//
//            })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
