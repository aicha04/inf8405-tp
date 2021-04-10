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
    public UserInfo currentUser = new UserInfo();
    private  String currentTheme = constants.LIGHT_THEME;
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> allUserIds = new ArrayList<String>();


    private ArrayList<Device> devices = new ArrayList<>();

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

    public void setCurrentUser(UserInfo user) {
        this.currentUser = user;
    }
    /**Get devices
     * @param  -
     * @return devices
     */
    public ArrayList<Device> getCurrentUserDevices() {
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
    void fetchCurrentUserDevices(){
        this.resetUserDevicesLocally();
        databaseRef.child(currentUser.getUserId()).child(constants.DEVICES_DATABASE_NAME).get().addOnCompleteListener(task -> {
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
            DatabaseReference pushedPostRef = databaseRef.child(currentUser.getUserId()).child(constants.DEVICES_DATABASE_NAME).push();
            device.setDbKey(pushedPostRef.getKey());
            pushedPostRef.setValue(device);
        }
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
            databaseRef.child(currentUser.getUserId())
                    .child(constants.DEVICES_DATABASE_NAME).updateChildren(map);
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
            databaseRef.child(currentUser.getUserId()).child(constants.DEVICES_DATABASE_NAME).updateChildren(map);
        }
    }

    /**Get current theme(Light or dark)
     * @param -
     * @return -
     */
    public String getCurrentUserTheme() {
        return currentUser.getTheme();
    }

    /**Set current theme(Light or dark)
     * @param currentTheme the new theme
     * @return -
     */
    public void setCurrentUserTheme(String currentTheme) {
        this.currentUser.setTheme(currentTheme);
        updateUserInfo();
    }

    /**Set current language
     * @param currentUserLanguage
     * @return -
     */
    public void setCurrentUserLanguage(String currentUserLanguage) {
        this.currentUser.setLanguage(currentUserLanguage);
        updateUserInfo();
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


    public void addNewUser(String userId){
        allUserIds.add(userId);
    }

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserId() {
        return currentUser.getUserId();
    }

    public void updateUserInfo(){
        databaseRef.child(currentUser.getUserId()).child(constants.USER_INFO_DATABASE_NAME).setValue(currentUser);
    }


}
