package com.lohyenjeong.mybuddy.gesture;

import android.content.Context;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lohyenjeong.mybuddy.R;
import com.lohyenjeong.mybuddy.challengingbehaviour.CBData;
import com.lohyenjeong.mybuddy.data.SensorDataPoint;
import com.lohyenjeong.mybuddy.models.Occurrence;

import org.w3c.dom.Attr;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import weka.classifiers.bayes.BayesNet;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * Created by lohyenjeong on 8/8/16.
 */
public class GestureRecognition {
    private static String TAG = "MyBuddy/GestureRecognition";

    //Set the threshold for null gesture
    private static double THRESHOLD = 0.95;

    private static int NOFEATURES = FeatureExtraction.TOTALNOFEATURES;
    private static int NOGESTURES = GestureNames.TOTALNOGESTURES;
    private static int GESTUREWINDOWSIZE = 5;
    private static final int WINDOWTRESHOLD = 3;

    private static BayesNet bayesNet = null;
    private Context context;
    private Instances header;

    private static ArrayList<Attribute> attributeList;
    private static ArrayList<String> classList;

    private Attribute maxX;
    private Attribute maxY;
    private Attribute maxZ;
    private Attribute minX;
    private Attribute minY;

    private Attribute meanCrossingX;
    private Attribute meanCrossingY;
    private Attribute absMeanY;
    private Attribute stdDeviationY;
    private Attribute skewnessX;

    private Attribute skewnessY;
    private Attribute skewnessZ;
    private Attribute kurtosisX;
    private Attribute rootMeanSquareX;
    private Attribute rootMeanSquareZ;

    private Attribute percentile25X;
    private Attribute percentile25Y;
    private Attribute percentile25Z;
    private Attribute percentile50X;
    private Attribute percentile50Y;

    private Attribute percentile50Z;
    private Attribute percentile75X;

    private Attribute classAtt;

    private static int count = 0;

    private int[] gesturesArray;

    private static CBData cbData;

    private DatabaseReference mDatabase;
    private String username;

    private SharedPreferences prefs;

    public GestureRecognition(Context context){


        prefs = context.getSharedPreferences(context.getString(R.string.user_data), 0);
        username = prefs.getString(context.getString(R.string.user_username), "");
        Log.d(TAG, "username is " + username);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("gestures").child(username);

        this.context = context;

        if(classList == null){
            classList = new ArrayList<String>(7);
            classList.add("gesture-pacing");
            classList.add("gesture-rocking");
            classList.add("gesture-hairpulling");
            classList.add("gesture-scratching");
            classList.add("gesture-hitting");
            classList.add("gesture-punching");
            classList.add("gesture-still");
        }

        if(attributeList == null){
            attributeList = new ArrayList<>(NOFEATURES + 1);

            maxX = new Attribute("maxX");
            maxY = new Attribute("maxY");
            maxZ = new Attribute("maxZ");
            minX = new Attribute("minX");
            minY = new Attribute("minY");

            meanCrossingX = new Attribute("meanCrossingX");
            meanCrossingY = new Attribute("meanCrossingY");
            absMeanY = new Attribute("absMeanY");
            stdDeviationY = new Attribute("stdDeviationY");
            skewnessX = new Attribute("skewnessX");

            skewnessY = new Attribute("skewnessY");
            skewnessZ = new Attribute("skewnessZ");
            kurtosisX = new Attribute("kurtosisX");
            rootMeanSquareX = new Attribute("rootMeanSquareX");
            rootMeanSquareZ = new Attribute("rooteMeanSquareZ");

            percentile25X = new Attribute("percentile25X");
            percentile25Y = new Attribute("percentile25Y");
            percentile25Z = new Attribute("percentile25Z");
            percentile50X = new Attribute("percentile50X");
            percentile50Y = new Attribute("percentile50Y");

            percentile50Z = new Attribute("percentile50Z");
            percentile75X = new Attribute("percentile75X");

            classAtt = new Attribute("gestureClass", classList);

            attributeList.add(maxX);
            attributeList.add(maxY);
            attributeList.add(maxZ);
            attributeList.add(minX);
            attributeList.add(minY);

            attributeList.add(meanCrossingX);
            attributeList.add(meanCrossingY);
            attributeList.add(absMeanY);
            attributeList.add(stdDeviationY);
            attributeList.add(skewnessX);

            attributeList.add(skewnessY);
            attributeList.add(skewnessZ);
            attributeList.add(kurtosisX);
            attributeList.add(rootMeanSquareX);
            attributeList.add(rootMeanSquareZ);

            attributeList.add(percentile25X);
            attributeList.add(percentile25Y);
            attributeList.add(percentile25Z);
            attributeList.add(percentile50X);
            attributeList.add(percentile50Y);

            attributeList.add(percentile50Z);
            attributeList.add(percentile75X);

            attributeList.add(classAtt);

        }

        if(bayesNet == null){
            try {
                ObjectInputStream ois = new ObjectInputStream(context.getAssets().open("gesture.model"));
                Vector v2 = (Vector) SerializationHelper.read(ois);
                bayesNet = (BayesNet) v2.get(0);
                header = (Instances) v2.get(1);
                Log.d(TAG, "Loaded Model");
            }catch(Exception e){
                Log.e(TAG, "Cannot load model ", e);
            }
        }

        if(gesturesArray == null){
            gesturesArray = new int[GESTUREWINDOWSIZE];
        }

        if(cbData == null){
            cbData = new CBData(context);
        }
    }

