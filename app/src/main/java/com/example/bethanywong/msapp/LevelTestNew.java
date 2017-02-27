package com.example.bethanywong.msapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class LevelTestNew extends Activity{

    LevelTestNewView gyroscopeView;
    SensorManager sensorManager;
    TextView timeTextView;
    boolean isCountingDown;
    private String[] results = new String[2];
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
            round++;
            saveDrawing();

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
                    gyroscopeView.startTrace();
                    timer.start();
                }
            };
            timeTextView.setText("10");
            warmUp.start();
        }
        isCountingDown = true;
    }


}
