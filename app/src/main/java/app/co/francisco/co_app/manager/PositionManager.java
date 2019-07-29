package app.co.francisco.co_app.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;

import static app.co.francisco.co_app.gui.MainActivity.context;

/**
 * Created by ASUS on 25/03/2019.
 */

public class PositionManager {

    private GoogleApiClient googleApiClient;
    public String TAG = "Position Manager";

    public Context ctx;
    public Activity activt;

    public  PositionManager(Context context, Activity activity){
        ctx =context;
        activt = activity;
    }

    public boolean isGpsAvailable() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "GPS permissions fail");
            return false;
        }

        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        return locationAvailability.isLocationAvailable();
    }

    public boolean checkPermission() {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "ACCESS_FINE_LOCATION permission disabled");
            return false;
        }

        return true;
    }

    public static boolean isLocationEnabled (Context context) {

        boolean enabled = false;

        try {

            if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE) != Settings.Secure.LOCATION_MODE_OFF) {

                enabled = true;
            }
        }
        catch (Settings.SettingNotFoundException e) {

            e.printStackTrace();
        }

        return enabled;
    }
}
