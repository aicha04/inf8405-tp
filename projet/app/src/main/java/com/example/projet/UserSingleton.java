package com.example.projet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSingleton {

   public static final UserSingleton instance = new UserSingleton();
    private Constants constants = new Constants();
    public String userId = "";
    private ArrayList<Device> devices = new ArrayList<Device>();
    private  String currentTheme = constants.LIGHT_THEME;
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    public Boolean hasProfilePhoto = true;

    private UserSingleton() { }

    /**Get the instance of the singleton
     * @param  -
     * @return  an object which represents the unique instance of this class
     */
    public static UserSingleton getInstance(){
        return instance;
    }

    /**Get user id
     * @param  -
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**Set user id
     * @param  userId, the new id
     * @return -
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**Get devices
     * @param  -
     * @return devices
     */
    public ArrayList<Device> getDevices() {
        return devices;
    }

    /**Reset devices array locally(not in db)
     * @param  -
     * @return -
     */
    public void resetUserDevicesLocally(){
        this.devices = new ArrayList<>();
    }

    /** Retrieve user saved devices from firebase database
     * @param -
     * @return -
     */
    void fetchUserDevices(){
        this.resetUserDevicesLocally();
        databaseRef.child(userId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                System.out.println( task.getException());
            }
            else {
                DataSnapshot snapshot = task.getResult();
                for(DataSnapshot shot:  snapshot.getChildren()) {
                    for (DataSnapshot val : shot.getChildren()) {
                        Device device = val.getValue(Device.class);
                        devices.add(device);
                    }
                }
            }
        });
    }

    /**Add new device to firebase database
     * @param device the new device
     * @return -
     */
    void addNewDeviceToDb(Device device){
        if(!contains(device)) {
            devices.add(device);
            DatabaseReference pushedPostRef = databaseRef.child(UserSingleton.getInstance()
                    .getUserId()).child(constants.DATABASE_NAME).push();
            device.setDbKey(pushedPostRef.getKey());
            pushedPostRef.setValue(device);
        }
    }

    /**Reset db for a specific user/phone
     * @param id user id
     * @return -
     */
    void resetDb(String id){
        databaseRef.child(id).child(constants.DATABASE_NAME).removeValue();
    }

    /**Add a device to favorites
     * @param devicePos the device position (in devices arrayList)
     * @return -
     */
    void addToFavorites(int devicePos){
       Device device = devices.get(devicePos);
       device.addToFavorite();
        if(!device.getDbKey().equals("")){
            Map<String, Object> map = new HashMap<>();
            map.put(device.getDbKey(), device);
            databaseRef.child(UserSingleton.getInstance().getUserId())
                    .child(constants.DATABASE_NAME).updateChildren(map);
        }
    }

    /**Remove a device from favorites
     * @param devicePos the device position (in devices arrayList)
     * @return -
     */
    void removeFromFavorites(int devicePos){
        Device device = devices.get(devicePos);
        device.removeFromFavorite();
        if(!device.getDbKey().equals("")){
            Map<String, Object> map = new HashMap<>();
            map.put(device.getDbKey(), device);
            databaseRef.child(UserSingleton.getInstance().getUserId()).child(constants.DATABASE_NAME).updateChildren(map);
        }
    }

    /**Get current theme(Light or dark)
     * @param -
     * @return -
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    /**Set current theme(Light or dark)
     * @param currentTheme the new theme
     * @return -
     */
    public void setCurrentTheme(String currentTheme) {
        this.currentTheme = currentTheme;
    }

    /**Verify if devices array contains a specific device
     * @param device the searched device
     * @return true if the devices already in arraylist(devices)
     */
    public Boolean contains(Device device){
        for(Device d: devices){
            if(d.position.equals(device.position) && d.id.equals(device.id)){
                return true;
            }
        }
        return false;
    }

    public void setHasProfilePhoto(Boolean hasProfilePhoto) {
        this.hasProfilePhoto = hasProfilePhoto;
    }
}
