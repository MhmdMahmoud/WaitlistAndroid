package com.example.waitlist.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.waitlist.Data.WaitlistContract.WaitlistEntry;

public class WaitlistDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wait.db";
    private static final int DATABASE_VERSION = 1;

    public WaitlistDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE "+WaitlistEntry.TABLE_NAME+"("+
                WaitlistEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                WaitlistEntry.COLUMN_GUEST_NAME + " TEXT NOT NULL,"+
                WaitlistEntry.COLUMN_PARTY_SIZE + " TEXT NOT NULL,"+
                WaitlistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"+
                ");";

        db.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+WaitlistEntry.TABLE_NAME);
        onCreate(db);
    }
}
