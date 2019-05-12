package com.broeders.touristy;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class SignInActivity extends AppCompatActivity {
    // UI Declarations
    Button btnLogIn, btnSignIn;
    EditText txtname,txtemail,txtpassword,txtlastname,txtpasswordconfirm,txtusername;
    Spinner spnnationality;
    String name,lastname,email,username,password,passwordconfirm,nationality,birth_date;
    TextView txtbirth_date,txtError;
    DatePickerDialog datePicker;
    ProgressDialog progressDialog;
    // Variables Declarations
    //sharedPreferences init
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    //end sharedPreferences init

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // UI Initialization
        progressDialog = new ProgressDialog(this);
        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();
        txtError = findViewById(R.id.errorTextView);
        txtname = findViewById(R.id.txtNameSignIn);
        txtlastname = findViewById(R.id.txtLastNameSignIn);
        txtusername = findViewById(R.id.txtUserSignIn);
        txtemail = findViewById(R.id.txtEmailSignIn);
        txtpassword = findViewById(R.id.txtPasswordSignIn);
        txtpasswordconfirm = findViewById(R.id.txtPasswordConfirmSignIn);
        spnnationality = findViewById(R.id.sprNationalitySignIn);
        btnLogIn = findViewById(R.id.btnLogIn_SignIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        btnSignIn = findViewById(R.id.btnSignIn_Signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        txtbirth_date = findViewById(R.id.txtBirthDateSignIn);
        txtbirth_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR); // current year
                int month = c.get(Calendar.MONTH); // current month
                int day = c.get(Calendar.DAY_OF_MONTH); // current day
                //Date picker dialog
                datePicker = new DatePickerDialog(SignInActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtbirth_date.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                        birth_date = dayOfMonth + "-"+ (monthOfYear + 1) + "-" + year;
                    }
                }, year, month, day);

                //User must have at least 10 years to sign up
                c.add(Calendar.YEAR,-10);
                datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
                //User must not have more than 100 years to sign up
                c.add(Calendar.YEAR, -100);
                datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
                datePicker.show();
            }
        });
        // End UI Initialization
        //sharedPref init
        pref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        //end sharedPref init
    }

    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
    }

    public void goToLogin(){
        Intent login = new Intent(getApplicationContext(),LogInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    public void goToHome() {
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
        finish();
    }

    public void signIn(){
        displayProgressDialog(R.string.Creating_Account, R.string.Please_Wait);
        setUserValues();
        if (verifyData()){
            registerUser();
        }
    }

    public void setUserValues(){
        name = txtname.getText().toString().trim();
        lastname = txtlastname.getText().toString().trim();
        email = txtemail.getText().toString().trim();
        username = txtusername.getText().toString().trim();
        password = txtpassword.getText().toString().trim();
        passwordconfirm = txtpasswordconfirm.getText().toString().trim();
        birth_date = txtbirth_date.getText().toString().trim();
        nationality = spnnationality.getSelectedItem().toString().trim();
    }

    protected boolean verifyData() {
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.Name, Toast.LENGTH_LONG).show();
            txtError.setText(R.string.Name);
            progressDialog.dismiss();
            return false;
        }
        if (lastname.isEmpty()) {
            txtError.setText(R.string.Last_name);
            Toast.makeText(this, R.string.Last_name, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (email.isEmpty()) {
            txtError.setText(R.string.Enter_your_email);
            Toast.makeText(this, R.string.Enter_your_email, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (username.isEmpty()) {
            txtError.setText(R.string.Enter_your_username);
            Toast.makeText(this, R.string.Enter_your_email, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (password.isEmpty()) {
            txtError.setText(R.string.Enter_your_password);
            Toast.makeText(this, R.string.Enter_your_password, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (passwordconfirm.isEmpty()) {
            txtError.setText(R.string.Confirm_your_password);
            Toast.makeText(this, R.string.Confirm_your_password, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (birth_date.isEmpty()) {
            txtError.setText(R.string.Birth_Date);
            Toast.makeText(this, R.string.Birth_Date, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if(password.length() < 5  || passwordconfirm.length() < 5 ){
            txtError.setText(R.string.Password_length);
            Toast.makeText(this, R.string.Password_length, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (!password.equals(passwordconfirm)) {
            txtError.setText(R.string.Password_match);
            Toast.makeText(this, R.string.Password_match, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        return true;
    }

    protected void registerUser(){
        txtError.setTextColor(getResources().getColor(R.color.colorError));

        if (isNetworkAvailable() == false) {
            txtError.setText(R.string.noNetwork);
            progressDialog.dismiss();
        } else {
            RequestQueue signinRequestQueue = Volley.newRequestQueue(this);

            String url = "http://ineke.broeders.be/touristy/webservice.aspx?do=addUser&email=" + email +"&username=" + username + "&password=" + password + "&firstname=" + name + "&lastname=" + lastname +"&country=" + nationality + "&profilepictureURL=" + "&birthdate=" + birth_date;
            StringRequest signInRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("true") || response.length() == 0) {
                        editor.putString("email",email);
                        editor.putString("country",nationality);
                        editor.putString("birthDate",birth_date);
                        //dateCreated is ne moeilijke
                        String dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                        editor.putString("dateCreated",dateCreated);
                        //einde dateCreated
                        editor.putString("firstName",name);
                        editor.putString("lastName",lastname);
                        editor.putString("password",password);
                        editor.putString("profilePictureURL","");
                        editor.putString("username",username);
                        editor.putBoolean("isNew", false);
                        editor.apply();

                        goToHome();
                    } else {
                        txtError.setText(R.string.signin_Error);
                    }
                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    txtError.setText(R.string.logInError);
                    progressDialog.dismiss();
                }
            });

            signinRequestQueue.add(signInRequest);
        }
    }
}
