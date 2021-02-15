package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

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
    boolean isGameWon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        gameInfos = GameInfoSingleton.getInstance();
        setUpGrid();
    }
    void setUpGrid() {
        int size = this.gameInfos.getGridSize();
        textViews = new TextView[size][size];

        gridLayout = findViewById(R.id.grid);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);
        gridLayout.setColumnCount(size);
        gridLayout.setRowCount(size);


        Typeface face = ResourcesCompat.getFont(this, R.font.baloo);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                textViews[i][j] = new TextView(gameActivity.this);
                textViews[i][j].setText("Q"); // TODO: Je mets Q pour suivre le nombre de O mit par le player 2
                textViews[i][j].setTypeface(face);
                textViews[i][j].setTextSize(60);
                textViews[i][j].setTextColor(getResources().getColor(R.color.white));
                textViews[i][j].setPadding(60, 60, 60, 60);
                textViews[i][j].setGravity(Gravity.CENTER);
                textViews[i][j].setBackground(getResources().getDrawable(R.drawable.grid_border));
                gridLayout.addView(textViews[i][j]);
            }
        }
        isGameWon = false;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                TextView textView = textViews[i][j];
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isGameWon) {
                            textView.setText(gameInfos.getCurrentPlayer());
                            TextView currentPlayerView = findViewById(R.id.currentPlayerView);

                            if (gameInfos.getCurrentPlayer().equals(Player.player1.getValue())) {
                                textView.setTextColor(getResources().getColor(R.color.palette_pink));
                                currentPlayerView.setText("Player 1 turn");

                            } else {
                                textView.setTextColor(getResources().getColor(R.color.palette_blue));
                                currentPlayerView.setText("Player 2 turn");
                            }
                            gameInfos.setNextPlayer();
                        }
                        else{
                            TextView currentPlayerView = findViewById(R.id.currentPlayerView);
                            gameInfos.setNextPlayer();
                            String winner = null;
                            if (gameInfos.getCurrentPlayer().equals("X"))
                                winner = "Player 1";
                            else
                                winner = "Player 2";
                            currentPlayerView.setText(winner + " wins!");
                        }
                        isGameWon = gameInfos.isGameWon(textViews, size);
                    }
                });

            }
        }
    }
}
