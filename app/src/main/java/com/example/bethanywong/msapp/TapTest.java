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

public class TapTest extends AppCompatActivity {
    CountDownTimer timer = new CountDownTimer(10000, 1000) {
        public void onTick(long millisUntilFinished) {
            timeTextView.setText(millisUntilFinished / 1000 + "");
        }

        public void onFinish() {
            isCountingDown = false;
            countHistory[round-1] = count;
            count=0;
            round++;

            switch (round) {
                case 4:
                case 5:
                case 6:
                    side = "left";
                    break;
                case 7:
                case 8:
                case 9:
                    side = "right";
                    body = "big toe";
                    break;
                case 10:
                case 11:
                case 12:
                    side = "left";
                    break;
                default:
                    break;

            }

            if (round <= 12) {
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
    String side;
    String body;
    private int[] countHistory = new int[12];
    Animation shrink;
    boolean isCountingDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        roundView = (TextView) findViewById(R.id.roundNumber);
        round = 1;
        side = "right";
        body = "index finger";
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
        instructions.setText("Keep your " + body + " in place!");
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
        instructions.setText("Tap the green circle as many times as you can with your " + side + " " + body);
        roundView.setText("Round " + round);
    }

    public void displayResults() {
        coolDown();
        timeTextView.setVisibility(View.GONE);
        instructions.setVisibility(View.GONE);
        tap.setVisibility(View.GONE);
        roundView.setText("Test Completed!");
        txtCount.setText("Right hand results: " + ((countHistory[0] + countHistory[1]+ countHistory[2]) / 3) +
                "\r\nLeft hand results: " + ((countHistory[3] + countHistory[4] + countHistory[5]) / 3) +
                "\r\nRight Foot results: " + ((countHistory[6] + countHistory[7] + countHistory[8]) / 3) +
                "\r\nLeft Foot results: " + ((countHistory[9] + countHistory[10] + countHistory[11]) / 3));
    }
}
