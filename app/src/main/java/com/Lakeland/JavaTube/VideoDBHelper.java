package com.Lakeland.JavaTube;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VideoDBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "myvideos.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation SQL statement
    private static final String CREATE_TABLE_VIDEO =
            "CREATE TABLE video(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "videoTitle text not null, videoLength float,"
                    + "videoAuthor text not null, videoLink text not null, timeStamp text not null);";

    public VideoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_VIDEO);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(VideoDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
    }
}
