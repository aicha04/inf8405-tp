package com.example.projet;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.location.LocationManager;
import android.content.IntentFilter;
import android.bluetooth.BluetoothDevice;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MainActivity extends BaseActivity{
    private static final String TAG = "MainActivity";
    private static boolean isAppRunning = false;
    private static final int REQUEST_ENABLE_BT = 3;
    private BluetoothAdapter bluetoothAdapter;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    private IMapController mapController = null;
    private MyLocationNewOverlay mLocationOverlay = null;
    private GpsMyLocationProvider mGpsMyLocationProvider = null;

    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentUserTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();
        createMap();

        Log.d(TAG, "onCreate: " + userSingleton.getCurrentUserDevices().size());

        // Add markers on the map
        for (int i = 0; i < userSingleton.getCurrentUserDevices().size(); i++) {
            Device device = userSingleton.getCurrentUserDevices().get(i);
            String[] latlon = device.position.split(",");
            if (latlon.length == 2) {
                GeoPoint location = new GeoPoint(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));
                addMarker(location, i);
            } else {
                Log.d("latlon array length", String.valueOf(latlon.length));
            }
        }

        swapToListFragment();


    }

    /** Get Bluetooth adapter and register receiver
     * Request permission to use Bluetooth to operating system when necessary
     * @param -
     * @return -
     */
    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            showToast(getApplicationContext(), R.string.device_no_BT);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            registerBroadcastReceiver();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed
        if(requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                showToast(getApplicationContext(), R.string.enable_BT);
            }
        }
    }

    /** Register receiver to be called when any intent matches one of the filters
     * @param -
     * @return -
     */
    private void registerBroadcastReceiver(){
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoverDevicesReceiver, discoverDevicesIntent);
        discoverDevicesIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoverDevicesReceiver, discoverDevicesIntent);

        IntentFilter BTStatusIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(BTStatusReceiver, BTStatusIntent);
    }

    /** If all conditions are met, look for any device with Bluetooth capabilities in the area
     * Start discovery if it isn't already on
     * @param -
     * @return -
     */
    public void discoverDevices() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (bluetoothAdapter.isEnabled() && isGPSEnabled()) {
                Log.d(TAG, "discoverDevices");
                if (!bluetoothAdapter.isDiscovering()) {
                    Log.d(TAG, "start discovering");
                    bluetoothAdapter.startDiscovery();
                }
            } else {
                showToast(getApplicationContext(), R.string.enable_BT_and_GPS);
            }
        }
    }

    /** Set up map and its parameters
     * @param -
     * @return -
     */
    private void createMap() {
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

        updateMapTheme();

        // default zoom buttons
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        // zoom with 2 fingers
        map.setMultiTouchControls(true);

        requestPermissionsIfNecessary(new String[] {
                // need this permission to discover devices and to show the current location
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(constants.DEFAULT_LATITUDE, constants.DEFAULT_LONGITUDE);
        mapController.setCenter(startPoint);
        mapController.animateTo(startPoint);

        mGpsMyLocationProvider = new GpsMyLocationProvider(ctx) {
            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Provider enabled");
                if (provider.equals("gps")) {
                    Log.d(TAG, "GPS provider");
                    discoverDevices();
                }
            }
            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "Provider disabled");
                if (provider.equals("network")) {
                    Log.d(TAG, provider + " provider");
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }

            }
        };

        mLocationOverlay = new MyLocationNewOverlay(mGpsMyLocationProvider, map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                final GeoPoint myLocation = mLocationOverlay.getMyLocation();
                if (myLocation != null) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapController.setCenter(myLocation);
                            mapController.animateTo(myLocation);
                            
                        }
                    });
                };
            }
        });
        map.getOverlays().add(mLocationOverlay);

    }

    /** Retrieve user current location
     * @param -
     * @return User current location
     */
    private GeoPoint getCurrentLocation() {
        if (mLocationOverlay == null || mGpsMyLocationProvider == null) {
            Log.d(TAG, "locationOverlay or GpsMyLocationProvider is null");
            return null;
        }
        return mLocationOverlay.getMyLocation();
    }

    /** Add marker at the given location for device with deviceIndex
     * @param location
     * @param deviceIndex device position in the list
     * @return -
     */
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

    /** Check if GPS is activated
     * @param -
     * @return True if GPS is activated. False otherwise.
     */
    public boolean isGPSEnabled() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    /** switch from current fragment to {@link ListFragment}
     * @param  -
     * @return -
     */
    public void  swapToListFragment(){
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ListFragment(), "FRAGMENT_LIST");
        ft.addToBackStack(null);
        ft.commit();
    }
    /** Refresh {@link ListFragment} to update  device list
     * @param  -
     * @return -
     */
    public void refreshListFragment(){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(currentFragment instanceof ListFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(currentFragment);
            ft.attach(currentFragment);
            ft.commit();
        }
    }
    /** switch from current fragment to {@link DeviceInfoFragment}
     * @param  deviceIndex index of device to display their collected information
     * @return -
     */
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
        isAppRunning = true;
        Log.d(TAG, "onResume");
        //this will refresh the osmdroid configuration on resuming.
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        discoverDevices();
        AppAnalyticsSingleton analyticsInstance = AppAnalyticsSingleton.getInstance();
        analyticsInstance.addBatteryLevel(getApplicationContext());

    }

    @Override
    public void onPause() {
        super.onPause();
        isAppRunning = false;
        //this will refresh the osmdroid configuration on resuming.
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        // False if BT not enabled
        if (bluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "OnPause: stop discovery");
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private final BroadcastReceiver BTStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "Bluetooth OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (isAppRunning) {
                            discoverDevices();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver discoverDevicesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                Log.d(TAG, "discoverDevicesReceiver: ACTION FOUND.");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!deviceExists(device)) {
                        GeoPoint location = getCurrentLocation();

                        if (location != null) {
                            addMarker(location, userSingleton.getCurrentUserDevices().size());
                            String position = location.getLatitude() + "," + location.getLongitude();
                            Device deviceDB = new Device(device.getAddress(),
                                                        position,
                                                        translateClassCode(device.getBluetoothClass().getDeviceClass()),
                                                        translateMajorClassCode(device.getBluetoothClass().getMajorDeviceClass()),
                                                        translateDeviceTypeCode(device.getType()),
                                                        device.getName(),
                                                        0);

                            userSingleton.addNewDeviceToDb(deviceDB);
                            // update list fragment
                            try {

                                refreshListFragment();
                            }
                            catch (Exception e){
                                System.out.println(e.getMessage());
                            }

                            Log.d(TAG, String.valueOf(userSingleton.getCurrentUserDevices().size()));
                            Log.d(TAG, "discoverDevicesReceiver: " + device.getAddress() + ": " +
                                    translateClassCode(device.getBluetoothClass().getDeviceClass()) + ": " +
                                    translateMajorClassCode(device.getBluetoothClass().getMajorDeviceClass()) + ": " +
                                    translateDeviceTypeCode(device.getType()) + ": " + device.getName());
                        }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG,"Entered the Finished ");
                if (isAppRunning) {
                    Log.d(TAG,"Activity is running ");
                    discoverDevices();
                }
            } else {
                Log.d(TAG, "discoverDevicesReceiver: Didn't find any device!");
            }
        }
    };

    /** Check if device already exists
     * @param device
     * @return true if device already exists, false otherwise
     */
    private boolean deviceExists(BluetoothDevice device){
        Log.d(TAG, "check if device exists");
        ArrayList<Device> devices = userSingleton.getCurrentUserDevices();
        boolean deviceExists = false;
        for(int i = 0; i < devices.size(); i++){
            if(devices.get(i).id.equals(device.getAddress())){
                Log.d(TAG, "device exists for " + device.getAddress());
                deviceExists = true;
            }
        }
        return deviceExists;
    }

    /** Translate the integer code returned by Bluetooth adapter into a readable device type
     *  If code does not map to any known device type, it is returned as is
     * @param - int code
     * @return - String deviceTypeName
     */
    private String translateDeviceTypeCode(int code) {
        String deviceTypeName = "";
        switch (code) {
            case 0 :
                deviceTypeName = "DEVICE_TYPE_UNKNOWN";
                break;
            case 1 :
                deviceTypeName = "DEVICE_TYPE_CLASSIC";
                break;
            case 2 :
                deviceTypeName = "DEVICE_TYPE_LE";
                break;
            case 3 :
                deviceTypeName = "DEVICE_TYPE_DUAL";
                break;
            default:
                deviceTypeName = String.valueOf(code);
        }
        return deviceTypeName;
    }

    /** Translate the integer code returned by Bluetooth adapter into a readable major class
     *  If code does not map to any known major class, it is returned as is
     * @param - int code
     * @return - String majorClassName
     */
    private String translateMajorClassCode(int code) {
        String majorClassName = "";
        switch (code) {
            case 1024 :
                majorClassName = "AUDIO_VIDEO";
                break;
            case 256 :
                majorClassName = "COMPUTER";
                break;
            case 2304 :
                majorClassName = "HEALTH";
                break;
            case 1536 :
                majorClassName = "IMAGING";
                break;
            case 0 :
                majorClassName = "MISC";
                break;
            case 768 :
                majorClassName = "NETWORKING";
                break;
            case 1280 :
                majorClassName = "PERIPHERAL";
                break;
            case 512 :
                majorClassName = "PHONE";
                break;
            case 2048 :
                majorClassName = "TOY";
                break;
            case 7936 :
                majorClassName = "UNCATEGORIZED";
                break;
            case 1792 :
                majorClassName = "WEARABLE";
                break;
            default:
                majorClassName = String.valueOf(code);
        }
        return majorClassName;
    }

    /** Will translate the integer code returned by Bluetooth adapter into a readable minor class
     *  If code does not map to any known minor class, it is returned as is
     * @param - int code
     * @return - String className
     */
    private String translateClassCode(int code) {
        String className = "";
        switch (code) {
            case 1076 :
                className = "AUDIO_VIDEO_CAMCORDER";
                break;
            case 1056 :
                className = "AUDIO_VIDEO_CAR_AUDIO";
                break;
            case 1032 :
                className = "AUDIO_VIDEO_HANDSFREE";
                break;
            case 1048 :
                className = "AUDIO_VIDEO_HEADPHONES";
                break;
            case 1064 :
                className = "AUDIO_VIDEO_HIFI_AUDIO";
                break;
            case 1044 :
                className = "AUDIO_VIDEO_LOUDSPEAKER";
                break;
            case 1040 :
                className = "AUDIO_VIDEO_MICROPHONE";
                break;
            case 1052 :
                className = "AUDIO_VIDEO_PORTABLE_AUDIO";
                break;
            case 1060 :
                className = "AUDIO_VIDEO_SET_TOP_BOX";
                break;
            case 1024 :
                className = "AUDIO_VIDEO_UNCATEGORIZED";
                break;
            case 1068 :
                className = "AUDIO_VIDEO_VCR";
                break;
            case 1072 :
                className = "AUDIO_VIDEO_VIDEO_CAMERA";
                break;
            case 1088 :
                className = "AUDIO_VIDEO_VIDEO_CONFERENCING";
                break;
            case 1084 :
                className = "AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER";
                break;
            case 1096 :
                className = "AUDIO_VIDEO_VIDEO_GAMING_TOY";
                break;
            case 1080 :
                className = "AUDIO_VIDEO_VIDEO_MONITOR";
                break;
            case 1028 :
                className = "AUDIO_VIDEO_WEARABLE_HEADSET";
                break;
            case 260 :
                className = "COMPUTER_DESKTOP";
                break;
            case 272 :
                className = "COMPUTER_HANDHELD_PC_PDA";
                break;
            case 268 :
                className = "COMPUTER_LAPTOP";
                break;
            case 276 :
                className = "COMPUTER_PALM_SIZE_PC_PDA";
                break;
            case 264 :
                className = "COMPUTER_SERVER";
                break;
            case 256 :
                className = "COMPUTER_UNCATEGORIZED";
                break;
            case 280 :
                className = "COMPUTER_WEARABLE";
                break;
            case 2308 :
                className = "HEALTH_BLOOD_PRESSURE";
                break;
            case 2332 :
                className = "HEALTH_DATA_DISPLAY";
                break;
            case 2320 :
                className = "HEALTH_GLUCOSE";
                break;
            case 2324 :
                className = "HEALTH_PULSE_OXIMETER";
                break;
            case 2328 :
                className = "HEALTH_PULSE_RATE";
                break;
            case 2312 :
                className = "HEALTH_THERMOMETER";
                break;
            case 2304 :
                className = "HEALTH_UNCATEGORIZED";
                break;
            case 2316 :
                className = "HEALTH_WEIGHING";
                break;
            case 516 :
                className = "PHONE_CELLULAR";
                break;
            case 520 :
                className = "PHONE_CORDLESS";
                break;
            case 523 :
                className = "PHONE_ISDN";
                break;
            case 528 :
                className = "PHONE_MODEM_OR_GATEWAY";
                break;
            case 524 :
                className = "PHONE_SMART";
                break;
            case 512 :
                className = "PHONE_UNCATEGORIZED";
                break;
            case 2064 :
                className = "TOY_CONTROLLER";
                break;
            case 2060 :
                className = "TOY_DOLL_ACTION_FIGURE";
                break;
            case 2068 :
                className = "TOY_GAME";
                break;
            case 2052 :
                className = "TOY_ROBOT";
                break;
            case 2048 :
                className = "TOY_UNCATEGORIZED";
                break;
            case 1812 :
                className = "WEARABLE_GLASSES";
                break;
            case 1808 :
                className = "WEARABLE_HELMET";
                break;
            case 1804 :
                className = "WEARABLE_JACKET";
                break;
            case 1800 :
                className = "WEARABLE_PAGER";
                break;
            case 1792 :
                className = "WEARABLE_UNCATEGORIZED";
                break;
            case 1796 :
                className = "WEARABLE_WRIST_WATCH";
                break;
            case 7936 :
                className = "UNCATEGORIZED";
                break;
            default:
                className = String.valueOf(code);
        }
        return className;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE){
            ArrayList<String> permissionsToRequest = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionsToRequest.add(permissions[i]);
                }
            }
            if (permissionsToRequest.size() > 0) {
                showToast(getApplicationContext(), R.string.permissions_required);
            }
        }
    }

    /** Request permissions when necessary
     * @param permissions
     * @return -
     */
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("These permissions are important for optimal use of the application. Please permit them!")
                        .setTitle("Important permissions required!");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
                    }
                });

                dialog.setNegativeButton("NO THANKS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast(getApplicationContext(), R.string.permissions_denied);
                    }
                });

                dialog.show();
            } else {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        }
    }
    /** Update the map theme
     * @param -
     * @return -
     */
    private void updateMapTheme(){
        if(userSingleton.getCurrentUserTheme().equals(constants.DARK_THEME)){
            //Matrix to inverse colors
            ColorMatrix negate = new ColorMatrix(constants.NEGATE_COLORS_MATRIX);

            //Matrix for black and white filter
            ColorMatrix blackAndWhiteTintMatrix = new ColorMatrix(constants.BLACK_AND_WHITE_COLORS_MATRIX);

            //Apply black and white filter over negate inverted colors
            negate.preConcat(blackAndWhiteTintMatrix);

            //Apply filter to map
            map.getOverlayManager().getTilesOverlay().setColorFilter( new ColorMatrixColorFilter(negate));
        }else{
            //Set default colors
            map.getOverlayManager().getTilesOverlay().setColorFilter(null);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called.");
        super.onDestroy();
        unregisterReceiver(BTStatusReceiver);
        unregisterReceiver(discoverDevicesReceiver);
    }

    void displayProfile() {
        Intent profileActivity = new Intent(MainActivity.this, Profile.class);
        startActivity(profileActivity);
        finish();
    }
    /** Go to analytics activity
     * @param -
     * @return -
     */
    public void switchToAnalyticsActivity() {

        Intent i = new Intent(MainActivity.this, AppAnalyticsActivity.class);
        startActivity(i);
        finish();
    }
    /** Go to sensors activity
     * @param -
     * @return -
     */
    public void switchToSensorsActivity() {
        Intent i = new Intent(MainActivity.this, SensorsActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(AppAnalyticsSingleton.getInstance().getTimeStamps().size() % 2 == 1){
            AppAnalyticsSingleton analyticsInstance = AppAnalyticsSingleton.getInstance();
            analyticsInstance.addBatteryLevel(getApplicationContext());
        }

    }


}
