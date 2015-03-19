package com.questio.projects.questiodevelopment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.questio.projects.questiodevelopment.models.PlaceObject;

import java.util.ArrayList;
import java.util.HashMap;

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


    /**
     * Inserts User into SQLite DB
     *
     * @param queryValues
     */
    public void insertPlace(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("placeid", queryValues.get("placeid"));
        values.put("placename", queryValues.get("placename"));
        values.put("placefullname", queryValues.get("placefullname"));
        values.put("qrcode", queryValues.get("qrcode"));
        values.put("sensorid", queryValues.get("sensorid"));
        values.put("latitude", queryValues.get("latitude"));
        values.put("longitude", queryValues.get("longitude"));
        values.put("radius", queryValues.get("radius"));
        database.insert("places", null, values);
        database.close();
    }

    public void deleteAllPlace() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("places", null, null);
    }

    /**
     * Get list of Place from SQLite DB as Cursor
     *
     * @return
     */

    public Cursor getAllPlacesCursor() {
        Cursor cursor;
        String selectQuery = "SELECT  placeid as _id, placename, placefullname, qrcode, sensorid, latitude, longitude, radius FROM places";
        SQLiteDatabase database = this.getWritableDatabase();
        cursor = database.rawQuery(selectQuery, null);
        return cursor;
    }

    public ArrayList<PlaceObject> getAllPlaceArrayList() {
        ArrayList<PlaceObject> list = new ArrayList<>();
        PlaceObject po;
        String selectQuery = "SELECT  * FROM places";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                po = new PlaceObject();
                po.setPlaceId(Integer.parseInt(cursor.getString(0)));
                po.setPlaceName(cursor.getString(1));
                po.setPlaceLat(Double.parseDouble(cursor.getString(4)));
                po.setPlaceLng(Double.parseDouble(cursor.getString(5)));
                list.add(po);
            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

}
