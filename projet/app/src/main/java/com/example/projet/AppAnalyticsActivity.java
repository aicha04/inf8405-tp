package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.format.DateFormat;
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

public class AppAnalyticsActivity extends AppCompatActivity {

    private ActivityAppAnalyticsBinding binding;
    private Constants constants = new Constants();
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

        GraphView graph = (GraphView) findViewById(R.id.graph);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        try {

            LineGraphSeries <DataPoint> series = new LineGraphSeries< >();
            for(int i =0; i < batteryLevels.size(); i++){
                series.appendData(new DataPoint(timestamps.get(i), batteryLevels.get(i)),false, batteryLevels.size());
            }

            graph.addSeries(series);
            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(), formatter));
            graph.getGridLabelRenderer().setNumHorizontalLabels(batteryLevels.size()); // only 4 because of the space

// set manual x bounds to have nice steps
            graph.getViewport().setMinX(timestamps.get(0).getTime());
            graph.getViewport().setMaxX(timestamps.get(timestamps.size()-1).getTime());
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(100);
            graph.getViewport().setYAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
            graph.getGridLabelRenderer().setHumanRounding(false);
            AppAnalyticsSingleton.getInstance().getTimeStamps().clear();
            AppAnalyticsSingleton.getInstance().getBatteryLevels().clear();
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

}