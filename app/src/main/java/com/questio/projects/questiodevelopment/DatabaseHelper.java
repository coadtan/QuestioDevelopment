package com.questio.projects.questiodevelopment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(Context c) {
        super(c, "place.db", null, 1);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("DatabaseHelper", "onCreate() is called");
        String query;
        query = "CREATE TABLE places ( placeid INTEGER PRIMARY KEY," +
                " placename TEXT," +
                " placefullname TEXT," +
                " qrcode INTEGER," +
                " sensorid INTEGER," +
                " latitude TEXT," +
                " longitude TEXT," +
                " radius INTEGER)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS users";
        database.execSQL(query);
        onCreate(database);
    }

}
