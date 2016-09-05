package com.lohyenjeong.mybuddy;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class GestureModelBuild extends AppCompatActivity {

    static final String TAG = "Crazy";

    private Attribute meanX;
    private Attribute maxX;
    private Attribute maxY;
    private Attribute maxZ;
    private Attribute minY;
    private Attribute meanCrossingY;
    private Attribute absMeanY;
    private Attribute stdDeviationX;
    private Attribute skewnessX;
    private Attribute skewnessY;
    private Attribute skewnessZ;
    private Attribute kurtosisX;
    private Attribute rootMeanSquareY;
    private Attribute rootMeanSquareZ;
    private Attribute percentile25X;
    private Attribute percentile25Y;
    private Attribute percentile25Z;
    private Attribute percentile50Y;
    private Attribute percentile50Z;
    private Attribute percentile75X;
    private Attribute percentile75Y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_model_build);

        try {
            // load training dataset
            InputStream is = getResources().openRawResource(R.raw.gesturedata200);
            BufferedReader datafile = new BufferedReader(new InputStreamReader(is));


            Instances inputDataset = new Instances(datafile);

            Remove remove = new Remove();
            int[] attributes = {
                    3,4,5,6,7,9,10,13,19,21,22,23,24,27,29,30,31,32,33,34,35,36,39
            };
            remove.setAttributeIndicesArray(attributes);
            remove.setInvertSelection(true);
            remove.setInputFormat(inputDataset);
            Instances trainDataset = Filter.useFilter(inputDataset, remove);


            // set class index to the last attribute
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);
            // get number of classes
            int numClasses = trainDataset.numClasses();
            // print out class values in the training dataset
            for (int i = 0; i < numClasses - 1; i++) {
                // get class string value using the class index
                String classValue = trainDataset.classAttribute().value(i);
                Log.d(TAG, "Class Value " + i + " is " + classValue);
            }

            int numAttributes = trainDataset.numAttributes();
            Log.d(TAG, "number of attributes are : " + String.valueOf(numAttributes - 1));
            for (int i = 0; i < numAttributes; i++) {
                String attributeValue = trainDataset.attribute(i).name();
                Log.d(TAG, "Attribute name is : " + i + " " + attributeValue);
            }

            //Randomise the data
            int seed = 42;
            int folds = 10;
            Random rand = new Random(seed);
            Instances randData = new Instances(trainDataset);
            randData.randomize(rand);
            randData.stratify(folds);


            // create and build the classifier
            BayesNet bayesNet = new BayesNet();


            if (randData.classAttribute().isNominal())
                randData.stratify(folds);

            // perform cross-validation
            Evaluation eval = new Evaluation(randData);
            for (int n = 0; n < folds; n++) {
                Instances train = randData.trainCV(folds, n);
                Instances test = randData.testCV(folds, n);
                // build and evaluate classifier
                BayesNet bayesNetCopy = (BayesNet) BayesNet.makeCopy(bayesNet);
                bayesNetCopy.buildClassifier(train);
                eval.evaluateModel(bayesNetCopy, test);
            }
            Log.d(TAG, " ");
            Log.d(TAG, "=== Setup ===");
            Log.d(TAG, "Classifier: " + bayesNet.getClass().getName() + " " + Utils.joinOptions(bayesNet.getOptions()));
            Log.d(TAG, "Dataset: " + inputDataset.relationName());
            Log.d(TAG, "Folds: " + folds);
            Log.d(TAG, "Seed: " + seed);
            Log.d(TAG, "");
            Log.d("", eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));

            bayesNet.buildClassifier(trainDataset);

            String DIRNAME = "MyBuddy3";
            Vector v = new Vector();
            v.add(bayesNet);
            v.add(new Instances(trainDataset, 0));

            String string = "hello world!";

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), DIRNAME);
            if (!dir.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
            File file = new File(dir, "gesture.model");

            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                SerializationHelper.write(oos, v);
                oos.flush();
                oos.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception when writing file " + e);
            }

            /*
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(bayesNet);
            oos.flush();
            oos.close();
            */

            /*
            ObjectInputStream ois = new ObjectInputStream(getAssets().open("gesture.model"));
            Vector v2 = (Vector) SerializationHelper.read(ois);
            BayesNet bayesNetLoaded = (BayesNet) v2.get(0);
            Instances header = (Instances) v2.get(1);

            ArrayList<Attribute> attributeList = new ArrayList<>(header.numAttributes());
            ArrayList<String> classList = new ArrayList<String>(6);


            Attribute meanX = new Attribute("meanX");
            Attribute maxX = new Attribute("maxX");
            Attribute maxY = new Attribute("maxY");
            Attribute maxZ = new Attribute("maxZ");
            Attribute minY = new Attribute("minY");

            Attribute meanCrossingY = new Attribute("meanCrossingY");
            Attribute absMeanY = new Attribute("absMeanY");
            Attribute stdDeviationX = new Attribute("stdDeviationX");
            Attribute skewnessX = new Attribute("skewnessX");
            Attribute skewnessY = new Attribute("skewnessY");

            Attribute skewnessZ = new Attribute("skewnessZ");
            Attribute kurtosisX = new Attribute("kurtosisX");
            Attribute rootMeanSquareY = new Attribute("rootMeanSquareY");
            Attribute rootMeanSquareZ = new Attribute("rooteMeanSquareZ");
            Attribute percentile25X = new Attribute("percentile25X");

            Attribute percentile25Y = new Attribute("percentile25Y");
            Attribute percentile25Z = new Attribute("percentile25Z");
            Attribute percentile50Y = new Attribute("percentile50Y");
            Attribute percentile50Z = new Attribute("percentile50Z");
            Attribute percentile75X = new Attribute("percentile75X");

            Attribute percentile75Y = new Attribute("percentile75Y");


            classList.add("gesture-pacing");
            classList.add("gesture-rocking");
            classList.add("gesture-hairpulling");
            classList.add("gesture-scratching");
            classList.add("gesture-hitting");
            classList.add("gesture-punching");

            Attribute classAtt = new Attribute("gestureClass", classList);

            attributeList.add(meanX);
            attributeList.add(maxX);
            attributeList.add(maxY);
            attributeList.add(maxZ);
            attributeList.add(minY);

            attributeList.add(meanCrossingY);
            attributeList.add(absMeanY);
            attributeList.add(stdDeviationX);
            attributeList.add(skewnessX);
            attributeList.add(skewnessY);

            attributeList.add(skewnessZ);
            attributeList.add(kurtosisX);
            attributeList.add(rootMeanSquareY);
            attributeList.add(rootMeanSquareZ);
            attributeList.add(percentile25X);

            attributeList.add(percentile25Y);
            attributeList.add(percentile25Z);
            attributeList.add(percentile50Y);
            attributeList.add(percentile50Z);
            attributeList.add(percentile75X);

            attributeList.add(percentile75Y);
            attributeList.add(classAtt);

            Instances newInstances = new Instances("newInstances", attributeList, 1);
            newInstances.setClassIndex(21);
            newInstances.add(new DenseInstance(22));

            newInstances.get(0).setValue(meanX, -8.582927);
            newInstances.get(0).setValue(maxX, 4.791559);
            newInstances.get(0).setValue(maxY, 27.741465);
            newInstances.get(0).setValue(maxZ, 0);
            newInstances.get(0).setValue(minY, -19.074903);
            Log.d(TAG, "set first 5");
            newInstances.get(0).setValue(meanCrossingY, 14);
            newInstances.get(0).setValue(absMeanY, 14.108377);
            newInstances.get(0).setValue(stdDeviationX, 41.30849);
            newInstances.get(0).setValue(skewnessX, -0.678053);
            newInstances.get(0).setValue(skewnessY, 0.09838);
            Log.d(TAG, "set 10");
            newInstances.get(0).setValue(skewnessZ, -2.039875);
            newInstances.get(0).setValue(kurtosisX, 1.696668);
            newInstances.get(0).setValue(rootMeanSquareY, 16.319323);
            newInstances.get(0).setValue(rootMeanSquareZ, 9.028564);
            newInstances.get(0).setValue(percentile25X, -13.468581);
            Log.d(TAG, "set 15");
            newInstances.get(0).setValue(percentile25Y, -16.449213);
            newInstances.get(0).setValue(percentile25Z, -8.365939);
            newInstances.get(0).setValue(percentile50Y, 3.114857);
            newInstances.get(0).setValue(percentile50Z, -7.380576);
            newInstances.get(0).setValue(percentile75X, -5.068076);
            Log.d(TAG, "set 20");
            newInstances.get(0).setValue(percentile75Y, 10.295666);

            try {
                Double result = bayesNetLoaded.classifyInstance(newInstances.get(0));
                Log.d(TAG, newInstances.get(0).classValue() + " -> " + result);

                double[] classP = new double[6];
                classP = bayesNetLoaded.distributionForInstance(newInstances.get(0));

                for (int i = 0; i < classP.length; i++) {
                    Log.d(TAG, i + " " + classP[i]);
                }


            } catch (Exception e) {
                Log.d(TAG, "catch failed to classify " + e);
            }

            */

            /*
            InputStream isTest = getResources().openRawResource(R.raw.unknown);
            BufferedReader testFile = new BufferedReader(new InputStreamReader(isTest));
            Instances data = new Instances(testFile);
            data.setClassIndex(21);

            Log.d(TAG, "numclasses " + header.numClasses());

            for (int i = 0; i < data.numInstances(); i++) {
                Instance curr = data.instance(i);
                // create an instance for the classifier that fits the training data
                // Instances object returned here might differ slightly from the one
                // used during training the classifier, e.g., different order of
                // nominal values, different number of attributes.
                Instance inst = new DenseInstance(header.numAttributes());
                inst.setDataset(header);
                Log.d(TAG, "Header number of att " + header.numAttributes());
                for (int n = 0; n < header.numAttributes(); n++) {
                    Attribute att = data.attribute(header.attribute(n).name());
                    Log.d(TAG, header.attribute(n).name());
                    // original attribute is also present in the current dataset
                    if (att != null) {
                        if (att.isNominal()) {
                            // is this label also in the original data?
                            // Note:
                            // "numValues() > 0" is only used to avoid problems with nominal
                            // attributes that have 0 labels, which can easily happen with
                            // data loaded from a database
                            if ((header.attribute(n).numValues() > 0) && (att.numValues() > 0)) {
                                String label = curr.stringValue(att);
                                int index = header.attribute(n).indexOfValue(label);
                                if (index != -1)
                                    inst.setValue(n, index);
                            }
                        }
                        else if (att.isNumeric()) {
                            inst.setValue(n, curr.value(att));
                        }
                        else {
                            throw new IllegalStateException("Unhandled attribute type!");
                        }
                    }
                }

                // predict class
                double pred = bayesNet.classifyInstance(inst);
                Log.d(TAG, inst.classValue() + " -> " + pred);
            }

            */



            /*
            // set class index to the last attribute
            testDataset.setClassIndex(testDataset.numAttributes() - 1);
            // loop through the new dataset and make predictions
            Instances labeled = new Instances(testDataset);

            Log.d(TAG, "===================");
            Log.d(TAG, "Actual Class, NB Predicted");
            for (int i = 0; i < testDataset.numInstances(); i++) {
                // get class double value for current instance
                double actualClass = bayesNet.classifyInstance(testDataset.instance(i));
                labeled.instance(i).setClassValue(actualClass);
                Log.d(TAG, labeled.toString());
                testDataset.delete(i);
                // get class string value using the class index using the class's
                // int value
            }
            */

        } catch (Exception e) {
            Log.e(TAG, "Exception here");
        }

    }
}
