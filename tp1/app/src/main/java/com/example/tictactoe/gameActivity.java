package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

public class gameActivity extends AppCompatActivity {
    GridLayout gridLayout;
    TextView[][] textViews;
    GameInfoSingleton gameInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        gameInfos = GameInfoSingleton.getInstance();
        setUpGrid();
    }
    void setUpGrid() {
        int size = this.gameInfos.getGridSize();

        gridLayout = findViewById(R.id.grid);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);

        gridLayout.setColumnCount(size);
        gridLayout.setRowCount(size);

        textViews = new TextView[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                textViews[i][j] = new TextView(gameActivity.this);
                textViews[i][j].setText("X");
                textViews[i][j].setTextSize(35);
                textViews[i][j].setTextColor(getResources().getColor(R.color.white));
                textViews[i][j].setPadding(60, 60, 60, 60);
                textViews[i][j].setGravity(Gravity.CENTER);
                textViews[i][j].setBackground(getResources().getDrawable(R.drawable.grid_border));
                gridLayout.addView(textViews[i][j]);
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                TextView textView = textViews[i][j];
                //Typeface face = Typeface.createFromAsset(getAssets(), "fonts/baloo.xml");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText(gameInfos.getCurrentPlayer());
                        //textView.setTypeface(face);
                        TextView currentPlayerView = findViewById(R.id.currentPlayerView);

                        if (gameInfos.getCurrentPlayer() == Player.player1.getValue()) {
                            textView.setTextColor(getResources().getColor(R.color.palette_pink));
                            currentPlayerView.setText("Player 1 turn");

                        } else {
                            textView.setTextColor(getResources().getColor(R.color.palette_blue));
                            currentPlayerView.setText("Player 2 turn");
                        }

                        gameInfos.setNextPlayer();
                    }
                });
            }
        }
    }
}
