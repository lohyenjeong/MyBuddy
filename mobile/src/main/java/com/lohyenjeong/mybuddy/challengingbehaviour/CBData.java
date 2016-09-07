package com.lohyenjeong.mybuddy.challengingbehaviour;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lohyenjeong on 9/8/16.
 */
public class CBData {
    private static final String TAG = "MyBuddy/CBData";

    private static final int WINDOWSIZE = 45;
    private static final int NOCBFEATURES = 5;
    private static ArrayList <Integer> warningsArray;
    private static int count = 0;

    private static CBRecognition cbRecognition;
    private static Context context;

    public CBData(Context context){
        if(warningsArray == null){
            warningsArray = new ArrayList<Integer>();
            resetWarningsArray();
        }
        if(context == null){
            this.context = context;
        }
        if(cbRecognition == null){
            cbRecognition = new CBRecognition(context);
        }

    }

    public void addData(int gestureType){
        Log.d(TAG, "adding Data");
        //If a 15minutes window is filled
        if(count == 45 && gestureType != 4 && gestureType != 5){
            Log.d(TAG, "Full data");
            //Find what was the first datapoint on the 15minutes window
            int first = warningsArray.get(0);

            //If first data point was a warning sign, retrain the randomForest to include warning signs that do not cause CB
            if(first >= 0 && first < 4){
                int [] totalSum = sumArray();
                totalSum[5] = 0;
                cbRecognition.addInstance(totalSum);
            }
            //Remove the firstgesture to make space for the new gesture
            removeFirstGesture();
        }
        //If the gesture is a CB
        if(gestureType == 4 || gestureType == 5){
            Log.d(TAG, "recording CB");
            recordCB(gestureType);
        }
        //If gesture is warning sign
        else if(gestureType == 0|| gestureType == 1 || gestureType == 2 || gestureType == 3){
            Log.d(TAG, "recording warning");
            recordWarning(gestureType);
        }
        //If gesture is a random gesture
        else if(gestureType == -1){
            Log.d(TAG, "recording null Occurrence");
            recordNullGesture();
        }
        else{
            Log.e(TAG, "unknown gesture type"  + gestureType);
        }

        Log.d(TAG, "completed adding data");
    }


    //If cb has occured
    private void recordCB(int gestureType){
        //If there were gestures before this
        if(count != 0){
            int [] totalSum = sumArray();
            boolean warning = false;
            //Check whether a warning sign has occurred before
            for(int i = 0; i < totalSum.length; i++){
                if(totalSum[i] != 0){
                    warning = true;
                }
            }
            //If a warning sign has occured before, add datapoint to randomForest
            if(warning) {
                totalSum[5] = 1;
                cbRecognition.addInstance(totalSum);
            }
            resetWarningsArray();
        }
    }


    private synchronized int [] sumArray(){
        int[] gestureArray = new int[6];
        for(int i =0; i < gestureArray.length; i++){
            gestureArray[i] = 0;
            Log.d(TAG, String.valueOf(gestureArray[i]));
        }
        for(int i : warningsArray){
            if(i < 4 && i >=0){
                //Increment count of gesture
                gestureArray[i]++;
                //Increment count of all
                gestureArray[4]++;
            }
        }
        Log.d(TAG, "in sum array ");
        return gestureArray;
    }


    private void recordWarning(int gestureType){
        warningsArray.add(gestureType);
        int [] totalSum = sumArray();
        totalSum[5] = -1;
        for(int i =0; i < totalSum.length; i++){
            Log.d(TAG, "recordwarning " + i + " " + String.valueOf(totalSum[i]));
        }
        cbRecognition.predictInstance(totalSum);
        incrementCount();
    }

    private void recordNullGesture(){
        warningsArray.add(-1);
        incrementCount();
    }

    private synchronized void addGesture(int gestureType){
        warningsArray.add(gestureType);
    }

    private synchronized void removeFirstGesture(){
        warningsArray.remove(0);
    }


    private synchronized void resetWarningsArray(){
        warningsArray.clear();
        resetCount();
    }

    private synchronized void resetCount(){
        count = 0;
    }

    private synchronized void incrementCount(){
        count++;
    }
}
