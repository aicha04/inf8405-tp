package com.example.tp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AnalyticsActivity extends AppCompatActivity {
    private ImageButton backButton = null;
    private Constants constants = new Constants();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserSingleton.getInstance().getCurrentTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_Tp2);
        }else{
            setTheme(R.style.Theme_Tp2_dark);
        }
        setContentView(R.layout.activity_analytics);

        backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(onClickListener);


        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

    }
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickBack(v);
        }
    };
    public void onClickBack(final View view) {
        Intent i = new Intent(AnalyticsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}