    public void recogniseGesture(long timestamp, float[] dataX, float [] dataY, float[] dataZ){
        float [] extractedFeatures;
        extractedFeatures = FeatureExtraction.extractFeatures(dataX, dataY, dataZ);

        double result = classifyNewInstance(extractedFeatures);
        String gestureType = GestureNames.getName((int) result);

        updateGestureArray((int) result);

        Log.d(TAG, "Final result " + gestureType);
    }


    private double classifyNewInstance(float[] features){
        Instances newInstances = new Instances("newInstances", attributeList, 1);
        newInstances.setClassIndex(NOFEATURES);
        newInstances.add(new DenseInstance(NOFEATURES+1));

        Log.d(TAG, "created denseInstance for feature extraction");

        newInstances.get(0).setValue(maxX, features[FeatureExtraction.FMAXX]);
        newInstances.get(0).setValue(maxY, features[FeatureExtraction.FMAXY]);
        newInstances.get(0).setValue(maxZ, features[FeatureExtraction.FMAXZ]);
        newInstances.get(0).setValue(minX, features[FeatureExtraction.FMINX]);
        newInstances.get(0).setValue(minY, features[FeatureExtraction.FMINY]);

        newInstances.get(0).setValue(meanCrossingX, features[FeatureExtraction.FMEANCROSSINGX]);
        newInstances.get(0).setValue(meanCrossingY, features[FeatureExtraction.FMEANCROSSINGY]);
        newInstances.get(0).setValue(absMeanY, features[FeatureExtraction.FABSMEANY]);
        newInstances.get(0).setValue(stdDeviationY, features[FeatureExtraction.FSTDDEVIATIONY]);
        newInstances.get(0).setValue(skewnessX, features[FeatureExtraction.FSKEWNESSX]);

        newInstances.get(0).setValue(skewnessY, features[FeatureExtraction.FSKEWNESSY]);
        newInstances.get(0).setValue(skewnessZ, features[FeatureExtraction.FSKEWNESSZ]);
        newInstances.get(0).setValue(kurtosisX, features[FeatureExtraction.FKURTOSISX]);
        newInstances.get(0).setValue(rootMeanSquareX, features[FeatureExtraction.FROOTMEANSQUAREX]);
        newInstances.get(0).setValue(rootMeanSquareZ, features[FeatureExtraction.FROOTMEANSQUAREZ]);

        newInstances.get(0).setValue(percentile25X, features[FeatureExtraction.FPERCENTILE25X]);
        newInstances.get(0).setValue(percentile25Y, features[FeatureExtraction.FPERCENTILE25Y]);
        newInstances.get(0).setValue(percentile25Z, features[FeatureExtraction.FPERCENTILE25Z]);
        newInstances.get(0).setValue(percentile50X, features[FeatureExtraction.FPERCENTILE50X]);
        newInstances.get(0).setValue(percentile50Y, features[FeatureExtraction.FPERCENTILE50Y]);

        newInstances.get(0).setValue(percentile50Z, features[FeatureExtraction.FPERCENTILE50Z]);
        newInstances.get(0).setValue(percentile75X, features[FeatureExtraction.FPERCENTILE75X]);

        Log.d(TAG, "set values");

        double result = -1;
        try {
            result = bayesNet.classifyInstance(newInstances.get(0));
            if(result != - 1){
                double [] probability = bayesNet.distributionForInstance(newInstances.get(0));
                //String resultString = GestureNames.getName((int) result);
                Log.d(TAG, "Result with the probability of : " + result + " " + probability[(int) result]);
                double likelihood = probability[(int) result];
                //set as null gesture if the likelihood of it being a labelled gesture is low
                if(likelihood < THRESHOLD){
                    result = -1;
                }
            }
        }catch (Exception e){
            Log.e(TAG, "instance cannot be classified ", e);
        }
        return result;

    }

