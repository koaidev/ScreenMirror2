package com.bangbangcoding.screenmirror.web.utils

import android.content.Context
import android.content.SharedPreferences
import com.bangbangcoding.screenmirror.web.KunApplication

class PreferenceUtils {
    private val SEPARATE_CHARACTER: String = " ::: "
    private val PREFS_NAME = "kotlincodes"
    val sharedPref: SharedPreferences = KunApplication.getAppInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun put(KEY_NAME: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, value)
        editor.apply()
    }

    fun put(KEY_NAME: String, value: Long) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putLong(KEY_NAME, value)
        editor.apply()
    }

    fun put(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.apply()
    }

    fun put(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME, status)
        editor.apply()
    }

    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }

    fun getValueInt(KEY_NAME: String): Int {
        return sharedPref.getInt(KEY_NAME, 0)
    }

    fun getValueInt(KEY_NAME: String, def: Int): Int {
        return sharedPref.getInt(KEY_NAME, def)
    }

    fun getValueLong(KEY_NAME: String): Long {
        return sharedPref.getLong(KEY_NAME, 0)
    }

    fun getValueBoolean(KEY_NAME: String): Boolean {
        return sharedPref.getBoolean(KEY_NAME, false)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor =
            sharedPref.edit() //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor.clear()
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }

    fun putRatioVideo(ratio: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(Constants.PREF_RATIO_VIDEO, ratio)
        editor.apply()
    }

    fun getRatioVideo(): Int {
        return sharedPref.getInt(Constants.PREF_RATIO_VIDEO, 0)
    }
}
