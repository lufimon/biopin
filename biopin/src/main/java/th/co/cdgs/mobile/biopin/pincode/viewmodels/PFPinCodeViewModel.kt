package th.co.cdgs.mobile.biopin.pincode.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import th.co.cdgs.mobile.biopin.pincode.security.PFResult
import th.co.cdgs.mobile.biopin.pincode.security.PFSecurityManager
import th.co.cdgs.mobile.biopin.pincode.security.callback.PFPinCodeHelperCallback
import th.co.cdgs.mobile.biopin.pincode.security.livedata.PFLiveData
import th.co.cdgs.mobile.biopin.settings.PreferencesSettings

class PFPinCodeViewModel : ViewModel() {

    fun isPinCodeEncryptionKeyExist(): LiveData<PFResult<Boolean>> {
        val liveData =
            PFLiveData<PFResult<Boolean>>()
        PFSecurityManager.instance.pinCodeHelper.isPinCodeEncryptionKeyExist(
            object :
                PFPinCodeHelperCallback<Boolean> {
                override fun onResult(result: PFResult<Boolean>) {
                    liveData.setData(result)
                }
            }
        )
        return liveData
    }

    fun encodePin(context: Context, pin: String): LiveData<PFResult<String>> {
        val liveData =
            PFLiveData<PFResult<String>>()
        PFSecurityManager.instance.pinCodeHelper.encodePin(
            context,
            pin,
            object :
                PFPinCodeHelperCallback<String> {
                override fun onResult(result: PFResult<String>) {
                    liveData.setData(result)
                }
            }
        )
        return liveData
    }

    fun confirmPin(context: Context, pin: String): LiveData<PFResult<Boolean>> {
        val liveData =
            PFLiveData<PFResult<Boolean>>()
        val encodedPin = PreferencesSettings.getCode(context)

        PFSecurityManager.instance.pinCodeHelper.checkPin(
            context,
            encodedPin,
            pin,
            object :
                PFPinCodeHelperCallback<Boolean> {
                override fun onResult(result: PFResult<Boolean>) {
                    if(result.mResult!!) {
                        liveData.setData(PFResult(true))
                    } else {
                        PreferencesSettings.saveToPref(context, PreferencesSettings.DEFAULT_CODE)
                        liveData.setData(PFResult(false))
                    }
                }
            }
        )
        return liveData
    }

    fun checkPin(context: Context, encodedPin: String, pin: String): LiveData<PFResult<Boolean>> {
        val liveData =
            PFLiveData<PFResult<Boolean>>()
        PFSecurityManager.instance.pinCodeHelper.checkPin(
            context,
            encodedPin,
            pin,
            object :
                PFPinCodeHelperCallback<Boolean> {
                override fun onResult(result: PFResult<Boolean>) {
                    liveData.setData(result)
                }
            }
        )
        return liveData
    }

    fun delete(): LiveData<PFResult<Boolean>> {
        val liveData =
            PFLiveData<PFResult<Boolean>>()
        PFSecurityManager.instance.pinCodeHelper.delete(
            object :
                PFPinCodeHelperCallback<Boolean> {
                override fun onResult(result: PFResult<Boolean>) {
                    liveData.setData(result)
                }
            }
        )
        return liveData
    }

}