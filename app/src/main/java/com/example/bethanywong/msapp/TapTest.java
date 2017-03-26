package com.example.bethanywong.msapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

public class TapTest extends FragmentActivity implements TapTestInstructionFragment.StartTestListener,
        TapTestFragment.OnTapTestFinishListener, TapScoreFragment.FinishTapTestListener {

    public static final String ROUND_NUMBER_KEY = "ROUND_NUMBER_KEY";
    public static final String BODY_PART_KEY = "BODY_PART_KEY";
    public static final String RESULTS_KEY = "RESULTS_KEY";
    // ********* TO CHANGE ORDER OF TEST, CHANGE TRIAL_ORDER, THEN CHANGE THE OTHER FOUR FOLLOWING VARIABLES TO REFLECT THE NEW INDICES IN TRIAL_ORDER **************
    public static final String[] TRIAL_ORDER = {"Right index finger", "Left index finger", "Right index finger",
            "Left index finger", "Right index finger", "Left index finger", "Right big toe", "Left big toe",
            "Right big toe", "Left big toe", "Right big toe", "Left big toe"};
    public static final int[] RIGHT_HAND_TRIALS = {0, 2, 4};
    public static final int[] LEFT_HAND_TRIALS = {1, 3, 5};
    public static final int[] RIGHT_FOOT_TRIALS = {6, 8, 10};
    public static final int[] LEFT_FOOT_TRIALS = {7, 9, 11};
    private int[] allResults = new int[TRIAL_ORDER.length];
    // *************************** END ***************************************
    private int roundNumber = 0;
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

    public static TapScoreFragment newInstance(int[] results) {
        TapScoreFragment fragment = new TapScoreFragment();
        Bundle args = new Bundle();
        args.putIntArray(RESULTS_KEY, results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        TapTestInstructionFragment fragment = new TapTestInstructionFragment();

        // place instructions in view automatically
        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed(){
        // disable back button
    }

    public void startTest() {
        TapTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToNext(int trialResult) {
        allResults[roundNumber] = trialResult;
        // set up for next trial
        roundNumber++;
        if (roundNumber < TRIAL_ORDER.length) {
            startTest();
        } else {
            // trials are complete - display score fragment
            TapScoreFragment fragment = newInstance(allResults);
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
