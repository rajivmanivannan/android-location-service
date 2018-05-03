package com.reeuse.location;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.reeuse.location.location.LocationHelper;

/**
 * LocationActivity.java
 * <p/>
 * To fetch the location of the user in foreground use this approach.
 */
public class LocationActivity extends AppCompatActivity
    implements LocationHelper.OnLocationCompleteListener {
  final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
  private String TAG = LocationActivity.class.getSimpleName();
  private TextView latitude;
  private TextView longitude;
  private LocationHelper locationHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location);

    latitude = (TextView) findViewById(R.id.latitude_value_label);
    longitude = (TextView) findViewById(R.id.longitude_value_label);

    int hasGetLocationPermission = ActivityCompat.checkSelfPermission(LocationActivity.this,
        Manifest.permission.ACCESS_FINE_LOCATION);
    if (hasGetLocationPermission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(LocationActivity.this,
          new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
    } else {
      locationHelper = new LocationHelper(LocationActivity.this, this);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode != REQUEST_CODE_ASK_PERMISSIONS) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      return;
    }
    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // we have permission,
      locationHelper = new LocationHelper(LocationActivity.this, this);
    }
  }

  @Override
  public void getLocationUpdate(Location location) {
    latitude.setText(String.valueOf(location.getLatitude()));
    longitude.setText(String.valueOf(location.getLongitude()));
  }

  @Override
  public void onError(ResolvableApiException resolvableApiException, String error) {
    try {
      // Show the dialog by calling startResolutionForResult(),
      // and check the result in onActivityResult().
      resolvableApiException.startResolutionForResult(
          this,
          LocationHelper.REQUEST_CHECK_SETTINGS);
    } catch (IntentSender.SendIntentException e) {
      // Ignore the error.
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    locationHelper.onActivityResult(requestCode, resultCode, data);
  }
}
