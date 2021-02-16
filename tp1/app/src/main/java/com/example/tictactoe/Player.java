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

    public int  getScore() {
        return this.score;
    }
    public String  getSign() {
        return this.sign;
    }
    public String  getName() {
        return this.name;
    }

    public void  incrementScore() {
         this.score++;
    }
    public void setScore(int score){this.score = score;}
}
