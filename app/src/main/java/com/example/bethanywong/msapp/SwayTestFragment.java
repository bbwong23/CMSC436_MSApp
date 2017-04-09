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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class SwayTestFragment extends Fragment {
    private static final String ROUND_NUMBER_KEY = "ROUND_NUMBER";
    private static final int TEST_TIME_LENGTH = 10;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private OnSwayRoundFinishListener callBack;
    private CountDownTimer warmUpTimer;
    private CountDownTimer testTimer;
    private double totalDistance;
    private int secondsPassed;
    private float[] avgData;
    private SensorManager sensorManager;
    private SensorEventListener sensorGyroListener;
    private float[][] rawData;
    private float[] gValues;
    private float[] calibration;
    private ToneGenerator generator;
    private Button startButton;
    private TextView roundText;
    private int roundNumber;
    private TextView swayText;
    private View view;
    private Activity activity;

    public interface OnSwayRoundFinishListener {
        public void goToNext(double trialResult);
    }

    public static SwayTestFragment newInstance(int roundNumber) {
        SwayTestFragment fragment = new SwayTestFragment();
        Bundle args = new Bundle();
        args.putInt(ROUND_NUMBER_KEY, roundNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_sway_test, container, false);
        startButton = (Button) view.findViewById(R.id.startButton);
        swayText = (TextView) view.findViewById(R.id.swayText);
        roundText = (TextView) view.findViewById(R.id.roundText);
        rawData = new float[TEST_TIME_LENGTH][3];
        gValues = new float[3];
        avgData = new float[3];
        secondsPassed = 0;
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        generator = new ToneGenerator(AudioManager.STREAM_DTMF, 70);
        roundNumber = getArguments().getInt(ROUND_NUMBER_KEY);

        roundText.setText("Round " + roundNumber);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swayText.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.INVISIBLE);
                warmUpTimer.start();
            }
        });

        sensorGyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                    gValues = event.values;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        testTimer = new CountDownTimer(TEST_TIME_LENGTH * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                // record current phone space data
                rawData[secondsPassed] = gValues.clone();
                secondsPassed++;
            }

            ;

            public void onFinish() {
                // determine average x, y, and z positions
                float x = 0;
                float y = 0;
                float z = 0;
                for (int i = 0; i < rawData.length; i++) {
                    x = x + rawData[i][0];
                    y = y + rawData[i][1];
                    z = z + rawData[i][2];
                }
                avgData[0] = x / rawData.length;
                avgData[1] = y / rawData.length;
                avgData[2] = z / rawData.length;
                Log.d("results", "calibration = " + calibration[0] + " " + calibration[1] + " " + calibration[2]);
                Log.d("results", "avgData = " + avgData[0] + " " + avgData[1] + " " + avgData[2]);
                generator.startTone(ToneGenerator.TONE_CDMA_PIP, 1000);
                callBack.goToNext(calculateDistance());

            }
        };

        warmUpTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                generator.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
            }

            @Override
            public void onFinish() {
                calibration = gValues.clone();
                testTimer.start();
            }
        };

        return view;
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
            callBack = (OnSwayRoundFinishListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        warmUpTimer.cancel();
        testTimer.cancel();
    }

    public double calculateDistance() {
        totalDistance = Math.sqrt(Math.pow(calibration[0] - avgData[0], 2) + Math.pow(calibration[1] - avgData[1], 2) + Math.pow(calibration[2] - avgData[2], 3));
        return totalDistance;
    }

//    public void saveDrawing(View v) {
//        view = v;
//        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        } else {
//            String imgSaved = MediaStore.Images.Media.insertImage(
//                    activity.getContentResolver(), screenShot(view),
//                    UUID.randomUUID().toString() + ".png", "drawing");
//            if (imgSaved != null) {
//                Toast savedToast = Toast.makeText(activity.getApplicationContext(),
//                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
//                savedToast.show();
//            } else {
//                Toast unsavedToast = Toast.makeText(activity.getApplicationContext(),
//                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
//                unsavedToast.show();
//            }
//            drawView.destroyDrawingCache();
//        }
//
//        drawView.clear();
//    }
//
//    public Bitmap screenShot(View view) {
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
//                view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//        return bitmap;
//    }
}
