package com.lohyenjeong.mybuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

public class PairingActivity extends AppCompatActivity {

    private final static String TAG = "MyBuddy/Pairing";

    //Firebase References
    private DatabaseReference mDatabase;

    //UI
    private String pairing;
    private Button mPairButton;
    private EditText inputPair;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("pairing");

        if (user != null) {
            userId = user.getUid();
            Log.d(TAG, userId);

        } else {
            // No user is signed in
            Log.d(TAG, " Not Signed in");
        }



        mPairButton = (Button) findViewById(R.id.btn_pair);
        inputPair = (EditText) findViewById(R.id.input_pairing);

        final ListView listView = (ListView) findViewById(R.id.pairing_list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);


        mPairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bundle to carry password and email to registration page
                Log.d(TAG, "in Onclick");
                pairing = inputPair.getText().toString();
                Log.d(TAG, "added pairing " + pairing);
                mDatabase.child(pairing).setValue(true);

            }
        });



        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                adapter.add((String) dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                adapter.add((String) dataSnapshot.child("pairing").getValue());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                adapter.remove((String) dataSnapshot.child("pairing").getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addChildEventListener(childEventListener);



    }

}
