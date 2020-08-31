package com.example.usaid_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnalyticsActivity extends AppCompatActivity {
    // This class works with the layout activity_analytics.xml.
    // It renders the menu containing species composition and monthly landings.

    public static String userName;
    public static String accessLevel;
    private static String companyName;
    AnalyticsActivity selff = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // find IDs for username and access level to replace it with the values passed on intents.
        TextView uName = findViewById(R.id.userName);
        TextView aLevel = findViewById(R.id.accessLevel);

        userName = getIntent().getStringExtra("USER_NAME");
        accessLevel = getIntent().getStringExtra("ACCESS_LEVEL");
        if (accessLevel != null) {
            if (accessLevel.equalsIgnoreCase("3"))
                companyName = getIntent().getStringExtra("COMPANY");
            else
                companyName = "";
        }

        String stringAccessLevel = "";
        if (accessLevel != null) {
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
        }

        uName.setText(String.format("Hello, %s!", userName));
        aLevel.setText(stringAccessLevel);

        // This button passes intent to the data setter CalendarMapsSD which then leads to SPC.
        LinearLayout mSpcAnalyticsButton = findViewById(R.id.spcButton);
        mSpcAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newSpcActivity = new Intent(selff, CalendarMapsSDActivity.class);
                newSpcActivity.putExtra("Flag", "ForSpcActivity");
                newSpcActivity.putExtra("USER_NAME", userName);
                newSpcActivity.putExtra("ACCESS_LEVEL", accessLevel);
                newSpcActivity.putExtra("COMPANY", companyName);
                newSpcActivity.putExtra("Activity", "Species Composition");
                startActivity(newSpcActivity);
            }
        });
        // This button passes intent to another menu rendered by CPUEMenu. CPUEMenu contains the button for monthly landing by gear.
        LinearLayout mCpueAnalyticsButton = findViewById(R.id.cpueButton);
        mCpueAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newCpueMenuActivity = new Intent(selff, CPUEMenuActivity.class);
                newCpueMenuActivity.putExtra("USER_NAME", userName);
                newCpueMenuActivity.putExtra("ACCESS_LEVEL", accessLevel);
                newCpueMenuActivity.putExtra("COMPANY", companyName);
                startActivity(newCpueMenuActivity);
            }
        });
    }

}
