package com.bangbangcoding.screenmirror.web.ui.studio.multidownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aspsine on 15-4-19.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "dezin.db";
    private static final int DB_VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    void createTable(SQLiteDatabase db) {
        ThreadInfoDao.createTable(db);
    }

    void dropTable(SQLiteDatabase db) {
        ThreadInfoDao.dropTable(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            dropTable(db);
            createTable(db);
        }
    }
}
