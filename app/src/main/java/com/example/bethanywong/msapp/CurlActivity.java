package com.example.bethanywong.msapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CurlActivity extends AppCompatActivity {
    Button startBtn;
    TextView instruction;
    long startTime;
    boolean isCountingDown;

    Vibrator vibrator;
    ToneGenerator toneG;

    int round;
    double leftSum;
    double rightSum;

    boolean curledUpwards = false;
    int curlsDone = 0;

    SensorManager sensorManager;

    float[] gValues = new float[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curl);

        startBtn = (Button) findViewById(R.id.curlStart);
        instruction = (TextView) findViewById(R.id.curlInstruction);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = System.nanoTime();
                startTest();
            }
        });

//        Button skipBtn = (Button) findViewById(R.id.skip_button);
//        skipBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finishTest();
//            }
//        });

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        round = 0;

        initSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    private final SensorEventListener sensorGyroListener = new SensorEventListener() {
        private final float[] mAccelerometerReading = new float[3];
        private final float[] mMagnetometerReading = new float[3];

        private final float[] mRotationMatrix = new float[9];
        private final float[] mOrientationAngles = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, mAccelerometerReading,
                        0, mAccelerometerReading.length);
            }
            else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, mMagnetometerReading,
                        0, mMagnetometerReading.length);
            } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                sensorManager.getRotationMatrix(mRotationMatrix, null,
                        mAccelerometerReading, mMagnetometerReading);

                sensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
                double pitch = Math.toDegrees( mOrientationAngles[1] );

                if (pitch <= -80) { // 2 ~= 120 degrees in radians
                    if (!curledUpwards) {
                        curledUpwards = true;
//                    vibrator.vibrate(500);
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    }

                    instruction.setText("Curl downwards!");
                } else if (mOrientationAngles[1] >= 0) {
                    if (curledUpwards) {
                        curledUpwards = false;
                        curlsDone++;

                        instruction.setText("Curl upwards!");

                        if (curlsDone >= 10) {
                            finishTest();
                        }
                    }
                }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void finishTest() {
        sensorManager.unregisterListener(sensorGyroListener);

        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);

        long endTime = System.nanoTime();

        double score = (endTime - startTime) / 1000000000.0;
        NumberFormat formatter = new DecimalFormat("#0.00");

        round++;
        if (round < 3) {
            instruction.setText("Hold your right arm out flat and press start to begin.");
            leftSum += score;
            startBtn.setVisibility(View.VISIBLE);
        } else {
            rightSum += score;
            if (round == 6) {
                rightSum /= 3;
                leftSum /= 3;

                instruction.setText("Left hand average: " + formatter.format(leftSum) +
                        "\n Right hand average: " + formatter.format(rightSum));
                startBtn.setVisibility(View.INVISIBLE);
            } else {
                startBtn.setVisibility(View.VISIBLE);
                instruction.setText("Hold your left arm out flat and press start to begin.");
            }
        }
    }

    public void startTest() {
        if (!isCountingDown){
            CountDownTimer warmUp = new CountDownTimer(3000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished/1000 == 2){
                        instruction.setText("Ready!");
                    } else if (millisUntilFinished/1000 == 1){
                        instruction.setText("Set!");
                    }
                }
                @Override
                public void onFinish() {
                    instruction.setText("Curl upwards!");
                    startBtn.setVisibility(View.INVISIBLE);
                    isCountingDown = false;

                    Sensor sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    Sensor sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                    Sensor sensorRotate = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                    sensorManager.registerListener(sensorGyroListener, sensorAccel, SensorManager.SENSOR_DELAY_UI);
                    sensorManager.registerListener(sensorGyroListener, sensorMagnetic, SensorManager.SENSOR_DELAY_UI);
                    sensorManager.registerListener(sensorGyroListener, sensorRotate, SensorManager.SENSOR_DELAY_UI);
                }
            };
            warmUp.start();
        }
        isCountingDown = true;
    }
}
