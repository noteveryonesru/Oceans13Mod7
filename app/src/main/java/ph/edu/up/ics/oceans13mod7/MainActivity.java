package ph.edu.up.ics.oceans13mod7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationTextView = findViewById(R.id.locationtextview);

        //Location Callback is a callback function that runs whenever a new location value is obtained
        //This is passed to the Fused Location Provider Client
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                locationTextView.setText("");

                //print all locations
                StringBuilder s = new StringBuilder();
                for (Location location : locationResult.getLocations()) {
                    s.append("Latitude : ").append(location.getLatitude()).append("\nLongitude : ").append(location.getLongitude()).append("\n");
                }
                locationTextView.setText(s.toString());
            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        //Permission check asking if user has already permitted location functionality for the app
        //The user's input it sent to the onRequestPermissionsResult method
        int hasFineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCourseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        String[] permissionArray = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCourseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissionArray, 1001);
        } else {
            //if permission already granted, run
            runGPSTracker();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //if permission already granted, run
            runGPSTracker();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start updates when on the app again
        if (requestingLocationUpdates) startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop updates when leaving the app
        stopLocationUpdates();
    }

    private void runGPSTracker() {
        requestingLocationUpdates = true;
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    //Suppressed the MissingPermission Error because Android Studio ain't smart enough to detect my rudimentary permission checks
    private void startLocationUpdates() {
        //location request is basically the settings I want for the retrieval
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}