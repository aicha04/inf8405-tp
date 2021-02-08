package com.example.tictactoe;

public enum Player {
    player1("X"),
    player2("O");

    private String value;
    Player(String value) {
        this.value = value;
    }

    public String  getValue() {
        return this.value;
    }
}
