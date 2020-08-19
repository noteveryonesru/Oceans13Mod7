package ph.edu.up.ics.oceans13mod7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.List;

import ph.edu.up.ics.oceans13mod7.database.OceansDatabase;
import ph.edu.up.ics.oceans13mod7.database.Record;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1001;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private OceansBroadcastReceiver oceansBroadcastReceiver;

    // A reference to the service used to get location updates.
    private OceansLocationService oceansService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;
    // UI elements.
    private Button startButton;
    private Button stopButton;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView speedTextView;
    private TextView bearingTextView;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OceansLocationService.LocalBinder binder = (OceansLocationService.LocalBinder) service;
            oceansService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            oceansService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oceansBroadcastReceiver = new OceansBroadcastReceiver();
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        speedTextView = findViewById(R.id.speedTextView);
        bearingTextView = findViewById(R.id.bearingTextView);
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    oceansService.requestLocationUpdates();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oceansService.removeLocationUpdates();
            }
        });

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(Utils.requestingLocationUpdates(this));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, OceansLocationService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(oceansBroadcastReceiver, new IntentFilter(OceansLocationService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(oceansBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Snackbar.make(findViewById(R.id.activity_main), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                oceansService.requestLocationUpdates();
            } else {
                // Permission denied.
                setButtonsState(false);
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES, false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    private void updateTextViews(Location location){
        String latitudeString = "Latitude : " + location.getLatitude();
        String longitudeString = "Longitude : " + location.getLongitude();
        String speedString = "Speed : " + location.getSpeed();
        String bearingString = "Bearing : " + location.getBearing();
        latitudeTextView.setText(latitudeString);
        longitudeTextView.setText(longitudeString);
        speedTextView.setText(speedString);
        bearingTextView.setText(bearingString);
    }

    public void mainToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private class OceansBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(OceansLocationService.EXTRA_LOCATION);
            if (location != null) {
                //Toast.makeText(MainActivity.this, Utils.getLocationText(location), Toast.LENGTH_SHORT).show();
                AsyncToast runner = new AsyncToast(OceansDatabase.getInstance(context), new WeakReference<MainActivity>(MainActivity.this));
                runner.execute(MainActivity.this);
                //List<Integer> ids = OceansDatabase.getInstance(context).recordDao().getIds();
                //Toast.makeText(MainActivity.this, ids.toString(), Toast.LENGTH_LONG).show();
                updateTextViews(location);
            }
        }
    }

    public static class AsyncToast extends AsyncTask<Context, Void, Void> {
        private OceansDatabase db;
        private String s;
        private WeakReference<MainActivity> activityReference;

        public AsyncToast(OceansDatabase db, WeakReference<MainActivity> activityReference){
            this.db = db;
            this.activityReference = activityReference;
        }
        @Override
        protected Void doInBackground(Context... contexts) {
            List<Integer> ids = db.recordDao().getIds();
            s = ids.toString();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.mainToast(s);
        }
    }

}