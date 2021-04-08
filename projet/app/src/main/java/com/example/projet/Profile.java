package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.projet.MyAppGlideModule.*;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        setContentView(R.layout.activity_profile);

        // Set the button listener
        Button swapButton = (Button) findViewById(R.id.swap_theme_button);
        swapButton.setOnClickListener(v -> {
            swapTheme();
        });

        if(userSingleton.hasProfilePhoto){
            loadProfilePicture();
        }
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
