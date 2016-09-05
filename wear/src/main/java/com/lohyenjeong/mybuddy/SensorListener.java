package com.lohyenjeong.mybuddy;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by lohyenjeong on 28/7/16.
 * Class that binds to required sensors
 * Detects new sensor events
 * Provides mechanism to control the start and stop of sensor listener
 */
public class SensorListener extends Service implements SensorEventListener {
    private static final String TAG = "MyBuddy/SensorListener";
    private static final int timeout = 10000;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mLinearAcceleration;
    private Sensor mGyroscope;

    private DataUploader dataUploader;

    //Creates a dataUploader for Google Play Services integration if not already present
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "SensorListener Started");

        dataUploader = DataUploader.getDataUploader(this);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("MyBuddy");
        builder.setContentTitle("Collecting sensor data");

        startForeground(1, builder.build());

        startSensorListener();
    }


    //Called when the service is shut
    //TODO: dont destroy listening
    @Override
    public void onDestroy(){
        super.onDestroy();
        stopSensorListener();
    }

    //Start listening to the accelerometer, gyroscope and linear accelerometer
    //TODO: check whether a change in Sensor delay is better. Android allows specific delays, stated in microseconds
    public void startSensorListener(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager != null) {
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
                mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "Linear acceleration results found");
            } else {
                Log.e(TAG, "No Linear accelerator found. Programme cannot proceed");
            }
            /*
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "Accelerometer Sensor found");
            } else {
                Log.e(TAG, "No Accelerometer Sensor found. Programme cannot proceed");
            }
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
                mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "Gyroscope found");
            } else {
                Log.e(TAG, "No Gyroscope found. Programme cannot proceed");
            }
            */
        }


    }


    //Stop listening to all registered sensors
    public void stopSensorListener() {
        if (mSensorManager != null){
            mSensorManager.unregisterListener(this);
        }
    }


    //Log output if accuracy change
    //TODO: decide whether you want it to create a visible effect
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){

        String currentAccuracy;
        switch(accuracy){
            case 1: currentAccuracy = "SENSOR_STATUS_ACCURACY_LOW";
                break;
            case 2: currentAccuracy = "SENSOR_STATUS_ACCURACY_MEDIUM";
                break;
            case 3: currentAccuracy = "SENSOR_STATUS_ACCURACY_HIGH";
                break;
            case -1: currentAccuracy = "SENSOR_STATUS_NO_CONTACT";
                break;
            case 0: currentAccuracy = "SENSOR_STATUS_UNRELIABLE";
                break;
            default: currentAccuracy = "NON_VALID_OPTION";
        }
        Log.d(TAG, "Sensor Accuracy Changed to " + currentAccuracy);
    }

    //Get and upload sensor data items when a new event is reported to the android wear network
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        dataUploader.sendSensorData(sensorEvent.sensor.getType(), sensorEvent.timestamp, sensorEvent.values, sensorEvent.accuracy);
    }

    //Required method to allow other activities to bind to this service if wanted
    //Not used in this app
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
