package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class winnerActivity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winner_activity);

        TextView textView = (TextView) findViewById(R.id.congratsTextView);
        String winner = GameInfoSingleton.getInstance().getWinner();
        textView.setText("Congrats " + winner + ",");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(winnerActivity.this, EndActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);

    }
}