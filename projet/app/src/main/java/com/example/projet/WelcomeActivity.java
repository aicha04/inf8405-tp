package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Sleeper;
import java.util.ArrayList;
import com.example.projet.databinding.ActivityWelcomeBinding;
import java.util.Locale;
public class WelcomeActivity extends BaseActivity {
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();
    private GridView grid = null;
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    private String language;
    private ActivityWelcomeBinding binding;

    public static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ImageButton create_profil_button = (ImageButton) findViewById(R.id.create_button);
        if(userSingleton.getAllUserInfos().size() == 0){
            TextView warningView = findViewById(R.id.warning);
            warningView.setText("No profile found !");
        }
        language = App.localeManager.getLanguage();
        create_profil_button.setOnClickListener(v -> {
            Intent welcomeAct = new Intent(WelcomeActivity.this, CreateProfileActivity.class);
            startActivity(welcomeAct);
            finish();
        });

        grid = findViewById(R.id.grid);
        loadGrid();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!language.equals(App.localeManager.getLanguage())) {
//            Context context = App.localeManager.setLocale(this);
//            Resources resources = context.getResources();
//
//            binding.createButton.setText(resources.getString(R.string.create_profile));
//            binding.loginButton.setText(resources.getString(R.string.log_in));
//            binding.textView.setText(resources.getString(R.string.welcome_to_findmydevice));
//
//            language = App.localeManager.getLanguage();
//        }
        Log.d(TAG, "onResume");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    void loadGrid(){
            UserAdapter adapter = new UserAdapter(WelcomeActivity.this, userSingleton.getAllUserInfos());
            grid.setAdapter(adapter);
    }

    public void openAccount(int position) {
        userSingleton.setCurrentUser(userSingleton.getAllUserInfos().get(position));
        userSingleton.fetchCurrentUserDevices();

        Intent mainAct = new Intent(this, MainActivity.class);
        startActivity(mainAct);
        finish();
    }
}