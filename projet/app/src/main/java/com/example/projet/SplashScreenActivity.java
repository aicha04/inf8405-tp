package com.example.projet;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class SplashScreenActivity extends AppCompatActivity {
    int SPLASH_SCREEN_TIME = 3000;

    private  UserSingleton userSingleton = UserSingleton.getInstance();
    private Constants constants = new Constants();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try{
            setContentView(R.layout.activity_splash_screen);
            super.onCreate(savedInstanceState);

            // create animation
            ImageView container = findViewById(R.id.container);
            if(userSingleton.getCurrentUserTheme().equals(constants.LIGHT_THEME)){
                container.setImageResource(R.drawable.light_splash_screen);
            }else{
                container.setImageResource(R.drawable.splash_screen);
            }

            userSingleton.fetchAllUsers();

        }catch(Exception e){
            e.printStackTrace();
            Intent i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
            startActivity(i);
            finish();
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

            Intent i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
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
                Intent i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                startActivity(i);

                // close this activity
                finish();

            }
        }, time);

    }
}