/** Represents a player informations
 * @author Team GR01_03
 */

package com.example.tictactoe;

public class Player {
    private String sign;
    private String name;
    private int score;

     public Player(String sign, String name, int score){
       this.score = score;
       this.name = name;
       this.sign = sign;
   }

    /**Get the player score
    * @param  -
    * @return the player score
    */
    public int  getScore() {
        return this.score;
    }

    /**Get the player sign(X or O)
    * @param  -
    * @return the player sign
    */
    public String  getSign() {
        return this.sign;
    }

    /**Get the player name
     * @param  -
     * @return the player name
     */
    public String  getName() {
        return this.name;
    }

    /**Increment the player score
     * @param -
     * @return -
     */
    public void  incrementScore() {
         this.score++;
    }


    /**Set the player score
    * @param  score new score
    * @return -
    */
    public void setScore(int score){this.score = score;}
}
