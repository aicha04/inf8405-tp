package com.example.tp2;

import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserSingleton {

   public static final UserSingleton instance = new UserSingleton();
    public String userUId = "";
    private ArrayList<Device> devices = new ArrayList<Device>();

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

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
        devices.add(device);
    }

    public void setUserUId(String userUId) {
        this.userUId = userUId;
    }

    public void resetUserDevicesLocally(){
        this.devices = new ArrayList<>();
    }

    /** Retrieve user saved devices from firebase database
     * @param -
     * @return -
     */
    void fetchUserDevices(){
        this.resetUserDevicesLocally();
        databaseRef.child(userUId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                System.out.println( task.getException());
            }
            else {
                DataSnapshot snapshot = task.getResult();
                for(DataSnapshot shot:  snapshot.getChildren()) {
                    for (DataSnapshot val : shot.getChildren()) {
                        Device device = val.getValue(Device.class);
                        this.addDevice(device);
                    }
                }
            }
        });
    }

    void addNewDeviceToDb(Device device){
        this.addDevice(device);
        databaseRef.child(UserSingleton.getInstance().getUserUId()).child("devices").push().setValue(device);
    }


}
