package com.example.tictactoe;

 /** Represents a singleton class the game information that are useful for many activities
 * @author Team GR01_03
 */
public class GameInfoSingleton {

    public static final GameInfoSingleton instance = new GameInfoSingleton();
    private Constants constants = new Constants();
    private Player player1 = new Player(constants.XSIGN, constants.PLAYER1_NAME, 0);
    private Player player2 = new Player(constants.OSIGN, constants.PLAYER2_NAME, 0);
    private GridSize gridSize = GridSize.FIVE;
    private Player currentPlayer = player1 ;
    private String winner = null;
    private boolean wasTieGame = false;

    private GameInfoSingleton() { }

    /**Get the instance of the singleton(GameInfos)
    * @param  -
    * @return  an object which represents the unique instance of this class
    */
     public static GameInfoSingleton getInstance(){
         return instance;
     }


    /** Updates winner score
    * @param  winnerSign: can be X or O
    * @return -
    */
    public void setWinner(String winnerSign) {
        if (winnerSign.equals(constants.XSIGN)) {
            winner = constants.PLAYER1_NAME;
            player1.incrementScore();
        }else{
            winner = constants.PLAYER2_NAME;
            player2.incrementScore();
        }
    }

    /** Get the winner
    * @param  -
    * @return  an object which represents the winner
    */
    public String getWinner() {
        return winner;
    }


    /** Set the current game grid size
    * @param -
    * @return -
    */
    public void setGridSize(GridSize size) {
        this.gridSize = size;
    }


    /** Get  the current game grid size
    * @param -
    * @return the grid size
    */
    public int getGridSize() {
        return this.gridSize.getValue();
    }


    /** Set the next player of the game
    * @param -
    * @return -
    */
    public void setNextPlayer() {
        if (this.currentPlayer.getName() == constants.PLAYER1_NAME) this.currentPlayer = player2;
        else this.currentPlayer = player1;
    }

    /** Get the current player
    * @param -
    * @return uan object that represents the current player
    */
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }


    /** Get a player name knowing this sign(X or O)
    * @param sign : the player sign(X or O)
    * @return an object that represents the current player
    */
    Player getPlayerNameBySign(String sign){
        if (sign.equals(constants.XSIGN)) {
            return  player1;
        }else{
            return player2;
        }
    }

    /** Set player 1 score
    * @param score : the new score
    * @return -
    */
    void setPlayer1Score(int score){
        player1.setScore(score);
    }

     /** Set player 2 score
      * @param score : the new score
      * @return -
      */
    void setPlayer2Score(int score){
        player2.setScore(score);
    }

    /** Check if the current game was a tie game(nobody won)
    * @param -
    * @return boolean : true if it was a tie game
    */
    boolean wasTieGame(){ return this.wasTieGame; }

    /** Set wasTieGame attribute
    * @param wasTieGame new value of wasTieGame
    * @return -
    */
    void setTieGame(boolean wasTieGame){ this.wasTieGame = wasTieGame; }


     /** Get player 2 score
      * @return player 2 score
      */
     int  getPlayer2Score(){
         return player2.getScore();
     }

     /** Get player 1 score
      * @return player 1 score
      */
     int  getPlayer1Score(){
         return player1.getScore();
     }

 }