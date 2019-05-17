package com.broeders.touristy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class FirstUseActivity extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_use);

        //animations
        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();
        //animations end

        Button loginButton = findViewById(R.id.LoginButton);
        Button createAccountButton = findViewById(R.id.CreateAccountButton);
        pref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        //initialiseer hier alle preferences

        //einde initialisatie

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent login = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(login);
                finish();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent create = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(create);
                finish();
            }
        });

    }

}