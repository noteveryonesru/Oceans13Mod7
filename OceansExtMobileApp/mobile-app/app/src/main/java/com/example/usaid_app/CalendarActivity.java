package com.example.usaid_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class CalendarActivity extends AppCompatActivity {
    // This class renders a data setter page for the monthly landings feature

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    CalendarActivity selff = this;
    public static String userName;
    public static String accessLevel;
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
        setContentView(R.layout.activity_calendar);

        editText = findViewById(R.id.startDateField);       // text field for start date
        editText.setOnClickListener(getDate(this));

        editText1 = findViewById(R.id.endDateField);        // text field for end date
        editText1.setOnClickListener(getDate(this));

        linearLayout = findViewById(R.id.gearCheckBoxes);   // layout used for dynamically adding radio buttons

        //get user info and access level
        userName = getIntent().getStringExtra("USER_NAME");
        accessLevel = getIntent().getStringExtra("ACCESS_LEVEL");
//        activityLabel = getIntent().getStringExtra("Activity");
        activityLabel = "Data Analytics"; // added for stripped down version

        // Dynamically adds the label for the data setter (Fishing Sites, Species Composition, Monthly Landings by Gear)
        dataSetterLabel = findViewById(R.id.dataSetterLabel);
        dataSetterLabel.setText(activityLabel);

        getGears(); // invokes API call to get existing gears in the database

        // adds gears dynamically as radio button
        radioGroup = new RadioGroup(selff);
        for(int i = 0; i < fishingGearList.size(); i++) {
            RadioButton ch = new RadioButton(selff);
            ch.setText(fishingGearList.get(i));
            radioGroup.addView(ch);
        }
        // add listener to each option in the radio button to check for empty fields
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkFieldsForEmptyValues();
            }
        });

        linearLayout.addView(radioGroup);


        fmaLayout = findViewById(R.id.fmaLayout);

        flagFMA = false;    // boolean value used to check if a value is selected in the dropdown menu for FMA Location
        Spinner spinner = findViewById(R.id.fmaSpinner);
        final String[] items = new String[]{"FMA 1", "FMA 2", "FMA 3", "FMA 4", "FMA 5", "FMA 6", "FMA 7",
                "FMA 8", "FMA 9", "FMA 10", "FMA 11", "FMA 12"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(selff, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(items[position]);
                chosenFMA = items[position];        // store the chosen FMA in the spinner/dropdown menu
                flagFMA = true;
                checkFieldsForEmptyValues();        // check again for empty values: if there are no empty values, enable OK button
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // adds radio buttons for dataset (municipal or commercial)
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

        // this is the OK button at the bottom of this data setter
        Button setDateButton = findViewById(R.id.setDateButton);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startDate = editText.getText().toString();
                endDate = editText1.getText().toString();

                ArrayList<String> AllCheckedBox = new ArrayList<>();

                for(int i=0; i < radioGroup.getChildCount(); i++) {
                    View nextChild = radioGroup.getChildAt(i);
                    // gets the string corresponding the radio button selected by the user
                    if (nextChild instanceof RadioButton)
                    {
                        RadioButton check = (RadioButton) nextChild;
                        if (check.isChecked()) {
                            AllCheckedBox.add(check.getText().toString());
                        }
                    }
                }

                ArrayList<String> AllCheckedBox2 = new ArrayList<>();

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

                // double checks the previous activity based on string passed through intent
//                String flag = String.valueOf(getIntent().getStringExtra("Flag"));
                String flag = "ForCpueGearActivity"; // added for stripped down version
                if (flag.equalsIgnoreCase("ForCpueGearActivity")){
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

                    DateFormat formatterYd = new SimpleDateFormat("MMM");   // formats months to three-letter format
                    ArrayList<String> monthsBetween = new ArrayList<>();

                    while (beginCalendar.before(finishCalendar)) {  // gets the number of months based on date range

                        String date = formatterYd.format(beginCalendar.getTime()).toUpperCase();
                        monthsBetween.add(date);
                        // Add One Month to get next Month
                        beginCalendar.add(Calendar.MONTH, 1);
                    }


                    if(monthsBetween.size() > 1) {   // graph throws error when the months are <= 1
                        Intent newGearIntent = new Intent(selff, CPUEActivity.class);
                        newGearIntent.putExtra("USER_NAME", userName);
                        newGearIntent.putExtra("ACCESS_LEVEL", accessLevel);
                        newGearIntent.putExtra("startDate", startDate);
                        newGearIntent.putExtra("endDate", endDate);
                        newGearIntent.putExtra("checkedGears", AllCheckedBox);
                        newGearIntent.putExtra("chosenFMA", chosenFMA);
                        newGearIntent.putExtra("chosenDataset", AllCheckedBox2);
                        startActivity(newGearIntent);
                        finish();
                    }
                    else {      // ensures that the user selected at least a month date range
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


    // checks fields for empty values to determine if the OK button can be enabled
    void checkFieldsForEmptyValues(){
        Button b = findViewById(R.id.setDateButton);

        String s1 = editText.getText().toString();
        String s2 = editText1.getText().toString();

        boolean flag = true;
        if (radioGroup.getCheckedRadioButtonId() == -1){
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

    // API call for getting all available gears
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

    // used to call a calendar when the text field is clicked
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
