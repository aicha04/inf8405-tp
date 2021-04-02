package com.example.tp2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;

import java.io.File;
import java.util.ArrayList;

import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private Button swapButton = null;

    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_Tp2);
        }else{
            setTheme(R.style.Theme_Tp2_dark);
        }

        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);

        swapButton = (Button) findViewById(R.id.swapeTheme);
        setSwapButtonListeners();

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setBuiltInZoomControls(true);// activation du zoom
        updateMapTheme();

        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(45.508888, -73.561668);
        mapController.setCenter(startPoint);

        requestPermissionsIfNecessary(new String[] {
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

  userSingleton.addNewDeviceToDb(new Device("31", "45.507888, -73.560668", "w", 1));
System.out.println(userSingleton.getDevices().size());
        swapToListFragment();

        for (int i=0; i < userSingleton.getDevices().size(); i++) {
            Device device = userSingleton.getDevices().get(i);
            String[] latlon = device.position.split(",");
            if (latlon.length == 2) {
                GeoPoint location = new GeoPoint(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));
                addMarker(location, i);
            } else {
                Log.d("latlon array length", String.valueOf(latlon.length));
            }
        }
    }

    private void addMarker(GeoPoint location, int deviceIndex) {
        Marker newMarker = new Marker(map);
        newMarker.setPosition(location);
        newMarker.setTitle(String.valueOf(deviceIndex));
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        newMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                swapToDeviceInfoFragment(deviceIndex);
                return true;
            }
        });
        map.getOverlays().add(newMarker);
    }

    public void  swapToListFragment(){
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ItemFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
    public void swapToDeviceInfoFragment(int deviceIndex){
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new DeviceInfoFragment(deviceIndex));
        ft.addToBackStack(null);
        ft.commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void updateMapTheme(){
        if(userSingleton.getCurrentTheme().equals(constants.DARK_THEME)){
            //Source : https://github.com/osmdroid/osmdroid/blob/master/osmdroid-android/src/main/java/org/osmdroid/views/overlay/TilesOverlay.java
            //Matrix to inverse colors
            ColorMatrix negate = new ColorMatrix(new float[] {
                    -1.0f, 0.0f, 0.0f, 0.0f, 255f, //red
                    0.0f, -1.0f, 0.0f, 0.0f, 255f, //green
                    0.0f, 0.0f, -1.0f, 0.0f, 255f, //blue
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f //alpha
            });

            //Source: https://kazzkiq.github.io/svg-color-filter/
            //Matrix for black and white filter
            ColorMatrix blackAndWhiteTintMatrix = new ColorMatrix(new float[] {
                    0, 1, 0, 0, 0, //red
                    0, 1, 0, 0, 0, //green
                    0, 1, 0, 0, 0, //blue
                    0, 1, 0, 1, 0, //alpha
            });

            //Apply black and white filter over negate inverted colors
            blackAndWhiteTintMatrix.preConcat(negate);

            //Apply filter to map
            map.getOverlayManager().getTilesOverlay().setColorFilter( new ColorMatrixColorFilter(blackAndWhiteTintMatrix));
        }else{
            //Set default colors
            map.getOverlayManager().getTilesOverlay().setColorFilter(null);
        }
    }

    private void setSwapButtonListeners(){
        swapButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(userSingleton.getCurrentTheme().equals(constants.DARK_THEME)){
                userSingleton.setCurrentTheme(constants.LIGHT_THEME);
                editor.putString(constants.CURRENT_THEME, constants.LIGHT_THEME);
            }else {
                userSingleton.setCurrentTheme(constants.DARK_THEME);
                editor.putString(constants.CURRENT_THEME, constants.DARK_THEME);
            }
            editor.commit();
            finish();
            startActivity(getIntent());
        });

    }
}
