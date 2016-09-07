package com.lohyenjeong.mybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.lohyenjeong.mybuddy.challengingbehaviour.CBRecognition;
import com.lohyenjeong.mybuddy.content.AlertContent;


public class CaregiverActivity extends AppCompatActivity implements AlertFragment.OnListFragmentInteractionListener{
    private static final String TAG = "MyBuddy/Caregiver";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver);

        //TODO: run in new thread
        CBRecognition cbRecognition = new CBRecognition(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        switch(id){
            case R.id.action_account:
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            case R.id.action_pair:
                startActivity(new Intent(this, PairingActivity.class));
                return true;
            case R.id.action_about:
                //TODO: check for ways to recycle aboutactivity if it already exist in backstack

                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    @Override
    public void onStart(){
        super.onStart();


    }


    @Override
    public void onListFragmentInteraction(AlertContent.AlertItem item) {

    }


}
