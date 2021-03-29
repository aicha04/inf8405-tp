package com.example.tp2;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

// import com.example.tp2.Devices.DeviceContent.DeviceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DeviceItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Device> mValues;
    private final RecyclerView recyclerView;

    public MyItemRecyclerViewAdapter(ArrayList<Device> items, RecyclerView recyclerView) {
        mValues = items;
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
        String itemInfo = mValues.get(itemPosition).id + "\n" + mValues.get(itemPosition).classCategory;
        if (view.getContext() instanceof MainActivity) {
            ((MainActivity)view.getContext()).swapToDeviceInfoFragment(itemInfo);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).classCategory);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Device mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}