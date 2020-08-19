package ph.edu.up.ics.oceans13mod7;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

public class Utils {

    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";
    static final String KEY_LAST_SESSION_ID = "last_session_id";

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     *
     * @param requestingLocationUpdates The location updates state.
     */
    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    static void incrementSessionId(Context context) {
        int newSessionId = getLastSessionId(context) + 1;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_LAST_SESSION_ID, newSessionId).apply();
    }

    public static int getLastSessionId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_LAST_SESSION_ID, 0);
    }

    /**
     * Returns the {@code location} object as a human readable string.
     *
     * @param location The {@link Location}.
     */
    static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    static String getLocationTitle(Context context) {
        return context.getString(R.string.location_updated, DateFormat.getDateTimeInstance().format(new Date()));
    }
}
