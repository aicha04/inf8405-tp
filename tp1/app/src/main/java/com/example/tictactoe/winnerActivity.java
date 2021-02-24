/** Represents the Winner view activity
 * @author Team GR01_03
 */
package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class winnerActivity extends AppCompatActivity {

    private Timer timer;
    private GameInfoSingleton gameInfos = GameInfoSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winner_activity);

        //Display depends on if it was a tie game(match null) or not
        if(gameInfos.wasTieGame()){
            TextView congratsTextView = (TextView) findViewById(R.id.congratsTextView);
            congratsTextView.setText("It\'s a tie game !");

            TextView winTextView = (TextView) findViewById(R.id.winTextView);
            winTextView.setText("");
        }else {
            TextView textView = (TextView) findViewById(R.id.congratsTextView);
            String winner = gameInfos.getWinner();
            textView.setText("Congrats " + winner + ",");
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(winnerActivity.this, EndActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}