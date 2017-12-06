package com.example.amank.locationtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amank on 05-12-2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "locationsManager";

    private static final String TABLE_LOCATIONS = "locations";

    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_ADDRESS = "address";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIME + " TEXT,"
                + KEY_ADDRESS + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public void addLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, location.getTime());
        values.put(KEY_ADDRESS, location.getAddress());

        db.insert(TABLE_LOCATIONS, null, values);
        db.close();
    }

    public List<Location> getAllContacts(Long startTime, Long endTime) {
        List<Location> locationsList = new ArrayList<Location>();

        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " where " + KEY_TIME + ">=" +startTime + " and " +KEY_TIME + " <= " + endTime;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setTime(cursor.getString(1));
                location.setAddress(cursor.getString(2));
                locationsList.add(location);
            } while (cursor.moveToNext());
        }

        return locationsList;
    }

}
