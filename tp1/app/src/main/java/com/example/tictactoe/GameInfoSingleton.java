package com.example.tictactoe;

import android.widget.TextView;

public class GameInfoSingleton {

    public static final GameInfoSingleton instance = new GameInfoSingleton();

    private GridSize gridSize = GridSize.THREE;
    private Player currentPlayer = Player.player1 ;
    private String winner = null;

    public void setWinner(String player) {
        if (winner == null) {
            winner = player;
        }
    }

    public String getWinner() {
        return winner;
    }

    public static GameInfoSingleton getInstance(){

        return instance;
    }

    private GameInfoSingleton() {
    }

    public void setGridSize(GridSize size) {
        this.gridSize = size;
    }

    public int getGridSize() {
        return this.gridSize.getValue();
    }


    public void setNextPlayer() {
        if (this.currentPlayer == Player.player1) this.currentPlayer = Player.player2;
        else this.currentPlayer = Player.player1;
    }

    public String getCurrentPlayer() {
        return this.currentPlayer.getValue();
    }

    public boolean isGameWon(TextView[][] textViews, int size) {
        int nbWinningMoves;
        if (size == 3) {
            nbWinningMoves = 8;
            String[] winningMoves = new String[nbWinningMoves];
            winningMoves[0] = textViews[0][0].getText().toString() + textViews[0][1].getText().toString() + textViews[0][2].getText().toString();
            winningMoves[1] = textViews[1][0].getText().toString() + textViews[1][1].getText().toString() + textViews[1][2].getText().toString();
            winningMoves[2] = textViews[2][0].getText().toString() + textViews[2][1].getText().toString() + textViews[2][2].getText().toString();
            winningMoves[3] = textViews[0][0].getText().toString() + textViews[1][0].getText().toString() + textViews[2][0].getText().toString();
            winningMoves[4] = textViews[0][1].getText().toString() + textViews[1][1].getText().toString() + textViews[2][1].getText().toString();
            winningMoves[5] = textViews[0][2].getText().toString() + textViews[1][2].getText().toString() + textViews[2][2].getText().toString();
            winningMoves[6] = textViews[0][0].getText().toString() + textViews[1][1].getText().toString() + textViews[2][2].getText().toString();
            winningMoves[7] = textViews[2][0].getText().toString() + textViews[1][1].getText().toString() + textViews[0][2].getText().toString();
            for (int i = 0; i < nbWinningMoves; i++) {
                if (winningMoves[i].equals("XXX") || winningMoves[i].equals("OOO"))
                    return true;
            }
        } else if (size == 4) {
            nbWinningMoves = 10;
            String[] winningMoves = new String[nbWinningMoves];
            winningMoves[0] = textViews[0][0].getText().toString() + textViews[0][1].getText().toString() + textViews[0][2].getText().toString() + textViews[0][3].getText().toString();
            winningMoves[1] = textViews[1][0].getText().toString() + textViews[1][1].getText().toString() + textViews[1][2].getText().toString() + textViews[1][3].getText().toString();
            winningMoves[2] = textViews[2][0].getText().toString() + textViews[2][1].getText().toString() + textViews[2][2].getText().toString() + textViews[2][3].getText().toString();
            winningMoves[3] = textViews[3][0].getText().toString() + textViews[3][1].getText().toString() + textViews[3][2].getText().toString() + textViews[3][3].getText().toString();
            winningMoves[4] = textViews[0][0].getText().toString() + textViews[1][0].getText().toString() + textViews[2][0].getText().toString() + textViews[3][0].getText().toString();
            winningMoves[5] = textViews[0][1].getText().toString() + textViews[1][1].getText().toString() + textViews[2][1].getText().toString() + textViews[3][1].getText().toString();
            winningMoves[6] = textViews[0][2].getText().toString() + textViews[1][2].getText().toString() + textViews[2][2].getText().toString() + textViews[3][2].getText().toString();
            winningMoves[7] = textViews[0][3].getText().toString() + textViews[1][3].getText().toString() + textViews[2][3].getText().toString() + textViews[3][3].getText().toString();
            winningMoves[8] = textViews[0][0].getText().toString() + textViews[1][1].getText().toString() + textViews[2][2].getText().toString() + textViews[3][3].getText().toString();
            winningMoves[9] = textViews[3][0].getText().toString() + textViews[2][1].getText().toString() + textViews[1][2].getText().toString() + textViews[0][3].getText().toString();
            for (int i = 0; i < nbWinningMoves; i++) {
                if (winningMoves[i].equals("XXXX") || winningMoves[i].equals("OOOO"))
                    return true;
            }
        } else {
            nbWinningMoves = 12;
            String[] winningMoves = new String[nbWinningMoves];
            winningMoves[0] = textViews[0][0].getText().toString() + textViews[0][1].getText().toString() + textViews[0][2].getText().toString() + textViews[0][3].getText().toString() + textViews[0][4].getText().toString();
            winningMoves[1] = textViews[1][0].getText().toString() + textViews[1][1].getText().toString() + textViews[1][2].getText().toString() + textViews[1][3].getText().toString() + textViews[1][4].getText().toString();
            winningMoves[2] = textViews[2][0].getText().toString() + textViews[2][1].getText().toString() + textViews[2][2].getText().toString() + textViews[2][3].getText().toString() + textViews[2][4].getText().toString();
            winningMoves[3] = textViews[3][0].getText().toString() + textViews[3][1].getText().toString() + textViews[3][2].getText().toString() + textViews[3][3].getText().toString() + textViews[3][4].getText().toString();
            winningMoves[4] = textViews[4][0].getText().toString() + textViews[4][1].getText().toString() + textViews[4][2].getText().toString() + textViews[4][3].getText().toString() + textViews[4][4].getText().toString();
            winningMoves[5] = textViews[0][0].getText().toString() + textViews[1][0].getText().toString() + textViews[2][0].getText().toString() + textViews[3][0].getText().toString() + textViews[4][4].getText().toString();
            winningMoves[6] = textViews[0][1].getText().toString() + textViews[1][1].getText().toString() + textViews[2][1].getText().toString() + textViews[3][1].getText().toString() + textViews[4][1].getText().toString();
            winningMoves[7] = textViews[0][2].getText().toString() + textViews[1][2].getText().toString() + textViews[2][2].getText().toString() + textViews[3][2].getText().toString() + textViews[4][2].getText().toString();
            winningMoves[8] = textViews[0][3].getText().toString() + textViews[1][3].getText().toString() + textViews[2][3].getText().toString() + textViews[3][3].getText().toString() + textViews[4][3].getText().toString();
            winningMoves[9] = textViews[0][4].getText().toString() + textViews[1][4].getText().toString() + textViews[2][4].getText().toString() + textViews[3][4].getText().toString() + textViews[4][4].getText().toString();
            winningMoves[10] = textViews[0][0].getText().toString() + textViews[1][1].getText().toString() + textViews[2][2].getText().toString() + textViews[3][3].getText().toString() + textViews[4][4].getText().toString();
            winningMoves[11] = textViews[4][0].getText().toString() + textViews[3][1].getText().toString() + textViews[2][2].getText().toString() + textViews[1][3].getText().toString() + textViews[0][4].getText().toString();
            for (int i = 0; i < nbWinningMoves; i++) {
                if (winningMoves[i].equals("XXXXX") || winningMoves[i].equals("OOOOO"))
                    return true;
            }
        }
        return false;
    }
}