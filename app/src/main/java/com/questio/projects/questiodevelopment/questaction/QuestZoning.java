package com.questio.projects.questiodevelopment.questaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.questio.projects.questiodevelopment.R;
import com.questio.projects.questiodevelopment.adapters.QuestAdapter;
import com.questio.projects.questiodevelopment.models.QuestObject;
import com.questio.projects.questiodevelopment.models.ZoneObject;

import net.sourceforge.zbar.Symbol;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import zbar.scanner.ZBarConstants;
import zbar.scanner.ZBarScannerActivity;

public class QuestZoning extends ActionBarActivity {
    private final String LOG_TAG = QuestZoning.class.getSimpleName();
    ImageView zonepic;
    ImageView areapic_mini;
    Bitmap bitmap;
    ProgressDialog pDialog;
    ArrayList<QuestObject> questList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_zoning);
        zonepic = (ImageView) findViewById(R.id.zonepic);
        areapic_mini = (ImageView) findViewById(R.id.areapic_mini);
        ZoneObject zoneObject = (ZoneObject) getIntent().getSerializableExtra("z");
        if (zoneObject != null) {
            Toast.makeText(this, zoneObject.getZoneName(), Toast.LENGTH_SHORT).show();


            String map_url = zoneObject.getZonePicPath();
            if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                map_url = map_url.replaceAll("screensize", "drawable-xxhdpi");
            } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
                map_url = map_url.replaceAll("screensize", "drawable-xhdpi");
            } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
                map_url = map_url.replaceAll("screensize", "drawable-hdpi");
            } else {
                map_url = map_url.replaceAll("screensize", "drawable-mdpi");
            }

            new LoadImageZone().execute("http://52.74.64.61" + map_url);
            new LoadImageMiniMap().execute("http://52.74.64.61" + zoneObject.getMiniMap());
            Toast.makeText(this,"http://52.74.64.61" + zoneObject.getMiniMap(),Toast.LENGTH_LONG).show();
        }
//        add for test git
        questList = QuestObject.getAllQuestByZoneId(zoneObject.getZoneId());
        for (QuestObject o : questList) {
            Log.d(LOG_TAG, o.toString());
        }
        QuestAdapter adapter = new QuestAdapter(this, questList);
        ListView listView = (ListView) findViewById(R.id.list_quest);
        listView.setAdapter(adapter);
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

    private class LoadImageZone extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                zonepic.setImageBitmap(image);
            } else {
                Toast.makeText(QuestZoning.this, "ไม่มีรูปในฐานข้อมูล หรือ อินเตอร์เน็ตมีปัญหา", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class LoadImageMiniMap extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                areapic_mini.setImageBitmap(image);
            } else {
                Toast.makeText(QuestZoning.this, "ไม่มีรูปในฐานข้อมูล หรือ อินเตอร์เน็ตมีปัญหา", Toast.LENGTH_SHORT).show();

            }
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
                    Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
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

