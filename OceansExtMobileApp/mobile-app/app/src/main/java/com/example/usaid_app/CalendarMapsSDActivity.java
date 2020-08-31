package com.example.usaid_app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalendarMapsSDActivity extends AppCompatActivity {
    // renders the data setter page for fishing sites and species composition features

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    CalendarMapsSDActivity selff = this;
    public static String userName;
    public static String accessLevel;
    public static String companyName;
    public static String startDate;
    public static String endDate;
    public static ArrayList<String> fishingGearList;
    public static EditText editText;
    public static EditText editText1;
    public static LinearLayout linearLayout, fmaLayout, datasetLayout;
    public static RadioGroup radioGroup, datasetRadioGroup;
    public static boolean flagFMA;
    public static String chosenFMA, activityLabel;
    public static TextView dataSetterLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calmapssd);

        editText = findViewById(R.id.startDateField);       // field for start date
        editText.setOnClickListener(getDate(this));

        editText1 = findViewById(R.id.endDateField);        // field for end date
        editText1.setOnClickListener(getDate(this));

        linearLayout = findViewById(R.id.gearCheckBoxes);   // layout used for dynamically adding check boxes

        //get user info and access level
        userName = getIntent().getStringExtra("USER_NAME");
        accessLevel = getIntent().getStringExtra("ACCESS_LEVEL");

        if (accessLevel.equalsIgnoreCase("3"))
            companyName = getIntent().getStringExtra("COMPANY");
        else
            companyName=" ";

        Log.i("USER", userName);
        Log.i("ACCESS", accessLevel);

        companyName = getIntent().getStringExtra("COMPANY");
        activityLabel = getIntent().getStringExtra("Activity");

        // Dynamically adds the label for the data setter (Fishing Sites, Species Composition, Monthly Landings by Gear)
        dataSetterLabel = findViewById(R.id.dataSetterLabel);
        dataSetterLabel.setText(activityLabel);

        getGears(); // API call to get all available gears in the database

        // dynamically adds gears as check boxes into the layout
        for(int i = 0; i < fishingGearList.size(); i++) {
            CheckBox ch = new CheckBox(selff);
            ch.setText(fishingGearList.get(i));
            ch.setChecked(true);
            // adds listener to each check box
            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkFieldsForEmptyValues();    // checks fields for empty values to enable the OK button
                }
            });

            linearLayout.addView(ch);
        }


        fmaLayout = findViewById(R.id.fmaLayout);

        flagFMA = false; // boolean value used to determine if the dropdown menu has been clicked
        Spinner spinner = findViewById(R.id.fmaSpinner);
        final String[] items = new String[]{"FMA 1", "FMA 2", "FMA 3", "FMA 4", "FMA 5", "FMA 6", "FMA 7", "FMA 8",
                "FMA 9", "FMA 10", "FMA 11", "FMA 12"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(selff, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(items[position]);
                flagFMA = true;
                chosenFMA = items[position];
                checkFieldsForEmptyValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // adds the radio button for dataset choices in the data setter
        datasetLayout = findViewById(R.id.datasetLayout);
        datasetRadioGroup = new RadioGroup(selff);

        RadioButton ch = new RadioButton(selff);
        ch.setText("Municipal");
        datasetRadioGroup.addView(ch);
        ch = new RadioButton(selff);
        ch.setText("Commercial");
        datasetRadioGroup.addView(ch);

        datasetRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkFieldsForEmptyValues();
            }
        });

        datasetLayout.addView(datasetRadioGroup);

        // This is the OK button at the bottom of the data setter
        Button setDateButton = findViewById(R.id.setDateButton);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                startDate = editText.getText().toString();
                endDate = editText1.getText().toString();

                ArrayList<String> AllCheckedBox = new ArrayList<>();
                // gets all the checked gears by retrieving the string corresponding the check box selected
                for(int i=0; i < linearLayout.getChildCount(); i++) {
                    View nextChild = linearLayout.getChildAt(i);

                    if (nextChild instanceof CheckBox)
                    {
                        CheckBox check = (CheckBox) nextChild;
                        if (check.isChecked()) {
                            AllCheckedBox.add(check.getText().toString());
                        }
                    }
                }

                ArrayList<String> AllCheckedBox2 = new ArrayList<>();
                // gets the data set by retrieving the string corresponding the radio button selected
                for(int i=0; i < datasetRadioGroup.getChildCount(); i++) {
                    View nextChild = datasetRadioGroup.getChildAt(i);

                    if (nextChild instanceof RadioButton)
                    {
                        RadioButton check = (RadioButton) nextChild;
                        if (check.isChecked()) {
                            AllCheckedBox2.add(check.getText().toString());
                        }
                    }
                }

                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                Calendar beginCalendar = Calendar.getInstance();
                Calendar finishCalendar = Calendar.getInstance();
                finishCalendar.add(Calendar.MONTH, 1);

                try {
                    beginCalendar.setTime(formatter.parse(startDate));
                    finishCalendar.setTime(formatter.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                DateFormat formatterYd = new SimpleDateFormat("MMM");
                ArrayList<String> monthsBetween = new ArrayList<>();

                // gets the number of months between the date range selected by the user
                while (beginCalendar.before(finishCalendar)) {
                    String date = formatterYd.format(beginCalendar.getTime()).toUpperCase();
                    monthsBetween.add(date);
                    // Add One Month to get next Month
                    beginCalendar.add(Calendar.MONTH, 1);
                }

                // Checks the previous intent to determine whether the data setter was used for Maps or for SPC
                String flag = String.valueOf(getIntent().getStringExtra("Flag"));
                if(flag.equalsIgnoreCase("ForMapsActivity")){
                    if(monthsBetween.size() > 1) { // considers at least a month range
                        Intent newHeatMapsIntent = new Intent(selff, MapsActivity.class);
                        newHeatMapsIntent.putExtra("startDate", startDate);
                        newHeatMapsIntent.putExtra("endDate", endDate);
                        newHeatMapsIntent.putExtra("USER_NAME", userName);
                        newHeatMapsIntent.putExtra("ACCESS_LEVEL", accessLevel);
                        newHeatMapsIntent.putExtra("checkedGears", AllCheckedBox);
                        newHeatMapsIntent.putExtra("chosenFMA", chosenFMA);
                        newHeatMapsIntent.putExtra("chosenDataset", AllCheckedBox2);

                        if (accessLevel.equalsIgnoreCase("3"))
                            newHeatMapsIntent.putExtra("COMPANY", companyName);

                        newHeatMapsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(newHeatMapsIntent);       // passes intent to MapsActivity
                        finish();
                    }
                    else {  // informs the user to give at least a month range
                        Toast.makeText(selff, "Invalid date range. Please give at least 32-day range.", Toast.LENGTH_LONG).show();
                    }

                }else if (flag.equalsIgnoreCase("ForSpcActivity")){
                    if(monthsBetween.size() > 1) {  // considers at least a month range
                        Intent newSpcIntent = new Intent(selff, SPCActivity.class);
                        newSpcIntent.putExtra("USER_NAME", userName);
                        newSpcIntent.putExtra("ACCESS_LEVEL", accessLevel);
                        newSpcIntent.putExtra("startDate", startDate);
                        newSpcIntent.putExtra("endDate", endDate);
                        newSpcIntent.putExtra("checkedGears", AllCheckedBox);
                        newSpcIntent.putExtra("chosenFMA", chosenFMA);
                        newSpcIntent.putExtra("chosenDataset", AllCheckedBox2);

                        if (accessLevel.equalsIgnoreCase("3"))
                            newSpcIntent.putExtra("COMPANY", companyName);

                        newSpcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(newSpcIntent);    // passes intent to SPCActivity
                        finish();
                    }
                    else { // informs users to give at least a month range
                        Toast.makeText(selff, "Invalid date range. Please give at least 32-day range.", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        editText.addTextChangedListener(textWatcher);
        editText1.addTextChangedListener(textWatcher);
        checkFieldsForEmptyValues();

    }

    // monitors changes on the text fields
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkFieldsForEmptyValues();
        }
    };


    // checks the fields for empty values. If there are no fields left blank, the OK button is enabled
    void checkFieldsForEmptyValues(){
        Button b = findViewById(R.id.setDateButton);
        boolean flag = true;

        String s1 = editText.getText().toString();
        String s2 = editText1.getText().toString();

        ArrayList<String> AllCheckedBox = new ArrayList<>();

        for (int i=0; i<linearLayout.getChildCount(); i++){
            View nextChild = linearLayout.getChildAt(i);

            if (nextChild instanceof CheckBox){
                CheckBox checkBox = (CheckBox) nextChild;
                if (checkBox.isChecked()){
                    AllCheckedBox.add(checkBox.getText().toString());
                }
            }
        }

        if (AllCheckedBox.size() == 0){
            flag = false;
        }

        boolean datasetFlag = true;
        if (datasetRadioGroup.getCheckedRadioButtonId() == -1){
            datasetFlag = false;
        }

        if(s1.equals("")|| s2.equals("") || flag == false || flagFMA == false || datasetFlag == false){
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

    // API call to get the available gears in the database
    private void getGears(){
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "effortCoords");
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_GEARS, params, 1024, selff);

        try {
            String res = request.execute().get();
            System.out.println(res);

            //parse json array
            JSONObject response = new JSONObject(res);
            JSONArray gearArray = response.getJSONArray("FishingGear");
            System.out.println(gearArray);

            fishingGearList = new ArrayList<>();
            for(int i = 0; i < gearArray.length(); i++){
                String gear = gearArray.getString(i);
                fishingGearList.add(gear);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // used to invoke Calendar upon clicking the text fields for start and end date
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
