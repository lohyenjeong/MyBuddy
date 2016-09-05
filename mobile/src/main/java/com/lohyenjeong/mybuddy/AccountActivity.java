package com.lohyenjeong.mybuddy;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    private Button btnChangeMode;
    private Button btnSaveMode;
    private TextView textUserEmail;
    private TextView textUserMode;
    private Spinner spinnerUserMode;
    private View borderMode;

    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        //Get references to UI elements
        btnChangeMode = (Button) findViewById(R.id.btn_change_mode);
        btnSaveMode = (Button) findViewById(R.id.btn_save_mode);
        textUserEmail = (TextView) findViewById(R.id.text_email_displayed);
        textUserMode = (TextView) findViewById(R.id.text_mode_displayed);
        spinnerUserMode = (Spinner) findViewById(R.id.spinner_mode);
        borderMode = (View) findViewById(R.id.border_mode);


        //Set account details based on saved Shared Preferences
        prefs = getSharedPreferences(getString(R.string.user_data), 0);
        String userEmail = prefs.getString(getString(R.string.user_email), "");
        textUserEmail.setText(userEmail);
        String userMode = prefs.getString(getString(R.string.user_mode), "");
        textUserMode.setText(userMode);


        //Set Spinner and save button as invisible
        spinnerUserMode.setVisibility(View.GONE);
        btnSaveMode.setVisibility(View.GONE);




        //When clicked, options to change mode appears
        btnChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnChangeMode.setVisibility(View.GONE);
                textUserMode.setVisibility(View.GONE);
                borderMode.setVisibility(View.GONE);
                spinnerUserMode.setVisibility(View.VISIBLE);
                btnSaveMode.setVisibility(View.VISIBLE);
                btnSaveMode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveChangedMode();
                    }
                });
            }
        });

    }

    private void saveChangedMode(){
        String newMode = spinnerUserMode.getSelectedItem().toString();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.user_mode), newMode);

        btnSaveMode.setVisibility(View.GONE);
        spinnerUserMode.setVisibility(View.GONE);

        String userMode = prefs.getString(getString(R.string.user_mode), "");
        textUserMode.setText(newMode);

        btnChangeMode.setVisibility(View.VISIBLE);
        textUserMode.setVisibility(View.VISIBLE);
        borderMode.setVisibility(View.VISIBLE);

        Toast.makeText(AccountActivity.this, "User mode has been changed to: " + newMode, Toast.LENGTH_SHORT).show();
    }



}
