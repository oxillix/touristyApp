package com.broeders.touristy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class splashScreenActivity extends AppCompatActivity {
    private LinearLayout l1;
    Animation alphaAnim;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        pref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        boolean isNew =  pref.getBoolean("isNew", true);

        if (isNew == false) {
            Intent home = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(home);
            finish();
        } else {
            //Hiding action bar
            getSupportActionBar().hide();

            l1 = findViewById(R.id.l1);
            alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_transition);
            l1.setAnimation(alphaAnim);

            Thread timer = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(2500);
                        Intent firstUse = new Intent(getApplicationContext(), FirstUseActivity.class);
                        startActivity(firstUse);
                        finish();
                        super.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.start();
        }
    }
}
