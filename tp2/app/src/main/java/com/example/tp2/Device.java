package com.example.tp2;

public class Device {
    public String id;
    public String position;
    public String classCategory;
    public int isFavorite;
    public String dbKey="";
    //Add any useful infos


    public Device(String id, String position, String classCategory, int isFavorite) {
        this.id = id;
        this.position = position;
        this.classCategory = classCategory;
        this.isFavorite = isFavorite;
    }

    public Device(){ }

    void addToFavorite(){
        this.isFavorite = 1;
    }

    void removeFromFavorite(){
        this.isFavorite = 0;
    }

    void setDbKey(String key){
        this.dbKey = key;
    }

    public String getDbKey() {
        return dbKey;
    }
}
