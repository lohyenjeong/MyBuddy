package com.lohyenjeong.mybuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lohyenjeong.mybuddy.content.AlertContent;
import com.lohyenjeong.mybuddy.content.AlertContent.AlertItem;
import com.lohyenjeong.mybuddy.content.GestureContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AlertFragment extends Fragment {
    private static final String TAG = "MyBuddy/Alert";

    //Firebase References
    private DatabaseReference mDatabase;
    SharedPreferences prefs;
    private String username;
    public static final List<AlertItem> ALERTS = new ArrayList<AlertItem>();
    private MyAlertRecyclerViewAdapter adapter;
    private LinearLayoutManager mManager;


    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlertFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AlertFragment newInstance(int columnCount) {
        AlertFragment fragment = new AlertFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getActivity().getSharedPreferences(this.getActivity().getString(R.string.user_data), 0);
        username = prefs.getString(this.getActivity().getString(R.string.user_username), "");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("gestures");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            mManager = new LinearLayoutManager(getActivity());
            mManager.setReverseLayout(true);
            mManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(mManager);

            adapter = new MyAlertRecyclerViewAdapter(ALERTS, mListener);

            recyclerView.setAdapter(adapter);
        }


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                for(DataSnapshot alertSnapShot : dataSnapshot.getChildren()){
                    Log.d(TAG, "finding children " + alertSnapShot.getKey());

                    Map<String, Object> dataItem = (Map<String, Object>) alertSnapShot.getValue();
                    String time = (String) dataItem.get("time");
                    Log.d(TAG, "time " + time);
                    String date = (String) dataItem.get("date");
                    Long gestureType = (Long) dataItem.get("type");
                    String user = (String) dataItem.get("user");
                    int type = gestureType.intValue();
                    Log.d(TAG, "test data download " + date + " " + time + " " + type);


                    AlertItem a1 = new AlertItem("1", date, type, time, user);
                    int curSize = adapter.getItemCount();
                    ALERTS.add(a1);
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
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



        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(AlertItem item);
    }
}
