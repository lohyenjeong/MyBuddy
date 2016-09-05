package com.lohyenjeong.mybuddy.data;

import android.util.SparseArray;

/**
 * Created by lohyenjeong on 1/8/16.
 */
public class SensorNames {
    public static SparseArray<String> names;

    public SensorNames(){
        names = new SparseArray<>();

        names.append(0, "Debug Sensor");
        names.append(android.hardware.Sensor.TYPE_ACCELEROMETER, "Accelerometer");
        names.append(android.hardware.Sensor.TYPE_GYROSCOPE, "Gyroscope");
        names.append(android.hardware.Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration");
    }

    public static String getName(int sensorId){
        String name = names.get(sensorId);

        if(name == null){
            name = "unknown";
        }

        return name;
    }

}
