package com.lohyenjeong.mybuddy.challengingbehaviour;

import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;

/**
 * Created by lohyenjeong on 3/8/16.
 */
public class GestureTypes {
    public static final String TAG = "MyBuddy/GestureTypes";

    public static final int noWarnings = 4;
    public static final int noCB = 2;

    public SparseArray<String> cbTypes;

    public GestureTypes(){
        cbTypes = new SparseArray<String>();
        cbTypes.append(0, "Null Gesture");
        cbTypes.append(1, "Rapid pacing");
        cbTypes.append(2, "Rocking chair");
        cbTypes.append(3, "Pinching hand");
        cbTypes.append(4, "Scratching head");
        cbTypes.append(5, "Hitting knee");
        cbTypes.append(6, "Punching front");
    }

    public String getCBTypes(int gestureID){
        String cbType = cbTypes.get(gestureID);
        if(cbType == null){
            cbType = "unknown";
            Log.e(TAG, "Unknown gesture found.");
        }
        return cbType;
    }

}
