package com.example.bethanywong.msapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StepTest extends AppCompatActivity {
    private Sensor sensor;
    private SensorManager mSensorManager;
    private int distance;
    private Button start;
    private boolean testStarted = false;
    private CountDownTimer timer;
    private TextView instruction;
    private CountDownTimer testTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_test);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (sensor != null) {
            mSensorManager.registerListener(sensorEvent, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Step Sensor not found", Toast.LENGTH_SHORT).show();
            finish();
        }

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
            if (testStarted)
                distance+=2;
        }
    };

    private void startTest() {
        testTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                finishTest();
            }
        };

        timer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                instruction.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                instruction.setText("Walk!");
                testStarted = true;
                testTimer.start();
            }
        };
    }

    private void finishTest() {
        instruction.setText("Press the start button and start walking!");
        testStarted = false;

        //calculate distance / second

        distance = 0;
    }
}
