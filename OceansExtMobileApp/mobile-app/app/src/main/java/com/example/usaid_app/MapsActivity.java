package com.example.usaid_app;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import static java.lang.Boolean.TRUE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // shows the fishing sites feature of the app

    private PolygonOptions philEEZ;
    private KmlLayer allFMA;
    private PolygonOptions FMA3Polygon;
    MapsActivity selff = this;  //for activity self referencing hack, useful for catching exceptions
    private GoogleMap mMap;
    private ArrayList<LatLng> philEEZone;
    public String userName;
    public String accessLevel;
    public String companyName=null;
    public static String startDate;
    public static String endDate;
    public static ArrayList<String> checkedGears;
    private ClusterManager<BoatLocationClusterItem> boatLocations;
    private BoatLocationClusterItem clickedClusterItem;
    public static ArrayList<String> chosenDataset;
    public static String chosenFMA;
    public ArrayList<LatLng> allCatchLocations;
    private JSONArray filteredAllCatchData;
    private JSONObject allCatchJSONDatafromDbase;
    private String allCatchRawDatafromDbase;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        verifyStoragePermissions(selff);
        userName = getIntent().getStringExtra("USER_NAME");
        accessLevel = getIntent().getStringExtra("ACCESS_LEVEL");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        checkedGears = getIntent().getStringArrayListExtra("checkedGears");
        chosenFMA = getIntent().getStringExtra("chosenFMA");
        chosenDataset = getIntent().getStringArrayListExtra("chosenDataset");

        allCatchLocations = new ArrayList<LatLng>();


        if (accessLevel.equalsIgnoreCase("3"))
            companyName = getIntent().getStringExtra("COMPANY");

        if (companyName!=null)
        Log.i("CNAME", companyName);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Button resetDateButton = findViewById(R.id.dateSetter);
        resetDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent calendarIntent = new Intent(selff, CalendarMapsSDActivity.class);
                calendarIntent.putExtra("Flag", "ForMapsActivity");
                calendarIntent.putExtra("USER_NAME", userName);
                calendarIntent.putExtra("ACCESS_LEVEL", accessLevel);
                if (accessLevel.equalsIgnoreCase("3"))
                    calendarIntent.putExtra("COMPANY",companyName);
                startActivity(calendarIntent);
                finish();
            }
        });


        Button downloadMapButton = findViewById(R.id.downloadButton);
        downloadMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    Bitmap bitmap;

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        bitmap = snapshot;

                        File direct = new File(Environment.getExternalStorageDirectory() + "/DAGAT");
                        String fileName = "fishing_sites_" + System.currentTimeMillis() + ".png";

                        if (!direct.exists()) {
                            File wallpaperDirectory = new File("/sdcard/DAGAT/");
                            wallpaperDirectory.mkdirs();
                        }

                        File file = new File("/sdcard/DAGAT/", fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            snapshot.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };

                mMap.snapshot(callback);
                Toast.makeText(selff, "File downloaded.", Toast.LENGTH_SHORT).show();
                Toast.makeText(selff, "File saved in the DAGAT folder.", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bm, String fileName, String dirPath) {
//        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);

        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        philEEZone = new ArrayList<LatLng>();

        mMap = googleMap;

        //initialize generic map setup
        initializeGenericMapSetup();

        //initialize specific map setup based on access type
        //zooming is limited for non-BFAR
        initializeSpecificMapSetup(accessLevel);

        //initialize filtered Raw JSON object dataset
        allCatchRawDatafromDbase = getAllJSONDataNeeded(endDate, startDate);

        //convert raw data into a JSONArray for easier iteration

        //Gets a polygon object for checking violations in the future
        //might not be needed once KML file is parsed and made compatible with google map android
        //will keep this functions as guides
        philEEZ=showPhilippineEEZ();
        FMA3Polygon = showFMA3BordersinMap();

        setUpMap();
    }

    private void setUpMap() {

        for (String gear : checkedGears) {
            //iterate each checked gear and assign the manager "boatLocations"
            //to all of them for clustering
            setCatchDetailsValues(allCatchRawDatafromDbase, gear);

            //draw heatmaps from the latlong array list fetched in setCatchdetailsvalues method
            if (!allCatchLocations.isEmpty())
             drawHeatMapsBasedOnGear(allCatchLocations, gear);
            //reset the latlng list to avoid duplicate data on next gear iteration
            allCatchLocations.clear();
        }

        boatLocations.cluster();

        boatLocations.getMarkerCollection().setOnInfoWindowAdapter(
                new MyCustomAdapterForItems());
        //custom marker info window
        //get boat information and plot them given a start date


    }
    void initializeSpecificMapSetup(String accesslevel) {
        //bfar and non-bfar access only --> can be changed to include more access levels
        if (!accesslevel.equalsIgnoreCase("1")){
            mMap.setMinZoomPreference(3.0f);
            mMap.setMaxZoomPreference(7.0f);
        }
        else {
            mMap.setMinZoomPreference(4.0f);
            mMap.setMaxZoomPreference(16.0f);
        }

    }

    void initializeGenericMapSetup() {
        //initialize map
        // Add a marker in GenSan and move the camera
        LatLng midPh = new LatLng(11, 122);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(midPh));

        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(midPh, 5);
        mMap.animateCamera(yourLocation);

        try {
            allFMA = new KmlLayer(mMap, R.raw.fma_final_no_colors, getApplicationContext());
            //might need to parse this KML file
            //the kml provided is not fully compatible with google map android
            //especially certain properties such as placemarks and colors
            allFMA.addLayerToMap();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setAllGesturesEnabled(TRUE);
        mMap.getUiSettings().setZoomControlsEnabled(TRUE);

        boatLocations = new ClusterManager<>(selff, mMap);
        mMap.setOnCameraIdleListener(boatLocations);
        mMap.setOnMarkerClickListener(boatLocations);

        mMap.setInfoWindowAdapter(boatLocations.getMarkerManager());

        mMap.setOnInfoWindowClickListener(boatLocations);


    }

    private LatLng getCenterofPolygon(ArrayList<LatLng> polypoints) {
        LatLngBounds.Builder lb = new LatLngBounds.Builder();
        LatLng center = null;

        for (int i = 0; i < polypoints.size(); i++) {
            lb.include(polypoints.get(i));
        }

        LatLngBounds mybounds = lb.build();

        center = mybounds.getCenter();
        return center;

    }

    PolygonOptions showPhilippineEEZ()  {
        PolygonOptions p = null;
        //final int COLOR_ORANGE_ARGB = 0xffF57F17;
        final int PATTERN_DASH_LENGTH_PX = 30;
        final int PATTERN_GAP_LENGTH_PX = 2;
        final PatternItem DOT = new Dot();
        final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
        final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
        InputStream is;
        String[] latlngpair;
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
             is = getResources().openRawResource(R.raw.phileez);

            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                is.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String phileezJSON = writer.toString();
        try {
            JSONObject philEEZ = new JSONObject(phileezJSON);
            JSONObject outerBoundary = philEEZ.getJSONObject("outerBoundaryIs");
            JSONObject linearRing = outerBoundary.getJSONObject("LinearRing");
            String coordinates = linearRing.getString("coordinates");
            StringTokenizer latlngstring = new StringTokenizer(coordinates," ");
            //get all coordinates from phileez data
            while (latlngstring.hasMoreTokens()) {
                String ltlppair = latlngstring.nextToken();
                latlngpair = ltlppair.split(",",2);
                double longitude = Double.parseDouble(latlngpair[0]);
                double latitude = Double.parseDouble(latlngpair[1]);
                philEEZone.add(new LatLng(latitude, longitude));

            }

             p = new PolygonOptions().addAll(philEEZone);

            return p; //return the polygon

        } catch(JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    //might need to remove this in case you just want to parse the kml file
    //as it will become redundant
    //will just leave this here for now for illustration purposes
    private PolygonOptions showFMA3BordersinMap() {
        PolygonOptions p;
        final int COLOR_ORANGE_ARGB = 0xffF57F17;
        final int PATTERN_DASH_LENGTH_PX = 20;
        final int PATTERN_GAP_LENGTH_PX = 5;
        final PatternItem DOT = new Dot();
        final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
        final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
        //get geojson file from raw resource
        try {
            GeoJsonLayer fma3GeoJsonLayer = new GeoJsonLayer(mMap, R.raw.fma3_geojson, selff);
            for (GeoJsonFeature e : fma3GeoJsonLayer.getFeatures()) {
                if (e.hasGeometry()) {
                    //get the coordinates from the geojson file of fma3
                    GeoJsonLineString myline = (GeoJsonLineString) e.getGeometry();
                    ArrayList<LatLng> coorpoints = (ArrayList<LatLng>) myline.getCoordinates();
                    //get polygon shape and draw it to represent fma3
                     p = new PolygonOptions().addAll(coorpoints);
                    return p;
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //get all JSON data in one go, and do it only once or during data refresh
    //such as changing date range or pressing refresh button (to make it pseudo-realtime)
    private String getAllJSONDataNeeded(String enddate, String startdate){
        String API_CALL;
        HashMap<String, String> params = new HashMap<>();
        params.put("GET", "CatchInfo");
        if (accessLevel.equalsIgnoreCase("3")){
            API_CALL = Api.URL_GET_CATCH_COMPANY+"&Company_Name="+companyName;
        }
        else {
            API_CALL = Api.URL_READ_ALLCATCHDETAILS;
        }

        PerformNetworkRequest request = new PerformNetworkRequest(API_CALL + "&Start_Date=" + startdate + "&End_Date=" + enddate,
                    params, 1024, selff);

        Log.i("API", API_CALL);

        try {
            String result = request.execute().get();
            return result;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //parse the JSON Object which contains info given a particular boat/vessel name and gear
    private void addVesselToBoatCluster(JSONObject data, String boatName, String gear) {
        String carrier, owner, dateCatch, license, boatCaptain;
        LatLng position = null;
        BoatLocationClusterItem eachBoat;
        //get all info from json
        try {
            carrier = data.getString("CarrierName");
            if (data.has("Owner"))
                owner = data.getString("Owner");
            else
                owner = data.getString("CompanyID");

            dateCatch = data.getString("DateOfCatch");
            license = data.getString("Permit");
            boatCaptain = data.getString("BoatCaptain");
            double lat = Math.abs(data.getDouble("Coordinates_Lat"));
            double lng = Math.abs(data.getDouble("Coordinates_Long"));
            position = new LatLng(lat, lng);

            String mytitle = boatName + " - " + owner;
            String mysnippet = String.format("Captain: " + boatCaptain + "\n"
                    + "Permit: " + license + "\n"
                    + "Date: " + dateCatch + "\n"
                    + "Gear Type: " + gear + "\n"
                    + "Lat.: " + position.latitude+"\n"
                    + "Long.: " + position.longitude+"\n");
            eachBoat = new BoatLocationClusterItem(mytitle, mysnippet, position);
            boatLocations.addItem(eachBoat);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //given the entire JSONobject from database, parse content by gear
    public void setCatchDetailsValues(String data, String gear) {
        JSONArray allCatchDetails = new JSONArray();
        try {
            JSONObject object = new JSONObject(data);
            //if data user is a company person
            if ( accessLevel.equalsIgnoreCase("3"))
                allCatchDetails = object.getJSONArray("CatchDetailsGivenCompany");
            else
                allCatchDetails = object.getJSONArray("CatchDetails");
            //get all boat names
            for (int i = 0; i < allCatchDetails.length(); i++) {
                JSONObject eachCatchDetail = allCatchDetails.getJSONObject(i);
                String bn = eachCatchDetail.getString("CatcherName");
                double lat = Math.abs(eachCatchDetail.getDouble("Coordinates_Lat"));
                double lng = Math.abs(eachCatchDetail.getDouble("Coordinates_Long"));
                LatLng pos = new LatLng(lat, lng);
                String mygear = eachCatchDetail.getString("FishingGear");
                PolygonOptions p = new PolygonOptions().addAll(philEEZone);

                //add only specified gear
                if (mygear.equalsIgnoreCase(gear)) {
                    //screen content should be inside philippine eez unless BFAR user
                    if (accessLevel.equalsIgnoreCase("1")) {   //bfar user
                        addVesselToBoatCluster(eachCatchDetail, bn, gear);
                        allCatchLocations.add(pos);
                    } else {  //non-bfar user
                        if (mygear.equalsIgnoreCase(gear) && PolyUtil.containsLocation(pos, p.getPoints(), true)) {
                            addVesselToBoatCluster(eachCatchDetail, bn, gear);
                            allCatchLocations.add(pos);

                        } else {
                            System.out.println("Not ADDING this boats: " + pos.toString());
                        } //outside the EEZ: ignore for non-bfar
                    }
                }//getting only info related to gear

            } //end of all each JSON object loop


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void drawHeatMapsBasedOnGear(ArrayList<LatLng> coords, String gear) {
        //set heat map colors for different types
        int[] colors;
        int[] PSColor = {
                Color.GREEN, Color.RED
        };
        int[] RNColor = {
                Color.BLUE, Color.RED
        };

        int[] HLColor = {
                Color.YELLOW, Color.RED
        };

        // Create the gradient.

        float[] startPoints = {
                0.1f, 1f
        };
        if (gear.equalsIgnoreCase("Purse Seine")) {
            colors = PSColor;
        } else if (gear.equalsIgnoreCase("Ring Net")) {
            colors = RNColor;
        } else
            colors = HLColor;

        Gradient gradient = new Gradient(colors, startPoints);


        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(coords)
                .gradient(gradient)
                .radius(30)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

    }

    class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker arg0) {
           return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            LinearLayout info = new LinearLayout(selff);
            info.setOrientation(LinearLayout.VERTICAL);

            TextView title = new TextView(selff);
            title.setTextColor(Color.BLACK);
            title.setGravity(Gravity.CENTER);
            title.setTypeface(null, Typeface.BOLD);

            title.setText(marker.getTitle());
            TextView snippet = new TextView(selff);
            snippet.setTextColor(Color.GRAY);
            snippet.setText(marker.getSnippet());

            //change marker color based on boat type
            String boatType = marker.getSnippet();
            if (boatType.contains("Purse Seine")){
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
            else if (boatType.contains("Ring Net")){
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
            else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }

            if (!accessLevel.equalsIgnoreCase("1")){
                title.setText("Generic Vessel: " + checkedGears.toString());
                snippet.setText("No details available");
            }
            info.addView(title);
            info.addView(snippet);

            return info;
        }
}

}

class BoatLocationClusterItem implements ClusterItem {
    private String mTitle;
    private String mSnippet;
    private LatLng mPosition;

    public BoatLocationClusterItem(String title, String snippet, LatLng location){
        this.mTitle = title;
        this.mSnippet = snippet;
        this.mPosition = location;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }


}

