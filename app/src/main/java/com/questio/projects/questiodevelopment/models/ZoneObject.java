package com.questio.projects.questiodevelopment.models;

import android.util.Log;

import com.questio.projects.questiodevelopment.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ZoneObject implements Serializable {
    private static final String LOG_TAG = ZoneObject.class.getSimpleName();
    private int zoneId;
    private int areaId;
    private String zoneName;
    private String zoneDetails;
    private String qrCode;
    private String sensorId;
    private double latitude;
    private double longitude;
    private String zonePicPath;
    private String miniMap;
    private String itemSet;

    // <editor-fold defaultstate="collapsed" desc="getter setter toString. Click on the + sign on the left to edit the code.">

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getZoneDetails() {
        return zoneDetails;
    }

    public void setZoneDetails(String zoneDetails) {
        this.zoneDetails = zoneDetails;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getZonePicPath() {
        return zonePicPath;
    }

    public void setZonePicPath(String zonePicPath) {
        this.zonePicPath = zonePicPath;
    }

    public String getMiniMap() {
        return miniMap;
    }

    public void setMiniMap(String miniMap) {
        this.miniMap = miniMap;
    }

    public String getItemSet() {
        return itemSet;
    }

    public void setItemSet(String itemSet) {
        this.itemSet = itemSet;
    }

    @Override
    public String toString() {
        return "ZoneObject{" +
                "zoneId=" + zoneId +
                ", areaId=" + areaId +
                ", zoneName='" + zoneName + '\'' +
                ", zoneDetails='" + zoneDetails + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", zonePicPath='" + zonePicPath + '\'' +
                ", miniMap='" + miniMap + '\'' +
                ", itemSet='" + itemSet + '\'' +
                '}';
    }
// </editor-fold>


    public static ZoneObject getZoneByZoneId(int zoneId){
        ZoneObject zoneObject = null;
        final String URL = "http://52.74.64.61/api/select_all_zone_by_zoneid.php?zoneid="+zoneId;
        try {
            String response = new HttpHelper().execute(URL).get();
            Log.d(LOG_TAG, "response: " + response);
            JSONArray arr = new JSONArray(response);
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    zoneObject = new ZoneObject();
                    zoneObject.setZoneId(Integer.parseInt(obj.get("zoneid").toString()));
                    zoneObject.setAreaId(Integer.parseInt(obj.get("areaid").toString()));
                    zoneObject.setZoneName(obj.get("zonename").toString());
                    zoneObject.setZoneDetails(obj.get("zonedetails").toString());
                    zoneObject.setQrCode(obj.get("qrcode").toString());
                    zoneObject.setSensorId(obj.get("sensorid").toString());
                    zoneObject.setLatitude(Double.parseDouble(obj.get("latitude").toString()));
                    zoneObject.setLongitude(Double.parseDouble(obj.get("longitude").toString()));
                    zoneObject.setZonePicPath(obj.get("zonepicpath").toString());
                    zoneObject.setMiniMap(obj.get("minimap").toString());
                    zoneObject.setItemSet(obj.get("itemset").toString());
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return zoneObject;
    }

    public static ArrayList<ZoneObject> getAllZoneByPlaceId(int placeId){
        ArrayList<ZoneObject> al = null;
        ZoneObject zoneObject;
        final String URL = "http://52.74.64.61/api/select_all_zone_by_placeid.php?placeid="+placeId;
        try {
            String response = new HttpHelper().execute(URL).get();
            Log.d(LOG_TAG,"response: "+response);
            JSONArray arr = new JSONArray(response);
            if (arr.length() != 0) {
                al = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    zoneObject = new ZoneObject();
                    JSONObject obj = (JSONObject) arr.get(i);
                    zoneObject.setZoneId(Integer.parseInt(obj.get("zoneid").toString()));
                    zoneObject.setAreaId(Integer.parseInt(obj.get("areaid").toString()));
                    zoneObject.setZoneName(obj.get("zonename").toString());
                    zoneObject.setZoneDetails(obj.get("zonedetails").toString());
                    zoneObject.setQrCode(obj.get("qrcode").toString());
                    zoneObject.setSensorId(obj.get("sensorid").toString());
                    zoneObject.setLatitude(Double.parseDouble(obj.get("latitude").toString()));
                    zoneObject.setLongitude(Double.parseDouble(obj.get("longitude").toString()));
                    zoneObject.setZonePicPath(obj.get("zonepicpath").toString());
                    zoneObject.setMiniMap(obj.get("minimap").toString());
                    zoneObject.setItemSet(obj.get("itemset").toString());
                    al.add(zoneObject);
                }

            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return al;
    }


}
