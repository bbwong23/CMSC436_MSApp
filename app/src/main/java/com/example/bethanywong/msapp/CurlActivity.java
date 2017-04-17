package com.example.bethanywong.msapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.umd.cmsc436.sheets.Sheets;

import static com.example.bethanywong.msapp.CurlTestFragment.newInstance;
import static com.example.bethanywong.msapp.CurlScoreFragment.newInstance;

public class CurlActivity extends FragmentActivity
        implements CurlTestInstructionFragment.StartCurlTestListener,
        CurlTestFragment.FinishCurlTrialListener,
        CurlScoreFragment.FinishCurlTestListener, Sheets.Host{
    private static final String[] TRIAL_ORDER = {"Right Arm", "Left Arm", "Right Arm", "Left Arm", "Right Arm", "Left Arm"};
    private static final int[] RIGHT_ARM_TRIALS = {0, 2, 4};
    private static final int[] LEFT_ARM_TRIALS = {1, 3, 5};
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private boolean hasBeenResumed;
    private float[] results;
    private Sheets sheet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curl);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        hasBeenResumed = false;
        results = new float[TRIAL_ORDER.length];
       String classID = "1YvI3CjS4ZlZQDYi5PaiA7WGGcoCsZfLoSFM0IdvdbDU";
       String trialID = "15e8fzzCQcYV3WxwV79g_CSyg-yeTyCrA1Z2e0uwpAiw";
       sheet = new Sheets(this, this, getString(R.string.app_name), classID, trialID);

        CurlTestInstructionFragment fragment = new CurlTestInstructionFragment();

        // place instruction fragment in view
        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed(){
        // disable back button
    }

    @Override
    public void onResume() {
        super.onResume();

        if (roundNumber >= 0 && roundNumber < TRIAL_ORDER.length-1 && hasBeenResumed) {
            CurlTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        hasBeenResumed = true;
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
        sheet.writeTrials(Sheets.TestType.RH_CURL,userId,right);
        sheet.writeTrials(Sheets.TestType.LH_CURL,userId,left);
    }

    public void sendToClassSheet(String userId, float dataR, float dataL){
        sheet.writeData(Sheets.TestType.RH_CURL, userId,dataR);
        sheet.writeData(Sheets.TestType.LH_CURL, userId,dataL);
    }
}

