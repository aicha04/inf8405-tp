package com.example.tp2;

public class Device {
    public String id; // Using MAC address as id
    public String position;
    public String classCategory;
    public String classMajorCategory;
    public String deviceType;
    public String friendlyName;
    //Add any useful infos


    public Device(String id, String position, String classCategory, String classMajorCategory, String deviceType, String friendlyName) {
        this.id = id;
        this.position = position;
        this.classCategory = classCategory;
        this.classMajorCategory = classMajorCategory;
        this.deviceType = deviceType;
        this.friendlyName = friendlyName;
    }

    public Device(){ }
}
