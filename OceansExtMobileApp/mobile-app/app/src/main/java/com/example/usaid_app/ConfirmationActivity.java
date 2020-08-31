package com.example.usaid_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ConfirmationActivity extends AppCompatActivity {
    // Uses the activity_confirmation.xml layout. Informs the user about the successful sign-up process
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
    }
}
