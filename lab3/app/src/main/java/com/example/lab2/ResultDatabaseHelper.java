package com.example.lab2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ResultDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "results.db";
    private static final int DB_VERSION = 1;

    public ResultDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE results (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS results");
        onCreate(db);
    }

    public void insertResult(String text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("text", text);
        db.insert("results", null, values);
    }

    public List<String> getAllResults() {
        List<String> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("results", new String[]{"text"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            results.add(cursor.getString(0));
        }
        cursor.close();
        return results;
    }
}
