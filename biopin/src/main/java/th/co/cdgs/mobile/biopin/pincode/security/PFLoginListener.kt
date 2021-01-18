package th.co.cdgs.mobile.biopin.pincode.security

interface PFLoginListener{
    fun onCodeInputSuccessful()
    fun onFingerprintSuccessful()
    fun onPinLoginFailed()
    fun onFingerprintLoginFailed()
    fun onFingerprintError(errorCode: Int, errorString: String)
    fun onForgetPassCode()
}