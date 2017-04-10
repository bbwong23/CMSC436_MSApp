package com.example.bethanywong.msapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import static com.example.bethanywong.msapp.BalloonTestFragment.newInstance;


public class BalloonTest extends FragmentActivity implements BalloonTestInstructionFragment.StartBalloonTestListener, BalloonTestFragment.FinishBalloonTrialListener, BalloonScoreFragment.FinishBalloonTestListener {
    private static String[] TRIAL_ORDER = {"Right hand", "Left hand", "Right hand", "Left hand", "Right hand", "Left hand"};
    private static int[] RIGHT_HAND_TRIALS = {0, 2, 4};
    private static int[] LEFT_HAND_TRIALS = {1, 3, 5};
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private boolean hasBeenResumed;
    double[] results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloon_test);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        results = new double[TRIAL_ORDER.length];
        hasBeenResumed = false;
        // begin with an instruction fragment
        BalloonTestInstructionFragment fragment = new BalloonTestInstructionFragment();
        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        // disable back button
    }

    @Override
    public void onResume() {
        super.onResume();
        if (roundNumber < TRIAL_ORDER.length && hasBeenResumed) {
            // replace old fragment for new fragment
            BalloonTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        hasBeenResumed = true;
    }



    public void startBalloonTest() {
        roundNumber++;
        BalloonTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void completeRound(long totalTime) {
        results[roundNumber] = totalTime / 10000000000.0;

        if (roundNumber < TRIAL_ORDER.length-1) {
            startBalloonTest();
        } else {
            BalloonScoreFragment fragment = BalloonScoreFragment.newInstance(TRIAL_ORDER, RIGHT_HAND_TRIALS, LEFT_HAND_TRIALS, results);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goHome() {
        finish();
    }
}
