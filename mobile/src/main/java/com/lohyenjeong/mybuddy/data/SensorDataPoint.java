package com.lohyenjeong.mybuddy.data;

/**
 * Created by lohyenjeong on 1/8/16.
 */
public class SensorDataPoint {
    private long timeStamp;
    private float[] values;
    private int accuracy;
    private int sensorType;


    public SensorDataPoint(long timeStamp, int accuracy, float[] values, int sensorType){
        this.timeStamp = timeStamp;
        this.accuracy = accuracy;
        this.values = values;
        this.sensorType = sensorType;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public float[] getValues(){
        return values;
    }

    public int getAccuracy(){
        return accuracy;
    }

    public int getSensorType(){
        return sensorType;
    }

}
