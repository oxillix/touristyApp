package com.broeders.touristy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    TextView textView1;
    TextView textView2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");

        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        textView1 = rootView.findViewById(R.id.textView_home_1);
        textView2 = rootView.findViewById(R.id.textView_home_2);

        textView1.setText(String.valueOf(pref.getBoolean("isDoingRoute", false)));
        textView2.setText(pref.getString("currentRouteID", "niet geinit"));
        return rootView;
    }
}