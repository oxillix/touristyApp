package com.broeders.touristy.models;

public class RouteItem {
    private String mImageUrl;
    private String mProfileImageUrl;
    private String mTitle;
    private String mCreator;
    private String mLocation;
    private String mDescription;
    private String mRouteID;
    private String mRouteLength;

    public RouteItem(String imageUrl, String profileImageUrl, String routeTitle, String routeCreator, String location, String description, String routeID, String routeLength) {
        mImageUrl = imageUrl;
        mProfileImageUrl = profileImageUrl;
        mCreator = routeCreator;
        mLocation = location;
        mTitle = routeTitle;
        mDescription = description;
        mRouteID = routeID;
        mRouteLength = routeLength;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }
    public String getRouteTitle() {
        return mTitle;
    }
    public String getCreator() {
        return mCreator;
    }
    public String getLocation() {
        return mLocation;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getRouteID() {
        return mRouteID;
    }
    public String getRouteLength() {
        return mRouteLength;
    }

}