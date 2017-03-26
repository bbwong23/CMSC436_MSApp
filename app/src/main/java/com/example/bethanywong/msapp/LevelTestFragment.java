package com.example.bethanywong.msapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.bethanywong.msapp.R.id.timeTextView;
import static com.example.bethanywong.msapp.SpiralTest.HAND_KEY;

public class LevelTestFragment extends Fragment {
    public static final String ROUND_KEY = "ROUND_KEY";
    private Button startButton;
    private TextView instructionText;
    private TextView timeText;
    private TextView subtext;
    private CountDownTimer timer;
    private CountDownTimer warmUpTimer;
    private LevelTestView gyroscopeView;
    private OnFinishLevelTestListener callBack;
    private SensorManager sensorManager;
    private SensorEventListener sensorGyroListener;
    private float[] gValues = new float[3];

    public interface OnFinishLevelTestListener {
        public void goToNext(int score);
        public void saveDrawing();
    }

    public static LevelTestFragment newInstance(String hand, int roundNumber) {
        LevelTestFragment fragment = new LevelTestFragment();
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
        View view = inflater.inflate(R.layout.fragment_level_test, container, false);
        startButton = (Button)view.findViewById(R.id.startButton);
        instructionText = (TextView)view.findViewById(R.id.instructionText);
        timeText = (TextView)view.findViewById(timeTextView);
        gyroscopeView = (LevelTestView)view.findViewById(R.id.gyroscope_view);
        subtext = (TextView)view.findViewById(R.id.subtext);

        timer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeText.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                callBack.saveDrawing();
                int score = computeResult();
                callBack.goToNext(score);

            }
        };

        warmUpTimer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished/1000 == 2){
                    subtext.setText("Ready!");
                } else if (millisUntilFinished/1000 == 1){
                    subtext.setText("Set!");
                }
            }
            @Override
            public void onFinish() {
                subtext.setText("Hold steady!");
                timer.start();
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gyroscopeView.startTrace();
                startButton.setVisibility(View.INVISIBLE);
                subtext.setVisibility(View.VISIBLE);
                warmUpTimer.start();
            }
        });

        sensorGyroListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
                    gValues = event.values;

                gyroscopeView.update(gValues[0], gValues[1], gValues[2]);
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        // set instructions
        int roundNumber = getArguments().getInt(ROUND_KEY);
        String hand = getArguments().getString(HAND_KEY);
        instructionText.setText("Round " + roundNumber + ": " + hand);

        initSensor();

        return view;
    }

    public int computeResult() {
        return gyroscopeView.computeResults();
    }

    void initSensor() {
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Sensor sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(sensorGyroListener, sensorGyroscope, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorGyroListener);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callBack = (OnFinishLevelTestListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
