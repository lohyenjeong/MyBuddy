package com.lohyenjeong.mybuddy.data;

import io.realm.RealmObject;

/**
 * Created by lohyenjeong on 8/8/16.
 */
public class GestureDataPoint extends RealmObject {
    private long timeStamp;
    private double gestureID;

    public GestureDataPoint(){

    }

    public GestureDataPoint(long timeStamp, double gestureID){
        this.timeStamp = timeStamp;
        this.gestureID = gestureID;
    }

    public void setTimeStamp(long Timestamp){
        this.timeStamp = timeStamp;
    }

    public void setGestureID(double gestureID){
        this.gestureID = gestureID;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public double getGestureID(){
        return gestureID;
    }


}
