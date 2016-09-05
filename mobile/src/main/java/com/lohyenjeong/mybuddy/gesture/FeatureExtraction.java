package com.lohyenjeong.mybuddy.gesture;

import android.util.Log;


import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;

import java.util.Arrays;

/**
 * Created by lohyenjeong on 2/8/16.
 */

//TODO: combine all extractions into one method to make it more efficient. Then you can have one mean, one length bla bla
public class FeatureExtraction {
    public final static String TAG = "MyBuddy/FeatureExt";

    //TODO: change back to 10 when recording new data
    private final static int length = 20;

    public final static int TOTALNOFEATURES = 22;


    public final static int FMAXX = 0;
    public final static int FMAXY = 1;
    public final static int FMAXZ = 2;
    public final static int FMINX = 3;
    public final static int FMINY = 4;

    public final static int FMEANCROSSINGX = 5;
    public final static int FMEANCROSSINGY = 6;
    public final static int FABSMEANY = 7;
    public final static int FSTDDEVIATIONY = 8;
    public final static int FSKEWNESSX = 9;

    public final static int FSKEWNESSY = 10;
    public final static int FSKEWNESSZ = 11;
    public final static int FKURTOSISX = 12;
    public final static int FROOTMEANSQUAREX = 13;
    public final static int FROOTMEANSQUAREZ = 14;

    public final static int FPERCENTILE25X = 15;
    public final static int FPERCENTILE25Y = 16;
    public final static int FPERCENTILE25Z = 17;
    public final static int FPERCENTILE50X = 18;
    public final static int FPERCENTILE50Y = 19;

    public final static int FPERCENTILE50Z = 20;
    public final static int FPERCENTILE75X = 21;


    public FeatureExtraction(){}

    public static float[] extractFeatures(float[] valuesX, float[] valuesY, float[] valuesZ){
        float[] featuresResult = new float[TOTALNOFEATURES];
        float meanX = getMean(valuesX);
        float meanY = getMean(valuesY);
        float meanZ = getMean(valuesZ);


        float maxX = getMax(valuesX);
        float maxY = getMax(valuesY);
        float maxZ = getMax(valuesZ);



        float minX = getMin(valuesX);
        float minY = getMin(valuesY);
        //float minZ = getMin(valuesZ);


        int meanCrossingX = getMeanCrossing(valuesX, meanX);
        int meanCrossingY = getMeanCrossing(valuesY, meanY);
        //int meanCrossingZ = getMeanCrossing(valuesZ, meanZ);


        //float absMeanX = getAbsMean(valuesX);
        float absMeanY = getAbsMean(valuesY);
        //float absMeanZ = getAbsMean(valuesZ);


        //float absDifferenceX = getAbsDifference(valuesX);
        //float absDifferenceY = getAbsDifference(valuesY);
        //float absDifferenceZ = getAbsDifference(valuesZ);


        float stdDeviationX = getStdDeviation(valuesX, meanX);
        float stdDeviationY = getStdDeviation(valuesY, meanY);
        float stdDeviationZ = getStdDeviation(valuesZ, meanZ);


        float skewnessX = getSkewness(valuesX, meanX, stdDeviationX);
        float skewnessY = getSkewness(valuesY, meanY, stdDeviationY);
        float skewnessZ = getSkewness(valuesZ, meanZ, stdDeviationZ);


        float kurtosisX = getKurtosis(valuesX, meanX, stdDeviationX);
        //float kurtosisY = getKurtosis(valuesY, meanY, stdDeviationY);
        //float kurtosisZ = getKurtosis(valuesZ, meanZ, stdDeviationZ);

        float rootMeanSquareX = getRootMeanSquare(valuesX);
        //float rootMeanSquareY = getRootMeanSquare(valuesY);
        float rootMeanSquareZ = getRootMeanSquare(valuesZ);


        float percentile25X = getPercentile(valuesX, 25);
        float percentile25Y = getPercentile(valuesY, 25);
        float percentile25Z = getPercentile(valuesZ, 25);

        float percentile50X = getPercentile(valuesX, 50);
        float percentile50Y = getPercentile(valuesY, 50);
        float percentile50Z = getPercentile(valuesZ, 50);

        float percentile75X = getPercentile(valuesX, 75);
        //float percentile75Y = getPercentile(valuesY, 75);
        //float percentile75Z = getPercentile(valuesZ, 75);


        featuresResult[FMAXX] = maxX;
        featuresResult[FMAXY] = maxY;
        featuresResult[FMAXZ] = maxZ;
        featuresResult[FMINX] = minX;
        featuresResult[FMINY] = minY;

        featuresResult[FMEANCROSSINGX] =meanCrossingX;
        featuresResult[FMEANCROSSINGY] =meanCrossingY;
        featuresResult[FABSMEANY] = absMeanY;
        featuresResult[FSTDDEVIATIONY] =stdDeviationY;
        featuresResult[FSKEWNESSX] = skewnessX;

        featuresResult[FSKEWNESSY]= skewnessY;
        featuresResult[FSKEWNESSZ] = skewnessZ;
        featuresResult[FKURTOSISX] = kurtosisX;
        featuresResult[FROOTMEANSQUAREX] =rootMeanSquareX;
        featuresResult[FROOTMEANSQUAREZ] = rootMeanSquareZ;

        featuresResult[FPERCENTILE25X] = percentile25X;
        featuresResult[FPERCENTILE25Y] = percentile25Y;
        featuresResult[FPERCENTILE25Z] = percentile25Z;
        featuresResult[FPERCENTILE50X] = percentile50X;
        featuresResult[FPERCENTILE50Y] = percentile50Y;

        featuresResult[FPERCENTILE50Z] = percentile50Z;
        featuresResult[FPERCENTILE75X] = percentile75X;


        return featuresResult;

    }

