package com.broeders.touristy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.broeders.touristy.HelperClasses.NetworkCheckingClass;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.broeders.touristy.Adapter.VerticalAdapter;
import com.broeders.touristy.HelperClasses.CircleTransform;
import com.broeders.touristy.HelperClasses.NetworkCheckingClass;
import com.broeders.touristy.models.PointItem;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class mapRouteFragment extends Fragment implements com.google.android.gms.location.LocationListener, RoutingListener {
    private RequestQueue mRequestQueue;
    private ArrayList<PointItem> mPointsList;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    //oncreate vars
    private TextView errorText;
    private TextView routeInfoTextView;
    private Button retryButton;
    ProgressBar progressBar;

    private String selectedRoute;

    Bitmap decodedBitmap;

    private double routeDistance;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorPrimary,R.color.color_dark_material_shadow};
    public GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;


    public LatLng startLatLng;

    private Boolean mapCreationFinished;
    private Boolean routeIsDrawed;
    private Boolean userStartedRoute;
    public  Integer currentPoint;

    public PolylineOptions polyOptions;
    Bitmap mBitmap;

    public Dialog myDialog;


    public boolean popupInProgress;
    public int distance;

    public TextView distanceTextView;
    LocationManager lm;
    boolean gps_enabled;
    boolean network_enabled;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HomeActivity) getActivity()).routeStarted();
        mapCreationFinished = false;
        routeIsDrawed = false;
        userStartedRoute = false;
        popupInProgress = false;
        currentPoint = 0;

        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        mPointsList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getContext());

        String currentRouteName = pref.getString("currentRouteName", "Touristy route");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentRouteName);

        polylines = new ArrayList<>();
        polyOptions = new PolylineOptions();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        //locationcheck
        lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;
        // end locationcheck
        parseJSON();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_route, container, false);
        //begin popup
        myDialog = new Dialog(getContext());
        //einde popup
        distanceTextView = rootView.findViewById(R.id.distanceTextView);
        routeInfoTextView = rootView.findViewById(R.id.routeInfoTextView);
        routeInfoTextView.setVisibility(View.GONE);
        progressBar = rootView.findViewById(R.id.start_route_progressBar);
        errorText = rootView.findViewById(R.id.startRoute_error_textView);
        errorText.setVisibility(View.GONE);
        retryButton = rootView.findViewById(R.id.button_retry_start_route);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(mapRouteFragment.this).attach(mapRouteFragment.this).commit();
            }
        });
        retryButton.setVisibility(View.GONE);
        //
        //------------------------------------------------------------------------------------------
        //
        //start invulling van kaart
        if (checkPlayServices() && NetworkCheckingClass.isNetworkAvailable(getContext()) && isLocationEnabled(getContext())) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.clear();

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                } else {
                    errorText.setText("Please enable location permissions");
                    retryButton.setVisibility(View.VISIBLE);
                }

                //getRouteToMarker(startLatLng);

                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                checkLocationPermission();
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                //fill in first marker of route
                if (mPointsList.size() > 0) {
                    PointItem mPointItem = mPointsList.get(0);
                    String startCoordinaat = mPointsList.get(0).getCoordinaat();
                    Double startCoordinaatLat = Double.parseDouble(startCoordinaat.substring(0, startCoordinaat.indexOf(",")));
                    Double startCoordinaatLong = Double.parseDouble(startCoordinaat.substring(startCoordinaat.indexOf(",") + 1));
                    startLatLng = new LatLng(startCoordinaatLat, startCoordinaatLong);

                    googleMap.addMarker(new MarkerOptions()
                                    .position(startLatLng)
                                    .title(mPointItem.getPointNaam())
                            //.icon(BitmapDescriptorFactory.fromBitmap(getImageBitmap()))
                            //.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.spider))
                            //.snippet("kkaas ofzo")


                    ).showInfoWindow();

                    CameraPosition startPoint = CameraPosition.builder()
                            .target(new LatLng(startCoordinaatLat, startCoordinaatLong))
                            .zoom(15)
                            .bearing(0)
                            .tilt(0)
                            .build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(startPoint), 10000, null);

                    mMap = googleMap;
                    routeInfoTextView.setText("Go to start point: " + mPointItem.getPointNaam());
                } else {
                    retryButton.setVisibility(View.VISIBLE);
                }
            }
        });

        progressBar.setVisibility(View.GONE);
        routeInfoTextView.setVisibility(View.VISIBLE);


        mapCreationFinished = true;
    } else{
            errorText.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        if (!NetworkCheckingClass.isNetworkAvailable(getContext())){
            errorText.setText("This app requires internet.");
        }
        if (!isLocationEnabled(getContext())){
                errorText.setText("This app requires location to be enabled.");
        }
        }
        return rootView;
    }
    public Bitmap getImageBitmap(){


        Picasso.get()
                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c6/Jews_montage.jpg/266px-Jews_montage.jpg")
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mBitmap = bitmap;
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        return mBitmap;
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    Dialog errorDialog;

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {

                if (errorDialog == null) {
                    errorDialog = googleApiAvailability.getErrorDialog(getActivity(), resultCode, 2404);
                    errorDialog.setCancelable(false);
                }

                if (!errorDialog.isShowing())
                    errorDialog.show();

            }
        }

        return resultCode == ConnectionResult.SUCCESS;
    }

    private void getRouteToMarker(LatLng pointLatLng) {
        Routing routing = new Routing.Builder()
                .key("AIzaSyDZ9Er0pqUttTXEctSvoB7NwDrz9ptnK74")
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pointLatLng)
                .build();
        routing.execute();
    }
    LatLng latLng = null;
    Location mLocation = null;
    Integer popupCounter = 1;
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (mMap != null) {
                for (Location location : locationResult.getLocations()) {
                    if (getContext() != null && mapCreationFinished && userStartedRoute) {
                        if (mLastLocation != null && location != null) {
                            routeDistance += mLastLocation.distanceTo(location) / 1000;
                        }
                        mLastLocation = location;
                        if (routeIsDrawed && mLastLocation != null && currentPoint < mPointsList.size()) {
                            PointItem mPointItem = mPointsList.get(currentPoint);
                            String coordinaat = mPointsList.get(currentPoint).getCoordinaat();
                            Double coordinaatLat = Double.parseDouble(coordinaat.substring(0, coordinaat.indexOf(",")));
                            Double coordinaatLong = Double.parseDouble(coordinaat.substring(coordinaat.indexOf(",") + 1));
                            latLng = new LatLng(coordinaatLat, coordinaatLong);

                            getRouteToMarker(latLng);
                            if (!popupInProgress && popupCounter == currentPoint) {
                                popupCounter += 1;
                                popupInProgress = true;
                                ShowPopup(getView());
                            }


                            mLocation = new Location("");//provider name is unnecessary
                            mLocation.setLatitude(latLng.latitude);
                            mLocation.setLongitude(latLng.longitude);

                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(mPointItem.getPointNaam())).showInfoWindow();

                            routeInfoTextView.setText("Go to point: " + mPointItem.getPointNaam());
                        } else if (currentPoint == mPointsList.size()) {
                            routeInfoTextView.setText("The route has finished!\nRetry or pick another route");
                            routeInfoTextView.setTextColor(getResources().getColor(R.color.colorSucces));
                            retryButton.setVisibility(View.VISIBLE);
                        }
                        if (latLng != null && mLocation != null && currentPoint < mPointsList.size()) {
                            float radius = (float) 50.0;
                            float distance = location.distanceTo(mLocation);
                            if (distance < radius) {
                                currentPoint += 1;
                            }
                        }
                    }
                    if (getContext() != null && mapCreationFinished && !userStartedRoute) {

                        if (mLastLocation != null && location != null) {
                            routeDistance += mLastLocation.distanceTo(location) / 1000;
                        }
                        mLastLocation = location;

                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                        //check if user is close to starting point
                        Location targetLocation = new Location("");//provider name is unnecessary
                        targetLocation.setLatitude(startLatLng.latitude);
                        targetLocation.setLongitude(startLatLng.longitude);

                        float radius = (float) 50.0;
                        float distance = location.distanceTo(targetLocation);
                        if (distance < radius && !userStartedRoute) {
                            if (currentPoint == 0) {
                                erasePolylines();
                            }
                            userStartedRoute = true;
                            currentPoint += 1;
                        }

                        distanceTextView.setText((int)distance + " meter");
                        if (!routeIsDrawed && mLastLocation != null) {
                            getRouteToMarker(startLatLng);
                            routeIsDrawed = true;
                        }
                    }
                }
            } else{routeInfoTextView.setText("An unknown error ocurred \n Try again");}
        }
    };

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permssion Required")
                        .setMessage("Please enable location permissions")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getActivity(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void ShowPopup(View v) {
        myDialog.setContentView(R.layout.custompopup);
        TextView txtclose;
        ImageView pointFullImage;
        TextView pointTitle;
        TextView pointItemDescription;
        RatingBar pointRatingBar;
        PointItem mPointitem;

        txtclose = myDialog.findViewById(R.id.txtclose);
        pointFullImage = myDialog.findViewById(R.id.point_full_image);
        pointTitle = myDialog.findViewById(R.id.point_title);
        pointItemDescription = myDialog.findViewById(R.id.pointItem_description);
        pointRatingBar = myDialog.findViewById(R.id.pointRatingBar);
        mPointitem = mPointsList.get(currentPoint-1);
        //pointFullImage
        pointTitle.setText(mPointitem.getPointNaam());
        pointItemDescription.setText(mPointitem.getDescription());
        String imageUrl = mPointitem.getPictureUrl();
        if (!imageUrl.contentEquals("")) {
            Picasso.get().load(imageUrl).into(pointFullImage);
        }

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                popupInProgress = false;
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    private void parseJSON() {
        selectedRoute = pref.getString("currentRouteID", "error");
        //Todo: if selectedRoute != "error" dan pas mag deze code uitgevoerd worden

        String url = "http://ineke.broeders.be/touristy/Webservice.aspx?do=getPointsRoute&routeID=" + selectedRoute;

        if (NetworkCheckingClass.isNetworkAvailable(getContext())) {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            //Get JSONObject route
                            JSONObject point = response.getJSONObject(i);

                            //get data
                            String pointID = point.getString("pointID");
                            String routeID = point.getString("routeID");
                            String naam = point.getString("naam");
                            String volgorde = point.getString("volgorde");
                            String coordinaat = point.getString("coordinaat");
                            String description = point.getString("description");
                            String pictureUrl = point.getString("pictureURL");

                            mPointsList.add(new PointItem(pointID, routeID, naam, volgorde, coordinaat, description, pictureUrl));
                        }
                    } catch (JSONException e) {
                        errorText.setText(e.toString());
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorText.setText("An internet connection is required!");
                        }
                    }
            );

            mRequestQueue.add(jsonArrayRequest);
        } else {
            errorText.setText("an unknown error occurred");
        }
    }


    public mapRouteFragment() {
        //Required empty public constructor
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        routeInfoTextView.setText("error - failure" + e.toString());
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                //poly.remove();
            }
        }
        //routeInfoTextView.setText("succes");

        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            distance = route.get(i).getDistanceValue();
        }
    }

    @Override
    public void onRoutingCancelled() {
        routeInfoTextView.setText("Error - Routing Cancelled");
    }

    @Override
    public void onLocationChanged(Location location) {
        routeInfoTextView.setText("Error - Location has Changed");
    }
}