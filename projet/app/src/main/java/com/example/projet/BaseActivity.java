package com.example.projet;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/** https://github.com/YarikSOffice/LanguageTest/blob/master/app/src/main/java/com/yariksoffice/languagetest/ui/BaseActivity.java */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    protected String language;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(App.localeManager.setLocale(base));
        language = App.localeManager.getLanguage();
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        App.localeManager.setLocale(this);
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
    }

    /**Show toast in the correct language
     * @param  c - context in which show the toast
     * @param  id - StringRes id to the string to display
     * @return -
     */
    protected void showToast(Context c, @StringRes int id) {
        Resources resources = App.localeManager.setLocale(c).getResources();
        Toast.makeText(c, resources.getString(id), Toast.LENGTH_LONG).show();
    }
}