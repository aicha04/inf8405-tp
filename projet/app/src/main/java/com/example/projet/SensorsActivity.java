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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projet.databinding.ActivitySensorsBinding;
import java.text.DecimalFormat;

public class SensorsActivity extends BaseActivity implements SensorEventListener {
    private final UserSingleton userSingleton = UserSingleton.getInstance();
    private final Constants constants = new Constants();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Sensor linearAcc;
    private Sensor stepDetector;
    private Sensor magnetometer;
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
    ToggleButton startPauseButton;
    Button resetButton;
    boolean isStart = false;


    private float DegreeStart = 0f;

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
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (accelerometer == null || gravity == null || linearAcc == null || stepDetector == null || magnetometer == null) {
            System.out.println("Sorry, sensors are unavailable on this device.");
            showToast(getApplicationContext(), R.string.sensors_unavailable);

        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI);
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
        compassImage = findViewById(R.id.compass_image);
        degreeTV = findViewById(R.id.degrees);
        stepCounterTV = findViewById(R.id.step_count);
        startPauseButton = findViewById(R.id.start_pause_button);
        resetButton = findViewById(R.id.reset_button);

        View.OnClickListener restartListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                reportedSteps = 0;
                stepsDetected = 0;
                stepsTaken = 0;
                System.out.println("Steps detected (Reset): " + stepsDetected);
                stepCounterTV.setText(String.valueOf(stepsDetected));
            }
        };
        resetButton.setOnClickListener(restartListener);

        View.OnClickListener togglePlayListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                isStart = !isStart;
            }
        };
        startPauseButton.setOnClickListener(togglePlayListener);

        if (sensorEvent.sensor == stepDetector) {
            if(isStart) {
                stepsDetected++;
                System.out.println("Steps detected: " + stepsDetected);
                stepCounterTV.setText(String.valueOf(stepsDetected));
            }
        }
        else {
//            float degree = Math.round(sensorEvent.values[0]);
//            degreeTV.setText(Float.toString(degree) + " degrees");
//            RotateAnimation ra = new RotateAnimation(
//                    DegreeStart,
//                    -degree,
//                    Animation.RELATIVE_TO_SELF, 0.5f,
//                    Animation.RELATIVE_TO_SELF, 0.5f);
//            ra.setFillAfter(true);
//            ra.setDuration(120);
//            compassImage.startAnimation(ra);
//            DegreeStart = -degree;

            if (sensorEvent.sensor == accelerometer) {
                System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
                lastAccelerometerSet = true;
            } else if (sensorEvent.sensor == magnetometer) {
                System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
                lastMagnetometerSet = true;
            }
            if (lastAccelerometerSet && lastMagnetometerSet) {
                SensorManager.getRotationMatrix(mR, null, lastAccelerometer, lastMagnetometer);
                SensorManager.getOrientation(mR, orientation);
                float azimuthInRadians = orientation[0];
                float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians)+360) % 360;
                System.out.println("Heading: " + df.format(azimuthInDegrees) + " degrees");
                degreeTV.setText(df.format(azimuthInDegrees) + " degrees");
                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        -azimuthInDegrees,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                ra.setDuration(210);
                ra.setFillAfter(true);
                compassImage.startAnimation(ra);
                currentDegree = -azimuthInDegrees;
            }
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
        if (accelerometer != null && gravity != null && linearAcc != null && stepDetector != null && magnetometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (accelerometer != null && gravity != null && linearAcc != null && stepDetector != null && magnetometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager.unregisterListener(this, gravity);
            sensorManager.unregisterListener(this, linearAcc);
            sensorManager.unregisterListener(this, stepDetector);
            sensorManager.unregisterListener(this, magnetometer);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accelerometer != null && gravity != null && linearAcc != null && stepDetector != null && magnetometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager.unregisterListener(this, gravity);
            sensorManager.unregisterListener(this, linearAcc);
            sensorManager.unregisterListener(this, stepDetector);
            sensorManager.unregisterListener(this, magnetometer);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (accelerometer != null && gravity != null && linearAcc != null && stepDetector != null && magnetometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager.unregisterListener(this, gravity);
            sensorManager.unregisterListener(this, linearAcc);
            sensorManager.unregisterListener(this, stepDetector);
            sensorManager.unregisterListener(this, magnetometer);
        }
    }
}
