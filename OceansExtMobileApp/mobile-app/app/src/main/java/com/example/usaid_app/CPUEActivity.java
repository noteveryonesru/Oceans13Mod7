package com.example.usaid_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.graphics.Color.rgb;

public class CPUEActivity extends AppCompatActivity {
    // provides the graphs for catch volume, fishing effort, and CPUE under the monthly landings by gear feature of the app.

    LineChart chart, scatterChart;
    BarChart mChart;
    public static String startDate;
    public static String endDate;
    public static  ArrayList<String> checkedGears;
    public static  ArrayList<ArrayList<Float>> cpueByGearYValues;
    public static String userName;
    public static String accessLevel;
    public static ArrayList<ArrayList<Float>> fishingEfforts;
    public static ArrayList<ArrayList<Float>> catchVolume;

    CPUEActivity selff = this;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpue);

        verifyStoragePermissions(selff); // checks if the app is permitted to store images in the app

        userName = getIntent().getStringExtra("USER_NAME");
        accessLevel = getIntent().getStringExtra("ACCESS_LEVEL");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        checkedGears = getIntent().getStringArrayListExtra("checkedGears");

        // API call to get values for catch volume, fishing efforts, and cpue
        getCPUEbyGear();

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();
        finishCalendar.add(Calendar.MONTH, 1);

        try {
            beginCalendar.setTime(Objects.requireNonNull(formatter.parse(startDate)));
            finishCalendar.setTime(Objects.requireNonNull(formatter.parse(endDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat formatterYd = new SimpleDateFormat("MMM");
        ArrayList<String> monthsBetween = new ArrayList<>();

        // determines the number of months between the inputted date range
        while (beginCalendar.before(finishCalendar)) {
            String date = formatterYd.format(beginCalendar.getTime()).toUpperCase();
            monthsBetween.add(date);
            // Add One Month to get next Month
            beginCalendar.add(Calendar.MONTH, 1);
        }

        // colors that may be used for the graphs (not used)
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(rgb(21,30,94));
        colors.add(rgb(28,43,127));
        colors.add(rgb(36,68,142));
        colors.add(rgb(49,107,167));
        colors.add(rgb(61,145,190));
        colors.add(rgb(70,170,206));
        colors.add(rgb(112,195,208));
        colors.add(rgb(179,221,204));

        mChart = findViewById(R.id.barchart);       // graph for catch volume
        setBarData(colors, checkedGears, monthsBetween);

        chart = findViewById(R.id.linechart);       // graph for cpue
        setLineData(colors, checkedGears, monthsBetween);
                                                        // graph for fishing effort
        scatterChart = findViewById(R.id.scatterchart); // previously rendered as scatter chart thus the variable name "scatterChart"
        setLineFishEffData(colors,checkedGears, monthsBetween);

        // set text descriptions for charts
        TextView gearDailyLanding = findViewById(R.id.gearStrDailyLandings);
        TextView gearFishEffort = findViewById(R.id.gearFishEffortStr);
        TextView gearCatchVol = findViewById(R.id.gearCatchVolumeStr);
        TextView dateDailyLanding = findViewById(R.id.dateStrDailyLandings);
        TextView dateFishEffort = findViewById(R.id.dateFishEffortStr);
        TextView dateCatchVol = findViewById(R.id.dateCatchVolumeStr);

        // formats date as dd-MMM-yyyy for chart labelling
        DateFormat formatterGraphLabel = new SimpleDateFormat("dd-MMM-yyyy");
        String labelStartDate = null;
        try {
            labelStartDate = formatterGraphLabel.format(Objects.requireNonNull(formatter.parse(startDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String labelEndDate = null;
        try {
            labelEndDate = formatterGraphLabel.format(Objects.requireNonNull(formatter.parse(endDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        gearDailyLanding.setText(String.format("by %s", checkedGears.get(0)));
        dateDailyLanding.setText(String.format("%s to %s", labelStartDate, labelEndDate));
        gearFishEffort.setText(String.format("by %s", checkedGears.get(0)));
        dateFishEffort.setText(String.format("%s to %s", labelStartDate, labelEndDate));
        gearCatchVol.setText(String.format("by %s", checkedGears.get(0)));
        dateCatchVol.setText(String.format("%s to %s", labelStartDate, labelEndDate));

        // download button for catch volume
        Button downloadButton = (Button) findViewById(R.id.downloadChart);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File direct = new File(Environment.getExternalStorageDirectory() + "/DAGAT");

                if (!direct.exists()) {
                    File newDirectory = new File("/sdcard/DAGAT/");
                    newDirectory.mkdirs();
                }

                mChart.saveToPath("catch_volume_"+System.currentTimeMillis(), "/DAGAT"); // 85 is the quality of the image
                Toast.makeText(selff, "File downloaded.", Toast.LENGTH_SHORT).show();
                Toast.makeText(selff, "File saved in the DAGAT folder.", Toast.LENGTH_SHORT).show();
            }
        });
        // download button for fishing effort
        Button downloadButton2 = (Button) findViewById(R.id.downloadChart2);
        downloadButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File direct = new File(Environment.getExternalStorageDirectory() + "/DAGAT");

                if (!direct.exists()) {
                    File newDirectory = new File("/sdcard/DAGAT/");
                    newDirectory.mkdirs();
                }

                scatterChart.saveToPath("fishing_effort_"+System.currentTimeMillis(), "/DAGAT"); // 85 is the quality of the image
                Toast.makeText(selff, "File downloaded.", Toast.LENGTH_SHORT).show();
                Toast.makeText(selff, "File saved in the DAGAT folder.", Toast.LENGTH_SHORT).show();
            }
        });
        // download button for cpue
        Button downloadButton3 = (Button) findViewById(R.id.downloadChart3);
        downloadButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File direct = new File(Environment.getExternalStorageDirectory() + "/DAGAT");

                if (!direct.exists()) {
                    File newDirectory = new File("/sdcard/DAGAT/");
                    newDirectory.mkdirs();
                }

                chart.saveToPath("cpue_"+System.currentTimeMillis(), "/DAGAT"); // 85 is the quality of the image
                Toast.makeText(selff, "File downloaded.", Toast.LENGTH_SHORT).show();
                Toast.makeText(selff, "File saved in the DAGAT folder.", Toast.LENGTH_SHORT).show();
            }
        });

        // informs the user about the interactivity of the graphs
        Toast.makeText(selff, "The graphs are interactive. You can pinch and slide on the graphs for better " +
                "data visualization.", Toast.LENGTH_LONG).show();

    }

    // API call to get the values for catch volume, fishing effort, and cpue.
    private void getCPUEbyGear(){
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "effortCoords");

        String encodedParam="";
        String checkedGearsStr = "";
        for(int i=0; i<checkedGears.size(); i++){
            checkedGearsStr = checkedGearsStr + checkedGears.get(i) + ",";
        }
        checkedGearsStr = checkedGearsStr.substring(0, checkedGearsStr.length() - 1);

        try {
            encodedParam = URLEncoder.encode(checkedGearsStr,"UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_CPUEBYGEAR+ encodedParam +"&Start_Date="+startDate+"&End_Date="+endDate, params, 1024, selff);

        try {
            String res = request.execute().get();

            //parse json array
            JSONObject response = new JSONObject(res);
            JSONArray cpueArray = response.getJSONArray("CBG");

            ArrayList<Float> tempList;
            ArrayList<Float> tempList2;
            ArrayList<Float> tempList3;
            cpueByGearYValues = new ArrayList<>();
            fishingEfforts = new ArrayList<>();
            catchVolume = new ArrayList<>();
            for (int i = 0; i < cpueArray.length(); i++) {
                tempList = new ArrayList<>();
                tempList2 = new ArrayList<>();
                tempList3 = new ArrayList<>();
                JSONObject jsonObject = cpueArray.getJSONObject(i);
                int j = 0;
                while(jsonObject.has(String.valueOf(j))){
                    JSONObject jsonObject1 = jsonObject.getJSONObject(String.valueOf(j));
                    String jsonObject2 = jsonObject1.getString("CPUE");
                    float value = Float.parseFloat(jsonObject2);
                    j++;
                    tempList.add(value);

                    jsonObject2 = jsonObject1.getString("Effort");
                    value = Float.parseFloat(jsonObject2);
                    tempList2.add(value);

                    jsonObject2 = jsonObject1.getString("Catch");
                    value = Float.parseFloat(jsonObject2);
                    tempList3.add(value);
                }
                cpueByGearYValues.add(tempList);
                fishingEfforts.add(tempList2);
                catchVolume.add(tempList3);
                System.out.println("CPUE: " + cpueByGearYValues);
                System.out.println("Fishing Efforts: " + fishingEfforts);
                System.out.println("Catch Volume: " + catchVolume);



            }

            System.out.println(cpueByGearYValues.size());


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    // verifies permission for downloading images in the phone
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    // sets the bar graph for catch volume
    public void setBarData(ArrayList colors, ArrayList<String> checkedGears, final ArrayList<String> monthsBetween) {
        ArrayList<BarEntry> yValues = new ArrayList<>();
        float[] tempArray;

        // iterates through the catch volume array list to fit the format for bar graph rendering
        for (int i = 0; i < monthsBetween.size(); i++){
            tempArray = new float[checkedGears.size()];
            for (int j = 0; j < checkedGears.size(); j++){
                tempArray[j] = (catchVolume.get(j).get(i))/1000;    // converts the values into metric tons
            }
            yValues.add(new BarEntry(i, tempArray));
        }


        XAxis xAxis = mChart.getXAxis();
        // dynamically updates the x-axis as three-letter month names
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return monthsBetween.get((int) value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12);

        // formats the Y-axis
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextSize(12);
        YAxis yAxis1 = mChart.getAxisRight();
        yAxis1.setTextSize(12);

        BarDataSet set1;

        set1 = new BarDataSet(yValues, "mt"); // legend for the bar graph
        set1.setDrawIcons(false);
        set1.setValueTextSize(12);

        String[] checkedGearsStr = new String[checkedGears.size()];
        for (int j = 0; j < checkedGears.size(); j++) {

            // Assign each value to String array
            checkedGearsStr[j] = checkedGears.get(j);
        }

        set1.setStackLabels(checkedGearsStr);
        set1.setColors(colors);

        BarData data = new BarData(set1);
        data.setValueFormatter(new MyValueFormatter());

        Legend legend = mChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);

        mChart.setData(data);
        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(false);
        mChart.animateXY(3000, 3000);
        mChart.invalidate();


    }

    // sets the line graph for CPUE
    public void setLineData(ArrayList colors, ArrayList<String> checkedGears, final ArrayList<String> monthsBetween) {
        ArrayList<Entry> dailyCatch = new ArrayList<>();

        // retrieves the values in the cpueByGearYValues array list to fit the insertion of values in the line chart
        for (int i = 0; i < monthsBetween.size(); i++){
            for (int j = 0; j < checkedGears.size(); j++){
                dailyCatch.add(new Entry(i, cpueByGearYValues.get(j).get(i)));
            }
        }

        LineDataSet dailyCatchDataset = new LineDataSet(dailyCatch, "CPUE"); // legend for the graph
        dailyCatchDataset.setColor(rgb(36,68,142));        // change this color
        dailyCatchDataset.setAxisDependency(YAxis.AxisDependency.LEFT);
        dailyCatchDataset.setDrawFilled(false);
        dailyCatchDataset.setValueTextSize(12);
        dailyCatchDataset.setCircleRadius(7);
        dailyCatchDataset.setLineWidth(5);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dailyCatchDataset);
        LineData lineData = new LineData(dataSets);
        lineData.setValueFormatter(new MyValueFormatter());


        XAxis xAxis = chart.getXAxis();
        // dynamically updates the x-axis as three-letter month names
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                    return monthsBetween.get((int) value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextSize(12);

        // formats the y-axis
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTextSize(12);
        float percent = 5;
        yAxis.setSpaceTop(percent);
        yAxis.setSpaceBottom(percent);


        Legend leg = chart.getLegend();
        leg.setWordWrapEnabled(true);
        leg.setDrawInside(false);


        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.animateXY(3000, 3000);
        chart.invalidate();
    }

    // sets the line graph for fishing effort
    public void setLineFishEffData(ArrayList colors, ArrayList<String> checkedGears, final ArrayList<String> monthsBetween) {
        ArrayList<Entry> dailyCatch = new ArrayList<>();
        // fits the values in the fishingEffort array list to the line chart
        for (int i = 0; i < monthsBetween.size(); i++){
            for (int j = 0; j < checkedGears.size(); j++){
                dailyCatch.add(new Entry(i, fishingEfforts.get(j).get(i)));
            }
        }


        LineDataSet dailyCatchDataset = new LineDataSet(dailyCatch, "days"); // legend for the graph
        dailyCatchDataset.setColor(rgb(36,68,142)); // change this color
        dailyCatchDataset.setAxisDependency(YAxis.AxisDependency.LEFT);
        dailyCatchDataset.setDrawFilled(false);
        dailyCatchDataset.setValueTextSize(12);
        dailyCatchDataset.setCircleRadius(7);
        dailyCatchDataset.setLineWidth(5);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dailyCatchDataset);
        LineData lineData = new LineData(dataSets);
        lineData.setValueFormatter(new MyValueFormatter());


        XAxis xAxis = scatterChart.getXAxis();
        // dynamically updates the x-axis as three-letter month names
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return monthsBetween.get((int) value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextSize(12);

        // formats the y-axis
        YAxis yAxis = scatterChart.getAxisLeft();
        yAxis.setTextSize(12);
        float percent = 5;
        yAxis.setSpaceTop(percent);
        yAxis.setSpaceBottom(percent);

        Legend leg = scatterChart.getLegend();
        leg.setWordWrapEnabled(true);
        leg.setDrawInside(false);

        scatterChart.setData(lineData);
        scatterChart.getDescription().setEnabled(false);
        scatterChart.getAxisRight().setEnabled(false);
        scatterChart.animateXY(3000, 3000);
        scatterChart.invalidate();

    }

    // limits the decimal value to one digit only
    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // remove decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value); // appends a percentage-sign to the values
        }
    }

}