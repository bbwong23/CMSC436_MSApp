package com.example.bethanywong.msapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StepTest extends AppCompatActivity {
    private Sensor sensor;
    private SensorManager mSensorManager;
    private float[] distance;
    private Button start;
    private boolean testStarted = false;
    private CountDownTimer timer;
    private TextView instruction;
    private int steps = 0;
    private float[] duration;
    private int trialNum = 0;
    private float[] scores;
    ToneGenerator toneAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_test);

        distance = new float[3];
        duration = new float[3];
        scores = new float[3];

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (sensor != null) {
            mSensorManager.registerListener(sensorEvent, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Step Sensor not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        toneAlarm = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        start = (Button) findViewById(R.id.button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest();
            }
        });

        instruction = (TextView) findViewById(R.id.textView);

    }

    private SensorEventListener sensorEvent = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (testStarted) {
                distance[trialNum] += 2.475; // Average height of 5'6" *.45 = typical stride length
                steps++;
            }
        }
    };

    private void startTest() {
        final float startTime = System.currentTimeMillis();

        timer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                instruction.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                instruction.setText("Round " + trialNum+1 + "\n\nWalk!");
                testStarted = true;
                while (steps != 25) {
                    // let the loop run till it hits 25
                }
                // alarm goes off when 25 steps are completed, meaning trial is over
                toneAlarm.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);

                // Compute/display scores
                duration[trialNum] = System.currentTimeMillis() - startTime;
                finishTest();
            }
        };
        start.setEnabled(false);
        timer.start();
    }

    private void finishTest() {
        if (trialNum < 2) {
            // score = inches/second traveled in 25 steps
            scores[trialNum] = distance[trialNum] / (duration[trialNum] / 1000);

            // reset values
            start.setEnabled(true);
            testStarted = false;
            steps = 0;
            trialNum++;
            instruction.setText("Round " + trialNum+1 + "\n\nPress the start button and start walking!");
        } else {
            float finalScore = (scores[0] + scores[1] + scores[2])/3;
            instruction.setText("Final Score: " + finalScore + "\nTrial Scores:" +
                    "\n" + scores[0] + "\n" + scores[1] + "\n" + scores[2]);
        }
    }
}
