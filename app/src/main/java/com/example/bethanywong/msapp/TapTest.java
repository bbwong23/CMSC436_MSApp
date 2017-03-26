package com.example.bethanywong.msapp;

import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class TapTest extends FragmentActivity implements TapTestInstructionFragment.StartTestListener, TapTestFragment.OnTapTestFinishListener {

    public static final String ROUND_NUMBER_KEY = "ROUND_NUMBER_KEY";
    public static final String BODY_PART_KEY = "BODY_PART_KEY";
    public static final String[] TRIAL_ORDER = {"right index finger", "left index finger", "right index finger",
            "left index finger", "right index finger", "left index finger", "right big toe", "left big toe",
            "right big toe", "left big toe", "right big toe", "left big toe"};
    private int roundNumber = 0;
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
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    public static TapTestFragment newInstance(String bodyPart, int roundNumber) {
        TapTestFragment fragment = new TapTestFragment();
        Bundle args = new Bundle();
        args.putString(BODY_PART_KEY, bodyPart);
        args.putInt(ROUND_NUMBER_KEY, roundNumber);
        fragment.setArguments(args);
        return fragment;
    }

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
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        TapTestInstructionFragment fragment = new TapTestInstructionFragment();

        // place instructions in view automatically
        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
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

        txtCount.setText("Right hand results: " + countHistory[0] + " || " + countHistory[1]+ " || " + countHistory[2] +
                "\r\nLeft hand results: " + countHistory[3] + " || " + countHistory[4]+ " || " + countHistory[5] +
                "\r\nRight Foot results: " + countHistory[6] + " || " + countHistory[7]+ " || " + countHistory[8] +
                "\r\nLeft Foot results: " + countHistory[9] + " || " + countHistory[10]+ " || " + countHistory[11]);
//        txtCount.setText("Right hand results: " + ((countHistory[0] + countHistory[1]+ countHistory[2]) / 3) +
//                "\r\nLeft hand results: " + ((countHistory[3] + countHistory[4] + countHistory[5]) / 3) +
//                "\r\nRight Foot results: " + ((countHistory[6] + countHistory[7] + countHistory[8]) / 3) +
//                "\r\nLeft Foot results: " + ((countHistory[9] + countHistory[10] + countHistory[11]) / 3));
    }

    public void startTest() {
        TapTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
        roundNumber++;
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToNext() {
        // set up for next trial
    }
}
