package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class GridOptionsActivity extends AppCompatActivity {
    private GridSize gridSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_options_activity);

        Button startButton = findViewById(R.id.start_button);
        Button threeButton = findViewById(R.id.three_by_three_button);
        Button fourButton = findViewById(R.id.four_by_four_button);

        startButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(GridOptionsActivity.this, gameActivity.class);
            startActivity(gameAct);
        });

        threeButton.setOnClickListener(v -> {
            this.gridSize = GridSize.THREE;
            Toast toast = Toast.makeText(getBaseContext(), "three", Toast.LENGTH_SHORT);
            toast.show();
        });

        fourButton.setOnClickListener(v -> {
            this.gridSize = GridSize.FOUR;
            Toast toast = Toast.makeText(getBaseContext(), "four", Toast.LENGTH_SHORT);
            toast.show();
        });
    }
}