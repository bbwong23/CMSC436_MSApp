package com.example.bethanywong.msapp;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.bethanywong.msapp.TapTest.ROUND_NUMBER_KEY;

public class TapTestFragment extends Fragment {
    public static final String BODY_PART_KEY = "BODY_PART_KEY";
    private OnTapTestFinishListener callback;
    private ImageView tapButton;
    private TextView timeTextView;
    private CountDownTimer timer;
    private CountDownTimer warmUpTimer;
    private TextView instructions;
    private TextView subtext;
    private Animation shrink;
    private TextView roundNumberView;
    private int count;
    private int roundNumber;
    private String bodyPart;

    public interface OnTapTestFinishListener {
        public void goToNext(int trialResult);
    }

    public static TapTestFragment newInstance(String bodyPart, int roundNumber) {
        TapTestFragment fragment = new TapTestFragment();
        Bundle args = new Bundle();
        args.putInt(ROUND_NUMBER_KEY, roundNumber);
        args.putString(BODY_PART_KEY, bodyPart);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tap_test, container, false);
        count = 0;
        shrink = AnimationUtils.loadAnimation(view.getContext(), R.anim.shrink);
        timeTextView = (TextView)view.findViewById(R.id.timeTextView);
        tapButton = (ImageView)view.findViewById(R.id.tapButton);
        instructions = (TextView)view.findViewById(R.id.instructions);
        subtext = (TextView)view.findViewById(R.id.subtext);
        roundNumberView = (TextView)view.findViewById(R.id.roundNumber);
        roundNumber = getArguments().getInt(ROUND_NUMBER_KEY);
        bodyPart = getArguments().getString(BODY_PART_KEY);

        // Set proper round number and body part instruction
        roundNumberView.setText("Round " + roundNumber);
        instructions.setText("Trial: " + bodyPart);

         timer = new CountDownTimer(10000, 1000) {
//        timer = new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeTextView.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                callback.goToNext(count);
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
