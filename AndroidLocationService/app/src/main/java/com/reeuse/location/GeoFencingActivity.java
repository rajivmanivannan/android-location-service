package com.reeuse.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.reeuse.location.geofencing.GeoFenceHelper;
import com.reeuse.location.geofencing.SetGeofence;

import java.util.ArrayList;
import java.util.List;

/**
 * GeoFencingActivity.java
 * <p/>
 * To set the Geo-Fencing
 */

public class GeoFencingActivity extends AppCompatActivity implements GeoFenceHelper.OnGeofencePreparedListener {
    protected static final String TAG = GeoFencingActivity.class.getSimpleName();
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private GeoFenceHelper geoFenceHelper;
    private SetGeofence setGeofence;
    // Internal List of Geofence objects. In a real app, these might be provided by an API based on
    // locations within the user's proximity.
    List<Geofence> mGeofenceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fencing);

        mGeofenceList = new ArrayList<>();
        setGeofence = new SetGeofence(
                "12",// geofenceId.
                13.019780, // latitude
                80.201202, //longitude
                100.0f, // radius in meter
                Geofence.NEVER_EXPIRE,// Expire time
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
        );
        mGeofenceList.add(setGeofence.toGeofence());

        int hasGetLocationPermission = ActivityCompat.checkSelfPermission(GeoFencingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasGetLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GeoFencingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        } else
            geoFenceHelper = new GeoFenceHelper(this, this);
    }

    @Override
    public void onSuccess(GoogleApiClient googleApiClient, PendingIntent pendingIntent) {
        LocationServices.GeofencingApi.addGeofences(googleApiClient, mGeofenceList,
                pendingIntent);
        Toast.makeText(this, "Geofencing added", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(ConnectionResult connectionResult, String error) {
        if (connectionResult != null) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this,
                            geoFenceHelper.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {

                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQUEST_CODE_ASK_PERMISSIONS) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission,
            geoFenceHelper = new GeoFenceHelper(this, this);
            return;
        }
    }
}





