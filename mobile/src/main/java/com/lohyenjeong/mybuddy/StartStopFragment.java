package com.lohyenjeong.mybuddy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lohyenjeong.mybuddy.communication.MessageSender;


public class StartStopFragment extends Fragment {
    private static final String TAG = "MyBuddy/PersonalGesture";

    //UI Elements
    public Button btnStart;
    public Button btnStop;

    private MessageSender messageSender;


    public StartStopFragment() {
    }

    public static StartStopFragment newInstance() {
        StartStopFragment fragment = new StartStopFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        messageSender = MessageSender.getMessageSender(getActivity());


    }


    @Override
    public void onPause(){
        super.onPause();
        messageSender.stopSensors();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.start_stop, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_caregiver) {
            //TODO: new intent to launch the monitoring activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_start_stop, container, false);
        btnStart = (Button) rootView.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageSender.startSensors();
                Log.d(TAG, "Sensor Service Started");
                messageSender.startSensors();
            }
        });


        btnStop = (Button) rootView.findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageSender.stopSensors();
                Log.d(TAG, "Sensor Service Stopped");
            }
        });

        return rootView;
    }

}
