package com.broeders.touristy.models;

public class PointItem {
    private String mPointID;
    private String mRouteID;
    private String mPointNaam;
    private String mVolgorde;
    private String mCoordinaat;
    private String mDescription;
    private String mPictureUrl;

    public PointItem(String pointID, String routeID, String pointNaam, String volgorde, String coordinaat, String description, String pictureUrl) {
        mPointID = pointID;
        mRouteID = routeID;
        mPointNaam = pointNaam;
        mVolgorde = volgorde;
        mCoordinaat = coordinaat;
        mDescription = description;
        mPictureUrl = pictureUrl;
    }

    public String getPointID() {
        return mPointID;
    }
    public String getRouteID() {
        return mRouteID;
    }
    public String getPointNaam() {
        return mPointNaam;
    }
    public String getVolgorde() {
        return mVolgorde;
    }
    public String getCoordinaat() {
        return mCoordinaat;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getPictureUrl() {
        return mPictureUrl;
    }
}