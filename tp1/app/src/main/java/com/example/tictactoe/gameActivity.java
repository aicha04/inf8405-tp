package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
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

        System.out.println( "Size" +size);
        gridLayout.setColumnCount(size);
        gridLayout.setRowCount(size);

        textViews  = new TextView[size*size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++) {
                int pos = i*size + j;
                textViews[pos] = new TextView(gameActivity.this);
                textViews[pos].setText("[" +i + "]" +"[" + j + "]" );
                textViews[pos].setTextSize(25);
                textViews[pos].setTextColor(getResources().getColor(R.color.palette_pink));
                textViews[pos].setPadding(50, 25, 10, 25);
                gridLayout.addView(textViews[pos]);
            }
        }
        //setContentView(gridLayout);

        for (int i=0; i < textViews.length; i++){
            TextView textView =  textViews[i];
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView.setText(gameInfos.getCurrentPlayer());
                    gameInfos.setNextPlayer();
                }
            });
        }
    }
}
