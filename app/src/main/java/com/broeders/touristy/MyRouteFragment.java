package com.broeders.touristy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.broeders.touristy.models.RouteItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class MyRouteFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private VerticalAdapter mVerticalAdapter;
    private ArrayList<RouteItem> mRoutesList;
    private RequestQueue mRequestQueue;

    private TextView descriptionText;

    private TextView errorText;
    private Button retryButton;
    ProgressBar progressBar;
    TextView myRouteInfoTextView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_route, container, false);
        progressBar = rootView.findViewById(R.id.myRoutes_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Routes");
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myRouteInfoTextView.setVisibility(View.GONE);
        mRoutesList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(getContext());

        descriptionText = rootView.findViewById(R.id.routeItem_description);

        errorText = rootView.findViewById(R.id.routes_error_textView);
        retryButton = rootView.findViewById(R.id.button_retry_routes);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJSON();
            }
        });
        myRouteInfoTextView = rootView.findViewById(R.id.myRouteInfoTextView);
        //end initialising

        parseJSON();
        if (mRoutesList.size() == 0) {
            myRouteInfoTextView.setVisibility(View.VISIBLE);
            myRouteInfoTextView.setTextColor(getResources().getColor(R.color.colorError));
            myRouteInfoTextView.setText("User doesn't have any routes");
        }

        return rootView;
    }
    SharedPreferences pref;
    private void parseJSON() {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);

        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        String userID = pref.getString("userID","error");
        String url;
        if (!userID.contentEquals("error")){
            url = "http://ineke.broeders.be/touristy/Webservice.aspx?do=getRouteUser&userid=" + userID;
        }else{
            url = "http://ineke.broeders.be/touristy/Webservice.aspx?do=getRoutes";
        }


        if (NetworkCheckingClass.isNetworkAvailable(getContext())) {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            //Get JSONObject route
                            JSONObject route = response.getJSONObject(i);

                            //get data
                            String imageURL = route.getString("PictureURL");
                            String profileImageURL = route.getJSONObject("User").getString("ProfilePictureURL");

                            String routeTitle = route.getString("Name");

                            String firstName = route.getJSONObject("User").getString("FirstName");
                            String lastName = route.getJSONObject("User").getString("LastName");
                            String userName = route.getJSONObject("User").getString("Username");

                            String description = route.getString("Description");
                            //info
                            String location = route.getString("Location");
                            String routeID = route.getString("RouteID");
                            String routeLength = route.getString("length");


                            mRoutesList.add(new RouteItem(imageURL, profileImageURL, routeTitle, userName, location, description, routeID, routeLength));
                        }

                        mVerticalAdapter = new VerticalAdapter(getContext(), mRoutesList);
                        mRecyclerView.setAdapter(mVerticalAdapter);
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
        } else {
            errorText.setText(getContext().getResources().getString(R.string.noNetwork));

            errorText.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
        }
    }
}
