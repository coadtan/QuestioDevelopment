package com.questio.projects.questiodevelopment.sections;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.questio.projects.questiodevelopment.MainActivity;
import com.questio.projects.questiodevelopment.PlaceListAdapter;
import com.questio.projects.questiodevelopment.PlaceObject;
import com.questio.projects.questiodevelopment.QuestBrowsing;
import com.questio.projects.questiodevelopment.R;
import com.questio.projects.questiodevelopment.data.DBController;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import AndroidGoogleDirectionAndPlaceLibrary.GoogleDirection;


public class SectionQuestmap extends Fragment implements LocationListener, GoogleMap.OnCameraChangeListener {
    public static final String LOG_TAG = SectionQuestmap.class.getSimpleName();
    // location attribute
    GoogleMap googleMap;
    LocationManager locationManager;
    Location location;
    Geocoder myLocation;
    String provider;
    //  end location attribute
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
    PlaceListAdapter mPlaceListAdapter;
    List<Address> placeList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new DBController(getActivity());
        placeListForDistance = controller.getAllPlaceArrayList();


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);
        Log.d(LOG_TAG, provider);
        location = locationManager.getLastKnownLocation(provider);
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();

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
        tv_place_detail = (TextView) sectionView.findViewById(R.id.tv_place_detail);
        tv_place_lat = (TextView) sectionView.findViewById(R.id.tv_place_lat);
        tv_place_lng = (TextView) sectionView.findViewById(R.id.tv_place_lng);
        mMapView = (MapView) sectionView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
        LatLng coordinate = new LatLng(currentLat, currentLng);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraChangeListener(this);


        if (location != null) {
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 10000, 0, this);
// end map
// begin place list

        cursor = controller.getAllPlacesCursor();

        if( cursor != null && cursor.moveToFirst() ){
        }


        mPlaceListAdapter = new PlaceListAdapter(getActivity(), cursor, 0);
        mListView = (ListView) sectionView.findViewById(R.id.listview_place);
        mListView.setAdapter(mPlaceListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvLat = (TextView) view.findViewById(R.id.placeLat);
                TextView tvLng = (TextView) view.findViewById(R.id.placeLng);
                LatLng fromPosition = new LatLng(currentLat, currentLng);
                LatLng toPosition = new LatLng(Double.parseDouble(tvLat.getText().toString()), Double.parseDouble(tvLng.getText().toString()));
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(toPosition).title("Destination"));

                GoogleDirection gd = new GoogleDirection(getActivity());
                gd.request(fromPosition, toPosition, GoogleDirection.MODE_DRIVING);
                gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
                    public void onResponse(String status, Document doc, GoogleDirection gd) {
                        googleMap.addPolyline(gd.getPolyline(doc, 3, Color.BLUE));
                    }
                });
            }
        });
// end place list


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
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.651029, 100.494195), 16.0f));
                isEnterQuestMap(currentLat, currentLng,13.651029, 100.494195,1, "kmutt");
                return true;

            case R.id.action_sciplanet_location:
                currentLat = 13.720424;
                currentLng = 100.583359;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.720176, 100.583163), 16.0f));
                isEnterQuestMap(currentLat, currentLng,13.720176, 100.583163,2, "sciplanet");
                return true;

            case R.id.action_nsm_location:
                currentLat = 14.048520;
                currentLng = 100.716716;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(14.048624, 100.717188), 16.0f));
                isEnterQuestMap(currentLat, currentLng,14.048624, 100.717188,3, "nsm");
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
//        float fixZoomMin = 16.0f;
//        if (cameraPosition.zoom  < fixZoomMin) {
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(fixZoomMin));
//        }

    }

    // This method call everytime when player's location change.
    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "LocationChanged called!");
        List<Address> myList = null;
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();

        double placeLat = 0;
        double placeLng = 0;
        String placeName = "";
        int placeId = 0;
        if (!placeListForDistance.isEmpty()) {
            for (PlaceObject po : placeListForDistance) {
                placeId = po.getPlaceId();
                placeName = po.getPlaceName();
                placeLat = po.getPlaceLat();
                placeLng = po.getPlaceLng();

                isEnterQuestMap(currentLat, currentLng, placeLat, placeLng,placeId, placeName);
                Log.d(LOG_TAG, placeName + " " + placeLat + " " + placeLng);
            }
        }

        try {
            myList = myLocation.getFromLocation(currentLat, currentLng, 1);
            if (myList != null) {
                Address address = myList.get(0);
                currentPlace = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mMarker != null) {
            mMarker.remove();
        }
        LatLng coordinate = new LatLng(currentLat, currentLng);
        mMarker = googleMap.addMarker(new MarkerOptions().position(coordinate).title("คุณอยู่นี่").snippet("ชื่อตัวละคร").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        tv_place_detail.setText("" + currentPlace);
        tv_place_lat.setText("" + currentLat);
        tv_place_lng.setText("" + currentLng);
        Log.d(LOG_TAG, coordinate + "");
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
                updateSQLite(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                prgDialog.hide();
                Log.d(LOG_TAG, "status code: " + statusCode);
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
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            if (arr.length() != 0) {

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    queryValues = new HashMap<String, String>();
                    queryValues.put("placeid", obj.get("placeid").toString());
                    queryValues.put("placename", obj.get("placename").toString());
                    queryValues.put("qrcodeid", obj.get("qrcodeid").toString());
                    queryValues.put("sensorid", obj.get("sensorid").toString());
                    queryValues.put("latitude", obj.get("latitude").toString());
                    queryValues.put("longitude", obj.get("longitude").toString());
                    controller.insertPlace(queryValues);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Method to check if player enter QuestMap
    public void isEnterQuestMap(double currentLat, double currentLng, double placeLat, double placeLng, final int placeId, final String placeName) {

        float[] results = new float[1];
        Location.distanceBetween(currentLat, currentLng,
                placeLat, placeLng, results);
        if (results[0] < 500) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("เข้าสู่ "+ placeName +"!")
                    .setMessage("จะเริ่มทำภารกิจในที่แห่งนี้เลยไหมครับ")
                    .setPositiveButton("เอาเลย!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), QuestBrowsing.class);
                            intent.putExtra("placeId", placeId);
                            intent.putExtra("placeName", placeName);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("ไม่", null)
                    .show();
        }
    }


}





