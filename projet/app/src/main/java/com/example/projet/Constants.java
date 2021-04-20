package com.example.projet;

public class Constants {
    public String SHARED_PREFERENCES_PATH = "/data/data/com.example.projet/shared_prefs/preferences.xml";
    public String SHARED_PREFERENCES_NAME ="preferences";
    public double DEFAULT_LATITUDE = 45.5578;
    public double DEFAULT_LONGITUDE = -73.6161;
    public String LIGHT_THEME= "LIGHT";
    public String DARK_THEME = "DARK";
    public String DEVICES_DATABASE_NAME = "devices";
    public String USER_INFO_DATABASE_NAME = "userInfo";
    public  String CURRENT_LANGUAGE = "My Lang";
    public final String[] LANGUAGES = {"Français", "English"};

    //Matrix to inverse colors
    // Source : https://github.com/osmdroid/osmdroid/blob/master/osmdroid-android/src/main/java/org/osmdroid/views/overlay/TilesOverlay.java
    public float[] NEGATE_COLORS_MATRIX = {
            -1.0f, 0.0f, 0.0f, 0.0f, 255f, //red
            0.0f, -1.0f, 0.0f, 0.0f, 255f, //green
            0.0f, 0.0f, -1.0f, 0.0f, 255f, //blue
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f //alpha
    };

    //Matrix for black and white filter
    // Source: https://kazzkiq.github.io/svg-color-filter/
    public float[] BLACK_AND_WHITE_COLORS_MATRIX = {
            0, 1, 0, 0, 0, //red
            0, 1, 0, 0, 0, //green
            0, 1, 0, 0, 0, //blue
            0, 1, 0, 1, 0, //alpha
    };
}
