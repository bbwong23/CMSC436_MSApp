package com.example.bethanywong.msapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import static com.example.bethanywong.msapp.SwayTestFragment.newInstance;
import static com.example.bethanywong.msapp.SwayScoreFragment.newInstance;

public class SwayTest extends FragmentActivity implements SwayTestInstructionFragment.StartSwayTestListener, SwayTestFragment.OnSwayRoundFinishListener, SwayScoreFragment.OnFinishSwayTestListener {
    public static int NUMBER_OF_TRIALS = 3;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private boolean hasBeenResumed;
    private double[] results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sway_test);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        hasBeenResumed = false;
        results = new double[NUMBER_OF_TRIALS];
        SwayTestInstructionFragment fragment = new SwayTestInstructionFragment();

        // place instructions in view automatically


        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        // disable back button
    }

    @Override
    public void onResume() {
        super.onResume();
        if (roundNumber >= 0 && roundNumber < NUMBER_OF_TRIALS && hasBeenResumed) {
            SwayTestFragment fragment = newInstance(roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        hasBeenResumed = true;
    }

    public void startTest() {
        roundNumber++;
        SwayTestFragment fragment = newInstance(roundNumber+1);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToNext(double trialResult) {
        results[roundNumber] = trialResult;
        if (roundNumber < NUMBER_OF_TRIALS-1) {
            startTest();
        } else {
            SwayScoreFragment fragment = newInstance(results);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void finishTest() {
        finish();
    }

}