    private synchronized void incrementCount(){
        count++;
    }

    private synchronized void resetCount(){
        count = 0;
    }


    private synchronized void updateGestureArray(int gestureID){
        if(count == GESTUREWINDOWSIZE){
            resetCount();
            int [] gestureCount = new int[NOGESTURES];
            for(int i = 0; i< NOGESTURES; i++){
                gestureCount[i] = 0;
            }
            for(int i =0; i < GESTUREWINDOWSIZE; i++){
                if(gesturesArray[i] == 0){
                    gestureCount[0]++;
                }
                if(gesturesArray[i] == 1){
                    gestureCount[1]++;
                }
                if(gesturesArray[i] == 2){
                    gestureCount[2]++;
                }
                if(gesturesArray[i] == 3){
                    gestureCount[3]++;
                }
                if(gesturesArray[i] == 4){
                    gestureCount[4]++;
                }
                if(gesturesArray[i] == 5){
                    gestureCount[5]++;
                }
                if(gesturesArray[i] == 6){
                    gestureCount[6]++;
                }

            }
            Log.d(TAG, "Gestures count " + Arrays.toString(gestureCount));

            int instance = -1;
            long timestamp = Calendar.getInstance().getTimeInMillis();
            SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat formatterTime = new SimpleDateFormat("HH.mm.ss");
                String date = formatterDate.format(timestamp);
                String time = formatterTime.format(timestamp);
                Log.d(TAG, "Test " + date + " " + time);

                for(int i = 0; i < NOGESTURES; i++){
                    //An occurrence of behaviour has been detected
                if(gestureCount[i] >= WINDOWTRESHOLD && i != -1 && i != 7){
                    instance = i;
                    //Add instance to firebase
                    String key = mDatabase.push().getKey();

                    Occurrence occur = new Occurrence(date, time, instance, username);
                    Map<String, Object> occurValues = occur.toMap();

                    Map<String, Object> childupdates = new HashMap<>();
                    childupdates.put(key, occurValues);

                    mDatabase.updateChildren(childupdates);


                    Log.d(TAG, "Test Occurrence instance " + GestureNames.getName(i));
                }
            }
            if(instance == -1){
                Log.d(TAG, "No warnings signs or challenging behaviour");
            }
            cbData.addData(instance);
        }
        gesturesArray[count] = gestureID;
        incrementCount();
    }

}
