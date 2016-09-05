package com.lohyenjeong.mybuddy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lohyenjeong.mybuddy.GestureListFragment.OnListFragmentInteractionListener;
import com.lohyenjeong.mybuddy.dummy.GestureContent.GestureItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link GestureItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGestureRecyclerViewAdapter extends RecyclerView.Adapter<MyGestureRecyclerViewAdapter.ViewHolder> {

    private final List<GestureItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyGestureRecyclerViewAdapter(List<GestureItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gesture, parent, false);
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
        public final TextView mDateView;
        public final TextView mTimeView;
        public final TextView mContentView;
        public GestureItem mItem;
        public final ImageView mGestureIcon;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDateView = (TextView) view.findViewById(R.id.gesture_date);
            mTimeView = (TextView) view.findViewById(R.id.gesture_time);
            mContentView = (TextView) view.findViewById(R.id.content);
            mGestureIcon = (ImageView) view.findViewById(R.id.gesture_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
