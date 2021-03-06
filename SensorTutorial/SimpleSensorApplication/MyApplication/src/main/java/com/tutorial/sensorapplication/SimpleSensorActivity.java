package com.tutorial.sensorapplication;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import java.util.List;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;

public class SimpleSensorActivity extends ActionBarActivity implements SensorEventListener, LocationListener {

    // Sensor Manager
    private SensorManager sensorManager;

    // Location Manager
    private LocationManager locationManager;

    // status of GPS
    private Boolean isGPSEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // initialize sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // get location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        // register this class as a listener for light sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);

        // register this class as a listener for ambient temperature sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
                SensorManager.SENSOR_DELAY_NORMAL);

        // register this class as a listener for step counter
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);

        // register this class as a listener for proximity
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);

        //check whether the sensors are avaliable
        List< Sensor > sl = sensorManager.getSensorList( Sensor.TYPE_LIGHT );
        if ( sl.size() == 0 ) {
            ((TextView) findViewById(R.id.textView4)).setText("light sensor NOT available!");
        }

        sl = sensorManager.getSensorList( Sensor.TYPE_AMBIENT_TEMPERATURE );
        if ( sl.size() == 0 ) {
            ((TextView) findViewById(R.id.textView5)).setText("ambient temperature sensor NOT available!");
        }

        sl = sensorManager.getSensorList( Sensor.TYPE_STEP_COUNTER );
        if ( sl.size() == 0 ) {
            ((TextView) findViewById(R.id.textView6)).setText("step counter NOT available!");
        }

        sl = sensorManager.getSensorList( Sensor.TYPE_PROXIMITY );
        if ( sl.size() == 0 ) {
            ((TextView) findViewById(R.id.textView7)).setText("proximity sensor NOT available!");
        }

        if(isGPSEnabled == Boolean.TRUE){
            Log.e("Xiang","gps enabled");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);

            if (locationManager != null) {
                Log.e("Xiang","location not null");



                Location location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    ((TextView) findViewById(R.id.textView8)).setText("Latitude: " + Double.toString(location.getLatitude()));
                    ((TextView) findViewById(R.id.textView9)).setText("Longitude: " + Double.toString(location.getLongitude()));
                }
            }else{
                Log.e("Xiang","location null");
                ((TextView) findViewById(R.id.textView8)).setText("last location is null");
            }
        }else{
            Log.e("Xiang","gps disabled");
        }

        if(isGPSEnabled == Boolean.FALSE){
            ((TextView) findViewById(R.id.textView8)).setText("Location NOT available");
            ((TextView) findViewById(R.id.textView9)).setText("Location NOT available");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    //update values for the sensors
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            // Movement
            float x = values[0];
            float y = values[1];
            float z = values[2];

            ((TextView) findViewById(R.id.textView1)).setText("accelerometer-x: " + Float.toString(x));
            ((TextView) findViewById(R.id.textView2)).setText("accelerometer-y: " + Float.toString(y));
            ((TextView) findViewById(R.id.textView3)).setText("accelerometer-z: " + Float.toString(z));
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float[] values = event.values;
            Log.e("light", Float.toString(values[0]));
            ((TextView) findViewById(R.id.textView4)).setText("light sensor: " + Float.toString(values[0]));
        }

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float[] values = event.values;
            Log.e("light", Float.toString(values[0]));
            ((TextView) findViewById(R.id.textView5)).setText("ambient temperature: " + Float.toString(values[0]));
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            float[] values = event.values;
            Log.e("light", Float.toString(values[0]));
            ((TextView) findViewById(R.id.textView6)).setText("step counter: " + Float.toString(values[0]));
        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float[] values = event.values;
            Log.e("light", Float.toString(values[0]));
            ((TextView) findViewById(R.id.textView7)).setText("proximity: " + Float.toString(values[0]));
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    // update GPS value
    public void onLocationChanged(Location location) {
        location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location != null) {
            ((TextView) findViewById(R.id.textView8)).setText("Latitude: " + Double.toString(location.getLatitude()));
            ((TextView) findViewById(R.id.textView9)).setText("Longitude: " + Double.toString(location.getLongitude()));
            Log.e("Xiang", "location changed - location not null");
            Log.e("Xiang", Double.toString(location.getLatitude()));
            Log.e("Xiang", Double.toString(location.getLongitude()));

        }else{
            Log.e("Xiang", "location changed - location is null");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
