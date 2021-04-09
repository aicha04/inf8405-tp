package com.example.projet;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends BaseActivity {
    private static final String TAG = "Profile";
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
        //loadLocale();
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
                        setNewLocale(App.localeManager.LANGUAGE_FRENCH);
                        break;
                    case 1:
                        // English
                        setNewLocale(App.localeManager.LANGUAGE_ENGLISH);
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
     * https://github.com/YarikSOffice/LanguageTest/blob/db5b3742bfcc083459e4f23aeb91c877babb0968/app/src/main/java/com/yariksoffice/languagetest/ui/SettingsActivity.java
     * @param -
     * @return -
     */
    private void setNewLocale(String lang) {
        App.localeManager.setNewLocale(this, lang);
        recreate();
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
