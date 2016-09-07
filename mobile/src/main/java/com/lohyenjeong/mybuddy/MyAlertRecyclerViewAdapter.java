package com.lohyenjeong.mybuddy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lohyenjeong.mybuddy.AlertFragment.OnListFragmentInteractionListener;
import com.lohyenjeong.mybuddy.content.AlertContent.AlertItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AlertItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAlertRecyclerViewAdapter extends RecyclerView.Adapter<MyAlertRecyclerViewAdapter.ViewHolder> {

    private final List<AlertItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyAlertRecyclerViewAdapter(List<AlertItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        int type = mValues.get(position).type;
        if (type == 0) {
            holder.mGestureIcon.setImageResource(R.drawable.ic_face_black_24dp);
            holder.mContentView.setText("Pacing Detected");
        }
        else if(type == 1){
            holder.mGestureIcon.setImageResource(R.drawable.ic_face_black_24dp);
            holder.mContentView.setText("Chair Rocking Detected");
        }
        else if(type == 2){
            holder.mGestureIcon.setImageResource(R.drawable.ic_face_black_24dp);
            holder.mContentView.setText("Hair Pulling Detected");
        }
        else if(type == 3){
            holder.mGestureIcon.setImageResource(R.drawable.ic_face_black_24dp);
            holder.mContentView.setText("Scratching Detected");
        }
        else if(type == 4){
            holder.mGestureIcon.setImageResource(R.drawable.ic_error_black_24dp);
            holder.mContentView.setText("Hitting Detected");
        }
        else if(type == 5){
            holder.mGestureIcon.setImageResource(R.drawable.ic_error_black_24dp);
            holder.mContentView.setText("Punching Detected");
        }
        else if(type == 6){
            holder.mGestureIcon.setImageResource(R.drawable.ic_mood_bad_black_24dp);
            holder.mContentView.setText("Challenging Behaviour Predicted!");
        }

        holder.mDateView.setText(mValues.get(position).date);

        holder.mTimeView.setText(mValues.get(position).time);

        holder.mNameView.setText(mValues.get(position).name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mDateView;
        public final TextView mTimeView;
        public final TextView mNameView;
        public final ImageView mGestureIcon;

        public AlertItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.alert_content);
            mDateView = (TextView) view.findViewById(R.id.alert_date);
            mNameView = (TextView) view.findViewById(R.id.alert_name);
            mTimeView = (TextView) view.findViewById(R.id.alert_time);
            mGestureIcon = (ImageView) view.findViewById(R.id.alert_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDateView.getText() + "'";
        }
    }
}
