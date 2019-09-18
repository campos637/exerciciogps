package com.example.gps_app;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.style.TtsSpan;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private TextView distanceTextview;
    private Button gpsPermissionButton;
    private Button startGpsButton;
    private Button stopGpsButton;
    private Button startRouteButton;
    private Button stopRouteButton;
    private ImageView findImageView;
    private EditText findEditText;

    private Chronometer chronometer;
    private boolean isRunning;

    private double latitudeInitial;
    private double longitudeInitial;
    private double latitudeFinal;
    private double longitudeFinal;
    private double distance;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_PERMISSION_CODE_GPS = 1001;

    private boolean onRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        distanceTextview = findViewById(R.id.distanceTextView);
        chronometer = findViewById(R.id.timeChronometer);
        isRunning = false;

        gpsPermissionButton = findViewById(R.id.gpsPermissionButton);
        gpsPermissionButton.setOnClickListener((v) -> requestGpsPermission());

        startGpsButton = findViewById(R.id.startGpsButton);
        startGpsButton.setOnClickListener((view -> startGPS()));

        stopGpsButton = findViewById(R.id.stopGpsButton);
        stopGpsButton.setOnClickListener((v) -> stopGPS());

        startRouteButton = findViewById(R.id.startRouteButton);
        startRouteButton.setOnClickListener((v) -> startRoute());

        stopRouteButton = findViewById(R.id.stopRouteButton);
        stopRouteButton.setOnClickListener((v) -> stopRoute());

        findImageView = findViewById(R.id.findImageView);
        findImageView.setOnClickListener((v) -> findLocations());

        findEditText = findViewById(R.id.findEditText);

        startRouteButton.setClickable(false);
        stopRouteButton.setClickable(false);
        startRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
        stopRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
        stopGpsButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
        stopGpsButton.setClickable(false);

        latitudeFinal = 0.0;
        latitudeFinal = 0.0;
        longitudeInitial = 0.0;
        longitudeFinal = 0.0;
        distance = 0.0;

        onRoute = false;
    }

    private void stopGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startGpsButton.setBackground(getResources().getDrawable(R.drawable.shadow, null));
            stopGpsButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
            startGpsButton.setClickable(true);
            stopGpsButton.setClickable(false);
            locationManager.removeUpdates(locationListener);
            distanceTextview.setText(R.string.default_gps);
            startRouteButton.setClickable(false);
            stopRouteButton.setClickable(false);
            startRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
            stopRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
            chronometer.setBase(SystemClock.elapsedRealtime());
            String elapsedTime = Long.toString((SystemClock.elapsedRealtime()-chronometer.getBase())/1000);
            String distanceTraveled = Integer.toString((int)distance);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());

            if(distance > 0) {
                String s = getString(R.string.elapsed_time_and_distance, distanceTraveled, elapsedTime );
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.didnt_move), Toast.LENGTH_SHORT).show();
            }
            distance = 0;

        } else {
            Toast.makeText(this, getString(R.string.permission_not_given), Toast.LENGTH_SHORT).show();
        }

    }

    private void startGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startGpsButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
            stopGpsButton.setBackground(getResources().getDrawable(R.drawable.shadow, null));
            startGpsButton.setClickable(false);
            stopGpsButton.setClickable(true);

            distanceTextview.setText(R.string.default_route);

            locationManager = (LocationManager) getSystemService(Context. LOCATION_SERVICE );
            isRunning = true;

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (latitudeInitial == 0.0 && latitudeFinal == 0.0){
                        latitudeInitial = location.getLatitude();
                        longitudeInitial = location.getLongitude();
                        latitudeFinal = location.getLatitude();
                        longitudeFinal = location.getLongitude();
                    } else {
                        latitudeInitial = latitudeFinal;
                        longitudeInitial = longitudeFinal;
                        latitudeFinal = location.getLatitude();
                        longitudeFinal = location.getLongitude();
                    }

                    if (onRoute) {
                        Location location1 = new Location("locationA");
                        Location location2 = new Location("locationB");

                        location1.setLatitude(latitudeInitial);
                        location1.setLongitude(longitudeInitial);
                        location2.setLatitude(latitudeFinal);
                        location2.setLongitude(longitudeFinal);

                        distance = distance + location1.distanceTo(location2);

                        String s2 = getString(R.string.meters, (int)distance);

                        distanceTextview.setText(s2);
                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,10,locationListener);
            startRouteButton.setClickable(true);
            startRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow, null));
            stopGpsButton.setBackground(getResources().getDrawable(R.drawable.shadow, null));
            stopGpsButton.setClickable(true);
        } else {
            Toast.makeText(this, getString(R.string.permission_not_given), Toast.LENGTH_SHORT).show();
        }
    }

    private void startRoute() {
        onRoute = true;
        startRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
        stopRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow, null));
        startRouteButton.setClickable(false);
        stopRouteButton.setClickable(true);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void stopRoute() {
        onRoute = false;
        startRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow, null));
        stopRouteButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
        startRouteButton.setClickable(true);
        stopRouteButton.setClickable(false);
        distanceTextview.setText(R.string.default_route);
        String elapsedTime = Long.toString((SystemClock.elapsedRealtime()-chronometer.getBase())/1000);
        String distanceTraveled = Integer.toString((int)distance);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());


        if(distance > 0) {
            String s = getString(R.string.elapsed_time_and_distance, distanceTraveled, elapsedTime );
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.didnt_move), Toast.LENGTH_SHORT).show();
        }

        distance = 0;


    }

    private void findLocations() {
        if (isRunning){
            Uri uri = Uri.parse(getString(R.string.uri_mapa,latitudeFinal,longitudeFinal,findEditText.getText()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.no_gps_no_map),Toast.LENGTH_SHORT).show();
        }

    }

    private void requestGpsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gpsPermissionButton.setBackground(getResources().getDrawable(R.drawable.shadow_select, null));
            Toast.makeText(this, getString(R.string.permission_given), Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_PERMISSION_CODE_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE_GPS) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 1000,5,locationListener
                    );
                }
            } else {
                Toast.makeText(this, getString(R.string.no_gps_no_app),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }


}