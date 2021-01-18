package th.co.cdgs.mobile.biomatric

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import th.co.cdgs.mobile.biopin.SecurityAuthentication
import th.co.cdgs.mobile.biopin.pincode.security.PFLoginListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SecurityAuthentication.Builder(this)
                .setPinLength(6)
                .setContainerView(R.id.container_view)
                .setUseFingerprint(false)
                .setAutoFingerprint(false)
                .setLoginListenerr(object : PFLoginListener {
                    override fun onCodeInputSuccessful() {
                        toast("onCodeInputSuccessful")
                    }

                    override fun onFingerprintSuccessful() {
                        toast("onFingerprintSuccessful")
                    }

                    override fun onPinLoginFailed() {
                        toast("onPinLoginFailed")
                    }

                    override fun onFingerprintLoginFailed() {
                        toast("onFingerprintLoginFailed")
                    }

                    override fun onFingerprintError(errorCode: Int, errorString: String) {
                        toast("onFingerprintError$errorCode$errorString")
                    }

                    override fun onForgetPassCode() {
                        toast("onForgetPassCode")
                    }
                })
                .build()
                .create()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
