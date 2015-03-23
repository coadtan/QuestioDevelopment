package com.questio.projects.questiodevelopment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.questio.projects.questiodevelopment.R;
import com.questio.projects.questiodevelopment.models.PlaceFeedObject;

import java.util.ArrayList;

/**
 * Created by coad4u4ever on 22-Mar-15.
 */
public class PlaceFeedAdapter extends ArrayAdapter<PlaceFeedObject> {
    public static final String LOG_TAG = PlaceFeedAdapter.class.getSimpleName();

    private static class ViewHolder {
        private TextView feedId;
        private TextView feed_date;
        private TextView feed_header;
        private TextView feed_content;

        public ViewHolder(View view) {

            feedId = (TextView) view.findViewById(R.id.feedId);
            feed_date = (TextView) view.findViewById(R.id.feed_date);
            feed_header = (TextView) view.findViewById(R.id.feed_header);
            feed_content = (TextView) view.findViewById(R.id.feed_content);

        }
    }

    public PlaceFeedAdapter(Context context, ArrayList<PlaceFeedObject> feed) {
        super(context, 0, feed);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PlaceFeedObject items = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_feed, parent, false);
        }
        // Lookup view for data population
        ViewHolder viewHolder = new ViewHolder(convertView);
        // Populate the data into the template view using the data object
        viewHolder.feedId.setText(Integer.toString(items.getFeedid()));
        viewHolder.feed_date.setText(items.getDatestarted());
        viewHolder.feed_header.setText(items.getFeedheader());
        viewHolder.feed_content.setText(items.getFeeddetails());
        // Return the completed view to render on screen
        return convertView;
    }
}

