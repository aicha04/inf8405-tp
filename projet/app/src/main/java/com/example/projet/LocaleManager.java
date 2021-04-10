package com.example.projet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/** https://github.com/YarikSOffice/LanguageTest/blob/master/app/src/main/java/com/yariksoffice/languagetest/LocaleManager.java */
public class LocaleManager {
    public static final  String LANGUAGE_ENGLISH   = "en";
    public static final  String LANGUAGE_FRENCH = "fr";
    private Constants constants = new Constants();
    private final SharedPreferences prefs;

    public LocaleManager(Context context) {
        //prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs = context.getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    public Context setLocale(Context c) {
        return updateResources(c, getLanguage());
    }

    public Context setNewLocale(Context c, String language) {
        persistLanguage(language);
        return updateResources(c, language);
    }

    public static Locale getLocale(Resources res) {
        Configuration config = res.getConfiguration();
        return isAtLeastVersion(Build.VERSION_CODES.N) ? config.getLocales().get(0) : config.locale;
    }

    public String getLanguage() {
        return prefs.getString(constants.CURRENT_LANGUAGE, "");
    }

    private void persistLanguage(String language) {
        // use commit() instead of apply(), because sometimes we kill the application process
        // immediately that prevents apply() from finishing
        prefs.edit().putString(constants.CURRENT_LANGUAGE, language).commit();
    }

    public Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        config.setLocale(locale);
        context = context.createConfigurationContext(config);

        return context;
    }

    public static boolean isAtLeastVersion(int version) {
        return Build.VERSION.SDK_INT >= version;
    }
}
