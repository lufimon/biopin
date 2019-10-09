package th.co.cdgs.mobile.biopin.pincode.security.callback

import th.co.cdgs.mobile.biopin.pincode.security.PFResult

interface PFPinCodeHelperCallback<T> {
    fun onResult(result: PFResult<T>)
}