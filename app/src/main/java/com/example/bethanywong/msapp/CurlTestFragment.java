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
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;


import org.w3c.dom.Text;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class CurlTestFragment extends Fragment {
    private static final String HAND_KEY = "HAND_KEY";
    private static final String ROUND_KEY = "ROUND_KEY";
    private String bodyPart;
    private int roundNumber;
    private TextView roundText;
    private TextView instructionText;
    private Button startButton;
    private FinishCurlTrialListener callBack;
    private CountDownTimer warmUpTimer;
    private SensorManager sensorManager;
    private int curlsDone;
    private boolean curledUpwards;
    private ToneGenerator toneG;
    private long startTime;
    private Vibrator vibrator;

    public interface FinishCurlTrialListener {
        public void goToNext(long score);
    }

    public static CurlTestFragment newInstance(String hand, int roundNumber) {
        // Required empty public constructor
        CurlTestFragment fragment = new CurlTestFragment();
        Bundle args = new Bundle();
        args.putString(HAND_KEY, hand);
        args.putInt(ROUND_KEY, roundNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_curl_test, container, false);
        bodyPart = getArguments().getString(HAND_KEY);
        roundNumber = getArguments().getInt(ROUND_KEY);
        roundText = (TextView)view.findViewById(R.id.roundText);
        instructionText = (TextView)view.findViewById(R.id.instructionsText);
        startButton = (Button)view.findViewById(R.id.startButton);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        curledUpwards = false;
        curlsDone = 0;

        initSensor();

        roundText.setText("Round " + roundNumber + ": " + bodyPart);

        warmUpTimer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 == 2) {
                    instructionText.setText("Ready!");
                } else if (millisUntilFinished / 1000 == 1) {
                    instructionText.setText("Set!");
                }
            }

            @Override
            public void onFinish() {
                startTime = System.nanoTime();
                instructionText.setText("Curl upwards!");
                startButton.setVisibility(View.GONE);

                Sensor sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                Sensor sensorRotate = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                sensorManager.registerListener(sensorGyroListener, sensorAccel, SensorManager.SENSOR_DELAY_UI);
                sensorManager.registerListener(sensorGyroListener, sensorMagnetic, SensorManager.SENSOR_DELAY_UI);
                sensorManager.registerListener(sensorGyroListener, sensorRotate, SensorManager.SENSOR_DELAY_UI);
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionText.setVisibility(View.VISIBLE);
                warmUpTimer.start();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callBack = (FinishCurlTrialListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initSensor() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
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

                    instructionText.setText("Curl downwards!");
                } else if (mOrientationAngles[1] >= 0) {
                    if (curledUpwards) {
                        curledUpwards = false;
                        curlsDone++;

                        instructionText.setText("Curl upwards!");

                        if (curlsDone >= 10) {
                            sensorManager.unregisterListener(sensorGyroListener);
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
                            // calculate time taken
                            long endTime = System.nanoTime();
                            long score = (endTime - startTime) / 1000000000;
                            callBack.goToNext(score);
                        }
                    }
                }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };




}
