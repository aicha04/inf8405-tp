package com.example.tp2;

import android.graphics.ColorMatrix;

public class Constants {
    public String SHARED_PREFERENCES_PATH = "/data/data/com.example.tp2/shared_prefs/preferences.xml";
    public String SHARED_PREFERENCES_NAME ="preferences";
    public String SHARED_USER_ID = "userId";
    public String LIGHT_THEME= "LIGHT";
    public String DARK_THEME = "DARK";
    public String CURRENT_THEME = "CURRENT_THEME";
    public String DATABASE_NAME = "devices";

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
