package com.example.bethanywong.msapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.umd.cmsc436.sheets.Sheets;

import static com.example.bethanywong.msapp.TapScoreFragment.newInstance;
import static com.example.bethanywong.msapp.TapTestFragment.newInstance;

public class TapTest extends FragmentActivity implements TapTestInstructionFragment.StartTestListener,
        TapTestFragment.OnTapTestFinishListener, TapScoreFragment.FinishTapTestListener, Sheets.Host {

    public static final String ROUND_NUMBER_KEY = "ROUND_NUMBER_KEY";
    public static final String RESULTS_KEY = "RESULTS_KEY";
    // ********* TO CHANGE ORDER OF TEST, CHANGE TRIAL_ORDER, THEN CHANGE THE OTHER FOUR FOLLOWING VARIABLES TO REFLECT THE NEW INDICES IN TRIAL_ORDER **************
    private static final String[] TRIAL_ORDER = {"Right index finger", "Left index finger", "Right index finger",
            "Left index finger", "Right index finger", "Left index finger", "Right big toe", "Left big toe",
            "Right big toe", "Left big toe", "Right big toe", "Left big toe"};
    private static final int[] RIGHT_HAND_TRIALS = {0, 2, 4};
    private static final int[] LEFT_HAND_TRIALS = {1, 3, 5};
    private static final int[] RIGHT_FOOT_TRIALS = {6, 8, 10};
    private static final int[] LEFT_FOOT_TRIALS = {7, 9, 11};
    private int[] allResults = new int[TRIAL_ORDER.length];
    // *************************** END ***************************************
    private int roundNumber;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private boolean hasBeenResumed;
    private Sheets sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        hasBeenResumed = false;
        TapTestInstructionFragment fragment = new TapTestInstructionFragment();
        String spreadsheetId = "1ASIF7kZHFFaUNiBndhPKTGYaQgTEbqPNfYO5DVb1Y9Y";
        sheet = new Sheets(this, getString(R.string.app_name), spreadsheetId);
        sendToSheets();

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
            TapTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        hasBeenResumed = true;
    }

    public void startTest() {
        TapTestFragment fragment = newInstance(TRIAL_ORDER[++roundNumber], roundNumber+1);
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
            TapScoreFragment fragment = newInstance(TRIAL_ORDER, RIGHT_HAND_TRIALS, LEFT_HAND_TRIALS, RIGHT_FOOT_TRIALS, LEFT_FOOT_TRIALS, allResults);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goHome() {
        finish();
    }

    @Override
    public int getRequestCode(Sheets.Action action) {
        switch (action) {
            case REQUEST_ACCOUNT_NAME:
                return 1;
            case REQUEST_AUTHORIZATION:
                return 2;
            case REQUEST_PERMISSIONS:
                return 3;
            case REQUEST_PLAY_SERVICES:
                return 4;
            default:
                return -1;
        }
    }

    @Override
    public void notifyFinished(Exception e) {
        if (e != null) {
            throw new RuntimeException(e);
        }
        Log.i(getClass().getSimpleName(), "Done");
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        this.sheet.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.sheet.onActivityResult(requestCode, resultCode, data);
    }

    private void sendToSheets() {
        String userId = "t8-testing";
        float data = 9.99f;
        sheet.writeData(Sheets.TestType.RH_TAP, userId, data);
    }
}
