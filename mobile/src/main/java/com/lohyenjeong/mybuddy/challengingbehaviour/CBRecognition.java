package com.lohyenjeong.mybuddy.challengingbehaviour;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lohyenjeong.mybuddy.R;

import com.lohyenjeong.mybuddy.gesture.GestureNames;
import com.lohyenjeong.mybuddy.models.Occurrence;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import weka.core.converters.ArffSaver;
import weka.core.pmml.Array;

/**
 * Created by lohyenjeong on 9/8/16.
 */
public class CBRecognition {
    private final static String TAG = "MyBuddy/CBRecognition";

    private static RandomForest randomForest;

    private static Attribute pacing;
    private static Attribute rocking;
    private static Attribute hairpulling;
    private static Attribute scratching;
    private static Attribute all;

    private static Attribute classAtt;

    private static ArrayList<Attribute> attributeList;
    private static ArrayList<String> classList;

    private Instances trainDataset;

    private Context context;

    private static String FILENAME = "MyBuddyBehaviours/behaviour2.arff";
    private static File file;


    private DatabaseReference mDatabase;
    private String username;

    private SharedPreferences prefs;

    public CBRecognition(Context context){
        this.context = context;

        prefs = context.getSharedPreferences(context.getString(R.string.user_data), 0);
        username = prefs.getString(context.getString(R.string.user_username), "");
        Log.d(TAG, "username is " + username);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("gestures").child(username);

        if(file == null || trainDataset == null) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), FILENAME);
            readDataSet();
        }

        if(randomForest == null){
            trainRandomForest();
        }
    }

    private synchronized void readDataSet(){
        try {
            InputStream in;
            if(file == null){
                in = context.getResources().openRawResource(R.raw.userdeployed);
                writeDatabase();
            }else {
                in = new DataInputStream(new FileInputStream(file));
            }
            BufferedReader datafile = new BufferedReader(new InputStreamReader(in));
            trainDataset = new Instances(datafile);
            pacing = trainDataset.attribute(0);
            rocking = trainDataset.attribute(1);
            hairpulling = trainDataset.attribute(2);
            scratching = trainDataset.attribute(3);
            all = trainDataset.attribute(4);
            classAtt = trainDataset.attribute(5);

            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            attributeList = new ArrayList<Attribute>(GestureNames.TOTALNOWARNINGS+2);
            attributeList.add(pacing);
            attributeList.add(rocking);
            attributeList.add(hairpulling);
            attributeList.add(scratching);
            attributeList.add(all);
            attributeList.add(classAtt);

        }catch (Exception e){
            Log.e(TAG, "Error when reading file");
        }
    }


    private synchronized void trainRandomForest(){
        try{
            randomForest = new RandomForest();
            randomForest.buildClassifier(trainDataset);
        }catch(Exception e){
            Log.e(TAG, "Error training forest: ", e);
        }


    }

    //Adds new instance to dataset
    public synchronized void addInstance(int[] instanceValues){
        Log.d(TAG, "adding instance");
        if(trainDataset == null){
            readDataSet();
        }

        //Forgets old training material that is more than 500 instances ago
        if(trainDataset.size() == 500){
            removeInstance();
        }

        trainDataset.add(new DenseInstance(6));
        Log.d(TAG, "created denseInstance");

        trainDataset.lastInstance().setValue(pacing,instanceValues[0]);
        trainDataset.lastInstance().setValue(rocking,instanceValues[1]);
        trainDataset.lastInstance().setValue(hairpulling,instanceValues[2]);
        trainDataset.lastInstance().setValue(scratching,instanceValues[3]);
        trainDataset.lastInstance().setValue(all,instanceValues[4]);

        if(instanceValues[5] == 0) {
            trainDataset.lastInstance().setValue(classAtt, "Cb-null");
        }else if(instanceValues[5] == 1){
            trainDataset.lastInstance().setValue(classAtt, "Cb-occurs");
        }

        trainRandomForest();
        Log.d(TAG, "added instances");
    }

    private synchronized  void removeInstance(){
        trainDataset.remove(0);
    }


    private synchronized void writeDatabase(){

        String DIRNAME = "MyBuddyBehaviours";
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), DIRNAME);
        if (!dir.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        File file = new File(dir, "behaviour2.arff");

        try {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(trainDataset);
            saver.setFile(file);
            saver.writeBatch();
        } catch (Exception e) {
            Log.d(TAG, "Exception when writing file " + e);
        }

    }


    public void predictInstance(int[] instanceValues){
        Instances newInstances = new Instances("newInstances", attributeList, 1);
        newInstances.setClassIndex(GestureNames.TOTALNOWARNINGS+1);
        newInstances.add(new DenseInstance(GestureNames.TOTALNOWARNINGS+2));

        Log.d(TAG, "Test predicting Instance");

        newInstances.get(0).setValue(pacing, instanceValues[0]);
        newInstances.get(0).setValue(rocking, instanceValues[1]);
        newInstances.get(0).setValue(hairpulling, instanceValues[2]);
        newInstances.get(0).setValue(scratching, instanceValues[3]);
        newInstances.get(0).setValue(all, instanceValues[4]);

        if(randomForest ==  null){
            if(trainDataset == null){
                readDataSet();
            }
            trainRandomForest();
        }
        double result = -1;
        try {
            result = randomForest.classifyInstance(newInstances.get(0));
        }catch(Exception e){
            Log.e(TAG, "Unable to classify instance " + e);
        }
        String r = "";
        if(result == 0.0){
            r = "Cb-occurs";
            //Add instance to database
            long timestamp = Calendar.getInstance().getTimeInMillis();
            SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat formatterTime = new SimpleDateFormat("HH.mm.ss");
            String date = formatterDate.format(timestamp);
            String time = formatterTime.format(timestamp);

            String key = mDatabase.push().getKey();
            Occurrence occur = new Occurrence(date, time, 6, username);
            Map<String, Object> occurValues = occur.toMap();

            Map<String, Object> childupdates = new HashMap<>();
            childupdates.put(key, occurValues);

            mDatabase.updateChildren(childupdates);
        }else if(result == 1.0){
            r = "Cb-null";
        }else{
            r = "unknown";
        }

        Log.d(TAG, "Test Challenging behaviour likelihood: " + r);

    }

}
