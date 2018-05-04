package com.reeuse.location.location;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.reeuse.location.geofencing.GeoFenceHelper;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationUpdateService extends Service implements LocationListener {

  public static final String TAG = LocationUpdateService.class.getSimpleName();
  /**
   * The desired interval for location updates. Inexact. Updates may be more or less frequent.
   */
  public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
  /**
   * The fastest rate for active location updates. Exact. Updates will never be more frequent
   * than this value.
   */
  public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
      UPDATE_INTERVAL_IN_MILLISECONDS / 2;
  private final IBinder binder = new LocalBinder();
  public double latitude = 0.0;
  public double longitude = 0.0;

  /**
   * Stores parameters for requests to the FusedLocationProviderClient.
   */
  private LocationRequest mLocationRequest;
  private LocationCallback mLocationCallback;
  private FusedLocationProviderClient fusedLocationProviderClient;

  @Override
  public void onCreate() {
    super.onCreate();
    fusedLocationProviderClient = new FusedLocationProviderClient(this);
    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
          return;
        }
        for (Location location : locationResult.getLocations()) {
          updateLocation(location);
        }
      }
    };

  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    createLocationRequest();
    startLocationUpdates();
    return START_STICKY;
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
  protected void createLocationRequest() {
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
   * Requests location updates from the FusedLocationApi.
   */
  private void startLocationUpdates() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
          mLocationCallback,
          Looper.myLooper());
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public void onLocationChanged(Location location) {
    updateLocation(location);
  }

  /**
   * Removes location updates from the FusedLocationApi.
   */
  protected void stopLocationUpdates() {
    // It is a good practice to remove location requests when the activity is in a paused or
    // stopped state. Doing so helps battery performance and is especially
    // recommended in applications that request frequent location updates.
    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
  }

  private void updateLocation(Location location) {
    Log.i(TAG, location.getProvider());
    Log.i(TAG, "Latitude :" + location.getLatitude());
    Log.i(TAG, "Longitude :" + location.getLongitude());
    latitude = location.getLatitude();
    longitude = location.getLongitude();
  }

  //Local binder to bind the service and communicate with this LocationUpdateService class.
  public class LocalBinder extends Binder {
    public LocationUpdateService getService() {
      return LocationUpdateService.this;
    }
  }
}
