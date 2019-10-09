package th.co.cdgs.mobile.biopin.pincode.security

class PFSecurityException : Exception {

    private var mCode: Int? = null

    constructor(message: String, code: Int) : super(message) {
        mCode = code
    }

    fun getError(): PFSecurityError {
        return PFSecurityError(message, mCode)
    }
}