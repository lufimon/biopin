package th.co.cdgs.mobile.biopin.settings

import android.content.Context

object PreferencesSettings {
    const val PREF_FILE = "settings_pref"
    const val KEY_CODE = "code"
    const val DEFAULT_CODE = ""

    fun saveToPref(context: Context, code: String) {
        val sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(KEY_CODE, code)
        editor.apply()
    }

    fun getCode(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString(
            KEY_CODE,
            DEFAULT_CODE
        )!!
    }
}