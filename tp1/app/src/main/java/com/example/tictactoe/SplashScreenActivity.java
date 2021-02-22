package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.VideoView;


public class SplashScreenActivity extends AppCompatActivity {
    int SPLASH_SCREEN_TIME = 4000;
    int currentTime = 0;
    int TIME_INTERVAL = 5000;
    private AnimationDrawable animationDrawable;
    private  MediaPlayer mediaPlayer;
    private VideoView container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        ImageView container = findViewById(R.id.container);
//        container.setBackgroundResource(R.drawable.splash_animation);
//        animationDrawable = (AnimationDrawable) container.getBackground();

        container = findViewById(R.id.container);
        container.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.logo_animation);
        try{
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.splash_screen_audio);
            container.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.logo_animation);
            container.start();
            mediaPlayer.start();
            container.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });


            checkAnimationStatus(TIME_INTERVAL, animationDrawable);


        }catch(Exception e){
            e.printStackTrace();
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);
        }



    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//
//        try{
//            animationDrawable.start();
//            checkAnimationStatus(TIME_INTERVAL, animationDrawable);
//        }
//        catch(Exception e){
//            e.printStackTrace();
//
//            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
//            startActivity(i);
//
//            // close this activity
//            finish();
//        }
//    }

    private void checkAnimationStatus(final int time, AnimationDrawable animationDrawable) {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                currentTime += 1;
                container.pause();
                mediaPlayer.pause();
                mediaPlayer.release();
                mediaPlayer = null;
                //create intent to start main activity
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();

            }
        }, time);

    }


}