package com.broeders.touristy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class mapRouteFragment extends Fragment {
    private RequestQueue mRequestQueue;
    private ArrayList<PointItem> mPointsList;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_route, container, false);
        //initialisatie
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Current Route");
        progressBar = rootView.findViewById(R.id.start_route_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        errorText = rootView.findViewById(R.id.startRoute_error_textView);
        retryButton = rootView.findViewById(R.id.button_retry_start_route);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJSON();
            }
        });


        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        //initialising
        mPointsList = new ArrayList<>();
        //Json
        mRequestQueue = Volley.newRequestQueue(getContext());
        //

        //einde initialisatie

        //json request die points invult in de mPointsList
        //Alles met de kaart
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

    private TextView errorText;
    private Button retryButton;
    ProgressBar progressBar;

    private String selectedRoute;

    private void parseJSON() {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);

        selectedRoute = pref.getString("currentRouteID", "error");
        //Todo: if selectedRoute != "error" dan pas mag deze code uitgevoerd worden

        String url = "http://ineke.broeders.be/touristy/Webservice.aspx?do=getPoints&routeID=" + selectedRoute;

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


                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        errorText.setText(e.toString());

                        errorText.setVisibility(View.VISIBLE);
                        retryButton.setVisibility(View.VISIBLE);

                        progressBar.setVisibility(View.GONE);
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorText.setText(error.getMessage());

                            errorText.setVisibility(View.VISIBLE);
                            retryButton.setVisibility(View.VISIBLE);

                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );

            mRequestQueue.add(jsonArrayRequest);

            progressBar.setVisibility(View.GONE);
        } else {
            errorText.setText(getContext().getResources().getString(R.string.noNetwork));

            errorText.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
        }
    }

    public mapRouteFragment() {
        //Required empty public constructor
    }
}