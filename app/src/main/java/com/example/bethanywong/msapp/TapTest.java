package com.example.bethanywong.msapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import static com.example.bethanywong.msapp.TapScoreFragment.newInstance;
import static com.example.bethanywong.msapp.TapTestFragment.newInstance;

public class TapTest extends FragmentActivity implements TapTestInstructionFragment.StartTestListener,
        TapTestFragment.OnTapTestFinishListener, TapScoreFragment.FinishTapTestListener {

    public static final String ROUND_NUMBER_KEY = "ROUND_NUMBER_KEY";
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
    private int roundNumber;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private boolean hasBeenResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        hasBeenResumed = false;
        TapTestInstructionFragment fragment = new TapTestInstructionFragment();

        // place instructions in view automatically
        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed(){
        // disable back button
    }

    @Override
    public void onResume() {
        super.onResume();
        // reset trial
        if (roundNumber >= 0 && roundNumber < TRIAL_ORDER.length-1 && hasBeenResumed) {
            TapTestFragment fragment = newInstance(roundNumber);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        hasBeenResumed = true;
    }

    public void startTest() {
        TapTestFragment fragment = newInstance(++roundNumber);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToNext(int trialResult) {
        if (roundNumber >= 0 && roundNumber < TRIAL_ORDER.length) {
            allResults[roundNumber] = trialResult;
        }

        if (roundNumber < TRIAL_ORDER.length-1) {
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
