package com.example.usaid_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CPUEMenuActivity extends AppCompatActivity {
    // Uses activity_cpue_menu.xml as layout. This renders the view with the monthly landings by gear button

    CPUEMenuActivity selff = this;
    public static TextView uName, aLevel;

    public static String userName;
    public static String accessLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpue_menu);

        uName = findViewById(R.id.userName);
        aLevel = findViewById(R.id.accessLevel);

        userName = getIntent().getStringExtra("USER_NAME");
        accessLevel = getIntent().getStringExtra("ACCESS_LEVEL");

        String stringAccessLevel = "";

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

        // dynamically sets the name and access level for the TextViews above the buttons
        uName.setText(String.format("Hello, %s!", userName));
        aLevel.setText(stringAccessLevel);

        // button for monthly landings by gear
        LinearLayout gearButton = findViewById(R.id.spcButton);
        gearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newCpueActivity = new Intent(selff, CalendarActivity.class);
                newCpueActivity.putExtra("Flag", "ForCpueGearActivity");
                newCpueActivity.putExtra("USER_NAME", userName);
                newCpueActivity.putExtra("ACCESS_LEVEL", accessLevel);
                newCpueActivity.putExtra("Activity", "Monthly Landings by Gear");
                startActivity(newCpueActivity);
            }
        });

    }
}
