package com.example.projet;


import java.util.ArrayList;

public  class UserInfo {
    private static final String LIGHT_THEME = "LIGHT" ;
    public static final String  DARK_THEME = "DARK";
    private static final String LANGUAGE_1 = "FRENCH" ;
    public static final String  LANGUAGE_2 = "ENGLISH";

    private String userId;
    private Boolean hasProfilePhoto;
    private String language = LANGUAGE_1;
    private String theme = LIGHT_THEME;


    /**Set user id
     * @param  userId, the new id
     * @return -
     */

    public String getUserId() {
        return userId;
    }

    public Boolean getHasProfilePicture() {
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


    /**Set current theme(Light or dark)
     * @param currentTheme the new theme
     * @return -
     */

    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**Get devices
     * @param  -
     * @return devices
     */
}