package com.example.bethanywong.msapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.umd.cmsc436.sheets.Sheets;

import static com.example.bethanywong.msapp.BalloonTestFragment.newInstance;


public class BalloonTest extends FragmentActivity
        implements BalloonTestInstructionFragment.StartBalloonTestListener,
        BalloonTestFragment.FinishBalloonTrialListener,
        BalloonScoreFragment.FinishBalloonTestListener, Sheets.Host{
    private static String[] TRIAL_ORDER = {"Right hand", "Left hand", "Right hand", "Left hand", "Right hand", "Left hand"};
    private static int[] RIGHT_HAND_TRIALS = {0, 2, 4};
    private static int[] LEFT_HAND_TRIALS = {1, 3, 5};
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private boolean hasBeenResumed;
    float[] results;

    private Sheets sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloon_test);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        results = new float[TRIAL_ORDER.length];
        hasBeenResumed = false;

        String classID = "1YvI3CjS4ZlZQDYi5PaiA7WGGcoCsZfLoSFM0IdvdbDU";
        String trialID = "15e8fzzCQcYV3WxwV79g_CSyg-yeTyCrA1Z2e0uwpAiw";
        sheet = new Sheets(this, getString(R.string.app_name), classID, trialID);

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
        results[roundNumber] = (float)(totalTime / 10000000000.0);

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

    public void sendToGroupSheet(String userId, float[] right, float[] left){
        sheet.writeTrials(Sheets.TestType.RH_POP,userId,right);
        sheet.writeTrials(Sheets.TestType.LH_POP,userId,left);
    }

    public void sendToClassSheet(String userId, float dataR, float dataL){
        sheet.writeData(Sheets.TestType.RH_POP, userId,dataR);
        sheet.writeData(Sheets.TestType.LH_POP, userId,dataL);
    }
}
