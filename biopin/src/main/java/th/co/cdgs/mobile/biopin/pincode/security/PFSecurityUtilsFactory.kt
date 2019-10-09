package th.co.cdgs.mobile.biopin.pincode.security

object PFSecurityUtilsFactory {
    fun getPFSecurityUtilsInstance(): IPFSecurityUtils {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PFSecurityUtils.instance
        } else {
            PFSecurityUtilsOld.instance
        }
    }
}