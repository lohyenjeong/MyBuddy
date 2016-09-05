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

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

//TODO: extract the firebase activities into seperate class. Only if free
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
    private String mode;
    private String email;
    private String password;

    //Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    boolean registered;
    boolean taskCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //Set up Firebase authentication database
        mAuth = FirebaseAuth.getInstance();
        registered = false;
        taskCompleted = false;
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // Set up the sign up form.
        inputEmail = (AutoCompleteTextView) findViewById(R.id.input_reg_email);
        inputPassword = (EditText) findViewById(R.id.input_reg_password);
        spinnerMode = (Spinner) findViewById(R.id.spinner_reg_mode);
        Button btnSignUp = (Button) findViewById(R.id.btn_reg_sign_up);
        TextView mSignInButton = (TextView) findViewById(R.id.btn_reg_login);

        //TODO: create own text above spinner as prompt
        Log.e(TAG, spinnerMode.getPrompt().toString());
        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mode = spinnerMode.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Get info passed in from last activity
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


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });


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
        mAuth.addAuthStateListener(mAuthListener);
    }


    //Stop the authenticator when the activity is stopped
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                taskCompleted = true;

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                } else {
                    registered = true;
                    Log.e(TAG, "not successful");
                }
            }
        });
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

        // Store values at the time of the login attempt.
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
        private final String mMode;

        UserRegistrationTask(String email, String password, String mode) {
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
                    editor.putString(getString(R.string.user_mode), mMode);
                    editor.commit();


                    Context context = getApplicationContext();
                    CharSequence text = "Registration successful!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();


                    startActivity(new Intent(getApplicationContext(), CaregiverActivity.class));
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

