package com.lohyenjeong.mybuddy.data;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by lohyenjeong on 9/8/16.
 */
public class ActionData {

    private int eventType;
    private String user;

    private ActionData(){

    }

    public ActionData(String user, int eventType){
        this.eventType = eventType;
        this.user = user;
    }

    public String getUser(){
        return user;
    }

    public int getEventType(){
        return eventType;
    }

}
