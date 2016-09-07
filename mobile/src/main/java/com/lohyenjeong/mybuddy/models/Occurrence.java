package com.lohyenjeong.mybuddy.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lohyenjeong on 6/9/16.
 */
@IgnoreExtraProperties
public class Occurrence {

    public String date;
    public String time;
    public int type;
    public String user;


    public Occurrence() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Occurrence(String date, String time, int type, String user) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.user = user;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("time", time);
        result.put("type", type);
        result.put("user", user);

        return result;
    }

}
