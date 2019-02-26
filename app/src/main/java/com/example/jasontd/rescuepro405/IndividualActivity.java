package com.example.jasontd.rescuepro405;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IndividualActivity extends AppCompatActivity implements SensorEventListener {

    private TextView mStatus;
    private Button mOkay;
    private SensorManager sensorManager;
    private long lastUpdate;
    private boolean crashMode;
    private float testMag;
    private float highestMag = 0;
    private float lowestMag = 0;
    private float fallMag = 0;
    private float accelMagnitude;
    private float testTime;
    private StringBuilder testLog;
    //private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);

        mStatus = (TextView) findViewById(R.id.status);
        mOkay = (Button) findViewById(R.id.alert);
        crashMode = false;
        updateStatus(crashMode);

        mOkay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view){
                crashMode = false;
                updateStatus(crashMode);
            }
        });

        //getDefaultSensor(SENSOR_TYPE_ACCELEROMETER);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event){
        float[] values = event.values;
        //Zero movement = 9.81
        float x = values[0]; //laying flat on table, pushed from left to right is (+, aka >9.81)
        float y = values[1]; //laying on table, raised to sky is (+, >9.81)
        float z = values[2]; //laying flat on desk, z is away from/closer from user
        accelMagnitude = vectorNorm(x,y,z);
        
        long actualTime = event.timestamp;
        
        if (y > highestMag){highestMag = y;}
        if (y < lowestMag){lowestMag = y;}
        
        //assumes object in near freefall, for MORE than 1.5 seconds. 3 meter fall lasts ~1.75 seconds
        if (accelMagnitude< 2){
            if (actualTime - lastUpdate < 1500){
                return;
            }
            testTime = actualTime - lastUpdate; //fall time in milliseconds
            fallMag = accelMagnitude;
            lastUpdate = actualTime;
            crashMode = true;
            testLog.append("Fall detected at " + lastUpdate + "\n");
            updateStatus(crashMode);
            //call EMS/family
        }
        mStatus.setText("Current mag value: " + accelMagnitude + "\n Highest mag so far: " +
                highestMag + "\n Lowest mag so far: " + lowestMag + "\n This fall: " + fallMag +
                "\n Fall time: " +testTime + "\n" + testLog);


    }

    private float vectorNorm(float x, float y, float z){
        float squareX = (float) Math.pow(x,2);
        float squareY = (float) Math.pow(y,2); 
        float squareZ = (float) Math.pow(z,2);
        return (float) Math.sqrt( squareX + squareY + squareZ );
    }

    private void updateStatus(boolean crashmode){

        if (crashmode == true){
            //mStatus.setText("Crash detected! Notifying Emergency contact shortly..."); //counter?
            mStatus.setText("Current mag value: " + accelMagnitude + "\n Highest mag so far: " +
                    highestMag + "\n Lowest mag so far: " + lowestMag + "\n This fall: " + fallMag  +
                    "\n Fall time: " +testTime + "\n" + testLog);
            mStatus.setBackgroundColor(Color.RED);
            mOkay.setEnabled(true);
            mOkay.setBackgroundColor(Color.GREEN);
            //notify Emergency contacts
        }
        else {
            //mStatus.setText("Nothing to Report"); //counter?
            mStatus.setText("Current mag value: " + accelMagnitude + "\n Highest mag so far: " +
                    highestMag + "\n Lowest mag so far: " + lowestMag + "\n This fall: " + fallMag
                    +
                    "\n Fall time: " +testTime + "\n" + testLog);
            mStatus.setBackgroundColor(Color.GREEN);
            mOkay.setEnabled(false);
            mOkay.setBackgroundColor(Color.TRANSPARENT);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        updateStatus(crashMode);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
