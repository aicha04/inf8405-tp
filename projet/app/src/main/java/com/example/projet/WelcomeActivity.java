package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Sleeper;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();
    private GridView grid = null;
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button create_profil_button = (Button) findViewById(R.id.create_button);

        if(userSingleton.getAllUserInfos().size() == 0){
            TextView warningView = findViewById(R.id.warning);
            warningView.setText("No profile found !");
        }

        create_profil_button.setOnClickListener(v -> {
            Intent welcomeAct = new Intent(WelcomeActivity.this, CreateProfileActivity.class);
            startActivity(welcomeAct);
            finish();
        });

        grid = findViewById(R.id.grid);
        loadGrid();
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