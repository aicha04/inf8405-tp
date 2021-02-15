package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

public class gameActivity extends AppCompatActivity {
<<<<<<< HEAD
    GridLayout gridLayout;
    TextView[][] textViews;
    GameInfoSingleton gameInfos;
    boolean isGameWon;
=======
    private GridLayout gridLayout;
    private TextView[][] textViews;
    private GameInfoSingleton gameInfos;
    private String emptyGrid = "P";
    private static int GRID_PADDIND = 40;
    private static int GRID_TEXT_SIZE = 45;
>>>>>>> main


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        gameInfos = GameInfoSingleton.getInstance();
        setUpGrid();

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> resetGrid());

        Button menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> {
            finish();
            Intent mainAct = new Intent(gameActivity.this, MainActivity.class);
            startActivity(mainAct);
        });
    }

    void setUpTextViews(int size){
        textViews = new TextView[size][size];

        Typeface face = ResourcesCompat.getFont(this, R.font.baloo);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                textViews[i][j] = new TextView(gameActivity.this);
<<<<<<< HEAD
                textViews[i][j].setText("Q"); // TODO: Je mets Q pour suivre le nombre de O mit par le player 2
=======
                textViews[i][j].setText(emptyGrid);
>>>>>>> main
                textViews[i][j].setTypeface(face);
                textViews[i][j].setTextSize(GRID_TEXT_SIZE);
                textViews[i][j].setTextColor(getResources().getColor(R.color.white));
                textViews[i][j].setPadding(GRID_PADDIND, GRID_PADDIND, GRID_PADDIND, GRID_PADDIND);
                textViews[i][j].setGravity(Gravity.CENTER);
                textViews[i][j].setBackground(getResources().getDrawable(R.drawable.grid_border));
                gridLayout.addView(textViews[i][j]);

                setOnTextViewClickActions(textViews[i][j]);
            }
        }
<<<<<<< HEAD
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

=======
    }

    void setOnTextViewClickActions(TextView textView){

        textView.setOnClickListener(view -> {
            if (textView.getText() != emptyGrid) {
               displayToast("This position is already taken");
            } else {
                textView.setText(gameInfos.getCurrentPlayer());
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

    void setUpGrid() {
        int size = this.gameInfos.getGridSize();
        gridLayout = findViewById(R.id.grid);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);
        gridLayout.setColumnCount(size);
        gridLayout.setRowCount(size);
        gridLayout.setUseDefaultMargins(true);
        setUpTextViews(size);
    }

    void resetGrid(){
        for (int i=0; i< textViews.length; i++){
            for (int j=0; j < textViews.length; j++){
                textViews[i][j].setText(emptyGrid);
                textViews[i][j].setTextColor(getResources().getColor(R.color.white));
>>>>>>> main
            }
        }
    }

    void displayToast(String text){
        System.out.println("Heree");
        try {
            Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        }catch (Exception error){
            System.out.println(error);
        }
    }

}
