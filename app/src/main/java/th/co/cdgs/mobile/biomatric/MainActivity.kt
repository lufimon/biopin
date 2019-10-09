package th.co.cdgs.mobile.biomatric

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import th.co.cdgs.mobile.biopin.SecurityAuthentication


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SecurityAuthentication.Builder(this)
            .setPinLength(6)
            .setContainerView(R.id.container_view)
            .setUseFingerprint(true)
            .setAutoFingerprint(true)
            .build()
            .create()
    }
}
