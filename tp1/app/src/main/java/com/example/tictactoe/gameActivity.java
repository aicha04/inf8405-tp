package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

public class gameActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView[][] textViews;
    private GameInfoSingleton gameInfos;
    private boolean isGameWon = false;
    private Constants constants = new Constants();

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
        printGrid();
    }

    void setUpTextViews(int size) {
        textViews = new TextView[size][size];

        Typeface face = ResourcesCompat.getFont(this, R.font.baloo);
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
                if (textView.getText() != constants.EMPTY_GRID) {
                    displayToast("This position is already taken");

                } else {
                    textView.setText(gameInfos.getCurrentPlayer().getSign());
                    TextView currentPlayerView = findViewById(R.id.currentPlayerView);

                    if (gameInfos.getCurrentPlayer().getName().equals(constants.PLAYER1_NAME)) {
                        textView.setTextColor(getResources().getColor(R.color.palette_pink));
                    } else {
                        textView.setTextColor(getResources().getColor(R.color.palette_blue));
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
        System.out.println("Heree");
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
        if(winner.getName().equals(constants.PLAYER1_NAME)){
            TextView view = findViewById(R.id.player1Score);
            view.setText(constants.PLAYER1_NAME + ":" + Integer.toString(winner.getScore()));
        }else{
            TextView view = findViewById(R.id.player2Score);
            view.setText(constants.PLAYER2_NAME + Integer.toString(winner.getScore()));

        }
        currentPlayerView.setText(winner.getName() + " wins!");
    }

}


