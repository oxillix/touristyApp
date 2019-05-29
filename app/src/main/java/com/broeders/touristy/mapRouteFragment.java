package com.broeders.touristy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.broeders.touristy.Adapter.VerticalAdapter;
import com.broeders.touristy.HelperClasses.NetworkCheckingClass;
import com.broeders.touristy.models.PointItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


import static android.content.Context.MODE_PRIVATE;

public class mapRouteFragment extends Fragment {
    private RequestQueue mRequestQueue;
    private ArrayList<PointItem> mPointsList;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    //oncreate vars
    private TextView errorText;
    private Button retryButton;
    ProgressBar progressBar;

    private String selectedRoute;

    Bitmap decodedBitmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialisatie
        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        String currentRouteName = pref.getString("currentRouteName", "Touristy route");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentRouteName);

        mPointsList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getContext());
        //end
        if (NetworkCheckingClass.isNetworkAvailable(getContext())) {
            parseJSON();
        }
        //String imageURL = "https://encrypted-tbn0.gstatic.com/images?https://www.worldwideholland.com/images/productimages/big/nederlandse-jong-belegen-kaas.jpg";
        //decodedBitmap = getBitmapFromURL(imageURL);
        //new getBitmapAsyncTask().execute("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_route, container, false);
        //initialisatie
        progressBar = rootView.findViewById(R.id.start_route_progressBar);
        errorText = rootView.findViewById(R.id.startRoute_error_textView);
        retryButton = rootView.findViewById(R.id.button_retry_start_route);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(mapRouteFragment.this).attach(mapRouteFragment.this).commit();
            }
        });
        //buttons resetten
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        //start invulling van kaart
        if (NetworkCheckingClass.isNetworkAvailable(getContext())) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    mMap.clear(); //clear old markers
                    parseJSON();

                    //Adding marker

                    for (int i = 0; i < mPointsList.size(); i++) {
                        PointItem mPointItem = mPointsList.get(i);

                        String startCoordinaat = mPointsList.get(i).getCoordinaat();
                        Double startCoordinaatLat = Double.parseDouble(startCoordinaat.substring(0, startCoordinaat.indexOf(",")));
                        Double startCoordinaatLong = Double.parseDouble(startCoordinaat.substring(startCoordinaat.indexOf(",") + 1));
                        //String imageURL = mPointsList.get(0).getPictureUrl();
                        //Bitmap decodedBitmap = getBitmapFromURL(imageURL);


                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(startCoordinaatLat, startCoordinaatLong))
                                .title(mPointItem.getPointNaam())
                                    //    .icon(BitmapDescriptorFactory.fromBitmap(decodedBitmap))
                                //.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.spider))
                                //snippet("kkaas ofzo")
                        );
                    }
                    //
                    String startCoordinaat = mPointsList.get(0).getCoordinaat();
                    Double startCoordinaatLat = Double.parseDouble(startCoordinaat.substring(0, startCoordinaat.indexOf(",")));
                    Double startCoordinaatLong = Double.parseDouble(startCoordinaat.substring(startCoordinaat.indexOf(",") + 1));

                    CameraPosition startPoint = CameraPosition.builder()
                            .target(new LatLng(startCoordinaatLat, startCoordinaatLong))
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(startPoint), 10000, null);
                }
            });


            progressBar.setVisibility(View.GONE);
        } else{
            errorText.setVisibility(View.VISIBLE);
            errorText.setText("An internet connection is required");
            retryButton.setVisibility(View.VISIBLE);
        }
        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers
                parseJSON();
                //Adding markers

                for (int i = 0; i < mPointsList.size(); i++) {
                PointItem mPointItem = mPointsList.get(i);

                String startCoordinaat = mPointsList.get(i).getCoordinaat();
                Double startCoordinaatLat = Double.parseDouble(startCoordinaat.substring(0,startCoordinaat.indexOf(",")));
                Double startCoordinaatLong = Double.parseDouble(startCoordinaat.substring(startCoordinaat.indexOf(",")+1));

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(startCoordinaatLat, startCoordinaatLong))
                            .title(mPointItem.getPointNaam())
                            //.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.spider))
                            //.snippet("")
                            );
                }
                //
                String startCoordinaat = mPointsList.get(0).getCoordinaat();
                Double startCoordinaatLat = Double.parseDouble(startCoordinaat.substring(0,startCoordinaat.indexOf(",")));
                Double startCoordinaatLong = Double.parseDouble(startCoordinaat.substring(startCoordinaat.indexOf(",")+1));

                CameraPosition startPoint = CameraPosition.builder()
                        .target(new LatLng(startCoordinaatLat,startCoordinaatLong))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(startPoint), 10000, null);
            }
        });
        //einde kaart
        */





        return rootView;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
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

                            mPointsList.add(new PointItem(pointID,routeID,naam,volgorde,coordinaat,description,pictureUrl));
                        }
                    } catch (JSONException e) {
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            );

            mRequestQueue.add(jsonArrayRequest);
        } else {
        }
    }
    public class getBitmapAsyncTask extends AsyncTask<String, Void, Void> {
        // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
        @Override
        protected Void doInBackground(String... params) {

            try {
                    try {
                        //URL url = new URL(src);
                        String src = "https://encrypted-tbn0.gstatic.com/images?https://www.worldwideholland.com/images/productimages/big/nederlandse-jong-belegen-kaas.jpg";
                        URL url = new URL(src);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        decodedBitmap = myBitmap;
                    } catch (IOException e) {
                        return null;
                    }
            } catch (Exception e) {
            }
            return null;
        }
    }
    public mapRouteFragment() {
        //Required empty public constructor
    }
}