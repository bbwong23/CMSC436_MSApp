package com.example.bethanywong.msapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.umd.cmsc436.sheets.Sheets;

import static com.example.bethanywong.msapp.SpiralScoreFragment.newInstance;
import static com.example.bethanywong.msapp.SpiralTestFragment.newInstance;

public class SpiralTest extends FragmentActivity implements SpiralTestFragment.OnFinishListener,
        SpiralScoreFragment.FinishSpiralTestListener, Sheets.Host {
    private static final String[] TRIAL_ORDER = {"right hand", "left hand", "right hand", "left hand", "right hand", "left hand"};
    private static final int[] RIGHT_HAND_TRIALS = {0, 2, 4};
    private static final int[] LEFT_HAND_TRIALS = {1, 3, 5};
    protected static final String RESULT_KEY = "RESULT_KEY";
    protected static final String ROUND_KEY = "ROUND_KEY";
//    private static final int PERMISSION_REQUEST_CODE = 1;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private static float[] scores;
    private double[] durations;
    private boolean hasBeenResumed;
    private Sheets sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = 0;
        scores = new float[6];
        durations = new double[6];
        hasBeenResumed = false;
        String classSpreadsheetId = "1YvI3CjS4ZlZQDYi5PaiA7WGGcoCsZfLoSFM0IdvdbDU";
        String groupSpreadsheetId = "15e8fzzCQcYV3WxwV79g_CSyg-yeTyCrA1Z2e0uwpAiw";
        sheet = new Sheets(this, "MS App", classSpreadsheetId,groupSpreadsheetId);

        // place initial test in view automatically
        SpiralTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
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
            SpiralTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        hasBeenResumed = true;
    }

    public void onFinish(float score, long duration) {
        durations[roundNumber] = duration;
        scores[roundNumber++] = score;

        if (roundNumber < TRIAL_ORDER.length) {
            // show next trial
            SpiralTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            // show score fragment
            SpiralScoreFragment fragment = newInstance(TRIAL_ORDER, RIGHT_HAND_TRIALS, LEFT_HAND_TRIALS, scores);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goHome() {
        finish();
    }

    public void sendToClassSheet(String userId, float dataR, float dataL){
        sheet.writeData(Sheets.TestType.RH_SPIRAL, userId,dataR);
        sheet.writeData(Sheets.TestType.LH_SPIRAL, userId,dataL);
    }
    public void sendToGroupSheet(String userId, float[] right, float[] left){
        sheet.writeTrials(Sheets.TestType.RH_SPIRAL,userId,right);
        sheet.writeTrials(Sheets.TestType.LH_SPIRAL,userId,left);
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
}
