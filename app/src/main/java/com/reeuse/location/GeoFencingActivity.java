package com.reeuse.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
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

public class GeoFencingActivity extends AppCompatActivity
    implements GeoFenceHelper.OnGeofencePreparedListener {
  protected static final String TAG = GeoFencingActivity.class.getSimpleName();
  final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
  // Internal List of Geofence objects. In a real app, these might be provided by an API based on
  // locations within the user's proximity.
  List<Geofence> mGeofenceList;
  private GeoFenceHelper geoFenceHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_geo_fencing);

    mGeofenceList = new ArrayList<>();
    SetGeofence setGeofence = new SetGeofence(
        "12",// geofenceId.
        13.019780, // latitude
        80.201202, //longitude
        100.0f, // radius in meter
        Geofence.NEVER_EXPIRE,// Expire time
        Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
    );
    mGeofenceList.add(setGeofence.toGeofence());

    int hasGetLocationPermission = ActivityCompat.checkSelfPermission(GeoFencingActivity.this,
        Manifest.permission.ACCESS_FINE_LOCATION);
    if (hasGetLocationPermission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(GeoFencingActivity.this,
          new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
          REQUEST_CODE_ASK_PERMISSIONS);
    } else {
      geoFenceHelper = new GeoFenceHelper(this, this);
    }
  }

  private GeofencingRequest getGeofencingRequest() {
    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
    builder.addGeofences(mGeofenceList);
    return builder.build();
  }

  @Override
  public void onSuccess(GoogleApiClient googleApiClient, PendingIntent pendingIntent) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      LocationServices.getGeofencingClient(this)
          .addGeofences(getGeofencingRequest(), pendingIntent);
      Toast.makeText(this, "Geofencing added", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onError(ConnectionResult connectionResult, String error) {
    if (connectionResult != null) {
      if (connectionResult.hasResolution()) {
        try {
          connectionResult.startResolutionForResult(this,
              GeoFenceHelper.CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e) {
          Log.e(TAG, e.getMessage());
        }
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode != REQUEST_CODE_ASK_PERMISSIONS) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      return;
    }
    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // we have permission,
      geoFenceHelper = new GeoFenceHelper(this, this);
    }
  }
}





