package com.example.usaid_app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DataFormActivity extends AppCompatActivity {
    // DataFormActivity uses the layout activity_dataform.xml.
    // This is the activity that renders the form for receiving new inputs for the database

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    DataFormActivity selff = this;
    EditText dateOfCatchField, degreeLatField, decimalLatField, degreeLongField, decimalLongField, schoolCodeField,
            volCatchField, catchidField;
    String dateOfCatch, schoolCode, volumeCatch, degreeLat, decimalLat, degreeLong, decimalLong, latCoordinate, longCoordinate, catchID,
            latestCatchID;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataform);

        getLatestCatchID();

        dateOfCatchField = findViewById(R.id.dateOfCatchEditText);
        dateOfCatchField.setOnClickListener(getDate(selff));
        degreeLatField = findViewById(R.id.degreeLatEditText);
        decimalLatField = findViewById(R.id.decimalLatEditText);
        degreeLongField = findViewById(R.id.degreeLongEditText);
        decimalLongField = findViewById(R.id.decimalLongEditText);
        schoolCodeField = findViewById(R.id.schCodeEditText);
        volCatchField = findViewById(R.id.volCatchEditText);
        catchidField = findViewById(R.id.catchIDeditText);

        // listener for the submit button found at the bottom of the view
        Button submitButton = findViewById(R.id.insertDataButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get inputs from the text fields
                dateOfCatch = dateOfCatchField.getText().toString();
                degreeLat = degreeLatField.getText().toString();
                decimalLat = decimalLatField.getText().toString();
                degreeLong = degreeLongField.getText().toString();
                decimalLong = decimalLongField.getText().toString();
                schoolCode = schoolCodeField.getText().toString();
                volumeCatch = volCatchField.getText().toString();
                catchID = catchidField.getText().toString();


                if(dateOfCatch.equals("")){
                    dateOfCatchField.setError("Please enter a valid date of catch");
                    dateOfCatchField.requestFocus();
                    return;
                }
                if(degreeLat.equals("")){
                    degreeLatField.setError("Please enter a valid degree value for latitude");
                    degreeLatField.requestFocus();
                    return;
                }
                if(decimalLat.equals("")){
                    decimalLatField.setError("Please enter a valid decimal value for latitude");
                    decimalLatField.requestFocus();
                    return;
                }
                if(degreeLong.equals("")){
                    degreeLongField.setError("Please enter a valid degree value for longitude");
                    degreeLongField.requestFocus();
                    return;
                }
                if(decimalLong.equals("")){
                    decimalLongField.setError("Please enter a valid decimal value for longitude");
                    decimalLongField.requestFocus();
                    return;
                }
                if(schoolCode.equals("")){
                    schoolCodeField.setError("Please enter a valid school code");
                    schoolCodeField.requestFocus();
                    return;
                }
                if(volumeCatch.equals("")){
                    volCatchField.setError("Please enter a valid catch volume");
                    volCatchField.requestFocus();
                    return;
                }
                // catchID should be an integer higher than the latest Catch ID
                if((catchID.equals("")) || (Integer.parseInt(catchID) > Integer.parseInt(latestCatchID))){
                    catchidField.setError("Please enter a valid Catch ID");
                    catchidField.requestFocus();
                    return;
                }


                // Output printing on the terminal for checking
                System.out.println("Date of Catch: " + dateOfCatch);
                System.out.println("Degree Lat: " + degreeLat);
                System.out.println("Decimal Lat: " + decimalLat);
                System.out.println("Degree Long: " + degreeLong);
                System.out.println("Decimal Long" + decimalLong);
                System.out.println("School Code: " + schoolCode);
                System.out.println("Volume Catch: " + volumeCatch);
                System.out.println("Catch ID: " + catchID);

                // clears the text after insertion to be able to receive new inputs
                dateOfCatchField.getText().clear();
                degreeLatField.getText().clear();
                decimalLatField.getText().clear();
                degreeLongField.getText().clear();
                decimalLongField.getText().clear();
                schoolCodeField.getText().clear();
                volCatchField.getText().clear();
                catchidField.getText().clear();

                float temp; // Latitude and Longitude formatting
                temp = Float.parseFloat(degreeLat) + (Float.parseFloat(decimalLat)/60);
                latCoordinate = String.valueOf(temp);
                temp = Float.parseFloat(degreeLong) + (Float.parseFloat(decimalLong)/60);
                longCoordinate = String.valueOf(temp);

                System.out.println("Latitude: " + latCoordinate);
                System.out.println("Longitude: " + longCoordinate);

                // Posts the inputs to the database
                HashMap<String, String> params = new HashMap<>();
                params.put("DateOfCatch", dateOfCatch);
                params.put("Coordinates_Long", latCoordinate);
                params.put("Coordinates_Lat", longCoordinate);
                params.put("SchoolCode", schoolCode);
                params.put("VolumeCatch", volumeCatch);
                params.put("CatchID", catchID);    //0 is not verified but I make it 1 so that you can log in

                //Check if the username already exists.
                PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_FISHING_EFFORT,params,CODE_POST_REQUEST,selff);
                try {
                    String s = request.execute().get();
                    Toast.makeText(getApplicationContext(), "Successfully added.", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    // API call to get the latest catch id
    private void getLatestCatchID(){
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "effortCoords");
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_LATESTCATCHID, params, 1024, selff);

        try {
            String res = request.execute().get();
            System.out.println(res);

            //parse json array
            JSONObject response = new JSONObject(res);
            latestCatchID = response.getString("Latest_CatchID");
            System.out.println("Latest Catch ID: " + latestCatchID);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // renders the Calendar upon clicking the date fields
    public View.OnClickListener getDate(final Context context){

        return new View.OnClickListener() {

            @Override
            public void onClick(View view){
                final Calendar startCalendar = Calendar.getInstance();
                final EditText editText = (EditText) view;
                String dateString = editText.getText().toString();

                if (!dateString.isEmpty()){
                    try {
                        Date date = sdf.parse(dateString);
                        startCalendar.setTime(date);



                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                final int day = startCalendar.get(Calendar.DAY_OF_MONTH);
                int month = startCalendar.get(Calendar.MONTH);
                int year = startCalendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.AlertDialogTheme ,new DatePickerDialog.OnDateSetListener(){
                    public void onDateSet (DatePicker view, int year, int month, int dayOfMonth){
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startCalendar.set(Calendar.MONTH, month);
                        startCalendar.set(Calendar.YEAR, year);
                        editText.setText(sdf.format(startCalendar.getTime()));
                    }
                }, year, month, day);

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        };

    }
}
