package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.projet.databinding.ActivityWelcomeBinding;

import java.util.Locale;

public class WelcomeActivity extends BaseActivity {
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();

    private String language;
    private ActivityWelcomeBinding binding;

    public static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        language = App.localeManager.getLanguage();

        binding.createButton.setOnClickListener(v -> {
            Intent welcomeAct = new Intent(WelcomeActivity.this, CreateProfileActivity.class);
            startActivity(welcomeAct);
            finish();
        });

        binding.loginButton.setOnClickListener(v -> {
            Intent welcomeAct = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(welcomeAct);
            fileList();
            finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!language.equals(App.localeManager.getLanguage())) {
            Context context = App.localeManager.setLocale(this);
            Resources resources = context.getResources();

            binding.createButton.setText(resources.getString(R.string.create_profile));
            binding.loginButton.setText(resources.getString(R.string.log_in));
            binding.textView.setText(resources.getString(R.string.welcome_to_findmydevice));

            language = App.localeManager.getLanguage();
        }
        Log.d(TAG, "onResume");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}