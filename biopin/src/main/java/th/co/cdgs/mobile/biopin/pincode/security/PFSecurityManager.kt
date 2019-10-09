package th.co.cdgs.mobile.biopin.pincode.security

class PFSecurityManager private constructor() {

    var pinCodeHelper =
        PFFingerprintPinCodeHelper.instance

    companion object {
        val instance = PFSecurityManager()
    }
}