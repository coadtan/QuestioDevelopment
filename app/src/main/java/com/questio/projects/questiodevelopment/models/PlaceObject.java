package com.questio.projects.questiodevelopment.models;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.questio.projects.questiodevelopment.DatabaseHelper;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PlaceObject extends Activity{
    private static final String LOG_TAG = PlaceObject.class.getSimpleName();
    private int placeId;
    private String placeName;
    private double placeLat;
    private double placeLng;
    HashMap<String, String> queryValues;
    DatabaseHelper databaseHelper;
    Context mContext;
    private final String URL = "http://52.74.64.61/api/select_all_place.php";
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

    public double getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(double placeLat) {
        this.placeLat = placeLat;
    }

    public double getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(double placeLng) {
        this.placeLng = placeLng;
    }

    public void updatePlaceSQLite() {
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
            Log.d(LOG_TAG,response);
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

                    databaseHelper.insertPlace(queryValues);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        databaseHelper.close();
    }

    public void delectAllPlace() {
        databaseHelper = new DatabaseHelper(mContext);
        databaseHelper.deleteAllPlace();
        databaseHelper.close();
    }



}
