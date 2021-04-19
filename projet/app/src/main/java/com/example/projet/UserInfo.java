package com.example.projet;

public  class UserInfo {
    public String userId;
    public Boolean hasProfilePhoto = false;
    public String language = App.localeManager.getAppLanguage();
    public String theme = "LIGHT";

    public UserInfo(){ }

    public UserInfo(String userId, Boolean hasProfilePhoto, String language, String theme) {
        this.userId = userId;
        this.hasProfilePhoto = hasProfilePhoto;
        this.language = language;
        this.theme = theme;
    }

    /**Set user theme
     * @param  theme the new theme
     * @return -
     */
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

    /**Get user id
     * @param
     * @return user Id
     */
    public String getUserId() {
        return userId;
    }

    /**Get if user has profile photo
     * @param
     * @return true user has profile photo
     */
    public Boolean hasProfilePicture() {
        return hasProfilePhoto;
    }

    /**Set if user has profile photo
     * @param  hasProfilePhoto true user has profile photo
     * @return -
     */
    public void setHasProfilePhoto(Boolean hasProfilePhoto) {
        this.hasProfilePhoto = hasProfilePhoto;
    }

    /**Get user language
     * @param
     * @return user language
     */
    public String getLanguage() {
        return language;
    }

    /**Get user theme
     * @param
     * @return user theme
     */
    public String getTheme() {
        return theme;
    }

    /**Set user language
     * @param  currentUserLanguage the new language
     * @return -
     */
    public void setLanguage(String currentUserLanguage) {
        this.language = currentUserLanguage;
    }
}