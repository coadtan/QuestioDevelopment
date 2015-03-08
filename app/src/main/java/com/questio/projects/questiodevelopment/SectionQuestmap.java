package com.questio.projects.questiodevelopment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SectionQuestmap extends Fragment implements LocationListener, GoogleMap.OnCameraChangeListener {
    private GoogleMap googleMap;
    public static final String LOG_TAG = SectionQuestmap.class.getSimpleName();
    Geocoder myLocation;
    DBController controller;
    ProgressDialog prgDialog;
    Cursor cursor;
    HashMap<String, String> queryValues;
    Marker mMarker;
    MapView mMapView;
    View sectionView;
    TextView tv_place_detail;
    TextView tv_place_lat;
    TextView tv_place_lng;
    String currentPlace = "";
    double kmuttLat = 13.651029;
    double kmuttLng = 100.494195;
    double currentLat = 0;
    double currentLng = 0;
    ListAdapter adapter;
    ListView mListView;
    ArrayList<PlaceObject> placeListForDistance;
    private PlaceListAdapter mPlaceListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new DBController(getActivity());
        placeListForDistance = controller.getAllPlaceArrayList();
        setHasOptionsMenu(true);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Sync place data, please wait...");
        prgDialog.setCancelable(false);
        myLocation = new Geocoder(getActivity(), Locale.getDefault());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sectionView = inflater.inflate(R.layout.fragment_section_questmap, container, false);
//begin map
        mMapView = (MapView) sectionView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately
        tv_place_detail = (TextView) sectionView.findViewById(R.id.tv_place_detail);
        tv_place_lat = (TextView) sectionView.findViewById(R.id.tv_place_lat);
        tv_place_lng = (TextView) sectionView.findViewById(R.id.tv_place_lng);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraChangeListener(this);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        Log.d("QuestMap:provider", provider);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }


//        LatLng coordinate = new LatLng(13.651029, 100.494195);
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
        locationManager.requestLocationUpdates(provider, 20000, 0, this);


// end map
// begin data
        cursor = controller.getAllPlacesCursor();

        mPlaceListAdapter = new PlaceListAdapter(getActivity(), cursor, 0);
        mListView = (ListView) sectionView.findViewById(R.id.listview_place);
        mListView.setAdapter(mPlaceListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.placeName);
                Toast.makeText(getActivity(), tv.getText().toString() + " " + position, Toast.LENGTH_SHORT).show();
            }
        });
// end data


        return sectionView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_questmap, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_kmutt_location:
                currentLat = 13.652948;
                currentLng = 100.494281;
                // calculate distance between 2 points
                float[] results = new float[1];
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(kmuttLat, kmuttLng), 16.0f));
                Location.distanceBetween(currentLat, currentLng,
                        kmuttLat, kmuttLng, results);
                Toast.makeText(getActivity(), "" + results[0], Toast.LENGTH_LONG).show();

                return true;
            case R.id.action_sciplanet_location:
                currentLat = 0;
                currentLng = 0;
                return true;
            case R.id.action_nsm_location:
                currentLat = 0;
                currentLng = 0;
                return true;
            case R.id.action_sync_data:
                syncSQLiteMySQLDB();
                return true;
            case R.id.action_delect_all_data:
                delectAllSQLiteRecords();
                return true;
            case R.id.action_qrcode_scan:
                ((MainActivity) getActivity()).launchQRScanner(sectionView);
                return true;
            default:
                break;
        }
        return false;
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
    public void onCameraChange(CameraPosition cameraPosition) {
        //Log.d("onCameraChange","camara changed!");
        float fixZoom = 16.0f;
        if (cameraPosition.zoom != fixZoom) {
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(fixZoom));
        }
    }

// This method call everytime when player's location change.
    @Override
    public void onLocationChanged(Location location) {
        List<Address> myList = null;
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double placeLat = 0;
        double placeLng = 0;
        String placeName = "";
        if (!placeListForDistance.isEmpty()) {
            for (PlaceObject po : placeListForDistance) {
                placeName = po.getPlaceName();
                placeLat = po.getPlaceLat();
                placeLng = po.getPlaceLng();

                isEnterQuestMap(currentLat,currentLng,placeLat,placeLng);
                Log.d(LOG_TAG, placeName + " " + placeLat + " " + placeLng);
            }
        }


        currentLat = lat;
        currentLng = lng;
        try {
            myList = myLocation.getFromLocation(currentLat, currentLng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (myList != null) {
            Address address = myList.get(0);
            currentPlace = address.getAddressLine(0);
        }
        if (mMarker != null) {
            mMarker.remove();
        }
        LatLng coordinate = new LatLng(lat, lng);
        mMarker = googleMap.addMarker(new MarkerOptions().position(coordinate).title("คุณอยู่นี่").snippet("ชื่อตัวละคร"));
        tv_place_detail.setText(currentPlace);
        tv_place_lat.setText("" + lat);
        tv_place_lng.setText("" + lng);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
        Log.d("QuestMap", coordinate + "");
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
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        prgDialog.show();
        client.post("http://128.199.190.130/select_all_place.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                prgDialog.hide();
                Log.d("syncSQLiteMySQLDB", response.toString());
                updateSQLite(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                prgDialog.hide();
                Log.d("syncSQLiteMySQLDB", "status code: " + statusCode);
                if (statusCode == 404) {
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

// Method to delete all data from places tabal
    public void delectAllSQLiteRecords() {
        controller.deleteAllPlace();
    }

    public void updateSQLite(String response) {
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            if (arr.length() != 0) {
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    queryValues.put("placeid", obj.get("placeid").toString());
                    queryValues.put("placename", obj.get("placename").toString());
                    queryValues.put("qrcodeid", obj.get("qrcodeid").toString());
                    queryValues.put("sensorid", obj.get("sensorid").toString());
                    queryValues.put("latitude", obj.get("latitude").toString());
                    queryValues.put("longitude", obj.get("longitude").toString());

                    // Insert Place into SQLite DB
                    controller.insertPlace(queryValues);

                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("id", obj.get("placeid").toString());
                    map.put("status", "1");
                    usersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                //  updateMySQLSyncSts(gson.toJson(usersynclist));
                // Reload the Main Activity
                // reloadActivity();

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

// Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncSts(String json) {
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

// Method to check if player enter QuestMap
    public void isEnterQuestMap(double currentLat, double currentLng, double placeLat, double placeLng){

        float[] results = new float[1];
        Location.distanceBetween(currentLat, currentLng,
                placeLat, placeLng, results);
        if (results[0] < 500) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Entering new Quest Map!")
                    .setMessage("Do you want to begin your Quest now?")
                    .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(),"Quest Map Being Load", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("Later", null)
                    .show();
        }
    }

}





