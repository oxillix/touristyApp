package com.broeders.touristy;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.broeders.touristy.HelperClasses.CircleTransform;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    SharedPreferences pref;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail= headerView.findViewById(R.id.emailTxtDrawer);
        TextView navName =  headerView.findViewById(R.id.nameTxtDrawer);
        ImageView profileImage = headerView.findViewById(R.id.navProfileImage);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        String user_Email =  pref.getString("email", "no email");
        String user_FirstName =  pref.getString("firstName", "no first name");
        String user_LastName =  pref.getString("lastName", "no last name");
        String user_ProfilePicture = pref.getString("profilePictureURL","error");

        if (!user_ProfilePicture.contentEquals("error")){
            Picasso.get().load(user_ProfilePicture).fit().centerInside().transform(new CircleTransform()).into(profileImage);
        }

        navEmail.setText(user_Email);
        navName.setText(user_FirstName + " " + user_LastName);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    public void routeStarted(){
        try {
        navigationView.getMenu().clear(); //clear old inflated items.
        navigationView.inflateMenu(R.menu.drawer_menu_stop); //inflate new items.
        navigationView.setCheckedItem(R.id.drawer_stop_route);
        }catch (Exception e){

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_stop_route:
                navigationView.getMenu().clear(); //clear old inflated items.
                navigationView.inflateMenu(R.menu.drawer_menu); //inflate new items.

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_routes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RoutesFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_my_routes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyRouteFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}