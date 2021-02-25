/** Represents the main function of the Game activity
 * @author Team GR01_03
 */

package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
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
    private int nTakenBox = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        gameInfos = GameInfoSingleton.getInstance();
        gameInfos.setTieGame(false);

        //Display the grid programmatically and set up styles
        setUpGrid();

        //Set up shared preferences database to store and retrieve the players scores
        setUpPreferences();

        // Display PlayesScores
        displayScores();

        //Set up click listeners for menu and reset buttons
        setUpButtons();

         player1_sound = MediaPlayer.create(this, R.raw.player1_sound);
         player2_sound = MediaPlayer.create(this, R.raw.player2_sound);

    }

    /** Display the grid programmatically
     * @param -
     * @return -
     */
    void setUpGrid() {
        int size = this.gameInfos.getGridSize();
        gridLayout = findViewById(R.id.grid);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);
        gridLayout.setColumnCount(size);
        gridLayout.setRowCount(size);
        gridLayout.setUseDefaultMargins(true);
        setUpTextViews(size);
    }

    /** Retrieve players previous scores through sharedpreferences and
     *  update the gameInfos singleton with the values
     *  Create a shared preferences database for players scores for the first use of the application
     * @param -
     * @return -
     */
    void setUpPreferences(){
        File file = new File(constants.SHAREDPREFERENCESPATH);
        if(file.exists()){
            sharedPreferences = getSharedPreferences(constants.SHAREDPREFERENCESNAME,
                    gameActivity.this.MODE_PRIVATE);
            if(sharedPreferences.contains(constants.SHAREDPLAYER1SCORE)){
                int player1Score = sharedPreferences.getInt(constants.SHAREDPLAYER1SCORE, 0);
                gameInfos.setPlayer1Score(player1Score);
            }
            if(sharedPreferences.contains(constants.SHAREDPLAYER2SCORE)){
                int player2Score = sharedPreferences.getInt(constants.SHAREDPLAYER2SCORE, 0);
                gameInfos.setPlayer2Score(player2Score);
            }
        }else{
            sharedPreferences = getApplicationContext().getSharedPreferences(
                    constants.SHAREDPREFERENCESNAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int score = 0;
            editor.putInt(constants.SHAREDPLAYER1SCORE, score);
            editor.putInt(constants.SHAREDPLAYER2SCORE, score);
            editor.commit();
        }
    }


    /** Display the scores in the views provied for this purpose
     * @param -
     * @return -
     */
    void displayScores(){
        TextView player1View = findViewById(R.id.player1Score);
        player1View.setText(constants.PLAYER1_NAME + ": " + Integer.toString(gameInfos.getPlayer1Score()));
        TextView player2View = findViewById(R.id.player2Score);
        player2View.setText(constants.PLAYER2_NAME + ": " + Integer.toString(gameInfos.getPlayer2Score()));
    }

    /** Set click listeners actions on menu button and reset button
     * @param -
     * @return -
     */
    void setUpButtons(){
        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            resetScore();
            resetGrid();
            resetMedia();
        });

        Button menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> {
            Intent mainAct = new Intent(gameActivity.this, MainActivity.class);
            startActivity(mainAct);
            finish();
        });

    }

    /** Set up programmatically the text views where signs X or O are displayed during the game
     *This function set the text views size and on click listeners actions
     *  @param size the grid size
     * @return -
     */
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

    /** Set on click listeners actions for each box of the grid
     * Set the sound that should be played when the box is clicked
     * Display a sign X or O when the box is clicked
     * Check if any player won the game when the box is clicked
     *  @param textView an objects that represents the text view( a box of the grid)
     * @return -
     */
    void setOnTextViewClickActions(TextView textView) {
        textView.setOnClickListener(view -> {
            startMedia();
            if(!isGameWon) {
                Player currentPlayer = gameInfos.getCurrentPlayer();
                if (!textView.getText().toString().equals(constants.EMPTY_GRID)) {
                    displayToast(constants.POSITION_TAKEN_TOAST);
                } else {
                    playMedia();
                    nTakenBox +=1;
                    textView.setText(currentPlayer.getSign());
                    TextView currentPlayerView = findViewById(R.id.currentPlayerView);

                    if (currentPlayer.getName().equals(constants.PLAYER1_NAME)) {
                        textView.setTextColor(getResources().getColor(R.color.palette_pink));

                    } else {
                        textView.setTextColor(getResources().getColor(R.color.palette_blue));
                        textView.setTextSize(constants.GRID_TEXT_SIZE-3);
                    }
                    isGameWon = wonVertical() || wonHorizontal() || wonFirstDiagonal() || wonSecondDiagonal();
                    if (!isGameWon) {
                        if (isGridFull()){
                            gameInfos.setTieGame(true);
                            Intent intent = new Intent(gameActivity.this, winnerActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            gameInfos.setNextPlayer();
                            currentPlayerView.setText(currentPlayer.getName() + " turn");
                        }
                    }
                }
            }
        });
    }

    void startMedia() {
        if (player1_sound == null || player1_sound == null) {
            player1_sound = MediaPlayer.create(this, R.raw.player1_sound);
            player2_sound = MediaPlayer.create(this, R.raw.player2_sound);
        }
    }
    void playMedia() {
        try {
            if (gameInfos.getCurrentPlayer().getName().equals(constants.PLAYER1_NAME))
                player1_sound.start();
            else
                player2_sound.start();
        }
        catch (Error error){
            throw error;
        }
    }

    void resetMedia() {
        if (player1_sound != null) {
            player1_sound.stop();
            player1_sound.reset();
            player1_sound.release();
            player1_sound = null;
            System.out.println(("player1_sound = null"));
        }
        if (player2_sound != null) {
            player2_sound.stop();
            player2_sound.reset();
            player2_sound.release();
            player2_sound = null;
            System.out.println(("player2_sound = null"));
        }
    }


    /** Reset the grid
     * Update the view
     *  @param -
     * @return -
     */
    void resetGrid() {
        nTakenBox =0;
        int size = gameInfos.getGridSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                textViews[i][j].setText(constants.EMPTY_GRID);
                textViews[i][j].setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    /** Check if the grid is full
     *  @param -
     * @return true if the grid is full
     */
    boolean isGridFull(){
        int size = this.gameInfos.getGridSize();
        return nTakenBox == size*size;

    }

    /** Display a toast
     *  @param text, message that should be display in the toast
     * @return true if the grid is full
     */
    void displayToast(String text) {
        try {
            Toast.makeText(gameActivity.this, text, Toast.LENGTH_SHORT).show();
        } catch (Exception error) {
            System.out.println(error);
        }
    }

    /** Check is user won by any horizontal possibilities
     *  @param -
     * @return true any players won
     */
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

    /** Check is user won by any vertical possibilities
     *  @param -
     * @return true any players won
     */
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

    /** Check is user won by the first diagonal possibility
     *  @param -
     * @return true any players won
     */
    boolean wonFirstDiagonal(){
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
        return false;
    }

    /** Check is user won by the second diagonal possibility
     *  @param -
     * @return true any players won
     */
    boolean wonSecondDiagonal(){
        int size = gameInfos.getGridSize();
        boolean secondDiagonal = true;
        int i = 0;
        int j = size - 1;
        String firstSign = textViews[i][j].getText().toString();
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

    /** Set the winner
     * Update the gameInfos and the shared preferences singleton by setting the new winner and his score
     * Update the view to display the winner
     * @return true any players won
     */
    public void setWinner(String winnerSign){
        resetMedia();
        gameInfos.setTieGame(false);
        gameInfos.setWinner(winnerSign);

        Player winner = gameInfos.getPlayerNameBySign(winnerSign);
        int winnerScore = winner.getScore();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(winner.getName().equals(constants.PLAYER1_NAME)){
            TextView view = findViewById(R.id.player1Score);
            view.setText(constants.PLAYER1_NAME + ": " + Integer.toString(winnerScore));
            editor.putInt(constants.SHAREDPLAYER1SCORE, winnerScore);
            editor.commit();
        }else{
            TextView view = findViewById(R.id.player2Score);
            view.setText(constants.PLAYER2_NAME + ": " + Integer.toString(winnerScore));
            editor.putInt(constants.SHAREDPLAYER2SCORE, winnerScore);
            editor.commit();
        }

        Intent intent = new Intent(gameActivity.this, winnerActivity.class);
        startActivity(intent);
        finish();
    }

    /** Reset the score
     * Set players scores to 0 in the shared preferences database
     *  @param -
     * @return -
     */
    void resetScore(){
        int score = 0;
        gameInfos.setPlayer1Score(score);
        gameInfos.setPlayer2Score(score);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(constants.SHAREDPLAYER1SCORE, score);
        editor.putInt(constants.SHAREDPLAYER2SCORE, score);
        editor.commit();

        TextView view = findViewById(R.id.player1Score);
        view.setText(constants.PLAYER1_NAME + ": " + Integer.toString(score));

        TextView view1 = findViewById(R.id.player2Score);
        view1.setText(constants.PLAYER2_NAME + ": " + Integer.toString(score));
    }
}


