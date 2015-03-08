package com.questio.projects.questiodevelopment;

/**
 * Created by coad4u4ever on 08-Mar-15.
 */
public class PlaceObject {
    private int placeId;
    private String placeName;
    private double placeLat;
    private double placeLng;

    public PlaceObject() {
    }

    public PlaceObject(int placeId, String placeName, double placeLat, double placeLng) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(double placeLat) {
        this.placeLat = placeLat;
    }

    public double getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(double placeLng) {
        this.placeLng = placeLng;
    }


}
