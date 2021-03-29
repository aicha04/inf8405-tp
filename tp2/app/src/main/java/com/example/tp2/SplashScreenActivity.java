package com.example.tp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.util.UUID;

public class SplashScreenActivity extends AppCompatActivity {
    int SPLASH_SCREEN_TIME = 3000;
    private SharedPreferences sharedPreferences;
    private  UserSingleton userSingleton = UserSingleton.getInstance();
    private Constants constants = new Constants();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.activity_splash_screen);

            // create animation
            ImageView container = findViewById(R.id.container);
            container.setImageResource(R.drawable.splash_screen);
            //Get user id
            setUpSharedPreferences();
            userSingleton.fetchUserDevices();

        }catch(Exception e){
            e.printStackTrace();
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);
        }

    }
    @Override
    protected void onResume(){
        super.onResume();

        try{
            // start splash screen
            startTimer(SPLASH_SCREEN_TIME);
        }
        catch(Exception e){
            e.printStackTrace();

            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);

            // close this activity
            finish();
        }
    }
    /**
     * Starts splash screen timer
     * @param  time  specifies splash screen total duration
     * @return
     */
    private void startTimer(final int time) {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //create intent to start main activity
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();

            }
        }, time);

    }

    /** Retrieve user id through shared preferences and
     *  update the userInfoSingleton singleton id with the value
     *  Create a shared preferences database for this user for the first use of the application
     * @param -
     * @return -
     */
    void setUpSharedPreferences(){
        File file = new File(constants.SHARED_PREFERENCES_PATH);
        if(file.exists()){
            sharedPreferences = getSharedPreferences(constants.SHARED_PREFERENCES_NAME, SplashScreenActivity.this.MODE_PRIVATE);
            if(sharedPreferences.contains(constants.SHARED_USER_ID)){
                userSingleton.setUserUId(sharedPreferences.getString(constants.SHARED_USER_ID, ""));
            }
        }else{
            sharedPreferences = getApplicationContext().getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String userUId =  UUID.randomUUID().toString();
            userSingleton.setUserUId(userUId);
            editor.putString(constants.SHARED_USER_ID, userUId);
            editor.commit();
        }
    }


}