package com.reeuse.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.location_background).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, LocationActivity.class));
      }
    });

    findViewById(R.id.location_foreground).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, LocationServiceActivity.class));
      }
    });

    findViewById(R.id.geo_fencing_activity).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, GeoFencingActivity.class));
      }
    });
  }
}
