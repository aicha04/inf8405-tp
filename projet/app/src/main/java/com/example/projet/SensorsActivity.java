package com.example.projet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.databinding.ActivitySensorsBinding;

import java.text.DecimalFormat;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "SensorsActivity";
    private ActivitySensorsBinding binding;
    private Constants constants = new Constants();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Sensor linearAcc;
    private Sensor stepCounter;
    private Sensor stepDetector;
    private Sensor magnetometer;
    private final static String NOT_SUPPORTED_MESSAGE = "Sorry, sensors are unavailable on this device.";
    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];
    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
    private int stepsTaken = 0;
    private int reportedSteps = 0;
    private int stepsDetected = 0;
    private ImageView compassImage;
    private float DegreeStart = 0f;
    TextView DegreeTV, StepCounterTV;
    private static DecimalFormat df = new DecimalFormat("0.00");
    ImageButton playPauseButton;
    ImageButton restartButton;
    boolean isPlay = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restartButton = (ImageButton) findViewById(R.id.restart_button);


        if(UserSingleton.getInstance().getCurrentUserTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        binding = ActivitySensorsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.backButton.setOnClickListener(onClickListener);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        linearAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (accelerometer == null || gravity == null || linearAcc == null || stepCounter == null || magnetometer == null) {
            System.out.println(NOT_SUPPORTED_MESSAGE);
            Toast t = Toast.makeText(getApplicationContext(), NOT_SUPPORTED_MESSAGE, Toast.LENGTH_SHORT);
            t.show();
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickBack(v);
        }
    };
    public void onClickBack(final View view) {
        Intent i = new Intent(SensorsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null && gravity != null && linearAcc != null && stepCounter != null || magnetometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (accelerometer != null && gravity != null && linearAcc != null && stepCounter != null || magnetometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager.unregisterListener(this, gravity);
            sensorManager.unregisterListener(this, linearAcc);
            sensorManager.unregisterListener(this, stepCounter);
            sensorManager.unregisterListener(this, magnetometer);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accelerometer != null && gravity != null && linearAcc != null && stepCounter != null || magnetometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager.unregisterListener(this, gravity);
            sensorManager.unregisterListener(this, linearAcc);
            sensorManager.unregisterListener(this, stepCounter);
            sensorManager.unregisterListener(this, magnetometer);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (accelerometer != null && gravity != null && linearAcc != null && stepCounter != null || magnetometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager.unregisterListener(this, gravity);
            sensorManager.unregisterListener(this, linearAcc);
            sensorManager.unregisterListener(this, stepCounter);
            sensorManager.unregisterListener(this, magnetometer);
        }
    }

    public void onSensorChanged (SensorEvent sensorEvent){
        compassImage = (ImageView) findViewById(R.id.compass_image);
        DegreeTV = (TextView) findViewById(R.id.DegreeTV);
        StepCounterTV = (TextView) findViewById(R.id.step_count);
        playPauseButton = (ImageButton) findViewById(R.id.play_button);

        View.OnClickListener restartButton = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                reportedSteps = 0;
                stepsDetected = 0;
                stepsTaken = 0;
                System.out.println("Number of steps: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + stepsTaken);
                StepCounterTV.setText(String.valueOf(stepsTaken));
            }
        };
        playPauseButton.setOnClickListener(restartButton);

        View.OnClickListener togglePlayButton = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isPlay){
                    v.setBackgroundResource(R.drawable.pause_black);
                }else{
                    v.setBackgroundResource(R.drawable.play_black);
                }
                isPlay = !isPlay; // reverse
            }
        };
        playPauseButton.setOnClickListener(togglePlayButton);


        if (sensorEvent.sensor == accelerometer) { // https://www.techrepublic.com/article/pro-tip-create-your-own-magnetic-compass-using-androids-internal-sensors/
            System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length); // https://www.codespeedy.com/simple-compass-code-with-android-studio/
            mLastAccelerometerSet = true;
        } else if (sensorEvent.sensor == magnetometer) {
            System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
            mLastMagnetometerSet = true;
        }
        else if(isPlay) {
            if (sensorEvent.sensor == stepCounter) { // https://gist.github.com/dyadica/93438442857b1a93f19e
                if (reportedSteps < 1) {
                    // Log the initial value
                    reportedSteps = (int) sensorEvent.values[0];
                }
                stepsTaken = (int) sensorEvent.values[0] - reportedSteps;
                System.out.println("Number of steps: " + stepsTaken);
                StepCounterTV.setText(String.valueOf(stepsTaken));
            } else if (sensorEvent.sensor == stepDetector) {
                stepsDetected++;
                System.out.println("Steps detected: " + stepsDetected);
            }
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            System.out.println("Heading: " + df.format(azimuthInDegress) + " degrees");
            DegreeTV.setText("Heading: " + df.format(azimuthInDegress) + " degrees");
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            compassImage.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }

    public void onAccuracyChanged (Sensor sensor,int i){
        // Auto-generated
    }
}
