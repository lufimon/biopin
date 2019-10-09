package th.co.cdgs.mobile.biopin.pincode.security

import android.content.Context
import th.co.cdgs.mobile.biopin.pincode.security.PFSecurityException

interface IPFSecurityUtils {

    @Throws(PFSecurityException::class)
    fun encode(
        context: Context,
        alias: String,
        input: String,
        isAuthorizationRequared: Boolean
    ): String

    @Throws(PFSecurityException::class)
    fun decode(alias: String, encodedString: String): String

    @Throws(PFSecurityException::class)
    fun isKeystoreContainAlias(alias: String): Boolean

    @Throws(PFSecurityException::class)
    fun deleteKey(alias: String)
}