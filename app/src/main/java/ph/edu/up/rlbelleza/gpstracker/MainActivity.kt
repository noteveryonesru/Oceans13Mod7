package ph.edu.up.rlbelleza.gpstracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var requestingLocationUpdates = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Location Callback is a function that runs whenever a new location value is obtained
        //This is passed to the Fused Location Provider Client
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return //IF NULL, RETURN
                locationtextview.text = "" //empty the text field
                //print all locations
                for (location in locationResult.locations) {
                    locationtextview.text = "${locationtextview.text}Latitude : ${location.latitude}\nLongitude : ${location.longitude}\n"
                }
            }
        }
        //Fused Location Provider Client from Google Api for location retrieval
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Permission check asking if user has already permitted location functionality for the app
        //The user's input it sent to the onRequestPermissionsResult method
        val hasFineLocationPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCourseLocationPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCourseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1001
                )
            }
        }else{
            //if permission already granted, run
            runGPSTracker()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            //request code is arbitrary. it just has to match the request code sent by the permission check
            1001 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //if permission already granted, run
                runGPSTracker()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onResume() {
        super.onResume()
        //start updates when on the app again
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        //stop updates when leaving the app
        stopLocationUpdates()
    }

    fun runGPSTracker() {
        requestingLocationUpdates = true
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")  //Suppressed the MissingPermission Error because Android Studio ain't smart enough to detect my permission checks
    private fun startLocationUpdates() {
        //location request is basically the settings I want for the retrieval
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //this actually runs the location retrieval
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}
