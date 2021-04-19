package com.example.projet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.projet.databinding.ActivityAppAnalyticsBinding;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AppAnalyticsActivity extends BaseActivity {
    private static final String TAG = "AppAnalyticsActivity";
    private ActivityAppAnalyticsBinding binding;
    private Constants constants = new Constants();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserSingleton.getInstance().getCurrentUserTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        binding = ActivityAppAnalyticsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            setWifiLinkSpeed();
            Toast.makeText(this, "Wifi", Toast.LENGTH_LONG).show();
        } else if (mobile.isConnectedOrConnecting ()) {
            setUplinkDownlinkBandwidth();
            Toast.makeText(this, "Mobile 3G ", Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
        }




        binding.backButton.setOnClickListener(onClickListener);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = level * 100 / (int)scale;

        AppAnalyticsSingleton analyticsInstance = AppAnalyticsSingleton.getInstance();
        ArrayList<Date> timestamps = analyticsInstance.getTimeStamps();
        ArrayList<Integer> batteryLevels = analyticsInstance.getBatteryLevels();
        batteryLevels.add(batteryPct);
        timestamps.add(new Date());
        System.out.println("aaaa" + batteryLevels.size());
        GraphView graph = (GraphView) findViewById(R.id.graph);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        try {

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            for(int i =0; i < batteryLevels.size(); i++){
                if(i % 2 == 0) {
                    series = new LineGraphSeries<>();
                    DataPoint dataPoint = new DataPoint(timestamps.get(i), batteryLevels.get(i));
                    series.appendData(dataPoint, true, 2);
                    graph.addSeries(series);

                }else{
                    DataPoint dataPoint = new DataPoint(timestamps.get(i), batteryLevels.get(i));
                    series.appendData(dataPoint, true, 2);
                }
            }


            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(), formatter));
            graph.getGridLabelRenderer().setNumHorizontalLabels(4);
            graph.getGridLabelRenderer().setNumVerticalLabels(5);
            graph.setBackgroundColor(getResources().getColor(R.color.white));
// set manual x bounds to have nice steps
            graph.getViewport().setMinX(timestamps.get(0).getTime());
            graph.getViewport().setMaxX(timestamps.get(timestamps.size()-1).getTime());
//            graph.getViewport().setMinX(0);
//            graph.getViewport().setMaxX(timestamps.size()-1);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(100);
            graph.getViewport().setYAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
            graph.getGridLabelRenderer().setHumanRounding(false);
        } catch (IllegalArgumentException e) {
            Toast.makeText(AppAnalyticsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickBack(v);
        }
    };
    public void onClickBack(final View view) {
        Intent i = new Intent(AppAnalyticsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setUplinkDownlinkBandwidth(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps()/1000;
        int upSpeed = nc.getLinkUpstreamBandwidthKbps()/1000;
        String upSpeedStr =  upSpeed + " Mbps";
        String downSpeedStr = upSpeed+ " MB";
        binding.uplinkView.setText(upSpeedStr);
        binding.downlinkView.setText(downSpeedStr);
        System.out.println(downSpeed);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setWifiLinkSpeed(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getLinkSpeed();
        String linkSpeedStr = linkSpeed +" Mbps";
        if(linkSpeed != WifiInfo.LINK_SPEED_UNKNOWN){
            binding.signalLevelView.setText(linkSpeedStr);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        if (!language.equals(App.localeManager.getLanguage())) {
            Context context = App.localeManager.setLocale(this);
            Resources resources = context.getResources();

            binding.batteryUsageView.setText(resources.getString(R.string.battery_usage));

            language = App.localeManager.getLanguage();
        }
        Log.d(TAG, "onResume");
    }

}