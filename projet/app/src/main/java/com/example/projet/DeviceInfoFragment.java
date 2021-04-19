package com.example.projet;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet.databinding.FragmentDeviceInfoBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceInfoFragment extends Fragment {
    private final UserSingleton userSingleton = UserSingleton.getInstance();

    private static int deviceIndex;
    private String language;
    private FragmentDeviceInfoBinding binding;

    public DeviceInfoFragment(int deviceIndex) {
        DeviceInfoFragment.deviceIndex = deviceIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        language = App.localeManager.getLanguage();
        // Inflate the layout for this fragment
        binding = FragmentDeviceInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        TextView deviceInfoView = (TextView) view.findViewById(R.id.device_info);
        String infoContent = setInfoContent();
        deviceInfoView.setText(infoContent);
        // Set listeners for buttons
        ImageButton back_button = (ImageButton) view.findViewById(R.id.back_button);
        back_button.setOnClickListener(onClickListener);

        updateFavoriteButton();
        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device device =  userSingleton.getCurrentUserDevices().get(deviceIndex);
                if (device.isFavorite == 0) {
                    ((MainActivity)getActivity()).showToast(((MainActivity)getActivity()).getApplicationContext(), R.string.device_added_favorite);
                    userSingleton.addToFavorites(deviceIndex);
                } else {
                    ((MainActivity)getActivity()).showToast(((MainActivity)getActivity()).getApplicationContext(), R.string.device_removed_favorite);
                    userSingleton.removeFromFavorites(deviceIndex);
                }
                updateFavoriteButton();
            }
        });

        ImageButton directions_button = (ImageButton) view.findViewById(R.id.directions);
        directions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((MainActivity)getActivity()).isGPSEnabled()) {
                    String[] latlon = userSingleton.getCurrentUserDevices().get(deviceIndex).position.split(",");
                    String latitude = latlon[0];
                    String longitude = latlon[1];
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else {
                    ((MainActivity)getActivity()).showToast(((MainActivity)getActivity()).getApplicationContext(), R.string.enable_GPS);;
                }

            }
        });

        ImageButton share_button = (ImageButton) view.findViewById(R.id.share);
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = setInfoContent();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Device \n" + message);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!language.equals(App.localeManager.getLanguage())) {
            Log.d("DeviceInfoFragment", "Language changed");
            updateFavoriteButton();

            language = App.localeManager.getLanguage();
        }
        Log.d("DeviceInfoFragment", "onResume");
    }

    /**Returns content to put in {@link DeviceInfoFragment}
     * @param  -
     * @return {@link String} content to display
     */
    private String setInfoContent(){
        Device device = userSingleton.getCurrentUserDevices().get(deviceIndex);
        String mac = "Mac: " + device.id;
        String friendlyName  = getString(R.string.friendly_name) + ": " + getString(R.string.deviceNameError);
        if(device.friendlyName != null){
            friendlyName  = getString(R.string.friendly_name) + ": " + device.friendlyName;
        }

        String majorCategory = getString(R.string.major_category) + ": " + device.classMajorCategory;
        String category = getString(R.string.category) + ": " + device.classCategory;
        String type = "Type: " + device.deviceType;
        String position  = "Position: " + device.position;
        return mac + "\n" + friendlyName + "\n" + majorCategory + "\n" + category + "\n" + type + "\n" + position;

    }
    /**Update favorites button text
     * @param  -
     * @return -
     */
    void updateFavoriteButton(){
        Context context = App.localeManager.setLocale(((MainActivity)getActivity()).getApplicationContext());
        Resources resources = context.getResources();
        Device device =  userSingleton.getCurrentUserDevices().get(deviceIndex);
        if (device.isFavorite == 1) {
            binding.favorite.setText(resources.getString(R.string.remove_favorite));
        } else {
            binding.favorite.setText(resources.getString(R.string.add_favorite));
        }
    }
}