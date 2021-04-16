package com.example.projet;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.projet.databinding.FragmentItemListBinding;

import java.util.Objects;

/**
 * A fragment representing a list of Devices.
 */
public class ListFragment extends Fragment {

    private FragmentItemListBinding binding;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickSwapButton();
        }
    };

    public void onClickSwapButton(){
        ((MainActivity) Objects.requireNonNull(getActivity())).displayProfile();
    }

    private final View.OnClickListener onClickListenerAnalyticsButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickAnalyticsButton();
        }
    };

    public void onClickAnalyticsButton(){
        ((MainActivity) Objects.requireNonNull(getActivity())).switchToAnalyticsActivity();
    }

    private final View.OnClickListener onClickListenerSensorsButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickSensorsButton();
        }
    };

    public void onClickSensorsButton(){
        ((MainActivity) Objects.requireNonNull(getActivity())).switchToSensorsActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        View viewLayout = binding.getRoot();

        // Set the adapter
        if (binding.list instanceof RecyclerView) {
            Context context = binding.list.getContext();
            RecyclerView recyclerView = (RecyclerView) binding.list;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(UserSingleton.getInstance().getCurrentUserDevices(), recyclerView));
        }else{
            System.out.println("---------error------");
        }
        // Set the button listener
        binding.profileButton.setOnClickListener(onClickListener);
        binding.appAnalytics.setOnClickListener(onClickListenerAnalyticsButton);
        binding.sensorsButton.setOnClickListener(onClickListenerSensorsButton);
        return viewLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}