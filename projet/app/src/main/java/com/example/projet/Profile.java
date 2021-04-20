package com.example.projet;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.projet.databinding.ActivityProfileBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends BaseActivity {
    private static final String TAG = "Profile";
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentUserTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(userSingleton.getCurrentUser().hasProfilePicture()){
            loadProfilePicture();
        }

        binding.usernameValue.setText(" " + userSingleton.getCurrentUserId());
        binding.themeValue.setText(userSingleton.getCurrentUserTheme().equals(constants.LIGHT_THEME)?
                R.string.light_theme_value : R.string.dark_theme_value);
        binding.languageValue.setText(R.string.language_value);

        // Set the button listener
        binding.swapThemeButton.setOnClickListener(v -> {
            swapTheme();
        });

        binding.backButton.setOnClickListener(v->{
            Intent mainAct = new Intent(this, MainActivity.class);
            startActivity(mainAct);
            finish();
        });

        binding.changeProfileButton.setOnClickListener(v->{
            Intent mainAct = new Intent(this, WelcomeActivity.class);
            startActivity(mainAct);
            finish();
        });
;
        binding.changeLanguageButton.setOnClickListener(v -> {
            showChangeLanguageDialog();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!language.equals(App.localeManager.getLanguage())) {
            Context context = App.localeManager.setLocale(this);
            Resources resources = context.getResources();

            binding.usernameView.setText(resources.getString(R.string.username));
            binding.userThemeView.setText(resources.getString(R.string.theme));
            binding.userLanguageView.setText(resources.getString(R.string.language));

            language = App.localeManager.getLanguage();
        }
        Log.d(TAG, "onResume");
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
        if (userSingleton.getCurrentUserTheme().equals(constants.DARK_THEME)) {
            userSingleton.setCurrentUserTheme(constants.LIGHT_THEME);
            setTheme(R.style.Theme_projet);
        } else {
            userSingleton.setCurrentUserTheme(constants.DARK_THEME);
            setTheme(R.style.Theme_projet_dark);
            userSingleton.setCurrentUserTheme(constants.DARK_THEME);
        }
        finish();
        startActivity(getIntent());
    }

    /**Load user profile picture with Glide
     * @param  -
     * @return -
     */
    //Source: https://github.com/firebase/FirebaseUI-Android/blob/master/storage/README.md
    private void loadProfilePicture(){
        try {
            // Reference to an image file in Cloud Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(userSingleton.getCurrentUserId());

            // Download directly from StorageReference using Glide
           Glide.with(this /* context */)
                    .load(storageReference)
                    .into(binding.profilePhotoView);
        }
        catch (Exception error){
            System.out.println(error.getMessage());
        }
    }
}
