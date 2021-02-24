
/** Represents an enum for all grid size
 * @author Team GR01_03
 */

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
