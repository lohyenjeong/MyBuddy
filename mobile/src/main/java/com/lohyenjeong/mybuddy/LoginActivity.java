package com.lohyenjeong.mybuddy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lohyenjeong.mybuddy.models.User;


import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */


public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    final static String TAG = "MyBuddy/LoginActivity";

    //Id to identity READ_CONTACTS permission request.
    private static final int REQUEST_READ_CONTACTS = 0;

    //Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView inputEmail;
    private EditText inputPassword;
    private ProgressDialog progressDialog;


    //SharedPreferences references
    private SharedPreferences prefs;
    private String userEmail;
    private String userPassword;
    private String username;

    //Completed password and email fields
    private String password;
    private String email;


    //Firebase references
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private boolean signInB;
    boolean taskCompleted;
    private Firebase mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Firebase Setup
        signInB = false;
        taskCompleted = false;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Set up the login form.
        inputEmail = (AutoCompleteTextView) findViewById(R.id.input_login_email);
        inputPassword = (EditText) findViewById(R.id.input_login_password);
        TextView mSignUpButton = (TextView) findViewById(R.id.btn_login_sign_up);
        Button btnSignIn = (Button) findViewById(R.id.btn_login_sign_in);


        btnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        btnSignIn.requestFocus();

        //Check whether email and password have been previously cached
        prefs = getSharedPreferences(getString(R.string.user_data), 0);
        userEmail = prefs.getString(getString(R.string.user_email), "");
        userPassword = prefs.getString(getString(R.string.user_password), "");
        if(!userPassword.equals("") && !userEmail.equals("")){
            inputEmail.setText(userEmail);
            inputPassword.setText(userPassword);
        }
        else {
            populateAutoComplete();
        }

        //TODO: start populateAutoComplete only when the edittext field for the email address is in focus
        btnSignIn.requestFocus();

        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bundle to carry password and email to registration page
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.user_email), email);
                bundle.putString(getString(R.string.user_password), password);
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }


    //Request permission to access contacts if permission is not granted
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(inputEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    //Callback received when a permissions request has been completed
    //Allows autocomplete if the permission is granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    //Attempts to sign in or register the account specified by the login form.
    //If there are form errors (invalid email, missing fields, etc.), the
    //errors are presented and no actual login attempt is made.
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        inputEmail.setError(null);
        inputPassword.setError(null);

        // Store values at the timestamp of the login attempt.
        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check whether the password fill is filled
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError(getString(R.string.error_missing_password));
            focusView = inputPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.error_field_required));
            focusView = inputEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            inputEmail.setError(getString(R.string.error_invalid_email));
            focusView = inputEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppThemeDialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating");
            progressDialog.show();
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    //Returns true if email is valid
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        inputEmail.setAdapter(adapter);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        final String iEmail = email;


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        taskCompleted = true;

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                            signInB= true;
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void onAuthSuccess(FirebaseUser user) {
        String fEmail = user.getEmail();
        int index = fEmail.indexOf('@');
        username = fEmail.substring(0,index);
        Log.d(TAG, "username is " + username);

        // Go to MainActivity
        startActivity(new Intent(LoginActivity.this, MonitorActivity.class));
        finish();
    }

    //Class that represents an asynchronous login/registration task used to authenticate the user
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            signIn(mEmail, mPassword);
            try{
                while (!taskCompleted) {
                    Thread.sleep(10);
                }
            }catch(InterruptedException e){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                if(signInB == true) {
                    Context context = getApplicationContext();
                    CharSequence text = "Sign in successful!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();


                    String mode="";
                    //Save the correct password and email to sharedprefences
                    if (prefs != null) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.user_email), mEmail);
                        editor.putString(getString(R.string.user_password), mPassword);
                        editor.putString(getString(R.string.user_username), username);
                        mode = prefs.getString("Mode", "");
                        editor.commit();
                    }
                    if(mode.equals("Personal Use")) {
                        startActivity(new Intent(LoginActivity.this, MonitorActivity.class));
                    }else{
                        startActivity(new Intent(LoginActivity.this, CaregiverActivity.class));
                    }
                }
                finish();
            } else {
                if(progressDialog != null){
                    progressDialog.dismiss();
                }
                inputPassword.setError(getString(R.string.error_incorrect_password));
                inputPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

