package com.example.usaid_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserVerificationActivity extends AppCompatActivity {
    // uses activity_verification.xml as layout
    // lists all the users with unverified status for verification by a user with BFAR access level

    UserVerificationActivity selff = this;

    public static ArrayList<String> userIDs;
    public static ArrayList<String> firstNames;
    public static ArrayList<String> lastNames;
    public static ArrayList<String> userNames;
    public static ArrayList<String> accessLevels;
    public static LinearLayout usersLayout;
    public static Button approveButton, declineButton;
    public static String approvedUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // API call to get all users with unverified users
        getUserNotVerified();

        usersLayout = findViewById(R.id.usersLayout);
        approveButton = findViewById(R.id.approveButton);
        declineButton = findViewById(R.id.declineButton);

        // lists all unverified as check box
        for(int i = 0; i < userIDs.size(); i++) {
            CheckBox ch = new CheckBox(selff);
            ch.setId(Integer.parseInt(userIDs.get(i)));
            ch.setPadding(20, 0, 0, 30);

            String stringAccessLevel = "";

            switch(accessLevels.get(i)){
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

            ch.setText(String.format("User ID: %s\nName: %s %s\nUsername: %s\nUser Access Level: %s",
                    userIDs.get(i), firstNames.get(i), lastNames.get(i), userNames.get(i), stringAccessLevel));
            ch.setChecked(true);

            usersLayout.addView(ch);
        }

        // approve button to set unverified status to verified
        approveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ArrayList<String> AllCheckedBox = new ArrayList<>();

                // gets the string corresponding the selected check boxes
                for (int i=0; i<usersLayout.getChildCount(); i++){
                    View nextChild = usersLayout.getChildAt(i);

                    if (nextChild instanceof CheckBox){
                        CheckBox checkBox = (CheckBox) nextChild;
                        if (checkBox.isChecked()){
                            int endIndex = checkBox.getText().toString().indexOf("\n");
                            AllCheckedBox.add(checkBox.getText().toString().substring(9, endIndex));
                        }
                    }
                }

                // checks if the user selected at least one check box
                if(AllCheckedBox.size()>0) {
                    approvedUsers = "";
                    for (int i = 0; i < AllCheckedBox.size(); i++) {
                        approvedUsers = approvedUsers + AllCheckedBox.get(i) + ",";
                    }
                    approvedUsers = approvedUsers.substring(0, approvedUsers.length() - 1);
                    System.out.println("Approved Users: " + approvedUsers);

                    updateVerifiedUser(); // API call to update status of users to verified

                    Intent intent = new Intent(selff,UserVerifyConfirmationActivity.class);
                    intent.putExtra("Action", "approve");
                    startActivity(intent);
                    finish();
                }
            }
        });

        // decline button (currently takes the names of the declined requests but does not remove it from the database: remains unverified)
        // UPDATE: this button deletes user when decline button is clicked
        declineButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ArrayList<String> AllCheckedBox = new ArrayList<>();
                
                // gets the string corresponding the selected check boxes
                for (int i=0; i<usersLayout.getChildCount(); i++){
                    View nextChild = usersLayout.getChildAt(i);

                    if (nextChild instanceof CheckBox){
                        CheckBox checkBox = (CheckBox) nextChild;
                        if (checkBox.isChecked()){
                            int endIndex = checkBox.getText().toString().indexOf("\n");
                            AllCheckedBox.add(checkBox.getText().toString().substring(9, endIndex));
                        }
                    }
                }

                if(AllCheckedBox.size()>0) {
                    for (int i = 0; i < AllCheckedBox.size(); i++) {
                        deleteUser(AllCheckedBox.get(i));
                        System.out.println("Deleted User: " + AllCheckedBox.get(i));
                    }

                    Intent intent = new Intent(selff, UserVerifyConfirmationActivity.class);
                    intent.putExtra("Action", "decline");
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    // API call to update user's status to verified
    private void updateVerifiedUser() {
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "effortCoords");
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_VERIFIEDUSER+approvedUsers,
                params, 1024, selff);

        try {
            String res = request.execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // API call to delete user
    private void deleteUser(String userToBeDeleted){
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "effortCoords");
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_USER+userToBeDeleted,
                params, 1024, selff);

        try {
            String res = request.execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // API call to get all unverified users
    private void getUserNotVerified(){
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "effortCoords");
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_USERNOTVERIFIED, params, 1024,selff);

        try {
            String res = request.execute().get();
            System.out.println(res);

            //parse json array
            JSONObject response = new JSONObject(res);
            JSONArray usersArray = response.getJSONArray("UserNotVerified");

            userIDs = new ArrayList<>();
            firstNames = new ArrayList<>();
            lastNames = new ArrayList<>();
            userNames = new ArrayList<>();
            accessLevels = new ArrayList<>();
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject jsonObject = usersArray.getJSONObject(i);
                userIDs.add(jsonObject.getString("UserId"));
                firstNames.add(jsonObject.getString("FirstName"));
                lastNames.add(jsonObject.getString("LastName"));
                userNames.add(jsonObject.getString("UserName"));
                accessLevels.add(jsonObject.getString("UserAccessLevel"));
            }
                System.out.println("user id: " + userIDs);
                System.out.println("first name: " + firstNames);
                System.out.println("last name: " + lastNames);
                System.out.println("user name: " + userNames);
                System.out.println("access level: " + accessLevels);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
