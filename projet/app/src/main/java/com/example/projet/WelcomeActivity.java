package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_welcome);

        Button create_profil_button = (Button) findViewById(R.id.create_button);
        create_profil_button.setOnClickListener(v -> {
            Intent welcomeAct = new Intent(WelcomeActivity.this, CreateProfileActivity.class);
            startActivity(welcomeAct);
            finish();
        });

        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(v -> {
            //Intent welcomeAct = new Intent(WelcomeActivity.this, MainActivity.class);
            //startActivity(welcomeAct);
            //finish();
        });
    }
}