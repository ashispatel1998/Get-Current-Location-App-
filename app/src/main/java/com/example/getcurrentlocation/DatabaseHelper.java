package com.example.getcurrentlocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "location.db";
    private static final String TABLE_NAME = "location_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "LATITUDE";
    private static final String COL_3 = "LONGITUDE";
    private final Context context;

    public DatabaseHelper(@Nullable Context context) {

        super(context, DATABASE_NAME, null, 1);
         this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE "+ TABLE_NAME +
                     " (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     COL_2 +" TEXT, " +
                     COL_3 +" TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("COL_2", latitude);
        contentValues.put("COL_3", longitude);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor getallData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
}

