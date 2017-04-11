package com.example.bethanywong.msapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import edu.umd.cmsc436.sheets.Sheets;

import static com.example.bethanywong.msapp.SwayTestFragment.newInstance;
import static com.example.bethanywong.msapp.SwayScoreFragment.newInstance;

public class SwayTest extends FragmentActivity
        implements SwayTestInstructionFragment.StartSwayTestListener,
        SwayTestFragment.OnSwayRoundFinishListener,
        SwayScoreFragment.OnFinishSwayTestListener,
        Sheets.Host {
    public static int NUMBER_OF_TRIALS = 3;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private boolean hasBeenResumed;
    private float[] results;
    private Sheets sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sway_test);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        hasBeenResumed = false;
        results = new float[NUMBER_OF_TRIALS];
        String classID = "1YvI3CjS4ZlZQDYi5PaiA7WGGcoCsZfLoSFM0IdvdbDU";
        String trialID = "15e8fzzCQcYV3WxwV79g_CSyg-yeTyCrA1Z2e0uwpAiw";
        sheet = new Sheets(this, getString(R.string.app_name), classID, trialID);
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
        results[roundNumber] = (float)trialResult;
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

    public void sendToGroupSheet(String userId, float[] trials){
        sheet.writeTrials(Sheets.TestType.HEAD_SWAY,userId,trials);
    }

    public void sendToClassSheet(String userId, float avg){
        sheet.writeData(Sheets.TestType.HEAD_SWAY, userId, avg);
    }
}
