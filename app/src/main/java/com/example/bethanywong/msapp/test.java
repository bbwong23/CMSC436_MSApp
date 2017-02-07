package com.example.bethanywong.msapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class test extends AppCompatActivity {
    CountDownTimer timer = new CountDownTimer(10000, 1000) {
        public void onTick(long millisUntilFinished) {
            timeTextView.setText(millisUntilFinished / 1000 + "");
        }

        public void onFinish() {
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
    Animation wiggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        roundView = (TextView) findViewById(R.id.roundNumber);
        round = 1;
        hand = "right";
        wiggle = AnimationUtils.loadAnimation(this, R.anim.wiggle);
    }

    public void runTimer(View view) {
        tap = (ImageView)findViewById(R.id.tapCount);
        tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                v.startAnimation(wiggle);
            }
        });
        timer.start();
        instructions = (TextView) findViewById(R.id.instructions);
        txtCount = (TextView) findViewById(R.id.tapBegin);
        count = 0;
        txtCount.setText("Tap!");
        instructions.setText("Keep your hand in place!");

    }

    public void setNewRound() {
        timeTextView.setText("TIME'S UP");
        txtCount.setText("Tap to begin!");
        instructions.setText("Place " + hand + " hand on phone with index finger on the corgi!");
        roundView.setText("Round " + round);

        if (round == 3) {
            txtCount.setText("Right hand results: " + ((countHistory[0] + countHistory[1]) / 2));
        }
    }

    public void displayResults() {
        hand = "right";
        round = 1;
        timeTextView.setText("TIME'S UP");
        instructions.setText("Tap the corgi to restart!");
        roundView.setText("Test Completed!");
        txtCount.setText("Left hand results: " + ((countHistory[2] + countHistory[3]) / 2));
    }
}
