package com.questio.projects.questiodevelopment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.questio.projects.questiodevelopment.R;
import com.questio.projects.questiodevelopment.models.QuestObject;

import java.util.ArrayList;

/**
 * Created by coad4u4ever on 22-Mar-15.
 */
public class QuestAdapter extends ArrayAdapter<QuestObject> {
    public static final String LOG_TAG = QuestAdapter.class.getSimpleName();

    private static class ViewHolder {
        private ImageView quest_status;
        private TextView questId;
        private TextView questName;
        private TextView questType;
        private TextView questLevel;
        private TextView questReward;

        public ViewHolder(View view) {
            quest_status = (ImageView) view.findViewById(R.id.quest_status);
            questId = (TextView) view.findViewById(R.id.questId);
            questName = (TextView) view.findViewById(R.id.questName);
            questType = (TextView) view.findViewById(R.id.questType);
            questLevel = (TextView) view.findViewById(R.id.questLevel);
            questReward = (TextView) view.findViewById(R.id.questReward);

        }
    }

    public QuestAdapter(Context context, ArrayList<QuestObject> quest) {
        super(context, 0, quest);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        QuestObject items = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_quest, parent, false);
        }
        // Lookup view for data population
        ViewHolder viewHolder = new ViewHolder(convertView);
        // Populate the data into the template view using the data object
        viewHolder.questId.setText(Integer.toString(items.getQuestId()));
        viewHolder.questName.setText(items.getQuestName());
        viewHolder.quest_status.setImageResource(R.drawable.ic_quest_unfinish);
        String type = items.getQuestType();
        if(type.equalsIgnoreCase("q")){
            type = "Quiz";
        }else if(type.equalsIgnoreCase("e")){
            type = "Explorer";
        }else if(type.equalsIgnoreCase("p")){
            type = "Picture Clue";
        }else if(type.equalsIgnoreCase("c")){
            type = "Clue";
        }
        viewHolder.questType.setText(type);


        String levelStar;
        switch (items.getWeight()) {
            case 1:
                levelStar = "*";
                break;
            case 2:
                levelStar = "**";
                break;
            case 3:
                levelStar = "***";
                break;
            default:
                levelStar = "*";

        }
        viewHolder.questLevel.setText(levelStar);
        viewHolder.questReward.setText(Integer.toString(items.getRewardid()));
        // Return the completed view to render on screen
        return convertView;
    }
}


