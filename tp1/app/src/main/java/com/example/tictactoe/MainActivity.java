/** Represents the main wiew activity
 * @author Team GR01_03
 */
package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Button playButton = findViewById(R.id.play_button);
        Button aboutButton = findViewById(R.id.about_button);
        Button exitButton = findViewById(R.id.exit_button);

        playButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(MainActivity.this, GridOptionsActivity.class);
            startActivity(gameAct);
            finish();
        });

        aboutButton.setOnClickListener(v -> {
            Intent gameAct = new Intent(MainActivity.this, aboutActivity.class);
            startActivity(gameAct);
            finish();
        });

        exitButton.setOnClickListener(v ->{
            System.exit(0);
            finish();
        } );
    }
}