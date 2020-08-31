package com.example.usaid_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    // MainActivity uses the layout activity_main.xml
    // First activity invoked upon clicking the app. Manages the log-in and sign-up buttons

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private String companyname;
    MainActivity selff = this;
    EditText userNameField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNameField = findViewById(R.id.userNameField);
        passwordField = findViewById(R.id.passwordField);

        //Button for login
        Button login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getUser(userNameField.getText().toString().trim(),passwordField.getText().toString().trim());
            }
        });

        // button for sign-up
        Button signup = findViewById(R.id.signupButton);
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                userNameField.getText().clear();           //remove the Log-in information
                passwordField.getText().clear();
                userNameField.requestFocus();

                Intent newIntent = new Intent(selff,SignUpActivity.class);  // redirects to the sign-up form
                startActivity(newIntent);
            }
        });

        // show password "eye" button beside the password field
        Button showPassword = findViewById(R.id.showPassButton);
        showPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                return false;
            }
        });


    }

    //check network state prompt if necessary
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // API call to verify user and password
    private void getUser(String username, String password){
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "user");

        if (isOnline()) {

            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_USER_PASSWORD + username + "&Password=" + password,
                    params, CODE_GET_REQUEST, selff);
            try {
                String result = request.execute().get();
                accessUser(result);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(selff,"Unable to connect. Make sure you have active internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    // checks the log-in credentials and redirects to the next activity
    public void accessUser(String s){
        try {
            JSONObject object = new JSONObject(s);
            if (!object.getBoolean("error")) {
                Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                JSONObject user = new JSONObject(object.getString("USER"));
                String nameUser = user.getString("UserName");
                String userName = user.getString("FirstName");
                String accessLevel = user.getString("UserAccessLevel");

                Intent newIntent = null;

                System.out.println("accesslevel:" + accessLevel);

                // redirects to HomeActivity or HomeNonBfarActivity depending on the user's access level
                if (accessLevel.equals("1")){
                    System.out.println("bfar");
                    newIntent = new Intent(selff, HomeActivity.class);  // redirects to HomeActivity since accessLevel = 1 = BFAR
                    newIntent.putExtra("USER", nameUser);
                    newIntent.putExtra("USER_NAME", userName);
                    newIntent.putExtra("ACCESS_LEVEL", accessLevel);

                    startActivity(newIntent);
                }
                else{
                    System.out.println("non-bfar");     // redirects to HomeNonBfarActivity since accessLevel != 1 != BFAR
                    newIntent = new Intent(selff, HomeNonBfarActivity.class);
                    newIntent.putExtra("USER_NAME", userName);
                    newIntent.putExtra("ACCESS_LEVEL", accessLevel);
                    if (accessLevel.equalsIgnoreCase("3")) {
                        //get company of username
                        Log.d("USER", userNameField.getText().toString().trim());
                        getCompanyName(userNameField.getText().toString().trim());
                        Log.d("COMPANY", companyname);
                        newIntent.putExtra("COMPANY", companyname);

                    }

                    userNameField.getText().clear();           // clear texts in the username and password fields
                    passwordField.getText().clear();
                    userNameField.requestFocus();

                    startActivity(newIntent);
                }
            }
            else{
                Toast.makeText(selff, "Error! Invalid username/password or account is not yet verified.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //not a valid json string, might be an error message
            e.printStackTrace();
            Toast.makeText(selff,"Error: " + s, LENGTH_SHORT).show();
        }
    }

    // API call to retrieve user's company name in the database
    private void getCompanyName(String uname) {
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "user");
        if (isOnline()) {
            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_GET_COMPANY_OF_USER + uname,
                    params, CODE_GET_REQUEST, selff);
            try {
                String result = request.execute().get();
                processCompanyName(result);
            } catch (Exception e) {
                Toast.makeText(selff, "Unable to connect to the database.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else{
            Toast.makeText(selff,"Unable to connect. Make sure you have active internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    // checks company name to avoid errors
    private void processCompanyName(String s){
            try {
                JSONObject cname = new JSONObject(s);
                companyname = cname.getString("Company");

            } catch(JSONException e){
                //not a valid json string, might be an error message
                e.printStackTrace();
                Toast.makeText(selff,"Error: " + s, LENGTH_SHORT).show();
            }

    }
}
