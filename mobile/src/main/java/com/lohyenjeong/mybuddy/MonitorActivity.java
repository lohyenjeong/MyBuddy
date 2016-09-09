package com.lohyenjeong.mybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lohyenjeong.mybuddy.challengingbehaviour.CBRecognition;
import com.lohyenjeong.mybuddy.content.GestureContent;


public class MonitorActivity extends AppCompatActivity implements GestureListFragment.OnListFragmentInteractionListener{
    private static final String TAG = "MyBuddy/Main";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        //TODO: run in new thread
        CBRecognition cbRecognition = new CBRecognition(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Signed in");
        } else {
            // No user is signed in
            Log.d(TAG, " Not Signed in");
        }



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

            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();


                startActivity(new Intent(this, LoginActivity.class));
                finish();
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
    public void onListFragmentInteraction(GestureContent.GestureItem item) {

    }


}
