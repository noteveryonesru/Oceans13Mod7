package com.example.usaid_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import java.util.HashMap;
import java.util.Objects;

import static android.graphics.Color.rgb;


public class SPCActivity extends AppCompatActivity {
    // Uses the activity_spc.xml as layout
    // View for the species composition rendered as pie chart

    PieChart pieChart;
    SPCActivity selff = this;
    public static String startDate;
    public static String endDate;
    public static  ArrayList<String> checkedGears;
    public static float SKJdist;
    public static float YFTdist;
    public static float BETdist;
    public static float BLT_FRTdist;
    public static float KAWdist;
    public static float MSDdist;
    public static float MIXdist;
    public static TextView gearLabel, dateLabel;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spc);

        verifyStoragePermissions(selff);

        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        checkedGears = getIntent().getStringArrayListExtra("checkedGears");

        gearLabel = findViewById(R.id.gearsStrSpeciesComp);
        dateLabel = findViewById(R.id.dateStrSpeciesComp);

        // formats the start date and end date for graph labelling
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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

        // substring is used to lose brackets from string-converted array list
        gearLabel.setText(String.format("by %s", checkedGears.toString().substring(1, checkedGears.toString().length() - 1)));
        dateLabel.setText(labelStartDate + " to " + labelEndDate);

        // API call to get the values for species composition (earlier code versions calls species composition as species distribution)
        getSpeciesDistributionByGear();

        pieChart = findViewById(R.id.piechart); // assigns the pie chart fragment

        ArrayList<Integer> colors = new ArrayList<Integer>();
        //TODO: update these colors (piechart)
        colors.add(rgb(239,83,80));
        colors.add(rgb(240,98,146));
        colors.add(rgb(206,147,216));
        colors.add(rgb(140,158,255));
        colors.add(rgb(187,222,251));
        colors.add(rgb(21,101,142));
        colors.add(rgb(79,195,247));
        colors.add(rgb(0,131,143));
        colors.add(rgb(77,182,172));
        colors.add(rgb(102, 187,106));
        colors.add(rgb(255,143,0));
        colors.add(rgb(255,112,67));
        colors.add(rgb(62,39,35));
        colors.add(rgb(66,66,66));


        // gets the total composition for all species for percentage computation
        float total = SKJdist + YFTdist + BETdist + KAWdist + BLT_FRTdist + MSDdist + MIXdist;
        System.out.println("TOTAL CATCH: " + total);
        ArrayList<PieEntry> yValues = new ArrayList<>();
        // if the total equals 0 the graph will place a message showing that there are no data for the selected date range
        // this approach screens percentage values lower than 1% which is not rendered in the pie chart
        if(total > 0) {
            if ((SKJdist / total)*100 >= 1) {
                yValues.add(new PieEntry(SKJdist, "SKJ", "SKJ"));
            }
            if ((YFTdist / total)*100 >= 1) {
                yValues.add(new PieEntry(YFTdist, "YFT", "YFT"));
            }
            if ((BETdist / total)*100 >= 1) {
                yValues.add(new PieEntry(BETdist, "BET", "BET"));
            }
            if ((KAWdist / total)*100 >= 1) {
                yValues.add(new PieEntry(KAWdist, "KAW", "KAW"));
            }
            if ((BLT_FRTdist / total)*100 >= 1) {
                yValues.add(new PieEntry(BLT_FRTdist, "BLT_FRT", "BLT_FRT"));
            }
            if ((MSDdist / total)*100 >= 1) {
                yValues.add(new PieEntry(MSDdist, "MSD", "MSD"));
            }
            if ((MIXdist / total)*100 >= 1) {
                yValues.add(new PieEntry(MIXdist, "MIX", "MIX"));
            }

            // setting up the pie chart for species composition
            PieDataSet dataSet = new PieDataSet(yValues, "Species");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colors);
            dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            dataSet.setValueLinePart1OffsetPercentage(14.f);
            dataSet.setValueLinePart1Length(0.7f);
            dataSet.setValueLinePart2Length(.2f);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            PieData data = new PieData((dataSet));
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            data.setValueFormatter(new MyValueFormatter());


            Legend legend = pieChart.getLegend();
            legend.setWordWrapEnabled(true);
            legend.setDrawInside(false);


            pieChart.setData(data);
            pieChart.animateXY(1500, 1500);
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setDragDecelerationFrictionCoef(0.95f);
            pieChart.setDrawHoleEnabled(false);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleRadius(61f);
            pieChart.setEntryLabelColor(Color.BLACK);

            // shows an alert dialog box showing the value of the species in metric tons on pie click
            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    String catchValue = String.valueOf(e.getY());
                    System.out.println("I'm selected " + e.getData() + " " + catchValue);

                    AlertDialog alertDialog = new AlertDialog.Builder(selff, R.style.AlertDialogTheme).create();
                    LayoutInflater factory = LayoutInflater.from(selff);
                    final View view = factory.inflate(R.layout.activity_speciesinfo, null);

                    TextView speciesName = view.findViewById(R.id.speciesName);
                    TextView speciesDetails = view.findViewById(R.id.speciesDetails);
                    LinearLayout imageLayout = view.findViewById(R.id.speciesImageLayout);

                    speciesDetails.setText(String.format("%s mt", catchValue));

                    // dynamically sets the values, text, and image on the alert dialog box
                    if (e.getData().equals("SKJ")) {
                        speciesName.setText("Skipjack Tuna");
                        ImageView image = new ImageView(selff);
                        image.setBackgroundResource(R.mipmap.skj_foreground);
                        imageLayout.addView(image);
                    }
                    else if (e.getData().equals("YFT")) {
                        speciesName.setText("Yellow Fin Tuna");
                        ImageView image = new ImageView(selff);
                        image.setBackgroundResource(R.mipmap.yft_foreground);
                        imageLayout.addView(image);
                    }
                    else if (e.getData().equals("BET")) {
                        speciesName.setText("Big-eye Tuna");
                        ImageView image = new ImageView(selff);
                        image.setBackgroundResource(R.mipmap.bet_foreground);
                        imageLayout.addView(image);
                    }
                    else if (e.getData().equals("KAW")) {
                        speciesName.setText("Mackerel Tuna (Kawa-kawa)");
                        ImageView image = new ImageView(selff);
                        image.setBackgroundResource(R.mipmap.kaw_foreground);
                        imageLayout.addView(image);
                    }
                    else if (e.getData().equals("BLT_FRT")) {
                        speciesName.setText("Bullet Tuna");
                        ImageView image = new ImageView(selff);
                        image.setBackgroundResource(R.mipmap.blt_frt_foreground);
                        imageLayout.addView(image);
                    }
                    else if (e.getData().equals("MSD")){
                        speciesName.setText("Mackerel Scad (Galunggong)");
                        ImageView image = new ImageView(selff);
                        image.setBackgroundResource(R.mipmap.msd_foreground);
                        imageLayout.addView(image);
                    }
                    else {//if (e.getData().equals("MIX")) {
                        speciesName.setText("Mixed Species");
                        ImageView image = new ImageView(selff);
                        image.setBackgroundResource(R.mipmap.mix_foreground);
                        imageLayout.addView(image);
                    }

                    alertDialog.setView(view);
                    alertDialog.show();

                }

                @Override
                public void onNothingSelected() {

                }
            });

        }
        else{ // if the total equals 0, this will clear the values in the piechart, if there are any
            yValues.clear();
            pieChart.clear();
            // text styling for NO DATA message
            pieChart.setNoDataText("No data for "+labelStartDate+ " to "+labelEndDate);
            pieChart.setNoDataTextColor(Color.RED);
            Paint p = pieChart.getPaint(Chart.PAINT_INFO);
//            p.setTextSize(50);
        }

        System.out.println("YVALUES: "+yValues);

        // button to download pie chart
        Button downloadButton = findViewById(R.id.downloadChart);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File direct = new File(Environment.getExternalStorageDirectory() + "/DAGAT");

                if (!direct.exists()) {
                    File newDirectory = new File("/sdcard/DAGAT/");
                    newDirectory.mkdirs();
                }

                String fileName = "spc_chart"+System.currentTimeMillis();
                pieChart.saveToPath(fileName,"/DAGAT" );
                Toast.makeText(selff, "File downloaded.", Toast.LENGTH_SHORT).show();
                Toast.makeText(selff, "File saved in the DAGAT folder.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // limits the decimal value to one digit only
    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + "%"; // appends a percentage-sign to the values
        }
    }


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

    // API call to get the values for species composition
    private void getSpeciesDistributionByGear(){
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "effortCoords");

        String checkedGearStr = "";
        String encodedParam="";
        for(int i=0; i<checkedGears.size(); i++){
            checkedGearStr = checkedGearStr + checkedGears.get(i) + ",";
        }
        checkedGearStr = checkedGearStr.substring(0, checkedGearStr.length() - 1);

        try {
            encodedParam = URLEncoder.encode(checkedGearStr,"UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_SPECIESDISTRIBUTIONBYGEAR+
                encodedParam+"&Start_Date="+startDate+"&End_Date="+endDate, params, 1024, selff);

        try {
            String res = request.execute().get();

            String[] resArray = res.split("\n");
            for (String s : resArray) {
                if (s.contains("{\"SDG\"")) {
                    res = s;
                }
            }

            System.out.println("res: " + res);

            //parse json array
            JSONObject response = new JSONObject(res);
            JSONArray speciesArray = response.getJSONArray("SDG");
            System.out.println("speciesArray: " + speciesArray);
            System.out.println("speciesArray.length(): " + speciesArray.length());

            SKJdist = 0f;
            for (int i = 0; i < speciesArray.length(); i++) {
                JSONObject jsonObject = speciesArray.getJSONObject(i);
                Double distribution = jsonObject.getDouble("SKJ");
                SKJdist = SKJdist + Float.parseFloat(distribution.toString());
            }
            System.out.println("SKJdist: " + SKJdist);

            YFTdist = 0f;
            for (int i = 0; i < speciesArray.length(); i++) {
                JSONObject jsonObject = speciesArray.getJSONObject(i);
                Double distribution = jsonObject.getDouble("YFT");
                YFTdist = YFTdist + Float.parseFloat(distribution.toString());
            }
            System.out.println("YFTdist: " + YFTdist);

            BETdist = 0f;
            for (int i = 0; i < speciesArray.length(); i++) {
                JSONObject jsonObject = speciesArray.getJSONObject(i);
                Double distribution = jsonObject.getDouble("BET");
                BETdist = BETdist + Float.parseFloat(distribution.toString());
            }
            System.out.println("BETdist: " + BETdist);

            BLT_FRTdist = 0f;
            for (int i = 0; i < speciesArray.length(); i++) {
                JSONObject jsonObject = speciesArray.getJSONObject(i);
                Double distribution = jsonObject.getDouble("BLT_FRT");
                BLT_FRTdist = BLT_FRTdist + Float.parseFloat(distribution.toString());
            }
            System.out.println("BLT_FRTdist: " + BLT_FRTdist);

            KAWdist = 0f;
            for (int i = 0; i < speciesArray.length(); i++) {
                JSONObject jsonObject = speciesArray.getJSONObject(i);
                Double distribution = jsonObject.getDouble("KAW");
                KAWdist = KAWdist + Float.parseFloat(distribution.toString());
            }
            System.out.println("KAWdist: " + KAWdist);

            MSDdist = 0f;
            for (int i = 0; i < speciesArray.length(); i++) {
                JSONObject jsonObject = speciesArray.getJSONObject(i);
                Double distribution = jsonObject.getDouble("MSD");
                MSDdist = MSDdist + Float.parseFloat(distribution.toString());
            }
            System.out.println("MSDdist: " + MSDdist);

            MIXdist = 0f;
            for (int i = 0; i < speciesArray.length(); i++) {
                JSONObject jsonObject = speciesArray.getJSONObject(i);
                Double distribution = jsonObject.getDouble("MIX");
                MIXdist = MIXdist + Float.parseFloat(distribution.toString());
            }
            System.out.println("MIXdist: " + MIXdist);


        }catch(Exception e){
            e.printStackTrace();
        }



    }
}
