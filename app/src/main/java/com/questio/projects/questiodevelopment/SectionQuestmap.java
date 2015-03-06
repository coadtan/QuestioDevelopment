package com.questio.projects.questiodevelopment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.questio.projects.questiodevelopment.data.DBController;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//
///**
// * Created by CHAKRIT on 16/2/2558.
// */
public class SectionQuestmap extends Fragment implements LocationListener {
    DBController controller;
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;
    Marker mMarker;
    MapView mMapView;
    private GoogleMap googleMap;
    TextView tv_place_detail;
    TextView tv_place_latlng;
    Button btn_kmutt_location;
    Button btn_sciplanet_location;
    Button btn_nsm_location;
    View v;
    double currentLat;
    double currentLng;
    double kmuttLat = 13.651029;
    double kmuttLng = 100.494195;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        this.v = inflater.inflate(R.layout.fragment_section_questmap, container,
                false);
        initializeView();

        //  begin data
        controller = new DBController(getActivity());
        ArrayList<HashMap<String, String>> placeList = controller.getAllPlaces();
        if (placeList.size() != 0) {
            // Set the User Array list in ListView
            ListAdapter adapter = new SimpleAdapter(getActivity(), placeList, R.layout.view_place_entry, new String[]{
                    "placeid", "placename", "latitude", "longitude"}, new int[]
                    {R.id.placeId, R.id.placeName, R.id.placeLat, R.id.placeLng});
            ListView myList = (ListView) v.findViewById(android.R.id.list);
            myList.setAdapter(adapter);
        }
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");
        prgDialog.setCancelable(false);

        // end data
        btn_kmutt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLat = 13.652948;
                currentLng = 100.494281;
                // calculate distance between 2 points
                float[] results = new float[1];
                Location.distanceBetween(currentLat, currentLng,
                        kmuttLat, kmuttLng, results);
                Toast.makeText(getActivity(), "" + results[0], Toast.LENGTH_LONG).show();
            }
        });
        btn_sciplanet_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLat = 0;
                currentLng = 0;
            }
        });
        btn_nsm_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncSQLiteMySQLDB();

            }
        });

        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately
        tv_place_detail = (TextView) v.findViewById(R.id.tv_place_detail);
        tv_place_latlng = (TextView) v.findViewById(R.id.tv_place_latlng);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        Log.d("QuestMap:provider", provider);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }


        LatLng coordinate = new LatLng(13.651029, 100.494195);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
        locationManager.requestLocationUpdates(provider, 20000, 0, this);


        // Perform any camera updates here
        return v;
    }

    public void initializeView() {

        btn_kmutt_location = (Button) v.findViewById(R.id.btn_kmutt_location);
        btn_sciplanet_location = (Button) v.findViewById(R.id.btn_sciplanet_location);
        btn_nsm_location = (Button) v.findViewById(R.id.btn_nsm_location);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(getActivity(), "ตำแหน่งล่าสุดเปลี่ยนแปลง", Toast.LENGTH_LONG).show();
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        tv_place_detail.setText("unknown");
//        tv_place_latlng.setText("lat: " +lat + " long: " + lng);
//        if (mMarker != null) {
//            mMarker.remove();
//        }
//        LatLng coordinate = new LatLng(lat, lng);
//        mMarker = googleMap.addMarker(new MarkerOptions().position(coordinate).title("คุณอยู่นี่").snippet("ชื่อตัวละคร"));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
//        Log.d("QuestMap", coordinate + "");
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    // Method to Sync MySQL to SQLite DB
    public void syncSQLiteMySQLDB() {
        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        // prgDialog.show();
        // Make Http call to getusers.php
        //client.post("http://192.168.2.4:9000/mysqlsqlitesync/getusers.php", params, new AsyncHttpResponseHandler() {
//        client.post("http://128.199.190.130/select_all_place.php", params, new AsyncHttpResponseHandler() {
//
//
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                // Hide ProgressBar
//                prgDialog.hide();
//                // Update SQLite DB with response sent by getusers.php
//                Log.d("syncSQLiteMySQLDB", Arrays.toString(bytes));
//                updateSQLite(bytes.toString());
//            }
//
//            // When error occured
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                // Hide ProgressBar
//                prgDialog.hide();
//                Log.d("syncSQLiteMySQLDB", "status code: " + i);
//                if (i == 404) {
//                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
//                } else if (i == 500) {
//                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getActivity(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        client.post("http://128.199.190.130/select_all_place.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                Log.d("syncSQLiteMySQLDB", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                int id = 0;
                String loc = "abc";
                for (int i = 0; i < response.length(); ++i) {
                    JSONObject rec = null;
                    try {
                        rec = response.getJSONObject(i);
                        id = rec.getInt("placeid");
                        loc = rec.getString("placename");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("syncSQLiteMySQLDB", "id: " + id + " name: " + loc);
                }

            }
        });


    }

    public void updateSQLite(String response) {
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if (arr.length() != 0) {
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("userId"));
                    System.out.println(obj.get("userName"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add userID extracted from Object
                    queryValues.put("userId", obj.get("userId").toString());
                    // Add userName extracted from Object
                    queryValues.put("userName", obj.get("userName").toString());
                    // Insert User into SQLite DB
                    controller.insertPlace(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("userId").toString());
                    map.put("status", "1");
                    usersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                //  updateMySQLSyncSts(gson.toJson(usersynclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncSts(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        client.post("http://192.168.2.4:9000/mysqlsqlitesync/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Toast.makeText(getActivity(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reloadActivity() {
        Intent objIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(objIntent);
    }
}





