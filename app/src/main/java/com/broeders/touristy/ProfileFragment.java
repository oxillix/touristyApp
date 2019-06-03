package com.broeders.touristy;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.broeders.touristy.HelperClasses.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    Button btnLogOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Profile");
        final SharedPreferences preferences = this.getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);

        String user_username = preferences.getString("username","no username");
        String user_country = preferences.getString("country","no country");
        String user_firstName = preferences.getString("firstName","no firstName");
        String user_lastName = preferences.getString("lastName","no lastName");
        String user_email = preferences.getString("email","no mail");
        String user_birthDate = preferences.getString("birthDate","no birthDate");
        String user_ProfilePicture = preferences.getString("profilePictureURL","error");

        TextView pfUsername = v.findViewById(R.id.usernameTextProfile);
        TextView pfCountry = v.findViewById(R.id.countryTextProfile);
        TextView pfName = v.findViewById(R.id.nameTxtProfile);
        TextView pfMail = v.findViewById(R.id.emailTxtProfile);
        TextView pfBirthDate = v.findViewById(R.id.birthDateTxtProfile);
        ImageView pfPicture = v.findViewById(R.id.pfPicture);

        if (!user_ProfilePicture.contentEquals("error")){
            Picasso.get().load(user_ProfilePicture).fit().centerInside().transform(new CircleTransform()).into(pfPicture);
        }

        pfUsername.setText(user_username);
        pfCountry.setText(user_country);
        pfName.setText(user_firstName + " " + user_lastName);
        pfMail.setText(user_email);
        pfBirthDate.setText(user_birthDate);

        btnLogOut = v.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        return v;
    }

    public void logOut(){
        goToLogIn();
    }
    public void goToLogIn(){
        Intent logIn = new Intent(getActivity().getApplicationContext(), LogInActivity.class);
        startActivity(logIn);
    }
}