package com.example.bethanywong.msapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import static com.example.bethanywong.msapp.CurlTestFragment.newInstance;
import static com.example.bethanywong.msapp.CurlScoreFragment.newInstance;

public class CurlActivity extends FragmentActivity implements CurlTestInstructionFragment.StartCurlTestListener, CurlTestFragment.FinishCurlTrialListener, CurlScoreFragment.FinishCurlTestListener {
    private static final String[] TRIAL_ORDER = {"Right Arm", "Left Arm", "Right Arm", "Left Arm", "Right Arm", "Left Arm"};
    private static final int[] RIGHT_ARM_TRIALS = {0, 2, 4};
    private static final int[] LEFT_ARM_TRIALS = {1, 3, 5};
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private boolean hasBeenResumed;
    private float[] results;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curl);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        hasBeenResumed = false;
        results = new float[TRIAL_ORDER.length];

        CurlTestInstructionFragment fragment = new CurlTestInstructionFragment();

        // place instruction fragment in view
        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    public void startCurlTest() {
        roundNumber++;
        CurlTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToNext(long score) {
        if (roundNumber >= 0 && roundNumber < TRIAL_ORDER.length) {
            results[roundNumber] = score;
        }

        if (roundNumber < TRIAL_ORDER.length-1) {
            startCurlTest();
        } else {
            // display score fragment
            CurlScoreFragment fragment = newInstance(TRIAL_ORDER, RIGHT_ARM_TRIALS, LEFT_ARM_TRIALS, results);
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
