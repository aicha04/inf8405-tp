package com.example.tp2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DEVICEINFO = "param1";

    // TODO: Rename and change types of parameters
    private String deviceInfo;

    public DeviceInfoFragment(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceInfoFragment newInstance(String deviceInfo) {
        DeviceInfoFragment fragment = new DeviceInfoFragment(deviceInfo);
        Bundle args = new Bundle();
//        args.putString(ARG_DEVICEINFO, deviceInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            deviceInfo = getArguments().getString(ARG_DEVICEINFO);
//        }
    }
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickBack(v);
        }
    };
    public void onClickBack(final View view) {
        if (view.getContext() instanceof MainActivity) {
            ((MainActivity)view.getContext()).swapToListFragment();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_info, container, false);
        TextView deviceInfoView = (TextView) view.findViewById(R.id.device_info);
        deviceInfoView.setText(this.deviceInfo);
        Button button = (Button) view.findViewById(R.id.button_id);
        button.setOnClickListener(onClickListener);
        return view;
    }
}