package com.example.tictactoe;

public class GameInfoSingleton {

    public static final GameInfoSingleton instance = new GameInfoSingleton();
    private Constants constants = new Constants();
    private Player player1 = new Player(constants.XSIGN, constants.PLAYER1_NAME, 0);
    private Player player2 = new Player(constants.OSIGN, constants.PLAYER2_NAME, 0);
    private GridSize gridSize = GridSize.FOUR;
    private Player currentPlayer = player1 ;
    private String winner = null;

    private GameInfoSingleton() {
    }

    public void setWinner(String winnerSign) {
        if (winnerSign.equals(constants.XSIGN)) {
            winner = constants.PLAYER1_NAME;
            player1.incrementScore(50);
        }else{
            winner = constants.PLAYER2_NAME;
            player1.incrementScore(50);
        }
    }

    public String getWinner() {
        return winner;
    }

    public static GameInfoSingleton getInstance(){
        return instance;
    }

    public void setGridSize(GridSize size) {
        this.gridSize = size;
    }

    public int getGridSize() {
        return this.gridSize.getValue();
    }


    public void setNextPlayer() {
        if (this.currentPlayer.getName() == constants.PLAYER1_NAME) this.currentPlayer = player2;
        else this.currentPlayer = player1;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    Player getPlayerNameBySign(String sign){
        if (sign.equals(constants.XSIGN)) {
            return  player1;
        }else{
            return player2;
        }
    }

}