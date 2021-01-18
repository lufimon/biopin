package th.co.cdgs.mobile.biopin.settings

import android.content.Context

object PreferencesSettings {
    const val PREF_FILE = "settings_pref"
    const val KEY_CODE = "code"
    const val KEY_IS_CONFIRM = "isConfirm"
    const val KEY_IS_USE_FINGERPRINT = "isUseFingerprint"
    const val DEFAULT_CODE = ""

    fun saveToPref(context: Context, code: String) {
        val sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(KEY_CODE, code)
        editor.apply()
    }

    fun saveToConfirm(context: Context, isConfirm: Boolean){
        val sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_CONFIRM, isConfirm)
        editor.apply()
    }

    fun saveToUseFingerprint(context: Context, isUseFingerprint: Boolean){
        val sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_USE_FINGERPRINT, isUseFingerprint)
        editor.apply()
    }

    fun getCode(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString(
            KEY_CODE,
            DEFAULT_CODE
        )!!
    }

    fun getConfirm(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(
            KEY_IS_CONFIRM,
            false
        )
    }

    fun getUseFingerprint(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(
            KEY_IS_USE_FINGERPRINT,
            false
        )
    }
}