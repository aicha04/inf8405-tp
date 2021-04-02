package com.example.tp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceInfoFragment extends Fragment {
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DEVICEINFO = "param1";

    // TODO: Rename and change types of parameters
    private static int deviceIndex;

    public DeviceInfoFragment(int deviceIndex) {
        this.deviceIndex = deviceIndex;
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
        String infoContent = setInfoContent();
        deviceInfoView.setText(infoContent);
        ImageButton back_button = (ImageButton) view.findViewById(R.id.back_button);
        back_button.setOnClickListener(onClickListener);
        Button favorite_button = (Button) view.findViewById(R.id.favorite);
        updateFavoriteButton(favorite_button);
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device device =  userSingleton.getDevices().get(deviceIndex);
                if (device.isFavorite == 0) {
                    Toast.makeText(getActivity(),"Device added to favorites", Toast.LENGTH_LONG).show();
                    userSingleton.addToFavorites(deviceIndex);
                } else {
                    Toast.makeText(getActivity(),"Device removed from favorites", Toast.LENGTH_LONG).show();
                    userSingleton.removeFromFavorites(deviceIndex);
                }
                updateFavoriteButton(favorite_button);
            }
        });
        ImageButton directions_button = (ImageButton) view.findViewById(R.id.directions);
        directions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] latlon = userSingleton.getDevices().get(deviceIndex).position.split(",");
                String latitude = latlon[0];
                String longitude = latlon[1];
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });
        return view;
    }
    private String setInfoContent(){
        Device device = userSingleton.getDevices().get(deviceIndex);
        String mac = "Mac: " + device.id;
        String friendlyName  = "Friendly Name: " + device.friendlyName;
        String majorCategory = "Major Category: " + device.classMajorCategory;
        String category = "Category: " + device.classCategory;
        String position  = "position: " + device.position;
        return mac + "\n" + friendlyName + "\n" + majorCategory + "\n" + category + "\n" + position;

    }
    void updateFavoriteButton(Button favorite_button){
        String removeFavoriteStr = "REMOVE FROM FAVORITES";
        String addToFavorisStr = "ADD TO FAVORITES";
        Device device =  userSingleton.getDevices().get(deviceIndex);
        if (device.isFavorite == 1) {
            favorite_button.setText(removeFavoriteStr);
        } else {
            favorite_button.setText(addToFavorisStr);
        }
    }
}