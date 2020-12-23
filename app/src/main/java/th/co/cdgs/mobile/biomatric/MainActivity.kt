package th.co.cdgs.mobile.biomatric

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import th.co.cdgs.mobile.biopin.SecurityAuthentication
import th.co.cdgs.mobile.biopin.pincode.security.PFLoginListener


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SecurityAuthentication.Builder(this)
            .setPinLength(6)
            .setContainerView(R.id.container_view)
            .setUseFingerprint(true)
            .setAutoFingerprint(true)
            .setLoginListenerr(object : PFLoginListener{
                override fun onCodeInputSuccessful() {
                    Log.e("onCodeInputSuccessful", "onCodeInputSuccessful")
                }

                override fun onFingerprintSuccessful() {
                    Log.e("onFingerprintSuccessful", "onFingerprintSuccessful")
                }

                override fun onPinLoginFailed() {
                }

                override fun onFingerprintLoginFailed() {
                }

                override fun onFingerprintError(errorCode: Int, errorString: String) {
                }

            })
            .build()
            .create()
    }
}
