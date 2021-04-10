package com.example.tp2;

import java.util.ArrayList;

public class AppAnalyticsSingleton {
    private static final AppAnalyticsSingleton instance = new AppAnalyticsSingleton();
    private ArrayList<String> batteryLevels = new ArrayList<String>();
    private ArrayList<String> timeStamps= new ArrayList<String>();
    private AppAnalyticsSingleton(){}
    public AppAnalyticsSingleton getInstance(){return instance;}
    public ArrayList<String> getBatteryLevels(){return batteryLevels;}
    public ArrayList<String> getTimeStanps(){return timeStamps;}

}
