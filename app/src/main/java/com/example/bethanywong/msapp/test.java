package com.example.bethanywong.msapp;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

public class test extends AppCompatActivity {
    CountDownTimer timer = new CountDownTimer(10000, 1000) {
        public void onTick(long millisUntilFinished) {
            timeTextView.setText(millisUntilFinished / 1000 + "");
        }

        public void onFinish() {
            isCountingDown = false;
            countHistory[round-1] = count;
            count=0;
            round++;

            if (round > 2) {
                hand = "left";
            }

            if (round <= 4) {
                setNewRound();
            } else {
                displayResults();
            }

            tap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runTimer(v);
                }
            });
        }
    };

    private int count;
    ImageView tap;
    TextView txtCount;
    TextView instructions;
    TextView roundView;
    TextView timeTextView;
    int round;
    String hand;
    private int[] countHistory = new int[4];
    Animation shrink;
    boolean isCountingDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        roundView = (TextView) findViewById(R.id.roundNumber);
        round = 1;
        hand = "right";
        shrink = AnimationUtils.loadAnimation(this, R.anim.shrink);
        isCountingDown = false;
    }

    public void runTimer(View view) {
        tap = (ImageView)findViewById(R.id.tapCount);
        instructions = (TextView) findViewById(R.id.instructions);
        txtCount = (TextView) findViewById(R.id.tapBegin);
        tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                //Log.d("runTimer",String.valueOf(count));
                v.startAnimation(shrink);
            }
        });
        if (!isCountingDown){
            tap.setEnabled(false);
            tap.setColorFilter(Color.rgb(123,123,123), PorterDuff.Mode.MULTIPLY);
            CountDownTimer warmUp = new CountDownTimer(3000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished/1000 == 2){
                        txtCount.setText("Ready!");
                    } else if (millisUntilFinished/1000 == 1){
                        txtCount.setText("Set!");
                    }
                }
                @Override
                public void onFinish() {
                    tap.clearColorFilter();
                    txtCount.setText("Tap!");
                    tap.setEnabled(true);
                    timer.start();
                }
            };
            timeTextView.setText("10");
            warmUp.start();
        }
        isCountingDown = true;
        count = 0;
        instructions.setText("Keep your hand in place!");
    }
    public void coolDown(){
        tap.setEnabled(false);
        CountDownTimer coolDown = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                tap.setEnabled(true);
            }
        };
        coolDown.start();
    }

    public void setNewRound() {
        coolDown();
        timeTextView.setText("TIME'S UP");
        txtCount.setText("Tap to begin!");
        instructions.setText("Place " + hand + " hand on phone with index finger on the green circle!");
        roundView.setText("Round " + round);
    }

    public void displayResults() {
        coolDown();
        hand = "right";
        round = 1;
        timeTextView.setText("TIME'S UP");
        instructions.setText("Tap the circle to restart!");
        roundView.setText("Test Completed!");
        txtCount.setText("Right hand results: " + ((countHistory[0] + countHistory[1]) / 2) +
                "\r\nLeft hand results: " + ((countHistory[2] + countHistory[3]) / 2));
    }
}
