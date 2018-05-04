package com.reeuse.location.geofencing;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.List;

/**
 * GeoFenceHelper.java
 */
public class GeoFenceHelper {
  private Context context;
  private GeofencingClient geofencingClient;
  private GeofencingRequest.Builder builder;
  private PendingIntent geoFenceRequestIntent;
  private GeoFenceStatusListener geoFenceStatusListener;

  public GeoFenceHelper(Context context, GeoFenceStatusListener geoFenceStatusListener) {
    this.context = context;
    this.geoFenceStatusListener = geoFenceStatusListener;
    geofencingClient = LocationServices.getGeofencingClient(context);
    geoFenceRequestIntent = getGeoFenceTransitionPendingIntent();
    builder = new GeofencingRequest.Builder();
  }

  public void setGeoFencingTriggerOnEnter(List<Geofence> geoFenceList) {
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
    builder.addGeofences(geoFenceList);
    builder.build();
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      geofencingClient.addGeofences(builder.build(), geoFenceRequestIntent)
          .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
              if (geoFenceStatusListener != null) {
                geoFenceStatusListener.addedSuccessfully();
              }
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
              if (geoFenceStatusListener != null) {
                geoFenceStatusListener.onAddFail(e);
              }
            }
          });
    }
  }

  public void setGeoFencingTriggerOnExit(List<Geofence> geoFenceList) {
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
    builder.addGeofences(geoFenceList);
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      geofencingClient.addGeofences(builder.build(), geoFenceRequestIntent)
          .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
              if (geoFenceStatusListener != null) {
                geoFenceStatusListener.addedSuccessfully();
              }
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
              if (geoFenceStatusListener != null) {
                geoFenceStatusListener.onAddFail(e);
              }
            }
          });
    }
  }

  public void removeGeoFencing() {
    geofencingClient.removeGeofences(geoFenceRequestIntent)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override public void onSuccess(Void aVoid) {
            if (geoFenceStatusListener != null) {
              geoFenceStatusListener.removedSuccessfully();
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            if (geoFenceStatusListener != null) {
              geoFenceStatusListener.onRemoveFailure(e);
            }
          }
        });
  }

  /**
   * Create a PendingIntent that triggers GeofenceTransitionIntentService when a geofence
   * transition occurs.
   */
  private PendingIntent getGeoFenceTransitionPendingIntent() {
    Intent intent = new Intent(context, GeoFenceTransitionsIntentService.class);
    return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  public interface GeoFenceStatusListener {
    void addedSuccessfully();

    void removedSuccessfully();

    void onAddFail(Exception exception);

    void onRemoveFailure(Exception exception);
  }
}