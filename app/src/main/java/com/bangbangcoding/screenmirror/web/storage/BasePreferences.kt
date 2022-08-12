package com.bangbangcoding.screenmirror.web.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by ThaoBK on 03/01/22.
 */

open class BasePreferences constructor(context: Context, prefName: String) {

    var pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun getString(key: String): String? {
        return pref.getString(key, null)
    }

    fun getString(key: String, default: String): String {
        return pref.getString(key, null) ?: default
    }

    fun setString(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

    @SuppressLint("ApplySharedPref")
    fun setStringImmediately(key: String, value: String) {
        pref.edit().putString(key, value).commit()
    }

    fun getBool(key: String, default: Boolean = false): Boolean {
        return pref.getBoolean(key, default)
    }

    fun setBool(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }

    @SuppressLint("ApplySharedPref")
    fun setBoolImmediately(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).commit()
    }

    fun getInt(key: String, default: Int = 0): Int {
        return pref.getInt(key, default)
    }

    fun setInt(key: String, value: Int) {
        pref.edit().putInt(key, value).apply()
    }

    @SuppressLint("ApplySharedPref")
    fun setIntImmediately(key: String, value: Int) {
        pref.edit().putInt(key, value).commit()
    }

    fun getLong(key: String, default: Long = 0): Long {
        return pref.getLong(key, default)
    }

    fun setLong(key: String, value: Long) {
        pref.edit().putLong(key, value).apply()
    }

    @SuppressLint("ApplySharedPref")
    fun setLongImmediately(key: String, value: Long) {
        pref.edit().putLong(key, value).commit()
    }

    fun remove(key: String) {
        pref.edit().remove(key).apply()
    }

    @SuppressLint("ApplySharedPref")
    fun removeImmediately(key: String) {
        pref.edit().remove(key).commit()
    }

    fun clear() {
        pref.edit().clear().apply()
    }
}
