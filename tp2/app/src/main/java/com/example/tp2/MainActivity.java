package com.example.tp2;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import android.content.IntentFilter;
import android.bluetooth.BluetoothDevice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.ArrayList;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    private BluetoothAdapter bluetoothAdapter;
    Button discoverNewDevices;
    public ArrayList<BluetoothDevice> pairedDevices;
    public DeviceList deviceListAdapter;
    // ListView pairedDevicesView;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    private SharedPreferences sharedPreferences;
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        discoverNewDevices = (Button) findViewById(R.id.discoverNewDevices);
        // pairedDevicesView = (ListView) findViewById(R.id.pairedDevices);
        pairedDevices = new ArrayList<>();
        // pairedDevicesView.setOnItemClickListener(MainActivity.this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        discoverNewDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBluetooth(view);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                discoverNewDevices(view);
            }
        });


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

        //map.setBuiltInZoomControls(true);// activation du zoom

        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(45.508888, -73.561668);
        mapController.setCenter(startPoint);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

        requestPermissionsIfNecessary(new String[] {
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });


        //Get user id
        setUpSharedPreferences();
        fetchUserDevices();
        //userSingleton.addDevice(new Device("ID14", "TOBBB", "TABBBB"));
        createListFragment();

    }
    private void  createListFragment(){
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        ft.add(R.id.fragment_container, new ItemFragment());
// or ft.replace(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
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

    public void enableBluetooth(View view) {
        if (bluetoothAdapter == null) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(context, "Bluetooth is not enabled!", duration).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
                Log.d(TAG, "discoverDevices: Enabling Bluetooth.");
                IntentFilter enableBTFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(enableBluetoothReceiver, enableBTFilter);

                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                Log.d(TAG, "discoverDevices: Making device discoverable for 300 seconds.");
                IntentFilter makeDiscoverableFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(makeDiscoverableReceiver, makeDiscoverableFilter);
            } else {
                bluetoothAdapter.disable();
                Log.d(TAG, "discoverDevices: Disabling Bluetooth.");
                IntentFilter enableBTFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);// TODO: Ces deux lignes sont necessaires?
                registerReceiver(enableBluetoothReceiver, enableBTFilter);
            }
        }
    }

    public void discoverNewDevices(View view) {
        if (!bluetoothAdapter.isDiscovering()) {
            requestBTPermissions();
            bluetoothAdapter.startDiscovery();
            Log.d(TAG, "discoverDevices: Looking for unpaired devices.");
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);// TODO: Pourquoi il ne trouve pas de trucs?
            registerReceiver(discoverDevicesReceiver, discoverDevicesIntent);
        } else {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "discoverDevices: Canceling discovery.");
            requestBTPermissions();
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(discoverDevicesReceiver, discoverDevicesIntent);
        }
    }

    private final BroadcastReceiver enableBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "bluetoothAdapter: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "bluetoothAdapter: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "bluetoothAdapter: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver makeDiscoverableReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "bluetoothAdapter: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "bluetoothAdapter: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "bluetoothAdapter: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "bluetoothAdapter: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "bluetoothAdapter: Connected.");
                        break;
                }
            }
        }
    };
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
                className = "UNCATEGORIZED"; // TODO: Ce code existe uniquement en MajorClass
                break;
            default:
                className = String.valueOf(code);
        }
        return className;
    }

    private final BroadcastReceiver discoverDevicesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!pairedDevices.contains(device)) {
                    Device deviceDB = new Device(device.getAddress(), "blabla", translateClassCode(device.getBluetoothClass().getDeviceClass())); // Position à changer avec la fonction de Maude
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!" + deviceDB.id);
                    userSingleton.addDevice(deviceDB); // TODO: Où est ce qu'on vérifie que l'objet n'est pas déjà dans la BD?
                    pairedDevices.add(device);
                    Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() + ": " + translateDeviceTypeCode(device.getType()) + ": " + translateMajorClassCode(device.getBluetoothClass().getMajorDeviceClass()) + ": " + translateClassCode(device.getBluetoothClass().getDeviceClass())); // TODO: Ajouter MajorClass DeviceType à DeviceDB
                    // deviceListAdapter = new DeviceList(context, R.layout.device_list_view, pairedDevices); // TODO: Est ce que R.layout.device_list_view = deviceUuid?
                    // pairedDevicesView.setAdapter(deviceListAdapter);
                }
            } else {
                Log.d(TAG, "onReceive: Didn't find a device!");
            }
        }
    };

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

    private void requestBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "checkBTPermissions: Checking permissions. SDK version >= LOLLIPOP.");
            int permissionCheck = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            }
            if (permissionCheck != 0) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
                }
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
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
                        // System.out.println("UPDATE: " + device.id); TODO: Trop de shits dans la DB
                        userSingleton.addDevice(device);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(enableBluetoothReceiver);
        unregisterReceiver(makeDiscoverableReceiver);
        unregisterReceiver(discoverDevicesReceiver);
    }
}
