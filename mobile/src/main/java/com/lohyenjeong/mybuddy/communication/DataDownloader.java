package com.lohyenjeong.mybuddy.communication;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;
import com.lohyenjeong.mybuddy.gesture.GestureRecognition;
import com.lohyenjeong.mybuddy.shared.DataMapKeys;
import com.lohyenjeong.mybuddy.data.SensorNames;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lohyenjeong on 1/8/16.
 * Responsible for downloading sensor data from the Android Wear Network
 */
public class DataDownloader extends WearableListenerService {
    private static final String TAG ="MyBuddy/DataDownloader";

    private static int dataPointCount;
    private ExecutorService executorService;
    private static long dataTimestamp;
    private static float dataX[];
    private static float dataY[];
    private static float dataZ[];

    private GestureRecognition gestureRecognition;


    //Log to console when watch is connected
    //TODO: add notification?
    @Override
    public void onPeerConnected(Node peer){
        super.onPeerConnected(peer);
        Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }


    //Log to console when watch is disconnected
    //TODO: add notification?
    @Override
    public void onPeerDisconnected(Node peer){
        super.onPeerDisconnected(peer);
        Log.i(TAG, "Disconnected: " + peer.getDisplayName()+ " (" +peer.getId() + ")");
    }


    //Receives the data items from changes in sensor data
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //Log.d(TAG, "onDataChanged()");
        if(gestureRecognition == null){
            gestureRecognition = new GestureRecognition(getApplicationContext());
        }

        if(dataX == null){
            dataX = new float[20];
            dataPointCount = 0;
        }
        if(dataY == null){
            dataY = new float[20];
            dataPointCount = 0;
        }
        if(dataZ == null){
            dataZ = new float[20];
            dataPointCount = 0;
        }

        if(executorService == null){
            this.executorService = Executors.newCachedThreadPool();
        }

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if (path.startsWith("/sensors/")) {
                    unpackSensorData(Integer.parseInt(uri.getLastPathSegment()), DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
            }
        }
    }

    private void unpackSensorData(int sensorID, DataMap dataMap){
        int accuracy = dataMap.getInt(DataMapKeys.ACCURACY);
        long timestamp = dataMap.getLong(DataMapKeys.TIMESTAMP);
        final float[] values = dataMap.getFloatArray(DataMapKeys.VALUES);

        String sensorName = SensorNames.getName(sensorID);

        //If 20 datapoints have already been collected, reset array
        if(dataPointCount == 20){
            dataPointCount = 0;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    gestureRecognition.recogniseGesture(dataTimestamp, dataX, dataY, dataZ);
                }
            });
        }
        //record the timestamp of the first datapoint
        if(dataPointCount == 0){
            dataTimestamp = timestamp;
        }

        //Collect data into array to extract features from
        dataX[dataPointCount] = values[0];
        dataY[dataPointCount] = values[1];
        dataZ[dataPointCount] = values[2];


        //Log.d(TAG, "Transformed Data " + sensorName + " = " + String.valueOf(timestamp) + " " + dataX[dataPointCount] + " " + dataY[dataPointCount] + " " + dataZ[dataPointCount]);
        dataPointCount++;

    }


}
