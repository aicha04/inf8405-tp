package com.example.tp2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private IMapController mapController = null;
    private MyLocationNewOverlay mLocationOverlay = null;

    private Button locationButton;

    private SharedPreferences sharedPreferences;
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        // default zoom buttons
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        // zoom with 2 fingers
        map.setMultiTouchControls(true);

        requestPermissionsIfNecessary(new String[] {
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        map.getOverlays().add(mLocationOverlay);

        mapController = map.getController();
        if(isGPSEnabled()) {
            mapController.setZoom(15.0);
        } else {
            mapController.setZoom(10.0);
            GeoPoint startPoint = new GeoPoint(constants.DEFAULT_LATITUDE, constants.DEFAULT_LONGITUDE);
            mapController.setCenter(startPoint);
        }

        locationButton = findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGPSEnabled) {
                    double latitude = mLocationOverlay.getMyLocationProvider().getLastKnownLocation().getLatitude();
                    double longitude = mLocationOverlay.getMyLocationProvider().getLastKnownLocation().getLongitude();

                    GeoPoint startPoint = new GeoPoint(latitude, longitude);
                    Marker startMarker = new Marker(map);
                    startMarker.setPosition(startPoint);
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(startMarker);
                    Toast.makeText(getApplicationContext(), Double.toString(latitude), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_LONG).show();
                }

            }
        });

        showMultipleIcons();

        //Get user id
        setUpSharedPreferences();
        fetchUserDevices();
        //userSingleton.addDevice(new Device("ID14", "TOBBB", "TABBBB"));
    }
    /** Retrieve user current location (latitude, longitude)
     * @param -
     * @return User current location
     */
    private ArrayList<Double> getCurrentLocation() {
        ArrayList<Double> position = new ArrayList<Double>();
        position.add(mLocationOverlay.getMyLocationProvider().getLastKnownLocation().getLatitude());
        position.add(mLocationOverlay.getMyLocationProvider().getLastKnownLocation().getLongitude());
        return position;
    }

    /** Check if GPS is activated
     * @param -
     * @return True if GPS is activated. False otherwise.
     */
    private boolean isGPSEnabled() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showMultipleIcons() {
        //your items
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Montreal", "Montreal", new GeoPoint(constants.DEFAULT_LATITUDE, constants.DEFAULT_LONGITUDE)));
        items.add(new OverlayItem("Laval", "Laval", new GeoPoint(45.612499, -73.707092)));

        //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        Toast.makeText(getApplicationContext(), "TAP", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, getApplicationContext());
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);
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

    /** Retrieve user id through shared preferences and
     *  update the userInfoSingleton singleton id with the value
     *  Create a shared preferences database for this user for the first use of the application
     * @param -
     * @return -
     */
    void setUpSharedPreferences(){
        File file = new File(constants.SHARED_PREFERENCES_PATH);
        if(file.exists()){
            sharedPreferences = getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MainActivity.this.MODE_PRIVATE);
            if(sharedPreferences.contains(constants.SHARED_USER_ID)){
                 userSingleton.setUserUId(sharedPreferences.getString(constants.SHARED_USER_ID, ""));
            }
        }else{
            sharedPreferences = getApplicationContext().getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String userUId =  UUID.randomUUID().toString();
            userSingleton.setUserUId(userUId);
            editor.putString(constants.SHARED_USER_ID, userUId);
            editor.commit();
        }
    }


    /** Retrieve user saved devices from firebase database
     * @param -
     * @return -
     */
    void fetchUserDevices(){
        userSingleton.getDatabaseRef().child(userSingleton.getUserUId()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                System.out.println( task.getException());
            }
            else {
                userSingleton.resetUserDevicesLocally();
                DataSnapshot snapshot = task.getResult();
                for(DataSnapshot shot:  snapshot.getChildren()) {
                    for (DataSnapshot val : shot.getChildren()) {
                        Device device = val.getValue(Device.class);
                        System.out.println("UPDATE" + device.id);
                        userSingleton.addDevice(device);
                    }
                }
            }
        });
    }

}
