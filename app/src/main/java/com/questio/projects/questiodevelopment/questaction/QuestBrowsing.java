package com.questio.projects.questiodevelopment.questaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.questio.projects.questiodevelopment.QuestioUtility;
import com.questio.projects.questiodevelopment.R;
import com.questio.projects.questiodevelopment.adapters.PlaceFeedAdapter;
import com.questio.projects.questiodevelopment.models.PlaceFeedObject;
import com.questio.projects.questiodevelopment.models.PlaceObject;
import com.questio.projects.questiodevelopment.models.ZoneObject;

import net.sourceforge.zbar.Symbol;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import zbar.scanner.ZBarConstants;
import zbar.scanner.ZBarScannerActivity;

public class QuestBrowsing extends ActionBarActivity implements View.OnClickListener{
    private final String LOG_TAG = QuestBrowsing.class.getSimpleName();
    TextView placeNameTV;
    TextView placeFullNameTV;
    TextView quest_phone1;
    TextView quest_phone2;
    TextView quest_domain;
    Button quest_btn_scan_qr;
    ImageView imageView;
    Bitmap bitmap;
    ProgressDialog pDialog;
    Toolbar toolbar;
    Button questBtnMoreDetail;
    PlaceObject po;
    HashMap placeDetail;
    ArrayList<PlaceFeedObject> placeFeed;
    String qrScanValue;
    ArrayList<ZoneObject> zoneList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_browsing);
        initial();
        placeFeed = PlaceFeedObject.getAllPlaceFeedByPlaceId(Integer.toString(po.getPlaceId()));
        zoneList = ZoneObject.getAllZoneByPlaceId(po.getPlaceId());
        PlaceFeedAdapter adapter = new PlaceFeedAdapter(this, placeFeed);
        ListView listView = (ListView) findViewById(R.id.listFeed);
        listView.setAdapter(adapter);

        questBtnMoreDetail.setOnClickListener(this);
        quest_btn_scan_qr.setOnClickListener(this);

        placeNameTV.setText(po.getPlaceName());
        placeFullNameTV.setText(po.getPlacefullname());
        handleNullTextView(quest_phone1, "placecontact1");
        handleNullTextView(quest_phone2, "placecontact2");
        handleNullTextView(quest_domain, "placewebsite");
    }



    public void initial() {
        po = (PlaceObject) getIntent().getSerializableExtra("p");
        placeDetail = po.getPalceDetailJSON(Integer.toString(po.getPlaceId()));
        quest_btn_scan_qr = (Button)findViewById(R.id.quest_btn_scan_qr);
        imageView = (ImageView) findViewById(R.id.questImgPlace);
        placeNameTV = (TextView) findViewById(R.id.questPlaceName);
        placeFullNameTV = (TextView) findViewById(R.id.questPlaceFullName);
        quest_phone1 = (TextView) findViewById(R.id.quest_phone1);
        quest_phone2 = (TextView) findViewById(R.id.quest_phone2);
        quest_domain = (TextView) findViewById(R.id.quest_domain);


        questBtnMoreDetail = (Button) findViewById(R.id.questBtnMoreDetail);
        toolbar = (Toolbar) findViewById(R.id.questAppBar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        //Determine screen size
        String placepicpath = placeDetail.get("placepicpath").toString();
         if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
             placepicpath = placepicpath.replaceAll("screensize","drawable-xxhdpi");
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
             placepicpath = placepicpath.replaceAll("screensize","drawable-xhdpi");
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
             placepicpath = placepicpath.replaceAll("screensize","drawable-hdpi");
        }
        else {
             placepicpath = placepicpath.replaceAll("screensize","drawable-mdpi");
        }

        Log.d(LOG_TAG,"path: "+ "http://52.74.64.61"+ placepicpath);
        new LoadImage().execute("http://52.74.64.61"+ placepicpath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_questaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_qrcode_scan:
                launchQRScanner();
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int elementClicked = v.getId();
        switch (elementClicked){
            case R.id.questBtnMoreDetail :
                AlertDialog.Builder builder1 = new AlertDialog.Builder(QuestBrowsing.this);
                builder1.setTitle("ข้อมูลสถานที่");
                builder1.setMessage(placeDetail.get("placedetail").toString());
                builder1.setCancelable(true);
                builder1.setNeutralButton("ขอบคุณ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder1.show();
                break;
            case R.id.quest_btn_scan_qr  :
                launchQRScanner();
                break;
            default:
                break;
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(QuestBrowsing.this);
            pDialog.setMessage("โหลดรูปสักครู่ครับ ....");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                imageView.setImageBitmap(image);
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(QuestBrowsing.this, "ไม่มีรูปในฐานข้อมูล หรือ อินเตอร์เน็ตมีปัญหา", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void handleNullTextView(TextView tv, String mapkey) {
        if (placeDetail.get(mapkey).toString().equalsIgnoreCase("null")) {
            tv.setText("");
        } else {
            tv.setText(placeDetail.get(mapkey).toString());
        }
    }
    //QR Scan

    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;


    public void launchQRScanner() {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, ZBarScannerActivity.class);
            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_SCANNER_REQUEST:
            case ZBAR_QR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    qrScanValue = data.getStringExtra(ZBarConstants.SCAN_RESULT);
                    int check = QuestioUtility.checkValidQRCode(QuestioUtility.getDeQRCode(qrScanValue),zoneList);
                    if(check == -1){
                    }else{
                        Intent intent = new Intent(this, QuestZoning.class);
                        intent.putExtra("z", zoneList.get(check));
                        startActivity(intent);
                    }

                } else if (resultCode == RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                    if (!TextUtils.isEmpty(error)) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
    //QR Scan end
}

