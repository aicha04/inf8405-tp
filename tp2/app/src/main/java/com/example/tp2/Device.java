package com.example.tp2;

public class Device {
    public String id;
    public String position;
    public String classCategory;
    public int isFavorite = 0;
    public String dbKey = "";

    public Device(String id, String position, String classCategory, int isFavorite) {
        this.id = id;
        this.position = position;
        this.classCategory = classCategory;
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
