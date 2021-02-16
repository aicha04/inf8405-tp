package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_activity);

        Button newGameButton = findViewById(R.id.newGameButton);
        Button restartButton = findViewById(R.id.restartButton);

        newGameButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(EndActivity.this, MainActivity.class);
            startActivity(gameAct);
        });

        restartButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(EndActivity.this, gameActivity.class);
            startActivity(gameAct);
        });

    }


}