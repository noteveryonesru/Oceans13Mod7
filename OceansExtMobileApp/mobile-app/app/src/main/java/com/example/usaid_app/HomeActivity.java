package com.example.usaid_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class HomeActivity extends AppCompatActivity {
    // HomeActivity uses the layout activity_home.xml
    // This renders the menu right after the log-in page IF the user's access level is BFAR

    private LinearLayout mDataAnalyticsButton;
    private LinearLayout mHeatMapButton;
    private LinearLayout mDataFormButton, verifyUsersButton;
    public static TextView uName, aLevel;
    public static String user;
    public static String userName;
    public static String companyName;
    public static String accessLevel;
    public static String ipAddress;
    AlertDialog.Builder builder;

    HomeActivity selff = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = getIntent().getStringExtra("USER");
        userName = getIntent().getStringExtra("USER_NAME");
        accessLevel = getIntent().getStringExtra("ACCESS_LEVEL");

        mDataAnalyticsButton = findViewById(R.id.analyticsButton);
        mHeatMapButton = findViewById(R.id.mapButton);
//        mDataFormButton = findViewById(R.id.dataFormButton);
//        verifyUsersButton = findViewById(R.id.verifyUserButton);
        uName = findViewById(R.id.userName);
        aLevel = findViewById(R.id.accessLevel);

        String stringAccessLevel = "";
        // switch case to get the equivalent string of the access levels in integer format
        switch(accessLevel){
            case "1":
                stringAccessLevel = "BFAR";
                break;
            case "2":
                stringAccessLevel = "LGUs, Coastguard and Marina";
                break;
            case "3":
                companyName = getIntent().getStringExtra("COMPANY");
                stringAccessLevel = "Industry";
                break;
            case "4":
                stringAccessLevel = "Researchers and NGOs";
                break;
            case "5":
                stringAccessLevel = "Fishers";
                break;
        }
        // sets the user name and access level on the text views above the buttons
        uName.setText(String.format("Hello, %s!", userName));
        aLevel.setText(stringAccessLevel);

//        // button for data analytics
        mDataAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent newAnalyticsIntent = new Intent(selff, AnalyticsActivity.class); // redirects to the data analytics menu
                Intent newAnalyticsIntent = new Intent(selff, CalendarActivity.class); // redirect to data setter
                newAnalyticsIntent.putExtra("USER_NAME", userName);
                newAnalyticsIntent.putExtra("ACCESS_LEVEL", accessLevel);

                startActivity(newAnalyticsIntent);
            }
        });
//
//        // button for insert data
//        mDataFormButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent newAnalyticsIntent = new Intent(selff, DataFormActivity.class);  // redirects to the insert data page
//                newAnalyticsIntent.putExtra("USER_NAME", userName);
//                newAnalyticsIntent.putExtra("ACCESS_LEVEL", accessLevel);
//
//                startActivity(newAnalyticsIntent);
//            }
//        });
//
//        // button for verify users
//        verifyUsersButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent newVerifyUsersIntent = new Intent(selff, UserVerificationActivity.class); // redirects to the verify users page
//                newVerifyUsersIntent.putExtra("USER_NAME", userName);
//                newVerifyUsersIntent.putExtra("ACCESS_LEVEL", accessLevel);
//
//                startActivity(newVerifyUsersIntent);
//            }
//        });

        // button for fishing sites
        mHeatMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent newHeatMapsIntent = new Intent(selff,CalendarMapsSDActivity.class);  // redirects to the data setter for maps
                newHeatMapsIntent.putExtra("Flag", "ForMapsActivity");
                newHeatMapsIntent.putExtra("USER_NAME", userName);
                newHeatMapsIntent.putExtra("ACCESS_LEVEL", accessLevel);
                newHeatMapsIntent.putExtra("Activity", "Fishing Sites");
                if (accessLevel.equalsIgnoreCase("3"))
                    newHeatMapsIntent.putExtra("COMPANY",companyName);
                startActivity(newHeatMapsIntent);
            }
        });

    }
}
