package com.lohyenjeong.mybuddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.lohyenjeong.mybuddy.shared.DataMapKeys;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by lohyenjeong on 28/7/16.
 * This class is responsible for Google Play Service integration
 * and sending data items to the Android Wear Network
 */
public class DataUploader {
    public static final String TAG = "MyBuddy/DataUploader";
    private static final int CONNECTION_TIMEOUT = 15000;

    public static DataUploader dataUploader;

    private Context context;
    private GoogleApiClient googleApiClient;
    private ExecutorService executorService;

    //Method calling constructor for DataUploader
    //Checks whether dataUploader already exists
    public static DataUploader getDataUploader(Context context) {
        if (dataUploader == null) {
            dataUploader = new DataUploader(context.getApplicationContext());
        }
        return dataUploader;
    }

    //Constructor for DataUploader
    private DataUploader(Context context) {
        this.context = context;

        //Builds the GoogleApiClient for Google Play Service integration
        googleApiClient = new GoogleApiClient.Builder(this.context).addApi(Wearable.API).build();

        //Creates a thread pool that creates new threads as needed
        //but will reuse previously constructed threads when they are available
        executorService = Executors.newCachedThreadPool();
    }

    //Use the executorService to send each sensor datapoint on a new thread in a cachedThreadPool
    //TODO: send sensor data only if it is more than 100 ms?
    public void sendSensorData(final int sensorType, final long timestamp, final float[] values, final int accuracy) {
        executorService.submit(new Runnable(){
            @Override
            public void run() {
                    sendSDTask(sensorType, timestamp, values, accuracy);
            }
        });
    }

    //Runnable to create new data items in the android wear network
    //TODO: remove accuracy as part of datamap maybe?
    //FIXME: check whether there is a need for a filter for each sensor type
    private void sendSDTask(int sensorType, long timestamp, float[] values, int accuracy) {
        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + sensorType);
        dataMap.getDataMap().putLong(DataMapKeys.TIMESTAMP, timestamp);
        dataMap.getDataMap().putFloatArray(DataMapKeys.VALUES, values);
        dataMap.getDataMap().putInt(DataMapKeys.ACCURACY, accuracy);

        PutDataRequest putDataRequest = dataMap.asPutDataRequest();
        uploadData(putDataRequest);
    }

    //Checks the connection to googleApiClient
    //Connects if not previously connected
    //Returns true if connection is successful
    private boolean checkConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }
        ConnectionResult connectionResult = googleApiClient.blockingConnect(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        return connectionResult.isSuccess();
    }


    //Uploads the data item to thej Android Wear Network where it is accessible by the app on all nodes
    private void uploadData(PutDataRequest putDataRequest) {
        if (checkConnection()) {
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }
}
