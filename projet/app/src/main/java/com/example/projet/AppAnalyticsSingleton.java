package com.example.projet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.ArrayList;
import java.util.Date;

public class AppAnalyticsSingleton {
    private static final AppAnalyticsSingleton instance = new AppAnalyticsSingleton();
    private ArrayList<Integer> batteryLevels = new ArrayList<Integer>();
    private ArrayList<Date> timeStamps= new ArrayList<Date>();
    private AppAnalyticsSingleton(){}
    public static AppAnalyticsSingleton getInstance(){return instance;}
    public ArrayList<Integer> getBatteryLevels(){return batteryLevels;}
    public ArrayList<Date> getTimeStamps(){return timeStamps;}

    public void addBatteryLevel(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = level * 100 / (int)scale;

        ArrayList<Date> timestamps = instance.getTimeStamps();
        ArrayList<Integer> batteryLevels = instance.getBatteryLevels();
        batteryLevels.add(batteryPct);
        timestamps.add(new Date());
    }

}
