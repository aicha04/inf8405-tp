package com.example.tictactoe;

public class GameInfoSingleton {

    public static final GameInfoSingleton instance = new GameInfoSingleton();
    private GridSize gridSize = GridSize.FIVE;
    private Player currentPlayer = Player.player1 ;
    public static GameInfoSingleton getInstance(){
        return instance;
    }

    private GameInfoSingleton(){
    }

    public void setGridSize(GridSize size){
        this.gridSize = size;
    }

    public int getGridSize(){
        return this.gridSize.getValue();
    }


    public void setNextPlayer(){
        if(this.currentPlayer == Player.player1) this.currentPlayer = Player.player2;
        else this.currentPlayer = Player.player1;
    }

    public String getCurrentPlayer(){
        return this.currentPlayer.getValue();
    }



}
