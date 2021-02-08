package com.example.tictactoe;

public enum GridSize{
        THREE(3),
        FOUR(4),
        FIVE(5);

        private int value;
        GridSize(int value) {
                this.value = value;
        }

        public int getValue() {
                return this.value;
        }
}
