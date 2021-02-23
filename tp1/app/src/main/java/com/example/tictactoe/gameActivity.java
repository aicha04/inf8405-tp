package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.media.MediaPlayer;

import java.io.File;

public class gameActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private GridLayout gridLayout;
    private TextView[][] textViews;
    private GameInfoSingleton gameInfos;
    private boolean isGameWon = false;
    private Constants constants = new Constants();
    private MediaPlayer player1_sound;
    private MediaPlayer player2_sound;

//    @param savedInstanceState: a copy of the
//    @param  name the location of the image, relative to the url argument
//    @return the image at the specified URL
//    @see Image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        gameInfos = GameInfoSingleton.getInstance();
        setUpGrid();

        File file = new File(constants.sharedPreferencesPath);
        if(file.exists()){
            sharedPreferences = getSharedPreferences(constants.sharedPreferencesName, Context.MODE_PRIVATE);
            if(sharedPreferences.contains(constants.sharedPlayer1Score)){
                TextView view = findViewById(R.id.player1Score);
                int player1Score = sharedPreferences.getInt(constants.sharedPlayer1Score, 0);
                view.setText(constants.PLAYER1_NAME + ": " + Integer.toString(player1Score));
                gameInfos.setPlayer1Score(player1Score);
            }
            if(sharedPreferences.contains(constants.sharedPlayer2Score)){
                TextView view = findViewById(R.id.player2Score);
                int player2Score = sharedPreferences.getInt(constants.sharedPlayer2Score, 0);
                view.setText(constants.PLAYER2_NAME + ": " + Integer.toString(player2Score));
                gameInfos.setPlayer2Score(player2Score);
            }
        }else{
            sharedPreferences = getApplicationContext().getSharedPreferences(constants.sharedPreferencesName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int score = 0;
            editor.putInt(constants.sharedPlayer1Score, score);
            editor.putInt(constants.sharedPlayer2Score, score);
            editor.commit();
        }


        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            resetScore();
            resetGrid();
        });

        Button menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> {
            finish();
            Intent mainAct = new Intent(gameActivity.this, MainActivity.class);
            startActivity(mainAct);
        });

         player1_sound = MediaPlayer.create(this, R.raw.player1_sound);
         player2_sound = MediaPlayer.create(this, R.raw.player2_sound);

    }

    void setUpTextViews(int size) {
        textViews = new TextView[size][size];

        Typeface face = ResourcesCompat.getFont(this, R.font.archivo);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                textViews[i][j] = new TextView(gameActivity.this);
                textViews[i][j].setText(constants.EMPTY_GRID);
                textViews[i][j].setTypeface(face);
                textViews[i][j].setTextSize(constants.GRID_TEXT_SIZE);
                textViews[i][j].setTextColor(getResources().getColor(R.color.white));
                textViews[i][j].setPadding(constants.GRID_PADDIND, constants.GRID_PADDIND, constants.GRID_PADDIND, constants.GRID_PADDIND);
                textViews[i][j].setGravity(Gravity.CENTER);
                textViews[i][j].setBackground(getResources().getDrawable(R.drawable.grid_border));
                gridLayout.addView(textViews[i][j]);
                setOnTextViewClickActions(textViews[i][j]);
            }
        }

    }

    void setOnTextViewClickActions(TextView textView) {
        textView.setOnClickListener(view -> {
            if(!isGameWon) {
                if (!textView.getText().toString().equals(constants.EMPTY_GRID)) {
                    displayToast(constants.POSITION_TAKEN_TOAST);

                } else {
                    playSound();
                    textView.setText(gameInfos.getCurrentPlayer().getSign());
                    TextView currentPlayerView = findViewById(R.id.currentPlayerView);

                    if (gameInfos.getCurrentPlayer().getName().equals(constants.PLAYER1_NAME)) {
                        textView.setTextColor(getResources().getColor(R.color.palette_pink));


                    } else {
                        textView.setTextColor(getResources().getColor(R.color.palette_blue));
                        textView.setTextSize(constants.GRID_TEXT_SIZE-3);
                    }
                    isGameWon = wonVertical() || wonHorizontal() || wonDiagonal();
                    if (!isGameWon) {
                        gameInfos.setNextPlayer();
                        currentPlayerView.setText(gameInfos.getCurrentPlayer().getName() + " turn");
                    }
                }
            }
        });
    }

    void playSound() {
        String PlayerName = gameInfos.getCurrentPlayer().getName();
        if (player1_sound.isPlaying()) {
            player1_sound.reset();
        }
        if (player2_sound.isPlaying()) {
            player2_sound.reset();
        }
        if(PlayerName.equals(constants.PLAYER1_NAME)){
            player1_sound.start();
        }else{
            player2_sound.start();
        }
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

    void resetGrid() {
        int size = gameInfos.getGridSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                textViews[i][j].setText(constants.EMPTY_GRID);
                textViews[i][j].setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    void printGrid() {
        int size = gameInfos.getGridSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(textViews[i][j].getText());
            }
        }

    }

    void displayToast(String text) {
        try {
            Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception error) {
            System.out.println(error);
        }
    }

    boolean wonHorizontal(){
        int size = gameInfos.getGridSize();
        for(int i = 0; i < size; i++){
            boolean isEqual = true;
            String firstSign = textViews[i][0].getText().toString();
            int j = 1;
            if(!firstSign.equals(constants.EMPTY_GRID)) {
                while (isEqual && j < size){
                    String currentPos = textViews[i][j].getText().toString();
                    if (!currentPos.equals(firstSign)) {
                        isEqual = false;
                        break;
                    }
                    j++;
                }
                if(isEqual) {
                    setWinner(firstSign);
                    return true;
                }
            }
        }
        return false;
    }
    boolean wonVertical() {
        int size = gameInfos.getGridSize();
        for (int j = 0; j < size; j++) {
            boolean isEqual = true;
            String firstSign = textViews[0][j].getText().toString();
            int i = 1;
            if (!firstSign.equals(constants.EMPTY_GRID)) {
                while (isEqual && i < size) {
                    String currentPos = textViews[i][j].getText().toString();
                    if (!currentPos.equals(firstSign)) {
                        isEqual = false;
                        break;
                    }
                    i++;
                }
                if(isEqual) {
                    setWinner(firstSign);
                    return true;
                }
            }
        }
        return false;
    }

    boolean wonDiagonal(){
        boolean firstDiagonal = true;
        int size = gameInfos.getGridSize();
        String firstSign = textViews[0][0].getText().toString();
        if (!firstSign.equals(constants.EMPTY_GRID)) {
            for(int i = 1; i < size; i++) {
                String currentPos = textViews[i][i].getText().toString();
                if (!currentPos.equals(firstSign)) {
                    firstDiagonal = false;
                    break;
                }
            }
            if(firstDiagonal) {
                setWinner(firstSign);
                return true;
            }
        }

        boolean secondDiagonal = true;
        int i = 0;
        int j = size - 1;
        firstSign = textViews[i][j].getText().toString();
        if (!firstSign.equals(constants.EMPTY_GRID)) {
            while (j >= 0) {
                String currentPos = textViews[i][j].getText().toString();
                if (!currentPos.equals(firstSign)) {
                    secondDiagonal = false;
                    break;
                }
                i++;
                j--;
            }
            if(secondDiagonal){
                setWinner(firstSign);
                return true;
            }
        }
        return false;
    }

    public void setWinner(String winnerSign){
        gameInfos.setWinner(winnerSign);
        TextView currentPlayerView = findViewById(R.id.currentPlayerView);
        Player winner = gameInfos.getPlayerNameBySign(winnerSign);
        int winnerScore = winner.getScore();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(winner.getName().equals(constants.PLAYER1_NAME)){
            TextView view = findViewById(R.id.player1Score);
            view.setText(constants.PLAYER1_NAME + ": " + Integer.toString(winnerScore));
            editor.putInt(constants.sharedPlayer1Score,winnerScore);
            editor.commit();
        }else{
            TextView view = findViewById(R.id.player2Score);
            view.setText(constants.PLAYER2_NAME + ": " + Integer.toString(winnerScore));
            editor.putInt(constants.sharedPlayer2Score,winnerScore);
            editor.commit();

        }
        //currentPlayerView.setText(winner.getName() + " wins!");
        Intent intent = new Intent(gameActivity.this, winnerActivity.class);
        startActivity(intent);
    }

    void resetScore(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int score = 0;

        editor.putInt(constants.sharedPlayer1Score, score);
        editor.putInt(constants.sharedPlayer2Score, score);
        editor.commit();

        TextView view = findViewById(R.id.player1Score);
        view.setText(constants.PLAYER1_NAME + ": " + Integer.toString(score));

        TextView view1 = findViewById(R.id.player2Score);
        view1.setText(constants.PLAYER2_NAME + ": " + Integer.toString(score));
    }
}


