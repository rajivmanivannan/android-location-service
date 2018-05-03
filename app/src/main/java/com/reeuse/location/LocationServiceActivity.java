package com.reeuse.location;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.reeuse.location.location.LocationUpdateService;

/**
 * LocationServiceActivity.java
 * <p/>
 * To fetch the location in the background use this approach Before start service check the location
 * settings is enabled.
 */
public class LocationServiceActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private TextView latitude;
    private TextView longitude;
    private LocationUpdateService mLocationUpdateService;

    // To manage Service class life cycle.
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mLocationUpdateService = ((LocationUpdateService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // while disconnecting the service.
            mLocationUpdateService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        latitude = (TextView) findViewById(R.id.latitude_value_label);
        longitude = (TextView) findViewById(R.id.longitude_value_label);

        int hasGetLocationPermission = ActivityCompat.checkSelfPermission(LocationServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasGetLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationServiceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            Intent i = new Intent(LocationServiceActivity.this, LocationUpdateService.class);
            startService(i);
            bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
        findViewById(R.id.get_value_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationUpdateService != null) {
                    latitude.setText(String.valueOf(mLocationUpdateService.latitude));
                    longitude.setText(String.valueOf(mLocationUpdateService.longitude));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQUEST_CODE_ASK_PERMISSIONS) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission,
            Intent i = new Intent(LocationServiceActivity.this, LocationUpdateService.class);
            startService(i);
            bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
            return;
        }
    }
}
