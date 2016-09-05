package com.lohyenjeong.mybuddy.data;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by lohyenjeong on 8/8/16.
 */
public class User extends RealmObject{
    private long userID;
    private String email;


    public long getUserID(){
        return userID;
    }

    public String getEmail(){
        return email;
    }


    public void setUserID(long userID){
        this.userID = userID;
    }

    public void setEmail(String email){
        this.email = email;
    }

}
