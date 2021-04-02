package com.example.tp2;

public class Device {
    public String id; // Using MAC address as id
    public String position;
    public String classCategory;
    public String classMajorCategory;
    public String deviceType;
    public String friendlyName;
    //Add any useful infos
    public int isFavorite = 0;
    public String dbKey = "";


    public Device(String id, String position, String classCategory, String classMajorCategory, String deviceType, String friendlyName, int isFavorite) {

        this.id = id;
        this.position = position;
        this.classCategory = classCategory;
        this.classMajorCategory = classMajorCategory;
        this.deviceType = deviceType;
        this.friendlyName = friendlyName;
        this.isFavorite = isFavorite;
    }

    public Device(){ }

    /**Add device to favorites
     * @param  -
     * @return -
     */
    void addToFavorite(){
        this.isFavorite = 1;
    }

    /**Remove device from favorites
     * @param  -
     * @return -
     */
    void removeFromFavorite(){
        this.isFavorite = 0;
    }

    /**Set db key(unique identifier of this device in firebase database
     * @param key
     * @return -
     */
    void setDbKey(String key){
        this.dbKey = key;
    }

    /**Get db key(unique identifier of this device in firebase database
     * @param
     * @return the key
     */
    public String getDbKey() {
        return dbKey;
    }

    /**Return a formatted string for displaying this device information
     * @param  -
     * @return -
     */
    public  String toString(){
        return this.id + "\n" + this.classCategory;
    }
}
