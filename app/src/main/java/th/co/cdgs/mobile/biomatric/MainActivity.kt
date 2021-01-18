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
        SecurityAuthentication.Builder(this)
                .setPinLength(6)
                .setContainerView(R.id.container_view)
                .setUseFingerprint(true)
                .setAutoFingerprint(true)
                .setLoginListenerr(object : PFLoginListener {
                    override fun onCodeInputSuccessful() {
                        toast("onCodeInputSuccessful")
                    }

                    override fun onFingerprintSuccessful() {
                        Log.e("TAG","onFingerprintSuccessful")
                    }

                    override fun onPinLoginFailed() {
                        toast("onPinLoginFailed")
                    }

                    override fun onFingerprintLoginFailed() {
                        Log.e("TAG","onFingerprintLoginFailed")
                    }

                    override fun onFingerprintError(errorCode: Int, errorString: String) {
                        Log.e("TAG","onFingerprintError$errorCode$errorString")
                    }

                    override fun onForgetPassCode() {
                        toast("onForgetPassCode")
                    }

                    override fun onConfirmSuccessful() {
                        toast("onConfirmSuccessful")
                    }

                    override fun onConfirmFailed() {
                        toast("onConfirmFailed")
                    }
                })
                .build()
                .create()
//        BiometricManager.BiometricBuilder(this)
//            .setTitle("ทดสอบ")
//            .build()
//            .authenticate(object : BiometricCallback{
//                override fun onSdkVersionNotSupported() {
//
//                }
//
//                override fun onBiometricAuthenticationNotSupported() {
//
//                }
//
//                override fun onBiometricAuthenticationNotAvailable() {
//
//                }
//
//                override fun onBiometricAuthenticationPermissionNotGranted() {
//
//                }
//
//                override fun onBiometricAuthenticationInternalError(error: String) {
//
//                }
//
//                override fun onAuthenticationFailed() {
//
//                }
//
//                override fun onAuthenticationCancelled() {
//
//                }
//
//                override fun onAuthenticationSuccessful() {
//
//                }
//
//                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
//
//                }
//
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//
//                }
//
//            })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
