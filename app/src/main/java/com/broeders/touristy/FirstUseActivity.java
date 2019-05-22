package com.broeders.touristy;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;


public class FirstUseActivity extends MaterialIntroActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //animations
        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();
        //animations end

        Button loginButton = findViewById(R.id.btnLogIn);
        pref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        //initialiseer hier alle preferences

        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.firstusebleubutton)
                .buttonsColor(R.color.firstUseBlack)
                .image(R.drawable.monuments)
                .title("let's spot some monuments!")
                .description("Want to see all the monuments in your city? Or do you just want to go for a walk?" +
                        " Let's do both with our app!")
                .build());
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.firstusebleubutton)
                .buttonsColor(R.color.firstUseBlack)
                .image(R.drawable.routes)
                .title("Relaxing routes")
                .description("Step around in the city or village " +
                        "and check out the historic buildings in your neighborhood.")
                .build());
        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.firstusebleubutton)
                        .buttonsColor(R.color.firstUseBlack)

                        .neededPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
                        .image(R.drawable.pointtopoint)
                        .title("Move point to point and discover your city")
                        .description("Enable your location and start exploring")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("Permissions already granted");

                    }
                }, "Grant Location permission"));



        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.firstusebleubutton)
                        .buttonsColor(R.color.firstUseBlack)
                        .image(R.drawable.profile)
                        .title("Already have an account?")
                        .description("Sign in or continue and create a new account.")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent login = new Intent(getApplicationContext(), LogInActivity.class);
                        startActivity(login);
                    }
                }, "Sign in"));




    }
    @Override
    public void onFinish() {
        Intent signin = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signin);
        super.onFinish();
        Toast.makeText(this, "Start by making an account!", Toast.LENGTH_SHORT).show();
    }
}