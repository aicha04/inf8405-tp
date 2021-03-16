package com.example.tp2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserSingleton {

   public static final UserSingleton instance = new UserSingleton();
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    public String userUId = "";
    private ArrayList<Device> devices = new ArrayList<Device>();

    private UserSingleton() { }

    /**Get the instance of the singleton
     * @param  -
     * @return  an object which represents the unique instance of this class
     */
    public static UserSingleton getInstance(){
        return instance;
    }

    public String getUserUId() {
        return userUId;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    void addDevice(Device device) {
        databaseRef.child(UserSingleton.getInstance().getUserUId()).child("devices").push().setValue(device);
    }

    public void setUserUId(String userUId) {
        this.userUId = userUId;
    }

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

    public void resetUserDevicesLocally(){
        this.devices = new ArrayList<>();
    }
}
