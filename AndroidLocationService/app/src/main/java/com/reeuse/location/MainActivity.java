package com.reeuse.location;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_main);

        latitude = (TextView) findViewById(R.id.latitude_value_label);
        longitude = (TextView) findViewById(R.id.longitude_value_label);
        Intent i = new Intent(MainActivity.this, LocationUpdateService.class);
        startService(i);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        findViewById(R.id.get_value_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitude.setText(mLocationUpdateService.latitude);
                longitude.setText(mLocationUpdateService.longitude);
            }
        });
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

}
