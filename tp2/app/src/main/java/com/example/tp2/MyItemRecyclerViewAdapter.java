package com.example.tp2;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Device}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Device> mValues;
    private final RecyclerView recyclerView;

    private  UserSingleton userSingleton = UserSingleton.getInstance();

    public MyItemRecyclerViewAdapter(ArrayList<Device> items, RecyclerView recyclerView) {
        mValues = userSingleton.getDevices();
        this.recyclerView = recyclerView;
    }
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickItem(v);
        }
    };
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    public void onClickItem(final View view) {
        int itemPosition = recyclerView.getChildLayoutPosition(view);

        if (view.getContext() instanceof MainActivity) {
            ((MainActivity)view.getContext()).swapToDeviceInfoFragment(itemPosition);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mValues = userSingleton.getDevices();
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).classCategory);

        if(mValues.get(position).isFavorite == 1){
            holder.mImageView.setImageResource(R.drawable.star);
        }else{
            holder.mImageView.setImageResource(R.drawable.emptystar);
        }
    }

    @Override
    public int getItemCount() {
        return userSingleton.getDevices().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView mImageView;
        public Device mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImageView = (ImageView) view.findViewById(R.id.imageView);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}