package com.reeuse.location.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * LocationHelper.java
 */
public class LocationHelper implements LocationListener {
  /**
   * Constant used in the location settings dialog.
   */
  public static final int REQUEST_CHECK_SETTINGS = 0x6;
  private static final String TAG = LocationHelper.class.getSimpleName();
  /**
   * The desired interval for location updates. Inexact. Updates may be more or less frequent.
   */
  private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
  /**
   * The fastest rate for active location updates. Exact. Updates will never be more frequent
   * than this value.
   */
  private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
      UPDATE_INTERVAL_IN_MILLISECONDS / 2;

  /**
   * Stores parameters for requests to the FusedLocationProviderClientApi.
   */
  private LocationRequest mLocationRequest;
  private LocationCallback mLocationCallback;
  private Context context;
  private OnLocationCompleteListener onLocationCompleteListener;
  private FusedLocationProviderClient fusedLocationProviderClient;

  public LocationHelper(Context context,
      final OnLocationCompleteListener onLocationCompleteListener) {
    this.context = context;
    this.onLocationCompleteListener = onLocationCompleteListener;
    fusedLocationProviderClient = new FusedLocationProviderClient(context);
    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
          return;
        }
        for (Location location : locationResult.getLocations()) {
          onLocationCompleteListener.getLocationUpdate(location);
        }
      }
    };
    checkLocationSettings();
    createLocationRequest();
  }

  /**
   * Sets up the location request. Android has two location request settings:
   * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
   * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
   * the AndroidManifest.xml.
   * <p/>
   * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
   * interval (5 seconds), the Fused Location Provider API returns location updates that are
   * accurate to within a few feet.
   * <p/>
   * These settings are appropriate for mapping applications that show real-time location
   * updates.
   */
  private void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    // Sets the desired interval for active location updates. This interval is
    // inexact. You may not receive updates at all if no location sources are available, or
    // you may receive them slower than requested. You may also receive updates faster than
    // requested if other applications are requesting location at a faster interval.
    mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

    // Sets the fastest rate for active location updates. This interval is exact, and your
    // application will never receive updates faster than this value.
    mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  /**
   * Requests location updates from the FusedLocationProviderClient.
   */
  public void startLocationUpdates() {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
          mLocationCallback,
          Looper.myLooper());
    }
  }

  /**
   * Removes location updates from the FusedLocationProviderClient.
   */
  public void stopLocationUpdates() {
    // It is a good practice to remove location requests when the activity is in a paused or
    // stopped state. Doing so helps battery performance and is especially
    // recommended in applications that request frequent location updates.
    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
  }

  @Override
  public void onLocationChanged(Location location) {
    onLocationCompleteListener.getLocationUpdate(location);
  }

  private void checkLocationSettings() {
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(new LocationRequest());
    Task<LocationSettingsResponse> result =
        LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());
    result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
      @Override
      public void onComplete(Task<LocationSettingsResponse> task) {
        try {
          LocationSettingsResponse response = task.getResult(ApiException.class);
          // All location settings are satisfied. The client can initialize location
          // requests here.

        } catch (ApiException exception) {
          switch (exception.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
              // Location settings are not satisfied. But could be fixed by showing the
              // user a dialog.
              try {
                // Cast to a resolvable exception.
                ResolvableApiException resolvable = (ResolvableApiException) exception;
                onLocationCompleteListener.onError(resolvable, null);
              } catch (ClassCastException e) {
                // Ignore, should be an impossible error.
              }
              break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
              // Location settings are not satisfied. However, we have no way to fix the
              // settings so we won't show the dialog.
              break;
          }
        }
      }
    });
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      // Check for the integer request code originally supplied to startResolutionForResult().
      case REQUEST_CHECK_SETTINGS:
        switch (resultCode) {
          case Activity.RESULT_OK:
            Log.i(TAG, "User agreed to make required location settings changes.");
            break;
          case Activity.RESULT_CANCELED:
            Log.i(TAG, "User choose not to make required location settings changes.");
            break;
        }
        break;
    }
  }

  public interface OnLocationCompleteListener {

    void getLocationUpdate(Location location);

    void onError(ResolvableApiException resolvableApiException, String error);
  }
}