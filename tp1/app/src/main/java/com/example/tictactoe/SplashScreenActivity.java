package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;


public class SplashScreenActivity extends AppCompatActivity {
    int SPLASH_SCREEN_TIME = 10;
    private ImageView container;
    private AnimationDrawable animationDrawable;
    private  MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        container = findViewById(R.id.container);
        container.setBackgroundResource(R.drawable.splash_animation);
        animationDrawable = (AnimationDrawable) container.getBackground();

        try{
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.splash_screen_audio);
            mediaPlayer.start();

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
            animationDrawable.start();
            checkAnimationStatus(SPLASH_SCREEN_TIME, animationDrawable);
        }
        catch(Exception e){
            e.printStackTrace();

            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);

            // close this activity
            finish();
        }
    }

    private void checkAnimationStatus(final int time, AnimationDrawable animationDrawable) {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (animationDrawable.getCurrent() != animationDrawable.getFrame(animationDrawable.getNumberOfFrames() - 1))
                    checkAnimationStatus(time, animationDrawable);
                else {
                    mediaPlayer.pause();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    //create intent to start main activity
                    Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();
                }
            }
        }, time);

    }


}