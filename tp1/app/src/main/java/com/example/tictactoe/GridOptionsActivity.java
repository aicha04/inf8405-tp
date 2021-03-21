/** Represents the grid options view activity
 * @author Team GR01_03
 */
package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class GridOptionsActivity extends AppCompatActivity {
    private GameInfoSingleton gameInfos;
    private Drawable selectedBackground;
    private Drawable notSelectedBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_options_activity);

         gameInfos = GameInfoSingleton.getInstance();
         selectedBackground = this.getResources().getDrawable(R.drawable.rounded_corner_selected);
         notSelectedBackground = this.getResources().getDrawable(R.drawable.rounded_corner);


        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(GridOptionsActivity.this, gameActivity.class);
            startActivity(gameAct);
            finish();
        });
        setGridSelectionListeners();

    }

    /** Update gameInfos singleton when user choose a grid size
     * Change button background when user clicks
     * @author Team GR01_03
     */
    void setGridSelectionListeners() {
        Button threeButton = findViewById(R.id.three_by_three_button);
        Button fourButton = findViewById(R.id.four_by_four_button);
        Button fiveButton = findViewById(R.id.five_by_five_button);

        threeButton.setOnClickListener(v -> {
            this.gameInfos.setGridSize(GridSize.THREE);

            //Change background when users clicks
            threeButton.setBackground(selectedBackground);
            fourButton.setBackground(notSelectedBackground);
            fiveButton.setBackground(notSelectedBackground);
        });

        fourButton.setOnClickListener(v -> {
            this.gameInfos.setGridSize(GridSize.FOUR);

            //Change background when users clicks
            threeButton.setBackground(notSelectedBackground);
            fourButton.setBackground(selectedBackground);
            fiveButton.setBackground(notSelectedBackground);
        });

        fiveButton.setOnClickListener(v -> {
            this.gameInfos.setGridSize(GridSize.FIVE);

            //Change background when users clicks
            threeButton.setBackground(notSelectedBackground);
            fourButton.setBackground(notSelectedBackground);
            fiveButton.setBackground(selectedBackground);
        });
    }
}