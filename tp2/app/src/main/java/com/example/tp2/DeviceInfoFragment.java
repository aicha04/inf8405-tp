package com.example.tp2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceInfoFragment extends Fragment {
    String removeFavoriteStr = "REMOVE FROM FAVORIS";
    String addToFavorisStr = "ADD TO FAVORIS";
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DEVICEINFO = "param1";

    // TODO: Rename and change types of parameters
    private static int deviceIndex;

    public DeviceInfoFragment(int deviceIndex) {
        this.deviceIndex = deviceIndex;
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
        DeviceInfoFragment fragment = new DeviceInfoFragment(deviceIndex);
        Bundle args = new Bundle();
//      args.putString(ARG_DEVICEINFO, deviceInfo);
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
        deviceInfoView.setText(userSingleton.getDevices().get(deviceIndex).toString());

        Button back_button = (Button) view.findViewById(R.id.back_button);
        back_button.setOnClickListener(onClickListener);


        Button favorite_button = (Button) view.findViewById(R.id.favorite);
        updateFavoriteButton(favorite_button);
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Put real device attribute is favorite
                Device device =  userSingleton.getDevices().get(deviceIndex);
                if (device.isFavorite == 0) {
                    Toast.makeText(getActivity(),"Device added to favoris", Toast.LENGTH_LONG).show();
                    device.addToFavorite();
                } else {
                    Toast.makeText(getActivity(),"Device removed from favoris", Toast.LENGTH_LONG).show();
                   device.removeFromFavorite();
                }
                updateFavoriteButton(favorite_button);
            }
        });
        return view;
    }

    void updateFavoriteButton(Button favorite_button){
        //Put real device attribute is favorite
        Device device =  userSingleton.getDevices().get(deviceIndex);
        if (device.isFavorite == 1) {
            favorite_button.setText(removeFavoriteStr);
        } else {
            favorite_button.setText(addToFavorisStr);
        }
    }
}