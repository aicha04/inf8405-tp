package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class GridOptionsActivity extends AppCompatActivity {
    private GameInfoSingleton gameInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_options_activity);

        gameInfos = GameInfoSingleton.getInstance();

        Button startButton = findViewById(R.id.start_button);
        Button threeButton = findViewById(R.id.three_by_three_button);
        Button fourButton = findViewById(R.id.four_by_four_button);
        Button fiveButton = findViewById(R.id.five_by_five_button);

        startButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(GridOptionsActivity.this, gameActivity.class);
            startActivity(gameAct);
        });

        threeButton.setOnClickListener(v -> {
            this.gameInfos.setGridSize(GridSize.THREE);
            Toast toast = Toast.makeText(getBaseContext(), "three", Toast.LENGTH_SHORT);
            toast.show();
        });

        fourButton.setOnClickListener(v -> {
            this.gameInfos.setGridSize(GridSize.FOUR);
            Toast toast = Toast.makeText(getBaseContext(), "four", Toast.LENGTH_SHORT);
            toast.show();
        });

        fiveButton.setOnClickListener(v -> {
            this.gameInfos.setGridSize(GridSize.FIVE);
            Toast toast = Toast.makeText(getBaseContext(), "five", Toast.LENGTH_SHORT);
            toast.show();
        });
    }
}