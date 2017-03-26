package com.example.bethanywong.msapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import static com.example.bethanywong.msapp.SpiralScoreFragment.newInstance;
import static com.example.bethanywong.msapp.SpiralTestFragment.newInstance;

public class SpiralTest extends FragmentActivity implements SpiralTestFragment.OnFinishListener, SpiralScoreFragment.FinishSpiralTestListener {
    public static final String[] TRIAL_ORDER = {"Right hand", "Left hand", "Right hand", "Left hand", "Right hand", "Left hand"};
    public static final int[] RIGHT_HAND_TRIALS = {0, 2, 4};
    public static final int[] LEFT_HAND_TRIALS = {1, 3, 5};
    protected static final String RESULT_KEY = "RESULT_KEY";
    protected static final String ROUND_KEY = "ROUND_KEY";
//    private static final int PERMISSION_REQUEST_CODE = 1;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private static int[] scores;
    private double[] durations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        SpiralTestFragment rFragment = newInstance(roundNumber);
        roundNumber = 0;
        scores = new int[6];
        durations = new double[6];
        // place initial test in view automatically
        transaction.add(R.id.fragmentContainer, rFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        // disable back button
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save round number to re-do round

    }

    @Override
    public void onResume() {
        super.onResume();

    }
    public void onFinish(int score, long duration) {
        durations[roundNumber] = duration;
        scores[roundNumber++] = score;

        if (roundNumber < TRIAL_ORDER.length) {
            // show next trial
            SpiralTestFragment fragment = newInstance(roundNumber);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            // show score fragment
            SpiralScoreFragment fragment = newInstance(scores);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goHome() {
        finish();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//      if (requestCode == PERMISSION_REQUEST_CODE){
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //if permission wasn't granted the first time, call save drawing again.
//                //reason we call again is because the permission request is async, pls help with the callback pls
//                saveDrawing(view);
//            } else {
//                //some error handling lolz
//            }
//        }
//    }
//

}
