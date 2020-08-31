package com.example.usaid_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeNonBfarActivity extends AppCompatActivity {
    // HomeNonBfarActivity uses the layout activity_homenonbfar.xml
    // This renders the menu right after the log-in page IF the user's access level is Non-BFAR

    HomeNonBfarActivity selff = this;

    private LinearLayout mDataAnalyticsButton;
    private LinearLayout mHeatMapButton;
    public static TextView uName, aLevel;
    public static String userName;
    public static String accessLevel;
    public static String companyName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homenonbfar);

        mDataAnalyticsButton = findViewById(R.id.analyticsButton);
        mHeatMapButton = findViewById(R.id.mapButton);
        uName = findViewById(R.id.userName);
        aLevel = findViewById(R.id.accessLevel);

        userName = getIntent().getStringExtra("USER_NAME");
        accessLevel = getIntent().getStringExtra("ACCESS_LEVEL");

        if (accessLevel.equalsIgnoreCase("3"))
            companyName = getIntent().getStringExtra("COMPANY");
        else
            companyName = "";

        String stringAccessLevel = "";

        Log.i("USER", userName);
        Log.i("ACCESS", accessLevel);
        Log.i("COMPANY", companyName);

        // switch case to get the equivalent string of the access levels in integer format
        switch(accessLevel){
            case "1":
                stringAccessLevel = "BFAR";
                break;
            case "2":
                stringAccessLevel = "LGUs, Coastguard and Marina";
                break;
            case "3":
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

        // button for data analytics
        mDataAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent newAnalyticsIntent = new Intent(selff, AnalyticsActivity.class); // redirects to another menu for data analytics
                Intent newAnalyticsIntent = new Intent(selff, CalendarActivity.class); // redirect to data setter
                newAnalyticsIntent.putExtra("USER_NAME", userName);
                newAnalyticsIntent.putExtra("ACCESS_LEVEL", accessLevel);
                if (accessLevel.equalsIgnoreCase("3"))
                    newAnalyticsIntent.putExtra("COMPANY", companyName);

                startActivity(newAnalyticsIntent);
            }
        });

        // button for fishing sites
        mHeatMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent newHeatMapsIntent = new Intent(selff,CalendarMapsSDActivity.class); // redirects to a data setter for maps
                newHeatMapsIntent.putExtra("Flag", "ForMapsActivity");
                newHeatMapsIntent.putExtra("USER_NAME", userName);
                newHeatMapsIntent.putExtra("ACCESS_LEVEL", accessLevel);

                if (accessLevel.equalsIgnoreCase("3"))
                    newHeatMapsIntent.putExtra("COMPANY", companyName);

                newHeatMapsIntent.putExtra("Activity", "Fishing Sites");
                startActivity(newHeatMapsIntent);
            }
        });

    }
}
