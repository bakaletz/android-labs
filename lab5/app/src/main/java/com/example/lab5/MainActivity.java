package com.example.lab5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private TextView pitchTextView;
    private TextView rollTextView;
    private LevelView levelView;


    private static final float ALPHA = 0.8f;


    private float[] gravity = new float[3];
    private float[] gyroValues = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pitchTextView = findViewById(R.id.pitch_value);
        rollTextView = findViewById(R.id.roll_value);
        levelView = findViewById(R.id.level_view);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Застосування фільтра низьких частот для відсіювання шуму
            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

            float pitch = calculatePitch(gravity);
            float roll = calculateRoll(gravity);


            updateUI(pitch, roll);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroValues[0] = event.values[0];
            gyroValues[1] = event.values[1];
            gyroValues[2] = event.values[2];


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float calculatePitch(float[] gravity) {
        double pitch = Math.atan2(gravity[1], Math.sqrt(gravity[0] * gravity[0] + gravity[2] * gravity[2]));
        return (float) Math.toDegrees(pitch);
    }

    private float calculateRoll(float[] gravity) {
        double roll = Math.atan2(-gravity[0], gravity[2]);
        return (float) Math.toDegrees(roll);
    }

    private void updateUI(float pitch, float roll) {

        pitchTextView.setText(String.format("Pitch: %.1f°", pitch));
        rollTextView.setText(String.format("Roll: %.1f°", roll));


        levelView.updateAngles(pitch, roll);
    }
}