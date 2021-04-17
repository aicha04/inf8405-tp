package com.example.projet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projet.databinding.ActivitySensorsBinding;
import java.text.DecimalFormat;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener {
    private final UserSingleton userSingleton = UserSingleton.getInstance();
    private final Constants constants = new Constants();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Sensor linearAcc;
    private Sensor stepCounter;
    private Sensor stepDetector;
    private Sensor magnetometer;
    private final static String NOT_SUPPORTED_MESSAGE = "Sorry, sensors are unavailable on this device.";
    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private final float[] mR = new float[9];
    private final float[] orientation = new float[3];
    private float currentDegree = 0f;
    private int stepsTaken = 0;
    private int reportedSteps = 0;
    private int stepsDetected = 0;
    ImageView compassImage;
    TextView degreeTV, stepCounterTV;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    ImageButton playPauseButton;
    ImageButton restartButton;
    boolean isPlay = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(UserSingleton.getInstance().getCurrentUserTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        com.example.projet.databinding.ActivitySensorsBinding binding = ActivitySensorsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.swapThemeButton.setOnClickListener(v -> {
            swapTheme();
        });
        binding.backButton.setOnClickListener(v->{
            Intent mainAct = new Intent(this, MainActivity.class);
            startActivity(mainAct);
            finish();
        });
        binding.changeProfileButton.setOnClickListener(v->{
            Intent mainAct = new Intent(this, WelcomeActivity.class);
            startActivity(mainAct);
            finish();
        });
        binding.changeLanguageButton.setOnClickListener(v -> {
            showChangeLanguageDialog();
        });


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

    /** Function collecting data from sensors to show functioning compass and step counter on screen
     * Step Counter inspired from https://gist.github.com/dyadica/93438442857b1a93f19e
     * Compass inspired from https://www.techrepublic.com/article/pro-tip-create-your-own-magnetic-compass-using-androids-internal-sensors/
     * and https://www.codespeedy.com/simple-compass-code-with-android-studio/
     * @param -
     * @return -
     */
    public void onSensorChanged (SensorEvent sensorEvent){
        compassImage = (ImageView) findViewById(R.id.compass_image);
        degreeTV = (TextView) findViewById(R.id.DegreeTV);
        stepCounterTV = (TextView) findViewById(R.id.step_count);
        playPauseButton = (ImageButton) findViewById(R.id.play_button);
        restartButton = (ImageButton) findViewById(R.id.restart_button);

        View.OnClickListener restartListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                reportedSteps = 0;
                stepsDetected = 0;
                stepsTaken = 0;
                System.out.println("Number of steps: " + stepsTaken);
                stepCounterTV.setText(String.valueOf(stepsTaken));
            }
        };
        restartButton.setOnClickListener(restartListener);

        View.OnClickListener togglePlayListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isPlay){
                    v.setBackgroundResource(R.drawable.pause_black);
                }else{
                    v.setBackgroundResource(R.drawable.play_black);
                }
                isPlay = !isPlay;
            }
        };
        playPauseButton.setOnClickListener(togglePlayListener);


        if (sensorEvent.sensor == accelerometer) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            lastAccelerometerSet = true;
        } else if (sensorEvent.sensor == magnetometer) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
            lastMagnetometerSet = true;
        }
        else if(isPlay) {
            if (sensorEvent.sensor == stepCounter) {
                if (reportedSteps < 1) {
                    // Log the initial value
                    reportedSteps = (int) sensorEvent.values[0];
                }
                stepsTaken = (int) sensorEvent.values[0] - reportedSteps;
                System.out.println("Number of steps: " + stepsTaken);
                stepCounterTV.setText(String.valueOf(stepsTaken));
            } else if (sensorEvent.sensor == stepDetector) {
                stepsDetected++;
                System.out.println("Steps detected: " + stepsDetected);
            }
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(mR, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            System.out.println("Heading: " + df.format(azimuthInDegress) + " degrees");
            degreeTV.setText("Heading: " + df.format(azimuthInDegress) + " degrees");
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            compassImage.startAnimation(ra);
            currentDegree = -azimuthInDegress;
        }
    }

    /** Function auto-generated for SensorEventListener
     * @param -
     * @return -
     */
    public void onAccuracyChanged (Sensor sensor, int i) {
    }

    /** Change the app language
     * https://github.com/YarikSOffice/LanguageTest/blob/db5b3742bfcc083459e4f23aeb91c877babb0968/app/src/main/java/com/yariksoffice/languagetest/ui/SettingsActivity.java
     * @param -
     * @return -
     */
    private void setNewLocale(String lang) {
        App.localeManager.setNewLocale(this, lang);
        recreate();
    }

    /** Update the app theme
     * @param -
     * @return -
     */
    public void swapTheme() {
        if (userSingleton.getCurrentUserTheme().equals(constants.DARK_THEME)) {
            userSingleton.setCurrentUserTheme(constants.LIGHT_THEME);
            setTheme(R.style.Theme_projet);
        } else {
            userSingleton.setCurrentUserTheme(constants.DARK_THEME);
            setTheme(R.style.Theme_projet_dark);
            userSingleton.setCurrentUserTheme(constants.DARK_THEME);
        }
        finish();
        startActivity(getIntent());
    }

    /** Show language options to user
     * https://www.youtube.com/watch?v=zILw5eV9QBQ
     * @param -
     * @return -
     */
    private void showChangeLanguageDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SensorsActivity.this);
        mBuilder.setTitle(getResources().getString(R.string.choose_language));
        mBuilder.setSingleChoiceItems(constants.LANGUAGES, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // French
                        setNewLocale(App.localeManager.LANGUAGE_FRENCH);
                        break;
                    case 1:
                        // English
                        setNewLocale(App.localeManager.LANGUAGE_ENGLISH);
                        break;
                    default:
                        Log.d("PROFILE", "No language chosen");
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
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
}
