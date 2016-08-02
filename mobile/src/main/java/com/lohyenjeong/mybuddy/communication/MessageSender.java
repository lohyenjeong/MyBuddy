package com.lohyenjeong.mybuddy.communication;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.lohyenjeong.mybuddy.data.SensorNames;
import com.lohyenjeong.mybuddy.shared.MessagePaths;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lohyenjeong on 1/8/16.
 * This class is responsible for sending start and stop actions to the connected wearable
 */
public class MessageSender {
    private static final String TAG = "MyBuddy/MobileMessageS";
    private static final int TIMEOUT = 15000;

    private static MessageSender messageSender;

    private Context context;
    private ExecutorService executorService;
    private GoogleApiClient googleApiClient;
    private SensorNames sensorNames;


    public static synchronized MessageSender getMobileMessageService(Context context){
        if(messageSender == null){
            messageSender = new MessageSender(context.getApplicationContext());
        }
        return messageSender;
    }

    private MessageSender(Context context){
        this.context = context;
        this.sensorNames = new SensorNames();
        this.googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
    }

    private boolean checkConnection(){
        if(googleApiClient.isConnected()){
            return true;
        }
        ConnectionResult connectionResult = googleApiClient.blockingConnect(TIMEOUT, TimeUnit.MILLISECONDS);

        return connectionResult.isSuccess();

    }

    public void startSensors(){
        executorService.submit(new Runnable(){
            @Override
            public void run() {
                controlSensors(MessagePaths.START_SENSORS);
            }
        });
    }

    public void stopSensors(){
        executorService.submit(new Runnable(){
            @Override
            public void run() {
                controlSensors(MessagePaths.STOP_SENSORS);
            }
        });
    }


    public void getNodes(ResultCallback<NodeApi.GetConnectedNodesResult> pCallback) {
        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(pCallback);
    }


    private void controlSensors(final String path){
        if(checkConnection()){
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();

            Log.d(TAG, "Sending to nodes: " + nodes.size());

            for (Node node : nodes) {
                Log.i(TAG, "add node " + node.getDisplayName());
                Wearable.MessageApi.sendMessage(
                        googleApiClient, node.getId(), path, null
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d(TAG, "controlMeasurementInBackground(" + path + "): " + sendMessageResult.getStatus().isSuccess());
                    }
                });
            }

        }
    }

}
