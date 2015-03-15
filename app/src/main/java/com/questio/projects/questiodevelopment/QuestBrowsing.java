package com.questio.projects.questiodevelopment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/**
 * Created by coad4u4ever on 15-Mar-15.
 */
public class QuestBrowsing extends FragmentActivity {
    TextView textView4;
    TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_browsing);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView6 = (TextView)findViewById(R.id.textView6);
        textView4.setText(""+ getIntent().getIntExtra("placeId", 0));
        textView6.setText(getIntent().getStringExtra("placeName"));

    }
}
