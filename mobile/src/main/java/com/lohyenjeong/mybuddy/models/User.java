package com.lohyenjeong.mybuddy.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by lohyenjeong on 5/9/16.
 * Class to define the structure of the user in the Firebase console
 */
@IgnoreExtraProperties
public class User {
    private static final String TAG = "MyBuddy/User";

    public static final int MODE_PERSONAL_USE = 0;
    public static final int MODE_CAREGIVER = 1;

    public String username;
    public String email;
    public int mode;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, int mode) {
        this.username = username;
        this.email = email;
        this.mode = mode;
    }

}