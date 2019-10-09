package th.co.cdgs.mobile.biopin.pincode.security

import android.content.Context
import th.co.cdgs.mobile.biopin.pincode.security.callback.PFPinCodeHelperCallback

interface IPFPinCodeHelper {

    fun encodePin(context: Context, pin: String, callBack: PFPinCodeHelperCallback<String>?)

    fun checkPin(
        context: Context,
        encodedPin: String,
        pin: String,
        callback: PFPinCodeHelperCallback<Boolean>?
    )

    fun delete(callback: PFPinCodeHelperCallback<Boolean>?)

    fun isPinCodeEncryptionKeyExist(callback: PFPinCodeHelperCallback<Boolean>?)
}