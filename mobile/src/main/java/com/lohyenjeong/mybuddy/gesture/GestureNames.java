package com.lohyenjeong.mybuddy.gesture;

import android.util.Log;
import android.util.SparseArray;

/**
 * Created by lohyenjeong on 8/8/16.
 */
public class GestureNames {

    private static SparseArray<String> names;


    public static int TOTALNOGESTURES = 7;
    public static int TOTALNOWARNINGS = 4;
    public static int TOTALNOCB = 2;

    public static String getName(int gestureId){
        if(names == null){
            names = new SparseArray<>();

            names.append(-1, "Null");
            names.append(0, "Pacing");
            names.append(1, "Rocking");
            names.append(2, "Pulling hair");
            names.append(3, "Scratching");
            names.append(4, "Hitting");
            names.append(5, "Punching");
            names.append(6, "Still");
        }

        String name = names.get(gestureId);

        if(name == null){
            name = "unknown";
        }

        return name;
    }
}
