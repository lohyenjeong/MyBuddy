package com.lohyenjeong.mybuddy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

/*
Class used for registration
Connected to Google Firebase for authentication purpose as well as database
Saves the user's username, email and mode as part of the datbase
*/

public class SignUpActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    final static String TAG = "MyBuddy/SignUpActivity";

    //Id to identity READ_CONTACTS permission request.
    private static final int REQUEST_READ_CONTACTS = 0;

    //Keep track of the login task to ensure we can cancel it if requested.
    private UserRegistrationTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView inputEmail;
    private EditText inputPassword;
    private ProgressDialog progressDialog;
    private Spinner spinnerMode;

    //User Data
    private int mode;
    private String email;
    private String password;
    private String username;

    //Firebase authentication and database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    boolean registered;
    boolean taskCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Set up Firebase authentication and database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        registered = false;
        taskCompleted = false;

        // Set up the sign up form.
        inputEmail = (AutoCompleteTextView) findViewById(R.id.input_reg_email);
        inputPassword = (EditText) findViewById(R.id.input_reg_password);
        spinnerMode = (Spinner) findViewById(R.id.spinner_reg_mode);
        Button btnSignUp = (Button) findViewById(R.id.btn_reg_sign_up);
        TextView mSignInButton = (TextView) findViewById(R.id.btn_reg_login);

        //Listener for changes in the mode spinner (caregiver or personal use)
        Log.e(TAG, spinnerMode.getPrompt().toString());
        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String modeS = spinnerMode.getSelectedItem().toString();
                if(modeS.equals("Personal User")){
                    mode = 0;
                }
                else if(modeS.equals("Caregiver")){
                    mode = 1;
                }
                else{
                    mode = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Get info passed in from sign in activity if they were already entered
        Bundle bundle = getIntent().getExtras();
        email = bundle.getString(getString(R.string.user_email));
        password = bundle.getString(getString(R.string.user_password));
        if (email != null) {
            inputEmail.setText(email);
        } else {
            populateAutoComplete();
        }
        if (password != null) {
            inputPassword.setText(password);
        }
        if (password != null && email != null) {
            btnSignUp.requestFocus();
        }

        //Listener for sign up button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        //Listener for button to switch over to sign in page
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }


    //Start the authenticator when the activity is started
    @Override
    public void onStart() {
        super.onStart();
        //Brings you to the main page if you are already signed in
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }


    //Stop the authenticator when the activity is stopped
    @Override
    public void onStop() {
        super.onStop();
    }

    //Creating authentication account on firebase
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        taskCompleted = true;

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                            registered = true;
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        int index = email.indexOf('@');
        username = email.substring(0,index);

        // Write new user into database
        writeNewUser(user.getUid(), username, user.getEmail(), mode);

        finish();
    }

    //Writing the user details into the database
    private void writeNewUser(String userId, String name, String email, int mode) {
        User user = new User(name, email, mode);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
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
    private void attemptRegistration() {
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

        //Checks whether the password field is valid
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError(getString(R.string.error_missing_password));
            focusView = inputPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            inputPassword.setError(getString(R.string.error_invalid_password));
            focusView = inputPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.error_missing_email));
            focusView = inputEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            inputEmail.setError(getString(R.string.error_invalid_email));
            focusView = inputEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog = new ProgressDialog(this, R.style.AppThemeDialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Registering");
            progressDialog.show();
            mAuthTask = new UserRegistrationTask(email, password, mode);
            mAuthTask.execute((Void) null);
        }
    }

    //Returns true if email is valid
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    //Returns true if password is valid
    private boolean isPasswordValid(String password) {
        return (password.length() > 5);
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
                new ArrayAdapter<>(SignUpActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        inputEmail.setAdapter(adapter);
    }


    //Class that represents an asynchronous login/registration task used to authenticate the user
    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final int mMode;

        UserRegistrationTask(String email, String password, int mode) {
            mEmail = email;
            mPassword = password;
            mMode = mode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            createAccount(mEmail, mPassword);
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
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (success) {

                if (registered == true) {
                    //Save the email, password and mode preference of the user
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.user_data), 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.user_email), mEmail);
                    editor.putString(getString(R.string.user_password), mPassword);
                    editor.putInt(getString(R.string.user_mode), mMode);
                    editor.putString(getString(R.string.user_username), username);
                    editor.commit();


                    Context context = getApplicationContext();
                    CharSequence text = "Registration successful!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    if(mode == User.MODE_PERSONAL_USE) {
                        startActivity(new Intent(SignUpActivity.this, MonitorActivity.class));
                    }
                    else if(mode == User.MODE_CAREGIVER){
                        startActivity(new Intent(SignUpActivity.this, CaregiverActivity.class));
                    }
                    else{
                        startActivity(new Intent(SignUpActivity.this, MonitorActivity.class));
                    }

                }
                finish();
            } else {
                //TODO: give reason why registration is not successful. E.g. existing user with same username/email
                inputPassword.setError(getString(R.string.error_incorrect_password));
                inputPassword.requestFocus();

            }
        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }
}

