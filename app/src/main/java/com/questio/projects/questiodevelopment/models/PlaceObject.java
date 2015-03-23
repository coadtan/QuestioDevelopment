package com.questio.projects.questiodevelopment.models;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.questio.projects.questiodevelopment.DatabaseHelper;
import com.questio.projects.questiodevelopment.HttpHelper;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class PlaceObject extends Activity implements Serializable {
    private static final String LOG_TAG = PlaceObject.class.getSimpleName();
    private int placeId;
    private String placeName;
    private String placefullname;
    private String placeQrCode;
    private String placeSensorId;
    private double placeLatitude;
    private double placeLongitude;
    private double placeRadius;

    HashMap<String, String> queryValues;
    DatabaseHelper databaseHelper;
    Context mContext;
    public final String PLACE_ID = "placeid";
    public final String PLACE_NAME = "placename";
    public final String PLACE_FULL_NAME = "placefullname";
    public final String PLACE_QR_CODE = "qrcode";
    public final String PLACE_SENSOR_ID = "sensorid";
    public final String PLACE_LATITUDE = "latitude";
    public final String PLACE_LONGITUDE = "longitude";
    public final String PLACE_RADIUS = "radius";


    public PlaceObject() {

    }

    public PlaceObject(Context c) {
        mContext = c;

    }


    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlacefullname() {
        return placefullname;
    }

    public void setPlacefullname(String placefullname) {
        this.placefullname = placefullname;
    }

    public String getPlaceQrCode() {
        return placeQrCode;
    }

    public void setPlaceQrCode(String placeQrCode) {
        this.placeQrCode = placeQrCode;
    }

    public String getPlaceSensorId() {
        return placeSensorId;
    }

    public void setPlaceSensorId(String placeSensorId) {
        this.placeSensorId = placeSensorId;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public double getPlaceRadius() {
        return placeRadius;
    }

    public void setPlaceRadius(double placeRadius) {
        this.placeRadius = placeRadius;
    }

    public void updatePlaceSQLite() {
        String URL = "http://52.74.64.61/api/select_all_place.php";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                updateSQLite(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(LOG_TAG, "status code: " + statusCode);
            }
        });


    }


    public void updateSQLite(String response) {
        try {
            databaseHelper = new DatabaseHelper(mContext);
            // Extract JSON array from the response
            Log.d(LOG_TAG, response);
            JSONArray arr = new JSONArray(response);
            if (arr.length() != 0) {

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    queryValues = new HashMap<>();
                    queryValues.put(PLACE_ID, obj.get(PLACE_ID).toString());
                    queryValues.put(PLACE_NAME, obj.get(PLACE_NAME).toString());
                    queryValues.put(PLACE_FULL_NAME, obj.get(PLACE_FULL_NAME).toString());
                    queryValues.put(PLACE_QR_CODE, obj.get(PLACE_QR_CODE).toString());
                    queryValues.put(PLACE_SENSOR_ID, obj.get(PLACE_SENSOR_ID).toString());
                    queryValues.put(PLACE_LATITUDE, obj.get(PLACE_LATITUDE).toString());
                    queryValues.put(PLACE_LONGITUDE, obj.get(PLACE_LONGITUDE).toString());
                    queryValues.put(PLACE_RADIUS, obj.get(PLACE_RADIUS).toString());

                    insertPlace(queryValues);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        databaseHelper.close();
    }

    public void delectAllPlace() {
        databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.delete("places", null, null);
        databaseHelper.close();
    }


    public void insertPlace(HashMap<String, String> queryValues) {
        databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
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

    public Cursor getAllPlacesCursor() {
        // 0 placeid as _id
        // 1 placename
        // 2 placefullname
        // 3 qrcode
        // 4 sensorid
        // 5 latitude
        // 6 longitude
        // 7 radius
        Cursor cursor;
        String selectQuery = "SELECT  placeid as _id, placename, placefullname, qrcode, sensorid, latitude, longitude, radius FROM places";
        databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        cursor = database.rawQuery(selectQuery, null);
        return cursor;
    }


    public ArrayList<PlaceObject> getAllPlaceArrayList() {
        ArrayList<PlaceObject> list = new ArrayList<>();
        PlaceObject po;
        String selectQuery = "SELECT  * FROM places";
        databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                po = new PlaceObject();
                po.setPlaceId(Integer.parseInt(cursor.getString(0)));
                po.setPlaceName(cursor.getString(1));
                po.setPlacefullname(cursor.getString(2));
                po.setPlaceQrCode(cursor.getString(3));
                po.setPlaceSensorId(cursor.getString(4));
                po.setPlaceLatitude(Double.parseDouble(cursor.getString(5)));
                po.setPlaceLongitude(Double.parseDouble(cursor.getString(6)));
                po.setPlaceRadius(Double.parseDouble(cursor.getString(7)));
                list.add(po);
            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

    public HashMap getPalceDetailJSON(String id) {
        JSONObject json;
        HashMap hashMap = new HashMap();

        try {
            String response = new HttpHelper().execute("http://52.74.64.61/api/select_all_placedetails_by_placeid.php?placeid=" + id).get();
            JSONArray arr = new JSONArray(response);
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    hashMap.put("placeid", obj.get("placeid").toString());
                    hashMap.put("placedetail", obj.get("placedetails").toString());
                    hashMap.put("placecontact1", obj.get("phonecontact1").toString());
                    hashMap.put("placecontact2", obj.get("phonecontact2").toString());
                    hashMap.put("placewebsite", obj.get("website").toString());
                    hashMap.put("placepicpath", obj.get("placelogopath").toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return hashMap;
    }



}


