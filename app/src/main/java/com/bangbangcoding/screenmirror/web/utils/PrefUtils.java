package com.bangbangcoding.screenmirror.web.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class PrefUtils {

    public static final String PREF_EXTERNAL_FILES_DIR = "f_dir";
    public static final String PREF_NOTIFICATIONS = "notification_show";
    private static final String PREF_WIFI = "wifi_download";
    public static final String PREF_BADGE_VALUE = "badge_download";
    public static final String PREF_BADGE_VALUE_FINISH = "badge_finish" ;
    public static final String PREF_TAB_QUANTITY = "PREF_TAB_QUANTITY" ;
    public static final String PREF_TAB_QUANTITY_PRIV = "PREF_TAB_QUANTITY_PRIV" ;
    public static final String PREFERENCE = "PREFERENCE";
    public static final String PREF_PATH_DOMAIN = "PREF_PATH_DOMAIN";
    public static final String PREFERENCE_USERID = "PREFERENCE_USERID";
    public static final String PREFERENCE_SESSIONID = "PREFERENCE_SESSIONID";


    public static void setShouldShowNotifications(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_NOTIFICATIONS, value).apply();
    }

    public static boolean shouldShowNotifications(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_NOTIFICATIONS, true);
    }

    public static void setWifiDownload(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_WIFI, value).apply();
    }

    public static boolean getWifiDownload(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_WIFI, false);
    }

    public static String getExternalFilesDirName(Context context) {
//        File downloadDir = new File(PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_EXTERNAL_FILES_DIR, new File(Environment.getExternalStorageDirectory(), Constants.PATH_VIDEO).getAbsolutePath()));
//        File downloadDir = new File(Environment.getExternalStorageDirectory(), Constants.PATH_VIDEO);
        File downloadDir =  new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + Constants.PATH_VIDEO);
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        }
        return downloadDir.getAbsolutePath();
    }

    public static void setExternalFilesDirName(Context context, String dirName) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_EXTERNAL_FILES_DIR, dirName).apply();
    }


    public static void putBoolean(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public static void putString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences mPrefs = context.getSharedPreferences("MY_SHARED", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putLong(key, value);
        prefsEditor.apply();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences mPrefs = context.getSharedPreferences("MY_SHARED", Context.MODE_PRIVATE);
        return mPrefs.getLong(key, 0);
    }


    public static void putInt(Context context, String key, int value) {
        SharedPreferences mPrefs = context.getSharedPreferences("MY_SHARED", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences mPrefs = context.getSharedPreferences("MY_SHARED", Context.MODE_PRIVATE);
        return mPrefs.getInt(key, 0);
    }

    public static void saveListPathDomain(Context context, ArrayList<String> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(PREF_PATH_DOMAIN, json);
        editor.apply();
    }

    public static ArrayList<String> getListPathDomain(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(PREF_PATH_DOMAIN, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public static void saveListPath(Context context, ArrayList<String> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("gs_videos_path", json);
        editor.apply();
    }

    public static ArrayList<String> getListPath(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("gs_videos_path", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public static void setOldPath(Context context, String dirName) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("OLD_PATH", dirName).apply();
    }

    public static String getOldPath(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("OLD_PATH", new File(Environment.getExternalStorageDirectory(), "DownloadVideo2019Pro").getAbsolutePath());
    }



    public static void setDownloadBadgeValue(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_BADGE_VALUE, value).apply();
    }

    public static int getDownloadBadgeValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_BADGE_VALUE, 0);
    }

    public static void setDownloadBadgeValueFinish(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_BADGE_VALUE_FINISH, value).apply();
    }

    public static int getDownloadBadgeValueFinish(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_BADGE_VALUE_FINISH, 0);
    }
}
