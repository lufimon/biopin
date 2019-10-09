package th.co.cdgs.mobile.biopin.pincode.security.livedata

import androidx.lifecycle.LiveData

class PFLiveData<T> : LiveData<T>() {
    fun setData(data: T) {
        value = data
    }
}