package com.reeuse.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.reeuse.location.geofencing.GeoFenceHelper;
import com.reeuse.location.geofencing.SetGeoFence;
import com.reeuse.location.utils.GeoCoordinatesValidatorUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GeoFencingActivity.java
 * <p/>
 * To set the Geo-Fencing
 */

public class GeoFencingActivity extends AppCompatActivity
    implements GeoFenceHelper.GeoFenceStatusListener {
  protected static final String TAG = GeoFencingActivity.class.getSimpleName();
  final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
  // Internal List of GeoFence objects. In a real app, these might be provided by an API based on
  // locations within the user's proximity.
  private List<Geofence> geoFenceList;
  private GeoFenceHelper geoFenceHelper;
  private TextInputLayout longitudeLabel;
  private TextInputLayout latitudeLabel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_geo_fencing);
    geoFenceList = new ArrayList<>();

    longitudeLabel = findViewById(R.id.til_latitude);
    latitudeLabel = findViewById(R.id.til_longitude);

    int hasGetLocationPermission = ActivityCompat.checkSelfPermission(GeoFencingActivity.this,
        Manifest.permission.ACCESS_FINE_LOCATION);
    if (hasGetLocationPermission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(GeoFencingActivity.this,
          new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
          REQUEST_CODE_ASK_PERMISSIONS);
    } else {
      initializeGeoFencingHelper();
    }

    findViewById(R.id.bt_set_geo_fence).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        validateAndSetGeoFence();
      }
    });
  }

  private void initializeGeoFencingHelper() {
    geoFenceHelper = new GeoFenceHelper(this, this);
  }

  private void validateAndSetGeoFence() {
    String longitude = latitudeLabel.getEditText().getText().toString();
    String latitude = longitudeLabel.getEditText().getText().toString();

    if (GeoCoordinatesValidatorUtils.isValidLatitude(latitude)) {
      latitudeLabel.setError(getString(R.string.valid_latitude));
      return;
    }

    if (GeoCoordinatesValidatorUtils.isValidLongitude(longitude)) {
      longitudeLabel.setError(getString(R.string.valid_longitude));
      return;
    }

    SetGeoFence setGeoFence = new SetGeoFence(
        randomGeoFenceId(),// geofenceId.
        Long.parseLong(latitude), // latitude
        Long.parseLong(longitude), //longitude
        100f, // radius in meter
        Geofence.NEVER_EXPIRE,// Expire time
        Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
    );
    geoFenceList.add(setGeoFence.toGeoFence());
    geoFenceHelper.setGeoFencingTriggerOnEnter(geoFenceList);
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
      initializeGeoFencingHelper();
    }
  }

  //---------------------------------------------------------//

  private String randomGeoFenceId() {
    return String.valueOf(new Random().nextInt(1 - 99));
  }

  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

  @Override public void addedSuccessfully() {
    showToast(getString(R.string.geo_fencing_added_successfully));
  }

  @Override public void removedSuccessfully() {
    showToast(getString(R.string.geo_fencing_removed_successfully));
  }

  @Override public void onAddFail(Exception exception) {
    showToast(getString(R.string.geo_fencing_add_failed));
  }

  @Override public void onRemoveFailure(Exception exception) {
    showToast(getString(R.string.geo_fencing_remove_failed));
  }
}





