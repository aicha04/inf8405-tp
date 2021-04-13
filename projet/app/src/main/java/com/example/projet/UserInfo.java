package com.example.projet;

public  class UserInfo {

    public String userId;
    public Boolean hasProfilePhoto = false;
    public String language = App.localeManager.getAppLanguage();
    public String theme = "LIGHT";

    public UserInfo(){

    }

    public UserInfo(String userId, Boolean hasProfilePhoto, String language, String theme) {
        this.userId = userId;
        this.hasProfilePhoto = hasProfilePhoto;
        this.language = language;
        this.theme = theme;
    }


    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**Set user id
     * @param  userId, the new id
     * @return -
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public Boolean hasProfilePicture() {
        return hasProfilePhoto;
    }

    public void setHasProfilePhoto(Boolean hasProfilePhoto) {
        this.hasProfilePhoto = hasProfilePhoto;
    }

    public String getLanguage() {
        return language;
    }

    public String getTheme() {
        return theme;
    }

    public void setLanguage(String currentUserLanguage) {
        this.language = currentUserLanguage;
    }
}