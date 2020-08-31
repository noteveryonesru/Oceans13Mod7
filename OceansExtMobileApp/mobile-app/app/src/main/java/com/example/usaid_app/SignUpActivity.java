package com.example.usaid_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static android.view.View.GONE;
import static android.widget.Toast.*;

public class SignUpActivity extends AppCompatActivity {
    // SignUpActivity uses activity_signup.xml layout
    // It renders the sign-up form

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    SignUpActivity selff = this;
    EditText passwordField, usernameField, firstNameField, lastNameField,rePasswordField;
    Button registerUserButton;
    Spinner dropdown,dropdown2;
    public static String[] companiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        passwordField = findViewById(R.id.passwordField);
        rePasswordField = findViewById(R.id.rePasswordField);
        usernameField = findViewById(R.id.userNameField);
        registerUserButton = findViewById(R.id.registerButton);
        String[] arraySpinner = new String[] {
                "Select..", "BFAR","LGUs, Coastguard, Marina (Non-BFAR)","Industry (Non-BFAR)",
                "Researchers, NGOs (Non-BFAR)","Fishers (Non-BFAR)"
        };
        dropdown = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);


        // API call to get the company names. Stores the company names to companiesList
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "effortCoords");
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_COMPANYNAMES, params, 1024,selff);

        try {
            String res = request.execute().get();
            System.out.println(res);

            //parse json array
            JSONObject response = new JSONObject(res);
            JSONArray companyNames = response.getJSONArray("CompanyNames");
            System.out.println(companyNames);

            companiesList = new String[companyNames.length()+1];
            companiesList[0] = "None";  // option for non-industry users
            for(int i = 0; i < companyNames.length(); i++){
                String companyNamesString = companyNames.getString(i);
                System.out.println(companyNamesString);
                companiesList[i+1]=companyNamesString;
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        dropdown2 = findViewById(R.id.spinner2);      // dropdown/spinner menu for company names
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, companiesList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown2.setAdapter(adapter2);

        // Register button creates a user using the inputs. All created users have a "not verified" status
        registerUserButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                createUser();
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

    // adds username upon successful validation
    public void addUsername(String s){
        try {
            JSONObject object = new JSONObject(s);
            if (!object.getBoolean("error")) {
                Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(selff, ConfirmationActivity.class); // redirects to sign-up confirmation page
                startActivity(newIntent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // validates if the username exists
    public void validateUsername(String s, HashMap params){
        try {
            JSONObject object = new JSONObject(s);
            if (object.getBoolean("error")) { //User is found
                Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_USER, params, CODE_POST_REQUEST,selff);
                try {
                    String res = request.execute().get();
                    addUsername(res);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Username is already used.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // creates the user using the validated inputs from the sign up form
    private void createUser(){
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String userName = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String repassword = rePasswordField.getText().toString().trim();
        String userType = dropdown.getSelectedItem().toString();
        String company = dropdown2.getSelectedItem().toString();
        String userAccessLevel = "";

        rePasswordField.setError(null);

        // validating inputs
        // checks if the first name field contains input
        if(TextUtils.isEmpty(firstName)){
            firstNameField.setError("Please enter first name.");
            firstNameField.requestFocus();
            return;
        }
        // checks if the last name field contains input
        if(TextUtils.isEmpty(lastName)){
            lastNameField.setError("Please enter last name.");
            lastNameField.requestFocus();
            return;
        }
        // checks if the username field contains input
        if(TextUtils.isEmpty(userName)){
            usernameField.setError("Please enter username.");
            usernameField.requestFocus();
            return;
        }
        // checks if the password field contains input
        if(TextUtils.isEmpty(password)){
            passwordField.setError("Please enter password.");
            passwordField.requestFocus();
            return;
        }
        // checks if the password contains a minimum of eight characters, at least one letter, one number and one special character
        if(!isValidPassword(password)){
            passwordField.setError("Password must contain at least 8 characters with " +
                    "at least one letter, at least one number, and at least one symbol (@$!%*#?&).");
            passwordField.requestFocus();
            return;
        }
        // checks if the re-typed password field is not empty
        if(TextUtils.isEmpty(repassword)){
            rePasswordField.setError("Please re-type password.");
            rePasswordField.requestFocus();
            return;
        }
        // checks if the inputs from the password and the re-typed password field match
        if(!(password.equals(repassword))){
            rePasswordField.setError("Password do not match.");
            rePasswordField.requestFocus();
            return;
        }
        // checks if the user chose a user type on the dropdown menu
        if(userType.equals("") || userType.equals("Select..")){ // if the user did not put his/her user type.
            TextView errorText = (TextView)dropdown.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            dropdown.requestFocus();
            Toast.makeText(getApplicationContext(), "Please select user type.", Toast.LENGTH_SHORT).show();
            return;
        }
        // checks if the user chose a company name or "None" on the dropdown menu
        if(company.equals("")){ // if the user did not put his/her user type.
            TextView errorText = (TextView)dropdown2.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            dropdown2.requestFocus();
            Toast.makeText(getApplicationContext(), "Please select a company name. Choose \"None\" if not applicable.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // checks if the user chose Industry as user type but did not indicate their company name
        if (company.equalsIgnoreCase("None") && userType.equalsIgnoreCase("Industry (Non-BFAR)")){
            TextView errorText = (TextView)dropdown2.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            dropdown2.requestFocus();
            Toast.makeText(getApplicationContext(), "Please indicate your company name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // UserAccessLevel assigned to their integer counterparts for the database
        switch (userType) {
            case "BFAR":
                userAccessLevel = "1";
                break;
            case "LGUs, Coastguard, Marina (Non-BFAR)":
                userAccessLevel = "2";
                break;
            case "Industry (Non-BFAR)":
                userAccessLevel = "3";
                break;
            case "Researchers, NGOs (Non-BFAR)":
                userAccessLevel = "4";
                break;
            case "Fishers (Non-BFAR)":
                userAccessLevel = "5";
                break;
        }

        //if all inputs passes all validation tests, the inputs are used to create a user
        HashMap<String, String> params = new HashMap<>();
        params.put("FirstName", firstName);
        params.put("LastName", lastName);
        params.put("UserName", userName);
        params.put("Password", password);
        params.put("UserAccessLevel", userAccessLevel);
        params.put("Verified", "0");        // 0 to indicate that the created user is not yet verified
        System.out.println("Company"+company);
        params.put("Company", company);

        //Check if the username already exists.
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_USER+userName,params,CODE_GET_REQUEST,selff);
        try {
            String s = request.execute().get();
            validateUsername(s, params);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    // can be used to verify email format
    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    // checks if the password contains a minimum of eight characters, at least one letter, one number and one special character
    public static boolean isValidPassword(String password)
    {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@$!%*#?&])[A-Za-z[0-9]@$!%*#?&]{8,}$";

        Pattern pat = Pattern.compile(passwordRegex);
        if (password == null)
            return false;
        return pat.matcher(password).matches();
    }



}
