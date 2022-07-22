package com.bangbangcoding.screenmirror.easybrowser;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.bangbangcoding.screenmirror.easybrowser.common.Const;
import com.bangbangcoding.screenmirror.easybrowser.entity.dao.AppDatabase;
import com.bangbangcoding.screenmirror.easybrowser.utils.SharedPreferencesUtils;

public class EasyApplication extends Application {

    AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        initSetting();
        initDB();
    }

    private void initSetting() {
        SharedPreferences sp = SharedPreferencesUtils.getSettingSP(this);
        if (sp == null) {
            return;
        }
        boolean hasBoot = sp.contains(SharedPreferencesUtils.KEY_FIRST_BOOT);
        SharedPreferences.Editor editor = sp.edit();
        if (hasBoot) {
            editor.putBoolean(SharedPreferencesUtils.KEY_FIRST_BOOT, false);
        } else {
            editor.putBoolean(SharedPreferencesUtils.KEY_FIRST_BOOT, true);
        }
        editor.apply();
    }

    private void initDB() {
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, Const.APP_DATABASE_NAME).build();
    }

    public AppDatabase getAppDatabase() {
        return db;
    }
}
