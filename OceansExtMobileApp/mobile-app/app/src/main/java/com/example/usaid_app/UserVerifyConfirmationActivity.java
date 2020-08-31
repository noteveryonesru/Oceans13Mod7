package com.example.usaid_app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserVerifyConfirmationActivity extends AppCompatActivity {
    // Confirmation page for user verification
    // uses activity_conf_userverify.xml for layout

    UserVerifyConfirmationActivity selff = this;
    public static String actionDone;
    public static TextView messageText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_userverify);

        actionDone = getIntent().getStringExtra("Action");

        messageText = findViewById(R.id.messageConfirmation);

        // dynamically sets the message depending on the action selected on the previous activity
        if(actionDone.equals("approve")){
            messageText.setText("Accounts have been successfully approved");
        }
        else{
            messageText.setText("Accounts have been disapproved");
        }


    }
}
