package com.questio.projects.questiodevelopment.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.questio.projects.questiodevelopment.PlaceObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DBController extends SQLiteOpenHelper {

    public DBController(Context c) {
        super(c, "place.db", null, 1);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("DBController", "onCreate() is called");
        String query;
        query = "CREATE TABLE places ( placeid INTEGER PRIMARY KEY," +
                " placename TEXT," +
                " qrcodeid INTEGER," +
                " sensorid INTEGER," +
                " latitude REAL," +
                " longitude REAL)";
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
        values.put("qrcodeid", queryValues.get("qrcodeid"));
        values.put("sensorid", queryValues.get("sensorid"));
        values.put("latitude", queryValues.get("latitude"));
        values.put("longitude", queryValues.get("longitude"));
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
        String selectQuery = "SELECT  placeid as _id, placename, qrcodeid, sensorid, latitude,longitude FROM places";
        SQLiteDatabase database = this.getWritableDatabase();
        cursor = database.rawQuery(selectQuery, null);
        // database.close();
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
