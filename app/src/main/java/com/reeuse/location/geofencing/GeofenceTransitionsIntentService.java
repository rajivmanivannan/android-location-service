package com.reeuse.location.geofencing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.reeuse.location.GeoFencingActivity;
import com.reeuse.location.R;
import java.util.List;

/**
 * GeofenceTransitionsIntentService.java
 */
public class GeofenceTransitionsIntentService extends IntentService {

  protected static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

  public GeofenceTransitionsIntentService() {
    super(GeofenceTransitionsIntentService.class.getSimpleName());
  }

  /**
   * Handles incoming intents.
   *
   * @param intent The Intent sent by Location Services. This Intent is provided to Location
   * Services (inside a PendingIntent) when addGeofences() is called.
   */
  @Override
  protected void onHandleIntent(Intent intent) {
    GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
    if (geoFenceEvent.hasError()) {
      int errorCode = geoFenceEvent.getErrorCode();
      Log.e(TAG, "Location Services error: " + errorCode);
    } else {
      int transitionType = geoFenceEvent.getGeofenceTransition();
      List<Geofence> geofenceList = geoFenceEvent.getTriggeringGeofences();
      for (Geofence geofence : geofenceList) {
        String triggeredGeoFenceId = geofence.getRequestId();
        if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
          sendNotification("Enter:: " + triggeredGeoFenceId);
        } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
          sendNotification("Exit:: " + triggeredGeoFenceId);
        }
      }
    }
  }

  /**
   * Posts a notification in the notification bar when a transition is detected.
   * If the user clicks the notification, control goes to the main Activity.
   *
   * @param message The message to show
   */
  private void sendNotification(String message) {
    // Create an explicit content Intent that starts the main Activity
    Intent notificationIntent =
        new Intent(getApplicationContext(), GeoFencingActivity.class);
    // Construct a task stack
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    // Adds the main Activity to the task stack as the parent
    stackBuilder.addParentStack(GeoFencingActivity.class);
    // Push the content Intent onto the stack
    stackBuilder.addNextIntent(notificationIntent);

    // Get a PendingIntent containing the entire back stack
    PendingIntent notificationPendingIntent =
        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    // Get a notification builder that's compatible with platform versions >= 4
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));
    // Set the notification contents
    builder.setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(message)
        .setContentIntent(notificationPendingIntent)
        .setSound(defaultSoundUri);
    // Get an instance of the Notification manager
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    // Issue the notification
    mNotificationManager.notify(0, builder.build());
  }
}