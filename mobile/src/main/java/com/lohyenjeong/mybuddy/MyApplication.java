package com.lohyenjeong.mybuddy;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by lohyenjeong on 9/8/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
