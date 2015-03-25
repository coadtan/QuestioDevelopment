package com.questio.projects.questiodevelopment.models;

import android.util.Log;

import com.questio.projects.questiodevelopment.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class QuestObject {
    private static final String LOG_TAG = QuestObject.class.getSimpleName();
    private int questId;
    private String questName;
    private String questType;
    private int zoneId;
    private int weight;
    private int rewardid;

    // <editor-fold defaultstate="collapsed" desc="getter setter toString. Click on the + sign on the left to edit the code.">

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public String getQuestName() {
        return questName;
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRewardid() {
        return rewardid;
    }

    public void setRewardid(int rewardid) {
        this.rewardid = rewardid;
    }

    @Override
    public String toString() {
        return "QuestObject{" +
                "questId=" + questId +
                ", questName='" + questName + '\'' +
                ", questType='" + questType + '\'' +
                ", zoneId=" + zoneId +
                ", weight=" + weight +
                ", rewardid=" + rewardid +
                '}';
    }
    //</editor-fold>

    public static ArrayList<QuestObject> getAllQuestByZoneId(int zoneId){
        ArrayList<QuestObject> al = null;
        QuestObject qo;
        final String URL = "http://52.74.64.61/api/select_all_quest_by_zoneid.php?zoneid="+zoneId;
        try {
            String response = new HttpHelper().execute(URL).get();
            Log.d(LOG_TAG,"response: "+response);
            JSONArray arr = new JSONArray(response);
            if (arr.length() != 0) {
                al = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    qo = new QuestObject();
                    JSONObject obj = (JSONObject) arr.get(i);
                    qo.setQuestId(Integer.parseInt(obj.get("questid").toString()));
                    qo.setQuestName(obj.get("questname").toString());
                    qo.setQuestType(obj.get("questtype").toString());
                    qo.setRewardid(Integer.parseInt(obj.get("rewardid").toString()));
                    qo.setWeight(Integer.parseInt(obj.get("weight").toString()));
                    qo.setZoneId(Integer.parseInt(obj.get("zoneid").toString()));
                    al.add(qo);
                }

            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }


        return al;
    }
}
