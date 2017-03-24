package com.example.bethanywong.msapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class LevelTest extends Activity{

    LevelTestView gyroscopeView;
    SensorManager sensorManager;
    TextView timeTextView;
    Button start;
    boolean isCountingDown;
    private String hand;
    private int[] results = new int[6];
    int round;

    private static final int PERMISSION_REQUEST_CODE = 1;

    protected TextView instructions;

    float[] gValues = new float[3];

    CountDownTimer timer = new CountDownTimer(10000, 1000) {
        public void onTick(long millisUntilFinished) {
            timeTextView.setText(millisUntilFinished / 1000 + "");
        }

        public void onFinish() {
            isCountingDown = false;
            saveDrawing();
            results[round] = computeResult();
            round++;
            start.setVisibility(View.VISIBLE);
            if (round == 3) {
                hand = "left";
            }
            instructions.setText("Hold your phone level to the ground in your " + hand + " hand and press start.");
            timeTextView.setText("Time's up!");

            if (round == 6) {
                start.setVisibility(View.INVISIBLE);
                displayResults();
            }

        }
    };

    public void saveDrawing() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), screenShot(findViewById(R.id.activity_level_test_new)),
                    UUID.randomUUID().toString() + ".png", "drawing");
            if (imgSaved != null) {
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            } else {
                Toast unsavedToast = Toast.makeText(getApplicationContext(),
                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }

        }

        gyroscopeView.clearPathTrace();
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        instructions = (TextView) findViewById(R.id.instructions);
        gyroscopeView = (LevelTestView) findViewById(R.id.gyroscope_view);
        start = (Button) findViewById(R.id.save);
        initSensor();
        hand = "right";
        round = 0;
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
        return gyroscopeView.computeResults();
    }

    public void displayResults() {
        instructions.setText("Test Completed!");
        int rScore = (results[0] + results[1] + results[2]) / 3;
        int lScore = (results[3] + results[4] + results[5]) / 3;
        timeTextView.setText("Right Hand Score: " + rScore + "\r\n" + "Left Hand Score: " + lScore
                + "\r\n" + "Right Trials: " + "\r\n" + "R1: " + results[0]
                + "\r\n" + "R2: " + results[1]
                + "\r\n" + "R3: " + results[2]
                + "\r\n" + "Left Trials: " + "\r\n" + "L1: " + results[3]
                + "\r\n" + "L2: " + results[4]
                + "\r\n" + "L3: " + results[5]);
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
            gyroscopeView.startTrace();
            start.setVisibility(View.INVISIBLE);
            warmUp.start();
        }
        isCountingDown = true;
    }
}
