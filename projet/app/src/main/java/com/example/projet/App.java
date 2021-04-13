package com.example.projet;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

/** https://github.com/YarikSOffice/LanguageTest/blob/db5b3742bfcc083459e4f23aeb91c877babb0968/app/src/main/java/com/yariksoffice/languagetest/App.java */
public class App extends Application {

    public static final String TAG = "App";

    // for the sake of simplicity. use DI in real apps instead
    public static LocaleManager localeManager;

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);
        super.attachBaseContext(base);
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
    }

}
