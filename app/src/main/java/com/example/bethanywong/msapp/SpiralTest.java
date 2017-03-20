package com.example.bethanywong.msapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class SpiralTest extends FragmentActivity implements SpiralTestFragment.OnFinishListener, SpiralScoreFragment.FinishSpiralTestListener {
    protected static final String SCORE_KEY = "SCORE_KEY";
    protected static final String R_HAND = "right";
    protected static final String L_HAND = "left";
    protected static final String HAND_KEY = "hand";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private DrawingView drawView;
    private ImageView original;
    private int lScore, rScore;
    private Button finishButton;
    private View screen;
    private TextView instructions;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int trial;
    private static int[] scores;
    private double[] durations;

    public static SpiralTestFragment newInstance(String hand) {
        SpiralTestFragment fragment = new SpiralTestFragment();
        Bundle args = new Bundle();
        args.putString(HAND_KEY, hand);
        fragment.setArguments(args);
        return fragment;
    }

    public static SpiralScoreFragment newInstance(int[] score) {
        SpiralScoreFragment fragment = new SpiralScoreFragment();
        Bundle args = new Bundle();
        args.putIntArray(SCORE_KEY, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test);
        drawView = (DrawingView)findViewById(R.id.drawing);
        original = (ImageView)findViewById(R.id.spiral);
        finishButton = (Button)findViewById(R.id.finish);
        screen = findViewById(R.id.activity_spiral_test);
        instructions = (TextView) findViewById(R.id.instructions);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        SpiralTestFragment rFragment = newInstance(R_HAND);
        trial = 0;
        scores = new int[6];
        durations = new double[6];
        // place initial test in view automatically
        transaction.add(R.id.fragmentContainer, rFragment).addToBackStack(null).commit();
    }

    public void onFinish(String hand, int score, long duration) {

        Log.i("info", "onFinish()");
        String testHand;
        scores[trial] = score;
        durations[trial] = duration;
        if (trial < 5){
            testHand = trial < 2 ? R_HAND:L_HAND;
            SpiralTestFragment nextTrialFragment = newInstance(testHand);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, nextTrialFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            trial++;
        } else {
            SpiralScoreFragment scoreFragment = newInstance(scores);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, scoreFragment);
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
