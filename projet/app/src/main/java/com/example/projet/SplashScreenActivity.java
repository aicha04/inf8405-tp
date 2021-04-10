package com.example.projet;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.util.UUID;

public class SplashScreenActivity extends AppCompatActivity {
    int SPLASH_SCREEN_TIME = 3000;
    private SharedPreferences sharedPreferences;
    private  UserSingleton userSingleton = UserSingleton.getInstance();
    private Constants constants = new Constants();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try{
            //Get user id and stored theme(light or dark)
            setUpSharedPreferences();

            if(userSingleton.getCurrentTheme().equals(constants.LIGHT_THEME)){
                setTheme(R.style.Theme_projet);
            }else{
                setTheme(R.style.Theme_projet_dark);
            }

            setContentView(R.layout.activity_splash_screen);
            super.onCreate(savedInstanceState);

            // create animation
            ImageView container = findViewById(R.id.container);
            if(userSingleton.getCurrentTheme().equals(constants.LIGHT_THEME)){
                container.setImageResource(R.drawable.light_splash_screen);
            }else{
                container.setImageResource(R.drawable.splash_screen);
            }

            //Fetch detected devices
            userSingleton.fetchUserDevices();
            System.out.println("ID" +userSingleton.getCurrentUser());

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
                userSingleton.setCurrentUser(sharedPreferences.getString(constants.SHARED_USER_ID, ""));
            }
            if(sharedPreferences.contains(constants.CURRENT_THEME)){
                userSingleton.setCurrentTheme(sharedPreferences.getString(constants.CURRENT_THEME, constants.LIGHT_THEME));
            }
        }else{
            sharedPreferences = getApplicationContext().getSharedPreferences(constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String userUId =  UUID.randomUUID().toString();
            userSingleton.setCurrentUser(userUId);
            editor.putString(constants.SHARED_USER_ID, userUId);

            editor.putString(constants.CURRENT_THEME, constants.LIGHT_THEME);
            editor.commit();
        }
    }

}