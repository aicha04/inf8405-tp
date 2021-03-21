/** Represents the GameEnd view activity
 * @author Team GR01_03
 */

package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, android.R.anim.fade_out);
        setContentView(R.layout.end_activity);

        Button newGameButton = findViewById(R.id.newGameButton);
        Button restartButton = findViewById(R.id.restartButton);

        newGameButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(EndActivity.this, GridOptionsActivity.class);
            startActivity(gameAct);
            finish();
        });

        restartButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(EndActivity.this, gameActivity.class);
            startActivity(gameAct);
            finish();
        });

    }


}