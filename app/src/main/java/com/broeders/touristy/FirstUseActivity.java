package com.broeders.touristy;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class FirstUseActivity extends AppCompatActivity {


    private static final String TAG = "FirstUseActivity";
    private  static final  String[] LOCATIONPERMISSION = {

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private  static final  String[] INTERNETPERMISSION= {Manifest.permission.INTERNET};
    private  static final  String[] NETWORKPERMISSION= {Manifest.permission.ACCESS_NETWORK_STATE};
    private  static final  String[] STORAGEPERMISSION= { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_use);

        //animations
        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();
        //animations end

        Button loginButton = findViewById(R.id.loginButton);
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

    }
    private void   verifyPermissions(){
        Log.d(TAG, "verifyPermissions: checking Permissions.");
        int permissionInternet = ActivityCompat.checkSelfPermission(FirstUseActivity.this, Manifest.permission.INTERNET);
        int permissionNetwork = ActivityCompat.checkSelfPermission(FirstUseActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
        int permissionReadStorage = ActivityCompat.checkSelfPermission(FirstUseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionwriteStorage = ActivityCompat.checkSelfPermission(FirstUseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionFineLocation = ActivityCompat.checkSelfPermission(FirstUseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarseLocation = ActivityCompat.checkSelfPermission(FirstUseActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionInternet != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirstUseActivity.this,
                    INTERNETPERMISSION,
                    1);

        }
        if(permissionNetwork != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirstUseActivity.this,
                    NETWORKPERMISSION,
                    1);

        }
        if(permissionReadStorage != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirstUseActivity.this,
                    STORAGEPERMISSION,
                    1);

        }
        if(permissionwriteStorage != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirstUseActivity.this,
                    STORAGEPERMISSION,
                    1);

        }
        if(permissionFineLocation != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirstUseActivity.this,
                    LOCATIONPERMISSION,
                    1);

        }
        if(permissionCoarseLocation != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirstUseActivity.this,
                    LOCATIONPERMISSION,
                    1);

        }
    }

}