    private static float getMean(float[] sensorValues){
        float sum = 0;
        for(int i = 0; i < length; i++ ){
            sum += sensorValues[i];
        }

        return (sum/length);
    }

    private static float getMax(float[] sensorValues){
        float max = 0;
        for(int i = 0; i < length; i++ ){
            if(max < sensorValues[i]){
                max = sensorValues[i];
            }
        }
        return max;
    }

    private static float getMin(float[] sensorValues){
        float min = 10000;
        for(int i = 0; i <length; i++){
            if(min > sensorValues[i]){
                min = sensorValues[i];
            }
        }
        return min;
    }

    private static int getMeanCrossing(float[] sensorValues, float mean){
        int count =0;
        for(int i=0; i < (length - 1); i++){
            if(sensorValues[i] < mean && sensorValues[i+1] > mean){
                count++;
            }
            else if(sensorValues[i] > mean && sensorValues[i+1] < mean){
                count++;
            }
        }
        return count;
    }

    private static float getAbsMean(float[] sensorValues){
        float sum = 0;
        for(int i = 0; i < length; i++){
            if(sensorValues[i]<0){
                sum += (sensorValues[i] * (float)-1);

            }else{
                sum += sensorValues[i];
            }
        }
        return sum/length;
    }


    private static float getAbsDifference(float[] sensorValues){
        float totalDifference = 0;

        for(int i = 0;  i< (length-1); i++){
            float difference = sensorValues[i+1] - sensorValues[i];
            if(difference < 0){
                difference = difference * ((float)-1);
            }
            totalDifference += difference;
        }
        return totalDifference/length;
    }

    private static float getStdDeviation(float [] sensorValues, float mean){
        float stdDev = 0;
        for(int i = 0; i <  length; i++){
            float difference = (sensorValues[i] - mean);
            stdDev += difference * difference;
        }
        return stdDev/length;
    }

    private static float getSkewness(float[] sensorValues, float stdDeviation, float mean){
        long n = 0;
        double m2 = 0.0;
        double m3 = 0.0;

        for (int i = 0; i < 20; i++) {
            n++;
            double delta = sensorValues[i] - mean;
            double deltaOverN = delta / n;
            double term1 = delta * deltaOverN * (n-1);
            mean += deltaOverN;
            m3 += term1 * deltaOverN * (n - 2) - 3.0 * deltaOverN * m2;
            m2 += term1;
        }

        double output = (m3/n)/Math.pow(m2/n, 1.5);
        output *= (Math.sqrt(n*(n-1))/(n-2));
        float result = (float) output;

        return result;

    }

    private static float getKurtosis(float[] sensorValues, float mean, float stdDeviation){
        double[] senDouble = new double[20];
        for(int i = 0; i < length; i++){
            senDouble[i] = sensorValues[i];
        }

        Kurtosis k = new Kurtosis();
        double result = k.evaluate(senDouble, 0, 20);
        return (float) result;
    }

    private static float getRootMeanSquare(float[] sensorValues){
        float totalSquared = 0;
        for(int i = 0; i < length; i++){
            float squared = (sensorValues[i] * sensorValues[i]);
            totalSquared += squared;
        }
        float avgRM = totalSquared/length;
        float rootMean = (float) Math.sqrt((double)avgRM);
        return rootMean;
    }

    private static float getPercentile(float[] sensorValues, int percent){
        int count = (int) Math.rint(((double)percent/100 * length)-1);
        Arrays.sort(sensorValues);

        return sensorValues[count];
    }

}
