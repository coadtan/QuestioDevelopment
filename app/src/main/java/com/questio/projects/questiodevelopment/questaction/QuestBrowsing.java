package com.questio.projects.questiodevelopment.questaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.questio.projects.questiodevelopment.R;
import com.questio.projects.questiodevelopment.adapters.PlaceFeedAdapter;
import com.questio.projects.questiodevelopment.models.PlaceFeedObject;
import com.questio.projects.questiodevelopment.models.PlaceObject;

import net.sourceforge.zbar.Symbol;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import zbar.scanner.ZBarConstants;
import zbar.scanner.ZBarScannerActivity;

public class QuestBrowsing extends ActionBarActivity {
    private final String LOG_TAG = QuestBrowsing.class.getSimpleName();
    TextView placeNameTV;
    TextView placeFullNameTV;
    TextView quest_phone1;
    TextView quest_phone2;
    TextView quest_domain;

    ImageView imageView;
    Bitmap bitmap;
    ProgressDialog pDialog;
    Toolbar toolbar;
    Button questBtnMoreDetail;
    PlaceObject po;
    HashMap mapDetail;
    ArrayList<PlaceFeedObject> placeFeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_browsing);
        initial();
        po = (PlaceObject) getIntent().getSerializableExtra("p");
        mapDetail = po.getPalceDetailJSON(Integer.toString(po.getPlaceId()));

        placeFeed = PlaceFeedObject.getAllPlaceFeedByPlaceId(Integer.toString(po.getPlaceId()));

        PlaceFeedAdapter adapter = new PlaceFeedAdapter(this, placeFeed);
        ListView listView = (ListView) findViewById(R.id.listFeed);
        listView.setAdapter(adapter);

        questBtnMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(QuestBrowsing.this);
                builder1.setTitle("ข้อมูลสถานที่");
                builder1.setMessage(mapDetail.get("placedetail").toString());
                builder1.setCancelable(true);
                builder1.setNeutralButton("ขอบคุณ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder1.show();
            }
        });

        placeNameTV.setText(po.getPlaceName());
        placeFullNameTV.setText(po.getPlacefullname());
        handleNullTextView(quest_phone1, "placecontact1");
        handleNullTextView(quest_phone2, "placecontact2");
        handleNullTextView(quest_domain, "placewebsite");
    }

    public void initial() {
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
        new LoadImage().execute("http://52.74.64.61/floorplan/dummyquest.png");
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

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(QuestBrowsing.this);
            pDialog.setMessage("Loading Image ....");
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
                Toast.makeText(QuestBrowsing.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void handleNullTextView(TextView tv, String mapkey) {
        if (mapDetail.get(mapkey).toString().equalsIgnoreCase("null")) {
            tv.setText("");
        } else {
            tv.setText(mapDetail.get(mapkey).toString());
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

