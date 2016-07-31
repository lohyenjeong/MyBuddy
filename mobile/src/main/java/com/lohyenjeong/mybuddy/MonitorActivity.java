package com.lohyenjeong.mybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MonitorActivity extends AppCompatActivity {
    private static final String TAG = "MyBuddy/Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        if (savedInstanceState == null) {
            PersonalGestureFragment personalGestureFragment = PersonalGestureFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.main_content, personalGestureFragment).commit();
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
                return true;
            case R.id.action_pair:
                return true;
            case R.id.action_about:
                //TODO: check for ways to recycle aboutactivity if it already exist in backstack
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

}
