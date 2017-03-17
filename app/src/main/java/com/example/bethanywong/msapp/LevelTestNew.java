package com.example.bethanywong.msapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class LevelTestNew extends Activity{

    LevelTestNewView gyroscopeView;
    SensorManager sensorManager;
    TextView timeTextView;
    boolean isCountingDown;
    private String[] results = new String[2];
    int round;

    protected TextView instructions;

    float[] gValues = new float[3];

    CountDownTimer timer = new CountDownTimer(10000, 1000) {
        public void onTick(long millisUntilFinished) {
            timeTextView.setText(millisUntilFinished / 1000 + "");
        }

        public void onFinish() {
            isCountingDown = false;
            round++;

            if (round == 1) {
                results[0] = "Right hand result: " + computeResult();
                instructions.setText("Hold your phone level to the ground in your left hand and press start.");
                timeTextView.setText("Time's up!");
            }

            else {
                results[1] = "Left hand result: " + computeResult();
                displayResults();
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test_new);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        instructions = (TextView) findViewById(R.id.instructions);
        gyroscopeView = (LevelTestNewView) findViewById(R.id.gyroscope_view);
        initSensor();
    }

    void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(sensorGyroListener, sensorGyroscope, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorGyroListener);
    }



    public int computeResult() {
        return 0;
    }

    public void displayResults() {
        instructions.setText("Test Completed!");
        timeTextView.setText(results[0] + "\r\n" + results[1]);
    }

    private final SensorEventListener sensorGyroListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
                gValues = event.values;

            gyroscopeView.update(gValues[0], gValues[1], gValues[2]);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void startTest(View view) {
        if (!isCountingDown){
            CountDownTimer warmUp = new CountDownTimer(3000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished/1000 == 2){
                        instructions.setText("Ready!");
                    } else if (millisUntilFinished/1000 == 1){
                        instructions.setText("Set!");
                    }
                }
                @Override
                public void onFinish() {
                    instructions.setText("Hold steady!");
                    timer.start();
                }
            };
            timeTextView.setText("10");
            warmUp.start();
        }
        isCountingDown = true;
    }
}
