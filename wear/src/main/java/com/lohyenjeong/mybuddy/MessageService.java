package com.lohyenjeong.mybuddy;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.lohyenjeong.mybuddy.shared.MessagePaths;

/**
 * Created by lohyenjeong on 30/7/16.
 * Class receives messages from the phone
 * Used to start or stop the sensor collection process
 */
public class MessageService extends WearableListenerService{
    private static final String TAG = "MyBuddy/MessageService";

    private MobileClient mobileClient;

    @Override
    public void onCreate(){
        super.onCreate();

        mobileClient =  MobileClient.getMobileClient(this);
    }

    //Receive message from phone to control whether to start or stop SensorListener Service
    //TODO: instead of reading path perhaps read the payload, then you can delete the shared MessagePaths class
    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        Log.d(TAG, "Message from phone: " + messageEvent.getPath());

        if(messageEvent.getPath().equals(MessagePaths.START_SENSORS)){
            Intent intent = new Intent(this, SensorListener.class);
            startService(intent);
        }
        if(messageEvent.getPath().equals(MessagePaths.STOP_SENSORS)) {
            Intent intent = new Intent(this, SensorListener.class);
            stopService(intent);
        }
    }
}
