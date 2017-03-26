package com.example.bethanywong.msapp;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.bethanywong.msapp.R.id.instructions;
import static com.example.bethanywong.msapp.R.id.timeTextView;
import static com.example.bethanywong.msapp.SpiralTest.HAND_KEY;
import static com.example.bethanywong.msapp.TapTest.BODY_PART_KEY;
import static com.example.bethanywong.msapp.TapTest.ROUND_NUMBER_KEY;


public class TapTestFragment extends Fragment {
    private OnTapTestFinishListener callback;
    private ImageView tapButton;
    private TextView timeTextView;
    private CountDownTimer timer;
    private CountDownTimer warmUpTimer;
    private TextView instructions;
    private TextView subtext;
    private Animation shrink;
    private TextView roundNumberView;
    private String bodyPart;
    private int count;

    public interface OnTapTestFinishListener {
        public void goToNext();
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
        bodyPart = getArguments().getString(BODY_PART_KEY);
        roundNumberView = (TextView)view.findViewById(R.id.roundNumber);

        // Set proper round number
        int roundNumber = getArguments().getInt(ROUND_NUMBER_KEY);
        roundNumberView.setText("Round " + roundNumber);

        timer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeTextView.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                callback.goToNext();
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
        instructions.setText("Trial: " + bodyPart);
    }

}
