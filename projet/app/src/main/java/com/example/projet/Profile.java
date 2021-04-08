package com.example.projet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.projet.MyAppGlideModule.*;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class Profile extends AppCompatActivity {
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_profile);

        // Set the button listener
        Button swapButton = (Button) findViewById(R.id.swap_theme_button);
        swapButton.setOnClickListener(v -> {
            swapTheme();
        });

        Button languageButton = (Button) findViewById(R.id.change_language_button);
        languageButton.setOnClickListener(v -> {
            showChangeLanguageDialog();
        });

        if(userSingleton.hasProfilePhoto){
            loadProfilePicture();
        }
    }
    /** Show language options to user
     * https://www.youtube.com/watch?v=zILw5eV9QBQ
     * @param -
     * @return -
     */
    private void showChangeLanguageDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Profile.this);
        mBuilder.setTitle(getResources().getString(R.string.choose_language));
        mBuilder.setSingleChoiceItems(constants.LANGUAGES, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // French
                        setLocale("fr");
                        recreate();
                        break;
                    case 1:
                        // English
                        setLocale("en");
                        recreate();
                        break;
                    default:
                        Log.d("PROFILE", "No language chosen");
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /** Change the app language
     * https://www.youtube.com/watch?v=zILw5eV9QBQ
     * @param -
     * @return -
     */
    private void setLocale(String lang) {
        Log.d("Profile", "setLocale");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
        editor.putString(constants.CURRENT_LANGUAGE, lang);
        editor.apply();
    }

    /** Load the app language
     * https://www.youtube.com/watch?v=zILw5eV9QBQ
     * @param -
     * @return -
     */
    private void loadLocale() {
        Log.d("Profile", "loadLocale");
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String language = sharedPreferences.getString(constants.CURRENT_LANGUAGE, "");
        setLocale(language);
    }

    /** Update the app theme
     * @param -
     * @return -
     */
    public void swapTheme() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (userSingleton.getCurrentTheme().equals(constants.DARK_THEME)) {
            userSingleton.setCurrentTheme(constants.LIGHT_THEME);
            editor.putString(constants.CURRENT_THEME, constants.LIGHT_THEME);
            setTheme(R.style.Theme_projet);
        } else {
            userSingleton.setCurrentTheme(constants.DARK_THEME);
            editor.putString(constants.CURRENT_THEME, constants.DARK_THEME);
            setTheme(R.style.Theme_projet_dark);
        }
        System.out.println(userSingleton.getCurrentTheme());
        editor.commit();

        finish();
        startActivity(getIntent());

    }

    //Source: https://github.com/firebase/FirebaseUI-Android/blob/master/storage/README.md

    private void loadProfilePicture(){
        try {
            // Reference to an image file in Cloud Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(userSingleton.getUserId());

            // ImageView in your Activity
            ImageView imageView = findViewById(R.id.profile_photo_view);

            // Download directly from StorageReference using Glide
            // (See MyAppGlideModule for Loader registration)
           Glide.with(this /* context */)
                    .load(storageReference)
                    .into(imageView);
        }
        catch (Exception error){
            System.out.println(error.getMessage());
        }
    }
}
