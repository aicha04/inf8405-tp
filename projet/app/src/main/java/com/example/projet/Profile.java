package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends AppCompatActivity {
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentUserTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button swapButton = findViewById(R.id.swap_theme_button);
        TextView userIdView = findViewById(R.id.username_view);
        TextView userThemeView = findViewById(R.id.user_theme_view);
        Button backButton = findViewById(R.id.back_button);
        Button changeProfileButton = findViewById(R.id.change_profile_button);

        if(userSingleton.getCurrentUser().hasProfilePicture()){
            loadProfilePicture();
        }

        userIdView.setText("Username: " + userSingleton.getCurrentUser().getUserId());
        userThemeView.setText("Theme: " + userSingleton.getCurrentUserTheme());

        // Set the button listener
        swapButton.setOnClickListener(v -> {
            swapTheme();
        });

        backButton.setOnClickListener(v->{
            Intent mainAct = new Intent(this, MainActivity.class);
            startActivity(mainAct);
            finish();

        });

        changeProfileButton.setOnClickListener(v->{
            Intent mainAct = new Intent(this, WelcomeActivity.class);
            startActivity(mainAct);
            finish();

        });
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

    //Source: https://github.com/firebase/FirebaseUI-Android/blob/master/storage/README.md

    private void loadProfilePicture(){
        try {
            // Reference to an image file in Cloud Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(userSingleton.getCurrentUserId());

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
