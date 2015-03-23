package com.questio.projects.questiodevelopment.models;

import com.questio.projects.questiodevelopment.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by coad4u4ever on 22-Mar-15.
 */
public class PlaceFeedObject {

    private int feedid;
    private String feedheader;
    private String feeddetails;
    private String datestarted;

    public int getFeedid() {
        return feedid;
    }

    public void setFeedid(int feedid) {
        this.feedid = feedid;
    }

    public String getFeedheader() {
        return feedheader;
    }

    public void setFeedheader(String feedheader) {
        this.feedheader = feedheader;
    }

    public String getFeeddetails() {
        return feeddetails;
    }

    public void setFeeddetails(String feeddetails) {
        this.feeddetails = feeddetails;
    }

    public String getDatestarted() {
        return datestarted;
    }

    public void setDatestarted(String datestarted) {
        this.datestarted = datestarted;
    }

    @Override
    public String toString() {
        return "PlaceFeedObject{" +
                "feedid=" + feedid +
                ", feedheader='" + feedheader + '\'' +
                ", feeddetails='" + feeddetails + '\'' +
                ", datestarted='" + datestarted + '\'' +
                '}';
    }

    public static ArrayList<PlaceFeedObject> getAllPlaceFeedByPlaceId(final String id) {
        ArrayList al = null;
        PlaceFeedObject pfo = null;
        final String URL = "http://52.74.64.61/api/select_all_placefeed_by_placeid.php?placeid="+id;
        try {
            String response = new HttpHelper().execute(URL).get();
            JSONArray arr = new JSONArray(response);
            if (arr.length() != 0) {
                al = new ArrayList();
                for (int i = 0; i < arr.length(); i++) {
                    pfo = new PlaceFeedObject();
                    JSONObject obj = (JSONObject) arr.get(i);
                    pfo.setFeedid(Integer.parseInt(obj.get("feedid").toString()));
                    pfo.setFeedheader(obj.get("feedheader").toString());
                    pfo.setFeeddetails(obj.get("feeddetails").toString());
                    pfo.setDatestarted(obj.get("datestarted").toString());
                    al.add(pfo);
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return al;
    }
}
