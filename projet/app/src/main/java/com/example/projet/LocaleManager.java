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

    /** Cronstructor
     * @param context
     * @return -
     */
    public LocaleManager(Context context) {
        prefs = context.getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    /** Set language for the app with the one in SharedPreferences
     * @param c context in which setting language
     * @return Context - context updated
     */
    public Context setAppLocale(Context c) {
        return updateResources(c, getAppLanguage());
    }

    /** Get language in SharedPreferences
     * @param -
     * @return String - language
     */
    public String getAppLanguage() {
        return prefs.getString(constants.CURRENT_LANGUAGE, "");
    }

    /** Set new language for the app
     * @param c context in which setting new language
     * @param language new language
     * @return c context updated
     */
    public Context setNewAppLocale(Context c, String language) {
        persistAppLanguage(language);
        return updateResources(c, language);
    }

    /** Save language in SharedPreferences
     * @param language language to save
     * @return -
     */
    private void persistAppLanguage(String language) {
        // use commit() instead of apply(), because sometimes we kill the application process
        // immediately that prevents apply() from finishing
        prefs.edit().putString(constants.CURRENT_LANGUAGE, language).commit();
    }

    /** Set language for the user with the one in UserSingleton
     * @param c context in which setting language
     * @return Context - context updated
     */
    public Context setLocale(Context c) {
        return updateResources(c, getLanguage());
    }

    /** Set new language for the user
     * @param c context in which setting new language
     * @param language new language
     * @return c context updated
     */
    public Context setNewLocale(Context c, String language) {
        persistLanguage(language);
        return updateResources(c, language);
    }

    /** Get language in UserSingleton
     * @param -
     * @return String - language
     */
    public String getLanguage() {
        return UserSingleton.getInstance().getCurrentUserLanguage();
    }

    /** Save language in UserSingleton
     * @param language language to save
     * @return -
     */
    private void persistLanguage(String language) {
        // use commit() instead of apply(), because sometimes we kill the application process
        // immediately that prevents apply() from finishing
        UserSingleton.getInstance().setCurrentUserLanguage(language);
    }

    /** Update context with new configuration
     * @param context context in which setting the language
     * @param language
     * @return c context updated
     */
    public Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        config.setLocale(locale);
        context = context.createConfigurationContext(config);

        return context;
    }

}
