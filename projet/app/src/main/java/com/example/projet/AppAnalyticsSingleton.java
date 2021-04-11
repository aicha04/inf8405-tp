package com.example.projet;

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

}
