package com.reeuse.location.geofencing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * GeoFenceHelper.java
 */
public class GeoFenceHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // Request code to attempt to resolve Google Play services connection failures.
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent geofenceRequestIntent;
    private OnGeofencePreparedListener onGeofencePreparedListener;

    public interface OnGeofencePreparedListener {

        void onSuccess(GoogleApiClient googleApiClient, PendingIntent pendingIntent);

        void onError(ConnectionResult connectionResult, String error);

    }

    public GeoFenceHelper(Context context, OnGeofencePreparedListener onGeofencePreparedListener) {
        this.context = context;
        this.onGeofencePreparedListener = onGeofencePreparedListener;
        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        geofenceRequestIntent = getGeofenceTransitionPendingIntent();
        onGeofencePreparedListener.onSuccess(mGoogleApiClient, geofenceRequestIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (geofenceRequestIntent != null)
            LocationServices.getGeofencingClient(context).removeGeofences(geofenceRequestIntent);
        onGeofencePreparedListener.onError(null, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        onGeofencePreparedListener.onError(connectionResult, null);
    }


    /**
     * Create a PendingIntent that triggers GeofenceTransitionIntentService when a geofence
     * transition occurs.
     */
    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}