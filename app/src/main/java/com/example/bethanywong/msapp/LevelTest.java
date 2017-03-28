package com.example.bethanywong.msapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import java.util.UUID;

import static com.example.bethanywong.msapp.LevelTestFragment.newInstance;
import static com.example.bethanywong.msapp.LevelScoreFragment.newInstance;

public class LevelTest extends FragmentActivity implements LevelTestInstructionFragment.StartLevelTestListener,
        LevelTestFragment.OnFinishLevelTestListener, LevelScoreFragment.FinishLevelTestListener {

    private static final String[] TRIAL_ORDER = {"Right hand", "Left hand", "Right hand", "Left hand", "Right hand", "Left hand",};
    private static final int [] RIGHT_HAND_TRIALS = {0, 2, 4};
    private static final int[] LEFT_HAND_TRIALS = {1, 3, 5};
    private int[] results = new int[TRIAL_ORDER.length];
    private static final int PERMISSION_REQUEST_CODE = 1;
    private int roundNumber;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private boolean hasBeenResumed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        hasBeenResumed = false;
        LevelTestInstructionFragment fragment = new LevelTestInstructionFragment();

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
            LevelTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        hasBeenResumed = true;
    }

    public void startLevelTest() {
        roundNumber++;
        LevelTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToNext(int score) {
        if (roundNumber >= 0 && roundNumber < TRIAL_ORDER.length) {
            results[roundNumber] = score;
        }

        if (roundNumber < TRIAL_ORDER.length-1) {
            startLevelTest();
        } else {
            // display score fragment
            LevelScoreFragment fragment = newInstance(TRIAL_ORDER, RIGHT_HAND_TRIALS, LEFT_HAND_TRIALS, results);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goHome() {
        finish();
    }

    public void saveDrawing() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), screenShot(findViewById(R.id.activity_level_test)),
                    UUID.randomUUID().toString() + ".png", "drawing");
            if (imgSaved != null) {
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            } else {
                Toast unsavedToast = Toast.makeText(getApplicationContext(),
                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
        }
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
