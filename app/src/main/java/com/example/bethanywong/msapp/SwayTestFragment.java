package com.example.bethanywong.msapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SwayTestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SwayTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SwayTestFragment extends Fragment {
    private static final String ROUND_NUMBER_KEY = "ROUND_NUMBER";
    private OnTapTestFinishListener callback;
    private CountDownTimer timer;
    private CountDownTimer calibrationTimer;
    private float totalDistance;
    private int secondsPassed;
    private float centerX;
    private float centerY;
    private float centerTilt;

    public interface OnTapTestFinishListener {
        public void goToNext(int trialResult);
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
        View view = inflater.inflate(R.layout.fragment_tap_test, container, false);

        calibrationTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                secondsPassed++;
                // find position of the phone to calculate the center.
            };

            public void onFinish() {
                // centerX = average X position
                //centerY = average Y position
                // centerTilt = average tilt
            }
        };

        timer = new CountDownTimer(3000,1000) {
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
                tapButton.clearColorFilter();
                subtext.setText("Tap!");
                tapButton.setEnabled(true);
                timer.start();
            }
        };

        tapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTimer(v);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        coolDown();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callback = (OnTapTestFinishListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runTimer(View view) {
        tapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                v.startAnimation(shrink);
            }
        });

        tapButton.setEnabled(false);
        tapButton.setColorFilter(Color.rgb(123,123,123), PorterDuff.Mode.MULTIPLY);
        timeTextView.setText("10");
        warmUpTimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        warmUpTimer.cancel();
        timer.cancel();
    }

    public void coolDown(){
        tapButton.setEnabled(false);
        CountDownTimer coolDown = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                tapButton.setEnabled(true);
            }
        };
        coolDown.start();
    }

}
