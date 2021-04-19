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
    private DatabaseReference databaseRef ;

    private ArrayList<UserInfo> allUserInfos = new ArrayList<UserInfo>();

    private ArrayList<Device> devices = new ArrayList<>();

    public ArrayList<UserInfo> getAllUserInfos() {
        return allUserInfos;
    }

    private UserSingleton() {
        //Enabling Offline Capabilities
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        databaseRef = database.getReference();
    }

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

    /** Retrieve current user saved devices from firebase database
     * @param -
     * @return -
     */
    void fetchCurrentUserDevices() {
        try {
            this.resetUserDevicesLocally();
            databaseRef.child(constants.DEVICES_DATABASE_NAME).child(currentUser.getUserId()).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    System.out.println(task.getException());
                } else {
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot shot : snapshot.getChildren()) {
                            Device device = shot.getValue(Device.class);
                            devices.add(device);
                        }
                    }
            });
        } catch (Exception e) {
            System.out.println("Something wrong with fetch devices");
        }
    }
    /**Add new device to firebase database
     * @param device the new device
     * @return -
     */
    void addNewDeviceToDb(Device device){
        if(!contains(device)) {
            devices.add(device);
            DatabaseReference pushedPostRef = databaseRef.child(constants.DEVICES_DATABASE_NAME).child(currentUser.getUserId()).push();
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
            databaseRef.child(constants.DEVICES_DATABASE_NAME)
                    .child(currentUser.getUserId())
                    .updateChildren(map);
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
            databaseRef.child(constants.DEVICES_DATABASE_NAME).child(currentUser.getUserId()).updateChildren(map);
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
        this.updateUserInfo(currentUser);
    }

    /**Set current language
     * @param currentUserLanguage
     * @return -
     */
    public void setCurrentUserLanguage(String currentUserLanguage) {
        this.currentUser.setLanguage(currentUserLanguage);
        updateUserInfo(currentUser);
    }
    public String getCurrentUserLanguage(){
        return currentUser.getLanguage();
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

    /**Add new user to the database
     * @param  user the new user information
     * @return -
     */
    public void addNewUser(UserInfo user){
        allUserInfos.add(user);
        databaseRef.child(constants.USER_INFO_DATABASE_NAME).child(user.getUserId()).setValue(user);
    }

    /**Get current user information
     * @param
     * @return current user information
     */
    public UserInfo getCurrentUser() {
        return currentUser;
    }

    /**Get current user id
     * @param
     * @return current user information
     */
    public String getCurrentUserId() {
        return currentUser.getUserId();
    }

    /**Get update user information
     * @param  userInfo the new user with updated information
     * @return
     */
    public void updateUserInfo(UserInfo userInfo){
        databaseRef.child(constants.USER_INFO_DATABASE_NAME).child(userInfo.getUserId()).setValue(userInfo);
    }


    /**Reset users infos array locally(not in db)
     * @param  -
     * @return -
     */
    public void resetAllUsersInfosLocally(){
        this.allUserInfos = new ArrayList<>();
    }

    /** Retrieve user saved devices from firebase database
     * @param -
     * @return -
     */
    void fetchAllUsers(){
        this.resetAllUsersInfosLocally();
        databaseRef.child(constants.USER_INFO_DATABASE_NAME).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                System.out.println( task.getException());
            }
            else {
                DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot val : snapshot.getChildren()) {
                        UserInfo userInfo = val.getValue(UserInfo.class);
                        allUserInfos.add(userInfo);
                    }
            }
        });
    }

    /**Chexk if user is already in the database
     * @param userId
     * @return -
     */
    public Boolean userExists(String userId){
        for(UserInfo user: allUserInfos){
            if(user.getUserId().equals(userId)){
                return true;
            }
        }
        return false;
    }
}
