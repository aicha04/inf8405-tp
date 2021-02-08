package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

public class gameActivity extends AppCompatActivity {
    GridLayout gridLayout;
    TextView[] textViews;
    GameInfoSingleton gameInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        gameInfos = GameInfoSingleton.getInstance();
        setUpGrid();
    }
    void setUpGrid(){
        int size = this.gameInfos.getGridSize();

        gridLayout  = findViewById(R.id.grid);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);

        gridLayout.setColumnCount(size);
        gridLayout.setRowCount(size);

        textViews  = new TextView[size*size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++) {
                int pos = i*size + j;
                textViews[pos] = new TextView(gameActivity.this);
                textViews[pos].setText("[" + i + "]" +"[" + j + "]" );
                textViews[pos].setTextSize(35);
                textViews[pos].set
                textViews[pos].setTextColor(getResources().getColor(R.color.palette_pink));
                textViews[pos].setPadding(40, 40, 40, 40);
                textViews[pos].setGravity(Gravity.CENTER);
                gridLayout.addView(textViews[pos]);

            }
        }

        for (int i=0; i < textViews.length; i++){
            TextView textView =  textViews[i];
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView.setText(gameInfos.getCurrentPlayer());
                    TextView  currentPlayerView = findViewById(R.id.currentPlayerView);
                    String currentPlayer = "";
                    if(gameInfos.getCurrentPlayer() == Player.player1.getValue()) {
                        textView.setTextColor(getResources().getColor(R.color.palette_pink));
                        currentPlayer = "Player 1 turn";
                    }
                    else {
                        textView.setTextColor(getResources().getColor(R.color.palette_blue));
                        currentPlayer ="Player 2 turn";
                    }
                    currentPlayerView.setText(currentPlayer);
                    gameInfos.setNextPlayer();
                }
            });
        }
    }
}
