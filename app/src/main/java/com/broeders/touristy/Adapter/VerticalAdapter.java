package com.broeders.touristy.Adapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.broeders.touristy.HomeFragment;
import com.broeders.touristy.RoutesFragment;
import com.broeders.touristy.mapRouteFragment;
import com.broeders.touristy.models.RouteItem;
import com.broeders.touristy.HelperClasses.CircleTransform;
import com.broeders.touristy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.RoutesViewHolder> {
    private Context mContext;
    private ArrayList<RouteItem> mRoutesList;

    public VerticalAdapter(Context context, ArrayList<RouteItem> routesList) {
        mContext = context;
        mRoutesList = routesList;
    }

    @Override
    public RoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.data_single_item, parent, false);
        return new RoutesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RoutesViewHolder holder, int position) {
        RouteItem currentItem = mRoutesList.get(position);

        //get
        String imageUrl = currentItem.getImageUrl();
        String profileImageUrl = currentItem.getProfileImageUrl();
        String routeTitle = currentItem.getRouteTitle();
        String creatorName = currentItem.getCreator();
        String routeDescription = currentItem.getDescription();
        //info
        String location = currentItem.getLocation();
        String routeLength = currentItem.getRouteLength();

        //set
        Picasso.get().load(imageUrl).fit().centerInside().into(holder.bigImageView);
        if (!profileImageUrl.contentEquals("")){
            Picasso.get().load(profileImageUrl).fit().centerInside().transform(new CircleTransform()).into(holder.ProfileImageView);
        }
        holder.TextViewTitle.setText(routeTitle);
        holder.TextViewCreator.setText(creatorName);
        holder.TextViewDescription.setText(routeDescription);
        //info
        holder.TextViewInfo.setText(location + " - " + routeLength + " km");
    }

    @Override
    public int getItemCount() {
        return mRoutesList.size();
    }

    public class RoutesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView bigImageView;
        public ImageView ProfileImageView;
        public TextView TextViewTitle;
        public TextView TextViewCreator;
        public TextView TextViewInfo;
        public TextView TextViewDescription;
        public Button startRouteButton;
        private Integer clickCounter = 1;

        CardView cardView;

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        public RoutesViewHolder(View itemView) {
            super(itemView);

            bigImageView = itemView.findViewById(R.id.full_image);
            ProfileImageView = itemView.findViewById(R.id.profile_image);
            TextViewTitle = itemView.findViewById(R.id.route_title);
            TextViewCreator = itemView.findViewById(R.id.profile_name);
            TextViewInfo = itemView.findViewById(R.id.text_view_info);
            TextViewDescription = itemView.findViewById(R.id.routeItem_description);
            //button
            startRouteButton = itemView.findViewById(R.id.startRouteButton);

            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

            startRouteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();

                    editor.putString("currentRouteID", mRoutesList.get(clickedPosition).getRouteID());
                    editor.putString("currentRouteName", mRoutesList.get(clickedPosition).getRouteTitle());
                    editor.putBoolean("isDoingRoute", true);
                    editor.apply();

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new HomeFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
                }
            });
        }

        @Override
        public void onClick(View v) {
            clickCounter += 1;

            pref = mContext.getSharedPreferences("pref", MODE_PRIVATE);
            editor = pref.edit();

            if (clickCounter % 2 == 0) {
                startRouteButton.setVisibility(View.VISIBLE);
            } else {
                startRouteButton.setVisibility(View.GONE);
                //breng naar routeactivity
            }
        }
    }
}
